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
import spreadsheet.range.api.Range;
import spreadsheet.range.impl.RangeImpl;
import spreadsheet.sheetmanager.api.SheetManager;
import spreadsheet.util.UpdateResult;
import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

// Implementation of a spreadsheet that supports cell dependencies, ranges, and formula calculations
// Manages a collection of cells and provides operations for updating and calculating cell values
public class SpreadSheetImpl implements SpreadSheet, Serializable {

    private final Dimension sheetDimension;
    private final Map<CellIdentifier, Cell> activeCells;
    private Map<String, Range> ranges = new HashMap<>();
    private String sheetName;

    public SpreadSheetImpl(Dimension sheetDimension,SheetManager sheetManager) {
        this.activeCells = new HashMap<>();
        this.sheetDimension = sheetDimension;
        this.ranges = new HashMap<>();
        this.sheetName = sheetManager.getSheetName();
    }

    // Returns the name of the spreadsheet
    @Override
    public String getSheetName(){return this.sheetName;}

    // Validates if a cell identifier is in correct format (e.g., A1, B2) and within sheet bounds
    // Throws IllegalArgumentException if the format is invalid or out of bounds
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

    // Returns the calculated value of a cell. If cell doesn't exist, creates an empty cell first
    @Override
    public EffectiveValue getCellEffectiveValue(CellIdentifier identifier) {
        Cell cell = activeCells.get(identifier);
        if(cell == null) {
            addEmptyCell(identifier);
        }
        return cell != null ? cell.getEffectiveValue() : null;
    }

    // Creates and adds an empty cell at the specified identifier
    @Override
    public void addEmptyCell (CellIdentifier identifier){
        CellImpl newCell = new CellImpl(identifier, "",1,this,"");
        newCell.calculateEffectiveValue();
        activeCells.put(identifier, newCell);
        this.updateDependenciesAndInfluences();
    }

    // Returns the sheet dimensions
    @Override
    public Dimension getSheetDimensions() {
        return sheetDimension;
    }

    // Returns all active (non-empty) cells in the sheet
    @Override
    public Map<CellIdentifier, Cell> getActiveCells() {
        return activeCells;
    }

    // Returns a specific cell by its identifier
    @Override
    public Cell getCell(CellIdentifier identifier) {
        return activeCells.get(identifier);
    }

