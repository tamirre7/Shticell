package expressions.expressionsimpl;

import expressions.Expression;
import spreadsheet.cell.api.CellType;
import spreadsheet.cell.api.EffectiveValue;
import spreadsheet.cell.impl.EffectiveValueimpl;

public class Concat extends BinaryExpression {

    public Concat(Expression argument1, Expression argument2) {
        super(argument1, argument2);
    }

    @Override
    protected EffectiveValue evaluate(EffectiveValue arg1, EffectiveValue arg2) {
        String value = arg1.extractValueWithExpectation(String.class) + arg2.extractValueWithExpectation(String.class);
        return new EffectiveValueimpl(CellType.STRING, value);
    }
}
