package functions.functionimpl;
import functions.Function;

public class Divide extends BinaryFunction {
    public Divide (Function argument1, Function argument2) {
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
