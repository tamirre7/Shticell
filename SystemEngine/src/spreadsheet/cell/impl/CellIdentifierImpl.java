package spreadsheet.cell.impl;

import spreadsheet.cell.api.CellIdentifier;

public class CellIdentifierImpl implements CellIdentifier
{
    private final int row;
    private final char col;


    public CellIdentifierImpl(int row, char col)
    {
        this.row = row;
        this.col = col;
    }

    @Override
    public int getRow(){ return row; }

    @Override
    public char getCol(){ return col; }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        CellIdentifierImpl that = (CellIdentifierImpl) obj;
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
