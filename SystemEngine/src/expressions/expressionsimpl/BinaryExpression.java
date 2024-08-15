package expressions.expressionsimpl;

import expressions.Expression;

public abstract class BinaryExpression implements Expression {

    private Expression argument1;
    private Expression argument2;

    public BinaryExpression(Expression argument1, Expression argument2) {
        this.argument1 = argument1;
        this.argument2 = argument2;
    }

    @Override
    public Object evaluate() {
        return evaluate(argument1.evaluate(), argument2.evaluate());
    }

    protected abstract Object evaluate(Object arg1, Object arg2);
}
