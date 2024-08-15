package expressions.expressionsimpl;

import expressions.Expression;

public class Concat extends BinaryExpression {

    public Concat(Expression str1, Expression str2) {
        super(str1, str2);
    }

    @Override
    protected Object evaluate(Object arg1, Object arg2) {
        if (arg1 instanceof String && arg2 instanceof String) {
            return ((String) arg1) + ((String) arg2);
        }
        throw new IllegalArgumentException("Arguments must be strings.");
    }
}
