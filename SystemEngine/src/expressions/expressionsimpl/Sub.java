package expressions.expressionsimpl;

import expressions.Expression;
import spreadsheet.api.SpreadSheet;
import spreadsheet.cell.api.CellType;
import spreadsheet.cell.api.EffectiveValue;
import spreadsheet.cell.impl.EffectiveValueimpl;

public class Sub extends TernaryExpression {

    public Sub(Expression argument1, Expression argument2, Expression argument3) {
        super(argument1, argument2, argument3);
    }

    @Override
    protected EffectiveValue evaluate(EffectiveValue arg1, EffectiveValue arg2, EffectiveValue arg3) {
        double value = arg1.extractValueWithExpectation(Double.class)
                - arg2.extractValueWithExpectation(Double.class)
                + arg3.extractValueWithExpectation(Double.class);
        return new EffectiveValueimpl(CellType.NUMERIC, value);
    }
}
