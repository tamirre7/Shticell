package expressions.expressionsimpl;
import expressions.Expression;

public class Divide extends BinaryExpression {
    public Divide (Expression argument1, Expression argument2) {
        super(argument1, argument2);
    }

    @Override
    protected Object evaluate(Object arg1, Object arg2) {
        if(arg2 instanceof Number)
        {
            double num2 = ((Number) arg2).doubleValue();
            if(num2 == 0)
                return Double.NaN;
        }
        return ((Number) arg1).doubleValue() / ((Number) arg2).doubleValue();
    }
}
