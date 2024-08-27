package expressions.expressionsimpl;

import expressions.api.Expression;
import spreadsheet.api.ReadOnlySpreadSheet;
import spreadsheet.cell.api.CellType;
import spreadsheet.cell.api.EffectiveValue;
import spreadsheet.cell.api.CellIdentifier;

public class Ref implements Expression {

    private final CellIdentifier cellIdentifier;

    public Ref(CellIdentifier cellIdentifier) {
        this.cellIdentifier = cellIdentifier;
    }

    @Override
    public EffectiveValue evaluate(ReadOnlySpreadSheet spreadSheet) {
        if (cellIdentifier.getRow() < 1 ||
                cellIdentifier.getRow() > spreadSheet.getSheetDimentions().getNumRows())
            throw new IllegalArgumentException("Invalid cell identifier - ROW out of range : Expected number between 1-" + spreadSheet.getSheetDimentions().getNumRows() + " but got " + cellIdentifier.getRow());

        if (cellIdentifier.getCol() < 'A' ||
                cellIdentifier.getCol() > spreadSheet.getSheetDimentions().getNumCols() + 'A')
            throw new IllegalArgumentException("Invalid cell identifier - COL out of range: Expected character between A - " + spreadSheet.getSheetDimentions().getNumCols() + 'A' + " but got " + cellIdentifier.getCol());

        return spreadSheet.getCellEffectiveValue(cellIdentifier);
    }


    @Override
    public CellType getFunctionResultType(ReadOnlySpreadSheet spreadSheet) {
        EffectiveValue cellEffectiveVal = spreadSheet.getCellEffectiveValue(cellIdentifier);
        if (cellEffectiveVal == null)
            return  CellType.INVALID_VALUE;
        return cellEffectiveVal.getCellType();
    }
}

