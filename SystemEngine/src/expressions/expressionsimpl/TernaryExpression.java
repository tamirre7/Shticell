package expressions.expressionsimpl;

import expressions.Expression;

public abstract class TernaryExpression implements Expression {

    private Expression argument1;
    private Expression argument2;
    private Expression argument3;

    public TernaryExpression(Expression argument1, Expression argument2, Expression argument3) {
        this.argument1 = argument1;
        this.argument2 = argument2;
        this.argument3 = argument3;
    }

    @Override
    public Object evaluate() {
        return evaluate(argument1.evaluate(), argument2.evaluate(), argument3.evaluate());
    }

    protected abstract Object evaluate(Object arg1, Object arg2, Object arg3);
}
