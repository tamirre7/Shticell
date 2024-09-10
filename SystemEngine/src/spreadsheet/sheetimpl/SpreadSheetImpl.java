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
import spreadsheet.util.UpdateResult;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

public class SpreadSheetImpl implements SpreadSheet, Serializable {

    private final Dimension sheetDimension;
    private String name;
    private int version;
    private Map<CellIdentifier, Cell> activeCells;
    int amountOfCellsChangedInVersion;
    private Map<String, RangeImpl> ranges = new HashMap<>();


    public SpreadSheetImpl(String name, int version, Dimension sheetDimension) {
        this.name = name;
        this.version = version;
        this.activeCells = new HashMap<>();
        this.sheetDimension = sheetDimension;
        this.ranges = new HashMap<>();
    }

    @Override
    public int getAmountOfCellsChangedInVersion(){return amountOfCellsChangedInVersion;}
    @Override
    public void setAmountOfCellsChangedInVersion(int amountOfCellsChangedInVersion) {
        this.amountOfCellsChangedInVersion = amountOfCellsChangedInVersion;
    }

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
            throw new IllegalArgumentException("Invalid cell identifier - Row: Expected number between 1-"
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
            //create new empty cell
            CellImpl newCell = new CellImpl(identifier, "",0,this);
            newCell.calculateEffectiveValue();
            activeCells.put(identifier, newCell);
            this.updateDependenciesAndInfluences();
        }
        return cell != null ? cell.getEffectiveValue() : null;
    }

    // Getters and Setters
    @Override
    public String getName() {
        return name;
    }
    @Override
    public void setName(String name) {
        this.name = name;
    }
    @Override
    public int getVersion() {
        return version;
    }
    @Override
    public Dimension getSheetDimentions() {
        return sheetDimension;
    }
    @Override
    public void setVersion(int version) {
        this.version = version;
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
    public UpdateResult updateCellValueAndCalculate(CellIdentifierImpl cellId, String originalValue) {
        SpreadSheetImpl newSheetVersion = this.copySheet();
        newSheetVersion.updateDependenciesAndInfluences();
        Cell newCell = new CellImpl(cellId, originalValue, newSheetVersion.getVersion() + 1, newSheetVersion);
        newSheetVersion.activeCells.put(cellId, newCell);

        try {
            List<Cell> cellsThatHaveChanged =
                    newSheetVersion
                            .orderCellsForCalculation()
                            .stream()
                            .filter(Cell::calculateEffectiveValue)
                            .collect(Collectors.toList());

            // successful calculation. update sheet and relevant cells version
             int newVersion = newSheetVersion.increaseVersion();
             cellsThatHaveChanged.forEach(cell -> cell.updateVersion(newVersion));
             newSheetVersion.setAmountOfCellsChangedInVersion(cellsThatHaveChanged.size());
             newSheetVersion.updateDependenciesAndInfluences();
             for (Cell cell : newSheetVersion.activeCells.values()) {
                 cell.calculateEffectiveValue();
             }

        return new UpdateResult(newSheetVersion,null);
        } catch (Exception e) {
            return new UpdateResult(this, e.getMessage());
        }
    }

    private int increaseVersion() {
        return ++version;
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
        String upperValue = value.toUpperCase();

        while (i < upperValue.length()) {
            // Find the start of a REF command
            int start = upperValue.indexOf("{REF,", i);

            // If no more REF commands are found, break the loop
            if (start == -1) {
               break;
            }

            // Move the index to where the cell ID should start
            int cellIdStart = start + 5; // Move past "{REF,"

            // Skip any whitespace after "{REF,"
            while (cellIdStart < upperValue.length() && upperValue.charAt(cellIdStart) == ' ') {
                cellIdStart++;
            }

            // Find the end of the cell ID (it's before the next comma or closing brace)
            int cellIdEnd = cellIdStart;
            while (cellIdEnd < upperValue.length() && upperValue.charAt(cellIdEnd) != ',' && upperValue.charAt(cellIdEnd) != '}') {
                cellIdEnd++;
            }

            // Extract and add the cell ID if it's valid
            if (cellIdEnd > cellIdStart) {
                String cellId = upperValue.substring(cellIdStart, cellIdEnd).trim();
                references.add(new CellIdentifierImpl(cellId)); // Assuming CellIdentifierImpl has this constructor
            }

            // Move the index to continue searching
            i = cellIdEnd;
        }

        return references;
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
        return version == sheet.version &&
                Objects.equals(name, sheet.name) &&
                Objects.equals(activeCells, sheet.activeCells);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, version, activeCells);
    }



    public boolean isRangeWithinBounds(CellIdentifierImpl topLeft, CellIdentifierImpl bottomRight) {
        return isCellWithinBounds(topLeft) && isCellWithinBounds(bottomRight);
    }

    private boolean isCellWithinBounds(CellIdentifierImpl cell) {
        int row = cell.getRow();
        char col = cell.getCol();
        return row >= 1 && row <= sheetDimension.getNumRows()
                && col >= 'A' && col < ('A' + sheetDimension.getNumCols());
    }

    public void addRange(String name, Cell topLeft, Cell bottomRight) throws IllegalArgumentException {
        if (ranges.containsKey(name)) {
            throw new IllegalArgumentException("Range name already exists");
        }
        if (!isRangeWithinBounds(topLeft.getIdentifier(), bottomRight.getIdentifier())) {
            throw new IllegalArgumentException("Range is out of bounds");
        }
        ranges.put(name, new RangeImpl (name, topLeft, bottomRight));
    }

    public void removeRange(String name) throws IllegalArgumentException {
        if (!ranges.containsKey(name)) {
            throw new IllegalArgumentException("Range not found");
        }
        ranges.remove(name);
    }

    public RangeImpl getRange(String name) throws IllegalArgumentException {
        if (!ranges.containsKey(name)) {
            throw new IllegalArgumentException("Range not found");
        }
        return ranges.get(name);
    }

    public List<RangeImpl> getAllRanges() {
        return new ArrayList<>(ranges.values());
    }
}



