package spreadsheet.range.impl;
import spreadsheet.api.Dimension;
import spreadsheet.cell.api.CellIdentifier;
import spreadsheet.cell.impl.CellIdentifierImpl;
import spreadsheet.range.api.Range;
import java.io.Serializable;
import java.util.*;

// Class RangeImpl implements Range interface and Serializable for object serialization
// Represents a rectangular range of cells in a spreadsheet
public class RangeImpl implements Range, Serializable {
    private String name;
    private CellIdentifier topLeft;
    private CellIdentifier bottomRight;
    private List cellsInRange;
    private boolean isActive;
    private Dimension sheetDimensions;


    public RangeImpl(String name, CellIdentifier topLeft, CellIdentifier bottomRight, Dimension sheetDimentions) {
        this.name = name;
        this.topLeft = topLeft;
        this.bottomRight = bottomRight;
        this.isActive = false;
        this.sheetDimensions = sheetDimentions;
        this.cellsInRange = new ArrayList<>();
        this.InitRangeList();
    }

    // Initializes the list of cells within the range
    // Performs validation and populates cellsInRange with all cells in the range
    @Override
    public void InitRangeList() {
        // Convert column letters to numeric indices (A=1, B=2, etc.)
        int startColumnIndex = (topLeft.getCol()) - 'A' + 1;
        int endColumnIndex = (bottomRight.getCol()) - 'A' + 1;

        // Get the row numbers for range boundaries
        int startRow = topLeft.getRow();
        int endRow = bottomRight.getRow();

        // Validate that range boundaries are logical
        if (startRow > endRow) {
            throw new IllegalArgumentException("The start row cannot be greater than the end row.");
        }
        if (startColumnIndex > endColumnIndex) {
            throw new IllegalArgumentException("The start column must be before or the same as the end column.");
        }

        // Validate that range fits within spreadsheet dimensions
        if (startRow < 1 || startRow > sheetDimensions.getNumRows() ||
                endRow < 1 || endRow > sheetDimensions.getNumRows()) {
            throw new IllegalArgumentException("Row range is out of spreadsheet bounds.");
        }
        if (startColumnIndex < 0 || startColumnIndex >= sheetDimensions.getNumCols() ||
                endColumnIndex < 0 || endColumnIndex >= sheetDimensions.getNumCols()) {
            throw new IllegalArgumentException("Column range is out of spreadsheet bounds.");
        }

        // Populate the cellsInRange list with all cells in the range
        for (int row = startRow; row <= endRow; row++) {
            for (int col = startColumnIndex; col <= endColumnIndex; col++) {
                // Convert numeric column back to letter (1=A, 2=B, etc.)
                char column = (char)((col - 1) + 'A');
                CellIdentifierImpl cellIdentifier = new CellIdentifierImpl(row, column);
                this.cellsInRange.add(cellIdentifier);
            }
        }
    }

    // Returns the range name
    @Override
    public String getName() {
        return name;
    }

    // Returns an unmodifiable view of cells in the range
    @Override
    public List getCellsInRange() {
        return Collections.unmodifiableList(cellsInRange);
    }

    // Returns the top-left cell identifier
    @Override
    public CellIdentifier getTopLeft() {
        return topLeft;
    }

    // Returns the bottom-right cell identifier
    @Override
    public CellIdentifier getBottomRight() {
        return bottomRight;
    }

    // Returns whether the range is currently active
    @Override
    public boolean isActive() {
        return isActive;
    }

    // Sets the active state of the range
    @Override
    public void setActive(boolean active) {
        isActive = active;
    }
}