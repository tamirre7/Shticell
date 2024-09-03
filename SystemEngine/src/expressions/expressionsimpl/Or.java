package expressions.expressionsimpl;

import expressions.api.Expression;
import spreadsheet.cell.api.CellType;
import spreadsheet.cell.api.EffectiveValue;
import spreadsheet.cell.impl.EffectiveValueImpl;

public class Or extends BinaryExpression {
    public Or(Expression argument1, Expression argument2) {
        super(argument1, argument2);
    }

    @Override
    protected EffectiveValue evaluate(EffectiveValue arg1, EffectiveValue arg2) {
        if (arg1 == null || arg2 == null) {
            return new EffectiveValueImpl(CellType.INVALID_VALUE, "UNKNOWN");
        }
        Boolean value1 = arg1.extractValueWithExpectation(Boolean.class);
        Boolean value2 = arg2.extractValueWithExpectation(Boolean.class);
        if (value1 == null || value2 == null) {
            return new EffectiveValueImpl(CellType.INVALID_VALUE, "UNKNOWN");
        }
        Boolean res = value1 || value2;
        return new EffectiveValueImpl(CellType.BOOLEAN, res);
    }
}
