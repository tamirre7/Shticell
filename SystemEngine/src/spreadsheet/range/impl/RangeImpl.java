package spreadsheet.range.impl;

import spreadsheet.cell.api.Cell;

public class RangeImpl {
    private String name;
    private Cell topLeft;
    private Cell bottomRight;

    public RangeImpl (String name, Cell topLeft, Cell bottomRight) {
        this.name = name;
        this.topLeft = topLeft;
        this.bottomRight = bottomRight;
    }

    public String getName() {
        return name;
    }

    public Cell getTopLeft() {
        return topLeft;
    }

    public Cell getBottomRight() {
        return bottomRight;
    }

    public boolean isCellContains(Cell cell) {
        int row = cell.getIdentifier().getRow();
        char col = cell.getIdentifier().getCol();

        return row >= topLeft.getIdentifier().getRow() && row <= bottomRight.getIdentifier().getRow()
                && col >= topLeft.getIdentifier().getCol() && col <= bottomRight.getIdentifier().getCol();
    }
}
