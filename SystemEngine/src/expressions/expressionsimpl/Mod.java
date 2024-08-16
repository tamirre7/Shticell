package expressions.expressionsimpl;

import expressions.Expression;
import spreadsheet.cell.api.CellType;
import spreadsheet.cell.api.EffectiveValue;
import spreadsheet.cell.impl.EffectiveValueimpl;

public class Mod extends BinaryExpression {

    public Mod(Expression argument1, Expression argument2) {
        super(argument1, argument2);
    }

    @Override
    protected EffectiveValue evaluate(EffectiveValue arg1, EffectiveValue arg2) {
        double value = arg1.extractValueWithExpectation(Double.class) % arg2.extractValueWithExpectation(Double.class);
        return new EffectiveValueimpl(CellType.NUMERIC, value);
    }
}
