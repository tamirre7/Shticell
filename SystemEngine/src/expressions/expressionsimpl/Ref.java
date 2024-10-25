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
            throw new IllegalArgumentException("\nInvalid cell identifier for REF function-\n" +
                    "ROW: Expected number between 1-" + spreadSheet.getSheetDimentions().getNumRows() +
                    "\n" + "COL: Expected character between A-" + (char) ('A' + spreadSheet.getSheetDimentions().getNumCols() - 1));

        EffectiveValue cellEffectiveVal = spreadSheet.getCellEffectiveValue(cellIdentifier);
        if (cellEffectiveVal == null)
            return new EffectiveValueImpl(CellType.NOT_INIT, "");
        if (cellEffectiveVal.getCellType() == CellType.NOT_INIT)
            return new EffectiveValueImpl(CellType.NOT_INIT, "");

        return spreadSheet.getCellEffectiveValue(cellIdentifier);
    }

}

