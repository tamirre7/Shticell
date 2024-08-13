package spreadsheet;
import java.util.Objects;

public class Dimentions {
    private final int numRows;
    private final int numCols;
    private final int widthCol;
    private final int heightRow;

    public Dimentions(int numRows, int numCols, int widthCol, int heightRow) {
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Dimentions that = (Dimentions) o;
        return numRows == that.numRows &&
                numCols == that.numCols &&
                widthCol == that.widthCol &&
                heightRow == that.heightRow;
    }

    @Override
    public int hashCode() {
        return Objects.hash(numRows, numCols, widthCol, heightRow);
    }

    @Override
    public String toString() {
        return "Dimentions{" +
                "numRows=" + numRows +
                ", numCols=" + numCols +
                ", widthCol=" + widthCol +
                ", heightRow=" + heightRow +
                '}';
    }


}
