package functions.functionimpl;

import functions.Function;

public class Times extends BinaryFunction {
    public Times (Function argument1, Function argument2) {
        super(argument1, argument2);
    }

    @Override
    protected Object evaluate(Object arg1, Object arg2) {
        return ((Number) arg1).doubleValue() * ((Number) arg2).doubleValue();
    }
}
