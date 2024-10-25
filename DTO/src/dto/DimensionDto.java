package dto;

public class DimensionDto {
    private final int numRows;    // Number of rows in the sheet
    private final int numCols;    // Number of columns in the sheet
    private final int widthCol;    // Width of each column
    private final int heightRow;   // Height of each row

    // Constructor to initialize the dimensions of the sheet
    public DimensionDto(int numRows, int numCols, int widthCol, int heightRow) {
        this.numRows = numRows;
        this.numCols = numCols;
        this.widthCol = widthCol;
        this.heightRow = heightRow;
    }

    public int getNumRows() {
        return numRows; // Returns the number of rows
    }

    public int getNumCols() {
        return numCols; // Returns the number of columns
    }

    public int getWidthCol() {
        return widthCol; // Returns the width of each column
    }

    public int getHeightRow() {
        return heightRow; // Returns the height of each row
    }
}
