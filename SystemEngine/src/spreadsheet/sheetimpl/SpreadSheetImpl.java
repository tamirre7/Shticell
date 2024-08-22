package spreadsheet.sheetimpl;

import spreadsheet.api.Dimentions;
import spreadsheet.api.SpreadSheet;
import spreadsheet.cell.api.Cell;
import spreadsheet.cell.api.CellIdentifier;
import spreadsheet.cell.api.EffectiveValue;
import spreadsheet.cell.impl.CellIdentifierImpl;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class SpreadSheetImpl implements SpreadSheet {

    private final Dimentions sheetDimentions;
    private String name;
    private int version;
    private Map<CellIdentifier, Cell> cells;

    public SpreadSheetImpl(String name, int version, Dimentions sheetDimentions) {
        this.name = name;
        this.version = version;
        this.cells = new HashMap<>();
        this.sheetDimentions = sheetDimentions;
    }

    @Override
    public boolean isValidCellID(CellIdentifier cellID) {
        if (cellID.getRow() < 1 ||
                cellID.getRow() > this.sheetDimentions.getNumRows())
            throw new IllegalArgumentException("Invalid cell identifier - ROW out of range : Expected number between 1-" + this.sheetDimentions.getNumRows() + " but got " + cellID.getRow());

        if (cellID.getCol() < 'A' ||
                cellID.getCol() > this.sheetDimentions.getNumCols() + 'A')
            throw new IllegalArgumentException("Invalid cell identifier - COL out of range: Expected character between A - " + this.sheetDimentions.getNumCols() + 'A' + " but got " + cellID.getCol());

        return true;
    }

    @Override
    public EffectiveValue getCellEffectiveValue(CellIdentifier identifier) {
        Cell cell = cells.get(identifier);
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
    public Dimentions getSheetDimentions() {
        return sheetDimentions;
    }
    @Override
    public void setVersion(int version) {
        this.version = version;
    }
    @Override
    public Map<CellIdentifier, Cell> getCells() {
        return cells;
    }
    @Override
    public void addOrUpdateCell(Cell cell) {
        cells.put(cell.getIdentifier(), cell);
    }
    @Override
    public Cell getCell(CellIdentifier identifier) {
        return cells.get(identifier);
    }
    @Override
    public void removeCell(CellIdentifier identifier) {
        cells.remove(identifier);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SpreadSheetImpl sheet = (SpreadSheetImpl) o;
        return version == sheet.version &&
                Objects.equals(name, sheet.name) &&
                Objects.equals(cells, sheet.cells);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, version, cells);
    }

    @Override
    public String toString() {
        int numCols = sheetDimentions.getNumCols();
        int numRows = sheetDimentions.getNumRows();
        int widthCol = sheetDimentions.getWidthCol();

        StringBuilder sb = new StringBuilder();

        // Print the current version and sheet name
        sb.append("Version: ").append(version).append("\n");
        sb.append("Sheet Name: ").append(name).append("\n");

        // Print the column headers
        sb.append("   "); // Leading space for row numbers
        for (int col = 0; col < numCols; col++) {
            char columnHeader = (char) ('A' + col); // Convert column index to letter
            sb.append(columnHeader).append(" ".repeat(widthCol)).append("|");
        }
        sb.append("\n");

        // Print each row with row numbers and cell values
        for (int row = 1; row <= numRows; row++) {
            sb.append(String.format("%02d", row)).append(" "); // Row number

            for (int col = 0; col < numCols; col++) {
                CellIdentifier cellId = new CellIdentifierImpl(row, (char) ('A' + col));
                Cell cell = cells.get(cellId);
                if (cell != null) {
                    String effectiveValueStr = cell.getEffectiveValue().getValue().toString();
                    sb.append(effectiveValueStr)
                            .append(" ".repeat(widthCol - effectiveValueStr.length()))
                            .append("|");
                } else {
                    sb.append(" ".repeat(widthCol)).append("|");
                }
            }
            sb.append("\n");
        }

        return sb.toString();
    }

}



