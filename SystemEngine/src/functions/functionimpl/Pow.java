
package functions.functionimpl;

import functions.Function;

public class Pow extends BinaryFunction {

    public Pow(Function function1, Function function2) {
        super(function1, function2);
    }

    @Override
    protected Object evaluate(Object arg1, Object arg2) {
        // Ensure that both arguments are cast to Double
        if (arg1 instanceof Number && arg2 instanceof Number) {
            double base = ((Number) arg1).doubleValue();
            double exponent = ((Number) arg2).doubleValue();
            return Math.pow(base, exponent);
        } else {
            throw new IllegalArgumentException("Arguments to Pow must be numeric");
        }
    }
}
