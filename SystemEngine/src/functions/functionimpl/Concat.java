package functions.functionimpl;

import functions.Function;

public class Concat extends BinaryFunction {

    public Concat(Function str1, Function str2) {
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
