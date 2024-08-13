package spreadsheet;

import cell.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;


public class SpreadSheet {
    private final Dimentions sheetDimentions;
    private String name;
    private int version;
    private Map<CellIdentifier, Cell> cells;


    public SpreadSheet(String name, int version, Dimentions sheetDimentions) {
        this.name = name;
        this.version = version;
        this.cells = new HashMap<>();
        this.sheetDimentions = sheetDimentions;
    }

    // Getters and Setters

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getVersion() {
        return version;
    }

    public Dimentions getSheetDimentions() {
        return sheetDimentions;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public Map<CellIdentifier, Cell> getCells() {
        return cells;
    }

    public void addOrUpdateCell(Cell cell) {
        cells.put(cell.getIdentifier(), cell);
    }

    public Cell getCell(CellIdentifier identifier) {
        return cells.get(identifier);
    }

    public void removeCell(CellIdentifier identifier) {
        cells.remove(identifier);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SpreadSheet sheet = (SpreadSheet) o;
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
                CellIdentifier cellId = new CellIdentifier(row, (char) ('A' + col));
                Cell cell = cells.get(cellId);
                if (cell != null) {
                    sb.append(cell.getEffectiveValue()).append(" ".repeat(widthCol - cell.getEffectiveValue().length())).append("|");
                } else {
                    sb.append(" ".repeat(widthCol)).append("|");
                }
            }
            sb.append("\n");
        }

        return sb.toString();
    }

}



