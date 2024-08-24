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

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

public class SpreadSheetImpl implements SpreadSheet, Serializable {

    private final Dimension sheetDimension;
    private String name;
    private int version;
    private Map<CellIdentifier, Cell> activeCells;
    int amountOfCellsChangedInVersion;

    public SpreadSheetImpl(String name, int version, Dimension sheetDimension) {
        this.name = name;
        this.version = version;
        this.activeCells = new HashMap<>();
        this.sheetDimension = sheetDimension;
    }

    @Override
    public int getAmountOfCellsChangedInVersion(){return amountOfCellsChangedInVersion;}

    @Override
    public boolean isValidCellID(CellIdentifier cellID) {
        if (cellID.getRow() < 1 ||
                cellID.getRow() > this.sheetDimension.getNumRows())
            throw new IllegalArgumentException("Invalid cell identifier - ROW out of range : Expected number between 1-" + this.sheetDimension.getNumRows() + " but got " + cellID.getRow());

        if (cellID.getCol() < 'A' ||
                cellID.getCol() > this.sheetDimension.getNumCols() + 'A')
            throw new IllegalArgumentException("Invalid cell identifier - COL out of range: Expected character between A - " + this.sheetDimension.getNumCols() + 'A' + " but got " + cellID.getCol());

        return true;
    }

    @Override
    public EffectiveValue getCellEffectiveValue(CellIdentifier identifier) {
        Cell cell = activeCells.get(identifier);
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
    public SpreadSheet updateCellValueAndCalculate(CellIdentifierImpl cellId, String originalValue) {


        SpreadSheetImpl newSheetVersion = copySheet();
        this.updateDependenciesAndInfluences();
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
             amountOfCellsChangedInVersion = cellsThatHaveChanged.size();

            return newSheetVersion;
        } catch (Exception e) {
            // deal with the runtime error that was discovered as part of invocation
            return this;
        }
    }

    private int increaseVersion() {
        return ++version;
    }

    public void updateDependenciesAndInfluences() {
        for (Cell cell : activeCells.values()) {
            // Parse the cell's value to find references
            String originalValue = cell.getOriginalValue();
            List<CellIdentifier> referencedCellIds = extractReferences(originalValue);  // Updated to use extractReferences

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
    }


    private List<CellIdentifier> extractReferences(String value) {
        List<CellIdentifier> references = new ArrayList<>();
        int i = 0;

        while (i < value.length()) {
            // Find the start of a REF command
            int start = value.indexOf("{REF,", i);

            // If no more REF commands are found, break the loop
            if (start == -1) {
                break;
            }

            // Move the index to where the cell ID should start
            int cellIdStart = start + 5; // Move past "{REF,"

            // Skip any whitespace after "{REF,"
            while (cellIdStart < value.length() && value.charAt(cellIdStart) == ' ') {
                cellIdStart++;
            }

            // Find the end of the cell ID (it's before the next comma or closing brace)
            int cellIdEnd = cellIdStart;
            while (cellIdEnd < value.length() && value.charAt(cellIdEnd) != ',' && value.charAt(cellIdEnd) != '}') {
                cellIdEnd++;
            }

            // Extract and add the cell ID if it's valid
            if (cellIdEnd > cellIdStart) {
                String cellId = value.substring(cellIdStart, cellIdEnd).trim();
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



}



