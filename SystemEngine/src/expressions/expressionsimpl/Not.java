package expressions.expressionsimpl;

import expressions.api.Expression;
import spreadsheet.api.ReadOnlySpreadSheet;
import spreadsheet.cell.api.CellType;
import spreadsheet.cell.api.EffectiveValue;
import spreadsheet.cell.impl.EffectiveValueImpl;

public class Not extends UnaryExpression {

    public Not(Expression argument) {
        super(argument);
    }

    @Override
    protected EffectiveValue evaluate(EffectiveValue arg) {
        if (arg == null) {
            return new EffectiveValueImpl(CellType.INVALID_VALUE, "UNKNOWN");
        }
        Boolean value = arg.extractValueWithExpectation(Boolean.class);
        if (value == null) {
            return new EffectiveValueImpl(CellType.INVALID_VALUE, "UNKNOWN");
        }
        return new EffectiveValueImpl(CellType.BOOLEAN,!value);
    }
}
