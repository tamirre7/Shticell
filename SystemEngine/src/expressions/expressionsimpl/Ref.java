package expressions.expressionsimpl;

import expressions.api.Expression;
import spreadsheet.api.ReadOnlySpreadSheet;
import spreadsheet.cell.api.CellType;
import spreadsheet.cell.api.EffectiveValue;
import spreadsheet.cell.api.CellIdentifier;
import spreadsheet.cell.impl.CellIdentifierImpl;
import spreadsheet.cell.impl.EffectiveValueImpl;

public class Ref implements Expression {

    private final CellIdentifierImpl cellIdentifier;

    public Ref(CellIdentifierImpl cellIdentifier) {
        this.cellIdentifier = cellIdentifier;
    }

    @Override
    public EffectiveValue evaluate(ReadOnlySpreadSheet spreadSheet) {
        if (cellIdentifier.getRow() < 1
                || cellIdentifier.getRow() > spreadSheet.getSheetDimentions().getNumRows()
        || cellIdentifier.getCol() < 'A'
        || cellIdentifier.getCol() > spreadSheet.getSheetDimentions().getNumCols() + 'A')
            throw new IllegalArgumentException("Invalid cell identifier for REF - ROW: Expected number between 1-" + spreadSheet.getSheetDimentions().getNumRows()+
                    " but got " + cellIdentifier.getRow() + "\n" + "COL: Expected character between A -" + spreadSheet.getSheetDimentions().getNumCols() + 'A'
            + " but got " + cellIdentifier.getCol());

        EffectiveValue cellEffectiveVal = spreadSheet.getCellEffectiveValue(cellIdentifier);
        if (cellEffectiveVal == null)
            return new EffectiveValueImpl(CellType.INVALID_VALUE, "!UNDEFINED!");
        if (cellEffectiveVal.getCellType() == CellType.NOT_INIT)
            return new EffectiveValueImpl(CellType.INVALID_VALUE, "!UNDEFINED!");
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

