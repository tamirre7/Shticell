package expressions.expressionsimpl;

import expressions.api.Expression;
import spreadsheet.api.ReadOnlySpreadSheet;
import spreadsheet.cell.api.CellType;
import spreadsheet.cell.api.EffectiveValue;
import spreadsheet.cell.impl.EffectiveValueImpl;

public class Pow extends BinaryExpression {

    public Pow(Expression argument1, Expression argument2) {
        super(argument1, argument2);
    }

    @Override
    protected EffectiveValue evaluate(EffectiveValue arg1, EffectiveValue arg2) {
        if (arg1 == null || arg2 == null) {
            return new EffectiveValueImpl(CellType.NUMERIC, Double.NaN);
        }
        Double value1 = arg1.extractValueWithExpectation(Double.class);
        Double value2 = arg2.extractValueWithExpectation(Double.class);

        double res = Math.pow(value1, value2);
        return new EffectiveValueImpl(CellType.NUMERIC, res);
    }

    @Override
    public CellType getFunctionResultType(ReadOnlySpreadSheet spreadSheet) {
        return CellType.NUMERIC;
    }
}
