package spreadsheet.cell.impl;

import spreadsheet.cell.api.CellIdentifier;
import java.io.Serializable;

public class CellIdentifierImpl implements CellIdentifier, Serializable
{
    private final int row;
    private final char col;


    public CellIdentifierImpl(int row, char col)
    {
        this.row = row;
        this.col = col;
    }

    // Method to create a CellIdentifierImpl from a string
    public  CellIdentifierImpl(String cellId ) {
        if (cellId == null || cellId.isEmpty()) {
            throw new IllegalArgumentException("Cell ID cannot be null or empty");
        }

        // Extract the column (assuming single letter) and row
        int row;
        char col;
        try {
            // Parse the cell-id to extract the row and column
            col = cellId.charAt(0); // extract the column letter
            row = Integer.parseInt(cellId.substring(1, cellId.length())); // extract the row number
        } catch (NumberFormatException | StringIndexOutOfBoundsException e) {
            // Handle exceptions related to invalid row number or column letter
            throw new IllegalArgumentException("Invalid cell identifier: " + cellId, e);
        }


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
