package expressions.expressionsimpl;
import expressions.Expression;

public class Abs extends UnaryExpression {

    public Abs(Expression argument) {
        super(argument);
    }

    @Override
    protected Object evaluate(Object arg) {
        return Math.abs(((Number) arg).doubleValue());
    }
}
