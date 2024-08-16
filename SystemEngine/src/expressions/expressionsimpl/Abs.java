package expressions.expressionsimpl;

import expressions.Expression;
import spreadsheet.cell.api.CellType;
import spreadsheet.cell.api.EffectiveValue;
import spreadsheet.cell.impl.EffectiveValueimpl;

public class Abs extends UnaryExpression {

    public Abs(Expression argument) {
        super(argument);
    }

    @Override
    protected EffectiveValue evaluate(EffectiveValue arg) {
        double value = Math.abs(arg.extractValueWithExpectation(Double.class));
        return new EffectiveValueimpl(CellType.NUMERIC, value);
    }
}
