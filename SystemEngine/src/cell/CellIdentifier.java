package cell;

public class CellIdentifier
{
    private int row;
    private char col;


    public void CellIdentifier(int row, char col)
    {
        this.row = row;
        this.col = col;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        CellIdentifier that = (CellIdentifier) obj;
        return row == that.row && col == that.col;
    }

    @Override
    public int hashCode() {
        int result = Character.hashCode(col);
        result = 31 * result + Integer.hashCode(row);
        return result;
    }


    @Override
    public String toString() {
        return String.format("%c%d", col, row);
    }
}
