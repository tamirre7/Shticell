package expressions.expressionsimpl;

import expressions.Expression;
import spreadsheet.api.SpreadSheet;
import spreadsheet.cell.api.Cell;
import spreadsheet.cell.api.CellType;
import spreadsheet.cell.api.EffectiveValue;
import spreadsheet.cell.impl.CellIdentifierimpl;
import spreadsheet.cell.impl.EffectiveValueimpl;

public class Ref implements Expression {

    private final CellIdentifierimpl cellIdentifier;

    public Ref(CellIdentifierimpl cellIdentifier) {
        this.cellIdentifier = cellIdentifier;
    }

    @Override
    public EffectiveValue evaluate(SpreadSheet spreadSheet) {
        Cell cell = spreadSheet.getCell(cellIdentifier);
        if (cell != null) {
            return cell.getEffectiveValue();
        }
        return new EffectiveValueimpl(CellType.BLANK, null);
    }
}
