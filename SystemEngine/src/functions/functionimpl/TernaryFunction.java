package functions.functionimpl;

import functions.Function;

public abstract class TernaryFunction implements Function {

    private Function argument1;
    private Function argument2;
    private Function argument3;

    public TernaryFunction(Function argument1, Function argument2, Function argument3) {
        this.argument1 = argument1;
        this.argument2 = argument2;
        this.argument3 = argument3;
    }

    @Override
    public Object evaluate() {
        return evaluate(argument1.evaluate(), argument2.evaluate(), argument3.evaluate());
    }

    protected abstract Object evaluate(Object arg1, Object arg2, Object arg3);
}
