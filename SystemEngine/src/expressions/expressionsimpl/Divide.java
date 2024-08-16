package expressions.expressionsimpl;

import expressions.Expression;
import spreadsheet.cell.api.CellType;
import spreadsheet.cell.api.EffectiveValue;
import spreadsheet.cell.impl.EffectiveValueimpl;

public class Divide extends BinaryExpression {

    public Divide(Expression argument1, Expression argument2) {
        super(argument1, argument2);
    }

    @Override
    protected EffectiveValue evaluate(EffectiveValue arg1, EffectiveValue arg2) {
        double divisor = arg2.extractValueWithExpectation(Double.class);
        double result = divisor == 0 ? Double.NaN : arg1.extractValueWithExpectation(Double.class) / divisor;
        return new EffectiveValueimpl(CellType.NUMERIC, result);
    }

}
