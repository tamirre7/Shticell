package expressions.expressionsimpl;

import expressions.Expression;
import spreadsheet.cell.api.EffectiveValue;
import spreadsheet.api.ReadOnlySpreadSheet;

public abstract class TernaryExpression implements Expression {

    private final Expression argument1;
    private final Expression argument2;
    private final Expression argument3;

    public TernaryExpression(Expression argument1, Expression argument2, Expression argument3) {
        this.argument1 = argument1;
        this.argument2 = argument2;
        this.argument3 = argument3;
    }

    @Override
    public EffectiveValue evaluate(ReadOnlySpreadSheet spreadSheet) {
        return evaluate(
                argument1.evaluate(spreadSheet),
                argument2.evaluate(spreadSheet),
                argument3.evaluate(spreadSheet)
        );
    }

    protected abstract EffectiveValue evaluate(EffectiveValue arg1, EffectiveValue arg2, EffectiveValue arg3);
}
