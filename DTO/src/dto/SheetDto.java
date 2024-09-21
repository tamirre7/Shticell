package dto;



import java.util.Map;
import java.util.Collections;


public class SheetDto {
    private final int numRows;
    private final int numCols;
    private final int widthCol;
    private final int heightRow;
    private final String name;
    private final int version;
    private final Map<String, CellDto> cells;

    private final Map<String,RangeDto> sheetRanges;

    public SheetDto(int numCols, int numRows, int widthCol, int heightRow, String name, int version, Map<String, CellDto> cells, Map<String, RangeDto> sheetRanges) {
        this.name = name;
        this.version = version;
        this.numRows = numRows;
        this.numCols = numCols;
        this.widthCol = widthCol;
        this.heightRow = heightRow;
        this.sheetRanges = sheetRanges;
        this.cells = cells;
    }

    // Getters
    public String getName() {
        return name;
    }


    public Integer getVersion() {
        return version;
    }

    public int getHeightRow() { return heightRow; }
    public int getWidthCol() { return widthCol; }
    public int getNumCols() { return numCols; }
    public int getNumRows() { return numRows; }

    // Return an unmodifiable view of the map to prevent external modification
    public Map<String, CellDto> getCells() {
        return Collections.unmodifiableMap(cells);
    }

    public Map<String, RangeDto> getSheetRanges() {return Collections.unmodifiableMap(sheetRanges);}
}


