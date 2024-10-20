package spreadsheet.sheetimpl;

import spreadsheet.api.Dimension;
import spreadsheet.api.SpreadSheet;
import spreadsheet.cell.api.Cell;
import spreadsheet.cell.api.CellIdentifier;
import spreadsheet.cell.api.EffectiveValue;
import spreadsheet.cell.impl.CellIdentifierImpl;
import spreadsheet.cell.impl.CellImpl;
import spreadsheet.graph.api.DirGraph;
import spreadsheet.graph.impl.DirGraphImpl;
import spreadsheet.range.impl.RangeImpl;
import spreadsheet.sheetmanager.api.SheetManager;
import spreadsheet.util.UpdateResult;
import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

public class SpreadSheetImpl implements SpreadSheet, Serializable {

    private final Dimension sheetDimension;
    private final Map<CellIdentifier, Cell> activeCells;
    private Map<String, RangeImpl> ranges = new HashMap<>();
    private String sheetName;


    public SpreadSheetImpl(Dimension sheetDimension,SheetManager sheetManager) {
        this.activeCells = new HashMap<>();
        this.sheetDimension = sheetDimension;
        this.ranges = new HashMap<>();
        this.sheetName = sheetManager.getSheetName();
    }

    @Override
    public String getSheetName(){return this.sheetName;}

    @Override
    public boolean isValidCellID(String cellID) {
        // Regular expression to match one uppercase letter followed by one or more digits
        String regex = "^([A-Z])([0-9]+)$";

        // Check if the input matches the pattern1
        if (!cellID.matches(regex)) {
            throw new IllegalArgumentException("Invalid cell identifier format. Expected format: A1, B2, etc and received:" + cellID);
        }

        // Extract the column (first character) and row (remaining part)
        char col = cellID.charAt(0);
        int row = Integer.parseInt(cellID.substring(1));

        // Validate the row and column ranges
        if (row < 1 || row > this.sheetDimension.getNumRows()
                || col < 'A' || col > this.sheetDimension.getNumCols() + 'A') {
            throw new IllegalArgumentException("There is an invalid cell identifier in the sheet - \n" + "Row: Expected number between 1-"
                    + this.sheetDimension.getNumRows() + " and got " + row
                    + "\n" + "Col: Expected char between A - "
                    + (char) (this.sheetDimension.getNumCols() - 1 + 'A') + " and got " + col);
        }

        return true;
    }

    @Override
    public EffectiveValue getCellEffectiveValue(CellIdentifierImpl identifier) {
        Cell cell = activeCells.get(identifier);
        if(cell == null) {
           addEmptyCell(identifier);
        }
        return cell != null ? cell.getEffectiveValue() : null;
    }
    @Override
    public void addEmptyCell (CellIdentifierImpl identifier){
        CellImpl newCell = new CellImpl(identifier, "",1,this,"");
        newCell.calculateEffectiveValue();
        activeCells.put(identifier, newCell);
        this.updateDependenciesAndInfluences();
    }

    @Override
    public Dimension getSheetDimentions() {
        return sheetDimension;
    }

    @Override
    public Map<CellIdentifier, Cell> getActiveCells() {
        return activeCells;
    }
    @Override
    public Cell getCell(CellIdentifier identifier) {
        return activeCells.get(identifier);
    }
    @Override
    public void removeCell(CellIdentifier identifier) {
        activeCells.remove(identifier);
    }

    @Override
    public UpdateResult updateCellValueAndCalculate(CellIdentifierImpl cellId, String originalValue, boolean isDynamicUpdate,String modifyingUserName,int currentVersion) {
        SpreadSheetImpl newSheetVersion = this.copySheet();
        newSheetVersion.updateDependenciesAndInfluences();
        int versionUpdate = isDynamicUpdate ? 0 : 1;
        int newVer = currentVersion + versionUpdate;
        Cell newCell = new CellImpl(cellId, originalValue, newVer, newSheetVersion,modifyingUserName);
        Cell beforeUpdateCell = activeCells.get(cellId);
        if(beforeUpdateCell != null) {newCell.setCellStyle(beforeUpdateCell.getCellStyle());}
        newSheetVersion.activeCells.put(cellId, newCell);

        try {
            List<Cell> cellsThatHaveChanged =
                    newSheetVersion
                            .orderCellsForCalculation()
                            .stream()
                            .filter(Cell::calculateEffectiveValue)
                            .collect(Collectors.toList());

            // successful calculation. update sheet and relevant cells version
            if(!isDynamicUpdate) {
                cellsThatHaveChanged.forEach(cell -> cell.updateVersion(newVer));
            }
             newSheetVersion.updateDependenciesAndInfluences();
             for (Cell cell : newSheetVersion.activeCells.values()) {
                 cell.calculateEffectiveValue();
             }

        return new UpdateResult(newSheetVersion,null);
        } catch (Exception e) {
            return new UpdateResult(this, e.getMessage());
        }
    }

    public void updateDependenciesAndInfluences() {
        for (Cell cell : activeCells.values()) {
            cell.resetDependencies();
            cell.resetInfluences();
        }
        for (Cell cell : activeCells.values()) {
            // Parse the cell's value to find references
            String originalValue = cell.getOriginalValue();
            List<CellIdentifier> referencedCellIds = extractReferences(originalValue);

            for (CellIdentifier referencedCellId : referencedCellIds) {
                Cell referencedCell = activeCells.get(referencedCellId);

                if (referencedCell != null) {
                    // Add current cell as a dependency for the referenced cell
                    referencedCell.getInfluences().add(cell.getIdentifier());

                    // Add current cell to the influences of the referenced cell
                    cell.getDependencies().add(referencedCell.getIdentifier());
                }
            }
        }
        // Check for cycles immediately after updating dependencies and influences
        try {
            orderCellsForCalculation();  // If this method fails, it means a cycle exists.
        } catch (IllegalStateException e) {
            throw new IllegalStateException(e.getMessage());
        }
    }