    // Updates a cell's value and recalculates all dependent cells
    // Returns UpdateResult containing either the updated sheet or error message
    // isDynamicUpdate: if true, doesn't increment version number
    @Override
    public UpdateResult updateCellValueAndCalculate(CellIdentifier cellId, String originalValue, boolean isDynamicUpdate,String modifyingUserName,int currentVersion) {
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

    // Updates the dependency graph for all cells in the spreadsheet
    // Throws IllegalStateException if a circular dependency is detected
    public void updateDependenciesAndInfluences() {
        for (Cell cell : activeCells.values()) {
            cell.resetDependencies();
            cell.resetInfluences();
        }
        for (Cell cell : activeCells.values()) {
            String originalValue = cell.getOriginalValue();
            List<CellIdentifier> referencedCellIds = extractReferences(originalValue);

            for (CellIdentifier referencedCellId : referencedCellIds) {
                Cell referencedCell = activeCells.get(referencedCellId);

                if (referencedCell != null) {
                    referencedCell.getInfluences().add(cell.getIdentifier());
                    cell.getDependencies().add(referencedCell.getIdentifier());
                }
            }
        }
        try {
            orderCellsForCalculation();
        } catch (IllegalStateException e) {
            throw new IllegalStateException(e.getMessage());
        }
    }

    // Extracts cell references from formulas like {REF,A1} or {SUM,range1}
    // Returns list of cell identifiers that are referenced in the formula
    private List<CellIdentifier> extractReferences(String value) {
        List<CellIdentifier> references = new ArrayList<>();
        int i = 0;

        while (i < value.length()) {
            int start = value.indexOf("{", i);
            if (start == -1) {
                break;
            }

            String upperCommandPart = value.substring(start).toUpperCase();

            if (upperCommandPart.startsWith("{REF,")) {
                int cellIdStart = start + 5;
                int cellIdEnd = findCellIdEnd(value, cellIdStart);
                if (cellIdEnd > cellIdStart) {
                    String cellId = value.substring(cellIdStart, cellIdEnd).trim().toUpperCase();
                    references.add(new CellIdentifierImpl(cellId));
                }
                i = cellIdEnd;
            } else if (upperCommandPart.startsWith("{SUM,") || upperCommandPart.startsWith("{AVERAGE,")) {
                int rangeNameStart = start + (upperCommandPart.startsWith("{SUM,") ? 5 : 9);
                int rangeNameEnd = findCellIdEnd(value, rangeNameStart);
                if (rangeNameEnd > rangeNameStart) {
                    String rangeName = value.substring(rangeNameStart, rangeNameEnd).trim();
                    Range range = this.getRange(rangeName);
                    if (range != null) {
                        references.addAll(range.getCellsInRange());
                    }
                }
                i = rangeNameEnd;
            } else {
                i = start + 1;
            }
        }
        return references;
    }

    // Helper method to find the end of a cell identifier in a formula
    private int findCellIdEnd(String value, int start) {
        int end = start;
        while (end < value.length() && value.charAt(end) != ',' && value.charAt(end) != '}') {
            end++;
        }
        return end;
    }

    // Orders cells for calculation based on their dependencies
    // Returns a list of cells in topological order for safe calculation
    private List<Cell> orderCellsForCalculation() {
        DirGraph<Cell> graph = new DirGraphImpl<>();

        for (Map.Entry<CellIdentifier, Cell> entry : activeCells.entrySet()) {
            Cell cell = entry.getValue();
            graph.addNode(cell);
            for (CellIdentifier dependency : cell.getDependencies()) {
                Cell dependencyCell = activeCells.get(dependency);
                if (dependencyCell != null) {
                    graph.addEdge(dependencyCell, cell);
                }
            }
        }

        return graph.topologicalSort();
    }

    // Creates a deep copy of the current spreadsheet state
    // Used for safe calculation of updates without modifying original sheet
    private SpreadSheetImpl copySheet() {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(this);
            oos.close();

            ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(baos.toByteArray()));
            return (SpreadSheetImpl) ois.readObject();
        } catch (Exception e) {
            return this;
        }
    }

    // Compares two spreadsheets for equality based on their active cells
    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass()) return false;
        SpreadSheetImpl sheet = (SpreadSheetImpl) o;
        return Objects.equals(activeCells, sheet.activeCells);
    }

    // Generates hash code for the spreadsheet based on its active cells
    @Override
    public int hashCode() {
        return Objects.hash(activeCells);
    }

    // Verifies if a range defined by two corner cells is within sheet boundaries
    @Override
    public boolean isRangeWithinBounds(CellIdentifier topLeft, CellIdentifier bottomRight) {
        return isCellWithinBounds(topLeft) && isCellWithinBounds(bottomRight);
    }

    // Checks if a single cell is within sheet boundaries
    @Override
    public boolean isCellWithinBounds(CellIdentifier cell) {
        int row = cell.getRow();
        char col = cell.getCol();
        return row >= 1 && row <= sheetDimension.getNumRows()
                && col >= 'A' && col < ('A' + sheetDimension.getNumCols());
    }

    // Creates a new named range in the spreadsheet
    // Throws IllegalArgumentException if name exists or range is invalid
    @Override
    public void addRange(String name, CellIdentifier topLeft, CellIdentifier bottomRight) {
        for (Range range: ranges.values()) {
            if (range.getName().toUpperCase().equals(name.toUpperCase()))
                throw new IllegalArgumentException("Each range must have a unique name. Please check and try again.");
        }
        if (!isRangeWithinBounds(topLeft, bottomRight)) {
            throw new IllegalArgumentException("The range '"+ name +"' is out of bounds\n"+"The bounds are-\n"+"Rows between 1-" +
                    this.sheetDimension.getNumRows() + "\nCol between A -"+ (char) (this.sheetDimension.getNumCols() - 1 + 'A'));
        }
        ranges.put(name, new RangeImpl (name, topLeft, bottomRight,sheetDimension));
        List<CellIdentifier> cellsInRange = ranges.get(name).getCellsInRange();
        for (CellIdentifier cellIdentifier : cellsInRange) {
            if (!activeCells.containsKey(cellIdentifier)){
                addEmptyCell(cellIdentifier);
            }
        }
    }

    // Returns a specific range by its name
    @Override
    public Range getRange(String name) {
        for (Range range : ranges.values()) {
            if (range.getName().toUpperCase().equals(name.toUpperCase()))
                return range;
        }
        return null;
    }

    // Returns all ranges defined in the spreadsheet
    @Override
    public Map<String, Range> getRanges()  {
        return ranges;
    }

    // Removes a named range if it exists and is not being used in any formulas
    // Throws IllegalArgumentException if range doesn't exist or is in use
    @Override
    public void removeRange(String name) {
        updateRangeActivation(name);
        if (!ranges.containsKey(name)) {
            throw new IllegalArgumentException("Range not found");
        }
        if (ranges.get(name).isActive())
            throw new IllegalArgumentException("Range in use cannot be removed");
        ranges.remove(name);
    }

    // Updates the active status of a range by checking if it's used in any cell formulas
    private void updateRangeActivation(String name) {
        Range range = ranges.get(name);
        for (Cell cell: activeCells.values()) {
            if (cell.getOriginalValue().toUpperCase().equals("{SUM,"+name.toUpperCase()+"}") ||
                    cell.getOriginalValue().toUpperCase().equals("{AVERAGE,"+name.toUpperCase()+"}")) {
                return;
            }
        }
        range.setActive(false);
    }
}