package expressions.expressionsimpl;

import expressions.Expression;
import spreadsheet.api.ReadOnlySpreadSheet;
import spreadsheet.cell.api.EffectiveValue;

public abstract class BinaryExpression implements Expression {

    private final Expression argument1;
    private final Expression argument2;

    public BinaryExpression(Expression argument1, Expression argument2) {
        this.argument1 = argument1;
        this.argument2 = argument2;
    }

    @Override
    public EffectiveValue evaluate(ReadOnlySpreadSheet spreadSheet) {
        EffectiveValue value1 = argument1.evaluate(spreadSheet);
        EffectiveValue value2 = argument2.evaluate(spreadSheet);
        return evaluate(value1, value2);
    }

    protected abstract EffectiveValue evaluate(EffectiveValue arg1, EffectiveValue arg2);
}