    private List<CellIdentifier> extractReferences(String value) {
        List<CellIdentifier> references = new ArrayList<>();
        int i = 0;

        while (i < value.length()) {
            // Find the start of a REF, SUM, or AVERAGE command
            int start = value.indexOf("{", i);

            // If no more commands are found, break the loop
            if (start == -1) {
                break;
            }

            // Extract the command in uppercase (but not the whole value)
            String upperCommandPart = value.substring(start).toUpperCase();

            if (upperCommandPart.startsWith("{REF,")) {
                // Handle REF command (direct cell reference)
                int cellIdStart = start + 5; // Move past "{REF,"
                int cellIdEnd = findCellIdEnd(value, cellIdStart);
                if (cellIdEnd > cellIdStart) {
                    String cellId = value.substring(cellIdStart, cellIdEnd).trim().toUpperCase();
                    references.add(new CellIdentifierImpl(cellId));
                }
                i = cellIdEnd;
            } else if (upperCommandPart.startsWith("{SUM,") || upperCommandPart.startsWith("{AVERAGE,")) {
                // Handle SUM or AVERAGE command with a range name
                int rangeNameStart = start + (upperCommandPart.startsWith("{SUM,") ? 5 : 9); // Move past "{SUM," or "{AVERAGE,"
                int rangeNameEnd = findCellIdEnd(value, rangeNameStart);
                if (rangeNameEnd > rangeNameStart) {
                    String rangeName = value.substring(rangeNameStart, rangeNameEnd).trim(); // Keep original case for the range name
                    RangeImpl range = this.getRange(rangeName); // Fetch range by its name
                    if (range != null)
                        references.addAll(range.getCellsInRange());
                    // Add all cells in range
                }
                i = rangeNameEnd;
            } else {
                i = start + 1;} // Skip invalid command
        }
        return references;
    }

    private int findCellIdEnd(String value, int start) {
        int end = start;
        while (end < value.length() && value.charAt(end) != ',' && value.charAt(end) != '}') {
            end++;
        }
        return end;
    }

    private List<Cell> orderCellsForCalculation() {
        DirGraph<Cell> graph = new DirGraphImpl<>();

        // Step 1: Build the Graph
        for (Map.Entry<CellIdentifier, Cell> entry : activeCells.entrySet()) {
            Cell cell = entry.getValue();
            graph.addNode(cell);
            // Add edges based on cell dependencies
            for (CellIdentifier dependency : cell.getDependencies()) {
                Cell dependencyCell = activeCells.get(dependency);
                if (dependencyCell != null) {
                    graph.addEdge(dependencyCell, cell);
                }
            }
        }

        // Step 2: Perform Topological Sort
        List<Cell> orderedCells = graph.topologicalSort();

        // Step 3: Return the ordered list
        return orderedCells;
    }

    private SpreadSheetImpl copySheet() {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(this);
            oos.close();

            ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(baos.toByteArray()));
            return (SpreadSheetImpl) ois.readObject();
        } catch (Exception e) {
            // deal with the runtime error that was discovered as part of invocation
            return this;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass()) return false;
        SpreadSheetImpl sheet = (SpreadSheetImpl) o;
        return Objects.equals(activeCells, sheet.activeCells);
    }

    @Override
    public int hashCode() {
        return Objects.hash(activeCells);
    }

    public boolean isRangeWithinBounds(CellIdentifierImpl topLeft, CellIdentifierImpl bottomRight) {
        return isCellWithinBounds(topLeft) && isCellWithinBounds(bottomRight);
    }

    public boolean isCellWithinBounds(CellIdentifierImpl cell) {
        int row = cell.getRow();
        char col = cell.getCol();
        return row >= 1 && row <= sheetDimension.getNumRows()
                && col >= 'A' && col < ('A' + sheetDimension.getNumCols());
    }

    public void addRange(String name, CellIdentifierImpl topLeft, CellIdentifierImpl bottomRight) {
        if (ranges.containsKey(name)) {
            throw new IllegalArgumentException("Range name already exists");
        }
        if (!isRangeWithinBounds(topLeft, bottomRight)) {
            throw new IllegalArgumentException("Range is out of bounds");
        }
        ranges.put(name, new RangeImpl (name, topLeft, bottomRight,sheetDimension));
        List<CellIdentifierImpl> cellsInRange = ranges.get(name).getCellsInRange();
        for (CellIdentifierImpl cellIdentifier : cellsInRange) {
            if (!activeCells.containsKey(cellIdentifier)){
                addEmptyCell(cellIdentifier);
            }
        }

    }

    public void removeRange(String name) {
        if (!ranges.containsKey(name)) {
            throw new IllegalArgumentException("Range not found");
        }
        if (ranges.get(name).isActive())
            throw new IllegalArgumentException("Range in use cannot be removed");
        ranges.remove(name);
    }

    public RangeImpl getRange(String name) {
        if (!ranges.containsKey(name)) {
            return null;
        }
        return ranges.get(name);
    }

    public Map<String, RangeImpl> getRanges()  {
        return ranges;
    }


}



