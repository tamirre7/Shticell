package dto;

public class DimensionDto {
    private final int numRows;
    private final int numCols;
    private final int widthCol;
    private final int heightRow;

    public DimensionDto(int numRows, int numCols, int widthCol, int heightRow) {
        this.numRows = numRows;
        this.numCols = numCols;
        this.widthCol = widthCol;
        this.heightRow = heightRow;
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

    public int getHeightRow() {
        return heightRow;
    }
}
