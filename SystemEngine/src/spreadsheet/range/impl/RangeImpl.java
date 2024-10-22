package spreadsheet.range.impl;

import spreadsheet.api.Dimension;
import spreadsheet.cell.api.Cell;
import spreadsheet.cell.api.CellIdentifier;
import spreadsheet.cell.impl.CellIdentifierImpl;
import spreadsheet.range.api.Range;
import java.io.Serializable;
import java.util.*;

public class RangeImpl implements Range, Serializable {
    private String name;
    private CellIdentifier topLeft;
    private CellIdentifier bottomRight;
    private List<CellIdentifier> cellsInRange;
    private boolean isActive;
    private Dimension sheetDimensions;

    public RangeImpl (String name, CellIdentifier topLeft, CellIdentifier bottomRight,Dimension sheetDimentions) {
        this.name = name;
        this.topLeft = topLeft;
        this.bottomRight = bottomRight;
        this.isActive = false;
        this.sheetDimensions=sheetDimentions;
        this.cellsInRange = new ArrayList<>();
        this.InitRangeList();
    }

    public void InitRangeList()
    {
        // Convert columns of the top-left and bottom-right cells to numeric indexes
        int startColumnIndex = (topLeft.getCol()) - 'A' +1;
        int endColumnIndex = (bottomRight.getCol()) - 'A' +1;

        // Get rows from the cell identifiers
        int startRow = topLeft.getRow();
        int endRow = bottomRight.getRow();

        // Check if the top-left is after the bottom-right in terms of rows or columns
        if (startRow > endRow) {
            throw new IllegalArgumentException("The start row cannot be greater than the end row.");
        }
        if (startColumnIndex > endColumnIndex) {
            throw new IllegalArgumentException("The start column must be before or the same as the end column.");
        }

        // Check if the range is within the spreadsheet dimensions
        if (startRow < 1 || startRow > sheetDimensions.getNumRows() || endRow < 1 || endRow > sheetDimensions.getNumRows()) {
            throw new IllegalArgumentException("Row range is out of spreadsheet bounds.");
        }
        if (startColumnIndex < 0 || startColumnIndex >= sheetDimensions.getNumCols() || endColumnIndex < 0 || endColumnIndex >= sheetDimensions.getNumCols()) {
            throw new IllegalArgumentException("Column range is out of spreadsheet bounds.");
        }

        // Loop through the rows and columns within the range
        for (int row = startRow; row <= endRow; row++) {
            for (int col = startColumnIndex; col <= endColumnIndex; col++) {
                // Create a CellIdentifier and add it to the list
                char column = (char)((col - 1)+'A');
                CellIdentifierImpl cellIdentifier = new CellIdentifierImpl(row, column);
                this.cellsInRange.add(cellIdentifier);
            }
        }
    }
    @Override
    public String getName() {
        return name;
    }
    @Override
    public List<CellIdentifier> getCellsInRange() { return Collections.unmodifiableList(cellsInRange);}
    @Override
    public CellIdentifier getTopLeft() {
        return topLeft;
    }
    @Override
    public CellIdentifier getBottomRight() {
        return bottomRight;
    }

    public boolean isActive() {
        return isActive;
    }

    @Override
    public void setActive(boolean active) {
        isActive = active;
    }

    public boolean isCellContains(Cell cell) {
        int row = cell.getIdentifier().getRow();
        char col = cell.getIdentifier().getCol();

        return row >= topLeft.getRow() && row <= bottomRight.getRow()
                && col >= topLeft.getCol() && col <= bottomRight.getCol();
    }
}
