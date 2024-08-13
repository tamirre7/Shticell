package functions.functionimpl;

import functions.Function;

public abstract class UnaryFunction implements Function {

    private Function argument;

    public UnaryFunction(Function argument) {
        this.argument = argument;
    }

    @Override
    public Object evaluate() {
        return evaluate(argument.evaluate());
    }

    protected abstract Object evaluate(Object arg);
}
