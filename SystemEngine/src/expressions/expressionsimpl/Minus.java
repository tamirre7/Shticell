package expressions.expressionsimpl;

import expressions.api.Expression;
import spreadsheet.api.ReadOnlySpreadSheet;
import spreadsheet.cell.api.CellType;
import spreadsheet.cell.api.EffectiveValue;
import spreadsheet.cell.impl.EffectiveValueImpl;

public class Minus extends BinaryExpression {

    public Minus(Expression argument1, Expression argument2) {
        super(argument1, argument2);
    }

    @Override
    protected EffectiveValue evaluate(EffectiveValue arg1, EffectiveValue arg2) {
        double value = arg1.extractValueWithExpectation(Double.class) - arg2.extractValueWithExpectation(Double.class);
        return new EffectiveValueImpl(CellType.NUMERIC_DOUBLE, value);
    }

    @Override
    public CellType getFunctionResultType(ReadOnlySpreadSheet spreadSheet) {
        return CellType.NUMERIC_DOUBLE;
    }
}
