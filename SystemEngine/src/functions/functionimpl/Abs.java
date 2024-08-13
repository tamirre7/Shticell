package functions.functionimpl;
import functions.Function;

public class Abs extends UnaryFunction {

    public Abs(Function argument) {
        super(argument);
    }

    @Override
    protected Object evaluate(Object arg) {
        return Math.abs(((Number) arg).doubleValue());
    }
}
