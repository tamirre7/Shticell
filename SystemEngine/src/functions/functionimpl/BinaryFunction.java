package functions.functionimpl;

import functions.Function;

public abstract class BinaryFunction implements Function {

    private Function argument1;
    private Function argument2;

    public BinaryFunction(Function argument1, Function argument2) {
        this.argument1 = argument1;
        this.argument2 = argument2;
    }

    @Override
    public Object evaluate() {
        return evaluate(argument1.evaluate(), argument2.evaluate());
    }

    protected abstract Object evaluate(Object arg1, Object arg2);
}
