package functions.functionimpl;

import functions.Function;

public class Sub extends TernaryFunction {

    public Sub(Function source, Function startIndex, Function endIndex) {
        super(source, startIndex, endIndex);
    }

    @Override
    protected Object evaluate(Object source, Object startIndex, Object endIndex) {
        if (source instanceof String && startIndex instanceof Number && endIndex instanceof Number) {
            String str = (String) source;
            int start = ((Number) startIndex).intValue();
            int end = ((Number) endIndex).intValue();

            if (start < 0 || end >= str.length() || start > end) {
                return "UNDEFINED";
            }
            return str.substring(start, end + 1);
        }
        throw new IllegalArgumentException("Arguments must be a string and two integers.");
    }
}
