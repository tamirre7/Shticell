package expressions.expressionsimpl;
import expressions.Expression;

public class Plus extends BinaryExpression {

    public Plus (Expression argument1, Expression argument2) {
        super(argument1, argument2);
    }

    @Override
    protected Object evaluate(Object arg1, Object arg2) {
        return ((Number) arg1).doubleValue() + ((Number) arg2).doubleValue();
    }
}
