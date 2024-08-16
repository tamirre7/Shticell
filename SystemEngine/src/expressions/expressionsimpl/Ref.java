package expressions.expressionsimpl;

import expressions.Expression;
import spreadsheet.api.ReadOnlySpreadSheet;
import spreadsheet.cell.api.EffectiveValue;
import spreadsheet.cell.api.CellIdentifier;

public class Ref implements Expression {

    private final CellIdentifier cellIdentifier;

    public Ref(CellIdentifier cellIdentifier) {
        this.cellIdentifier = cellIdentifier;
    }

    @Override
    public EffectiveValue evaluate(ReadOnlySpreadSheet spreadSheet) {
        return spreadSheet.getCellEffectiveValue(cellIdentifier);
    }
}
