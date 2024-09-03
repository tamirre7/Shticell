package expressions.expressionsimpl;

import expressions.api.Expression;
import spreadsheet.api.ReadOnlySpreadSheet;
import spreadsheet.cell.api.CellType;
import spreadsheet.cell.api.EffectiveValue;
import spreadsheet.cell.impl.EffectiveValueImpl;

public class IdentityExpression implements Expression {

    private final Object value;
    private final CellType type;

    public IdentityExpression(Object value, CellType type) {
        this.value = value;
        this.type = type;
    }

    @Override
    public EffectiveValue evaluate(ReadOnlySpreadSheet spreadSheet) {
        return new EffectiveValueImpl(type, value);
    }
}