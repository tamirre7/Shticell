package expressions.expressionsimpl;

import expressions.api.Expression;
import spreadsheet.api.ReadOnlySpreadSheet;
import spreadsheet.cell.api.CellType;
import spreadsheet.cell.api.EffectiveValue;
import spreadsheet.cell.impl.EffectiveValueImpl;

public class Divide extends BinaryExpression {

    public Divide(Expression argument1, Expression argument2) {
        super(argument1, argument2);
    }

    @Override
    protected EffectiveValue evaluate(EffectiveValue arg1, EffectiveValue arg2) {
        if (arg1 == null || arg2 == null || arg1.getCellType() == CellType.INVALID_VALUE || arg2.getCellType() == CellType.INVALID_VALUE) {
            return new EffectiveValueImpl(CellType.NUMERIC, Double.NaN);
        }
        Double divisor = arg2.extractValueWithExpectation(Double.class);
        Double num = arg1.extractValueWithExpectation(Double.class);
        if (divisor == null || num == null || divisor == 0) {
            return new EffectiveValueImpl(CellType.NUMERIC, Double.NaN);
        }
        double result =  num / divisor;
        return new EffectiveValueImpl(CellType.NUMERIC, result);
    }

    @Override
    public CellType getFunctionResultType(ReadOnlySpreadSheet spreadSheet) {
        return CellType.NUMERIC;
    }

}
