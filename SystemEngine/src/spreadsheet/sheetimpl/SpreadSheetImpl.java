package spreadsheet.sheetimpl;

import spreadsheet.api.Dimension;
import spreadsheet.api.SpreadSheet;
import spreadsheet.cell.api.Cell;
import spreadsheet.cell.api.CellIdentifier;
import spreadsheet.cell.api.EffectiveValue;
import spreadsheet.cell.impl.CellIdentifierImpl;
import spreadsheet.cell.impl.CellImpl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class SpreadSheetImpl implements SpreadSheet {

    private final Dimension sheetDimension;
    private String name;
    private int version;
    private Map<CellIdentifier, Cell> activeCells;

    public SpreadSheetImpl(String name, int version, Dimension sheetDimension) {
        this.name = name;
        this.version = version;
        this.activeCells = new HashMap<>();
        this.sheetDimension = sheetDimension;
    }

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
    public void addOrUpdateCell(Cell cell) {
        activeCells.put(cell.getIdentifier(), cell);
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
            // int newVersion = newSheetVersion.increaseVersion();
            // cellsThatHaveChanged.forEach(cell -> cell.updateVersion(newVersion));

            return newSheetVersion;
        } catch (Exception e) {
            // deal with the runtime error that was discovered as part of invocation
            return this;
        }
    }

    private List<Cell> orderCellsForCalculation() {
        // data structure 1 0 1: Topological sort...
        // build graph from the cells. each cell is a node. each cell that has ref(s) constitutes an edge
        // handle case of circular dependencies -> should fail
        return null;
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
        if (this == o) return true;
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



