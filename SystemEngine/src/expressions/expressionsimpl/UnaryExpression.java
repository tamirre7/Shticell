package expressions.expressionsimpl;

import expressions.Expression;
import spreadsheet.api.SpreadSheet;
import spreadsheet.cell.api.EffectiveValue;

public abstract class UnaryExpression implements Expression {

    private final Expression argument;

    public UnaryExpression(Expression argument) {
        this.argument = argument;
    }

    @Override
    public EffectiveValue evaluate(SpreadSheet spreadSheet) {
        return evaluate(argument.evaluate(spreadSheet));
    }

    protected abstract EffectiveValue evaluate(EffectiveValue arg);
}
