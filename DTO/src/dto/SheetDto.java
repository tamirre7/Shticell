package dto;

public class SheetDto {
    private final String name;
    private final int version;
    private final int numRows;
    private final int numCols;
    private final int widthCol;
    private final Map<CellIdentifierDto, CellDto> cells; // Map of cell identifiers to their DTOs

    public SheetDto(String name, int version, int numRows, int numCols, int widthCol, Map<CellIdentifierDto, CellDto> cells) {
        this.name = name;
        this.version = version;
        this.numRows = numRows;
        this.numCols = numCols;
        this.widthCol = widthCol;
        this.cells = cells;
    }

    // Getters
    public String getName() {
        return name;
    }

    public int getVersion() {
        return version;
    }

    public int getNumRows() {
        return numRows;
    }

    public int getNumCols() {
        return numCols;
    }

    public int getWidthCol() {
        return widthCol;
    }

    public Map<CellIdentifierDto, CellDto> getCells() {
        return cells;
    }
}
