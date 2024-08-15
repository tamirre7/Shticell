package expressions.expressionsimpl;

import expressions.Expression;

public abstract class UnaryExpression implements Expression {

    private Expression argument;

    public UnaryExpression(Expression argument) {
        this.argument = argument;
    }

    @Override
    public Object evaluate() {
        return evaluate(argument.evaluate());
    }

    protected abstract Object evaluate(Object arg);
}
