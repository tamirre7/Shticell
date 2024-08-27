package expressions.expressionsimpl;

import expressions.api.Expression;
import spreadsheet.api.ReadOnlySpreadSheet;
import spreadsheet.cell.api.CellType;
import spreadsheet.cell.api.EffectiveValue;
import spreadsheet.cell.impl.EffectiveValueImpl;

public class Abs extends UnaryExpression {

    public Abs(Expression argument) {
        super(argument);
    }

    @Override
    protected EffectiveValue evaluate(EffectiveValue arg) {
        if (arg == null || arg.getCellType() == CellType.INVALID_VALUE) {
            return new EffectiveValueImpl(CellType.NUMERIC, Double.NaN);
        }
        Double value = Math.abs(arg.extractValueWithExpectation(Double.class));
        if (value == null) {
            return new EffectiveValueImpl(CellType.NUMERIC, Double.NaN);
        }
        return new EffectiveValueImpl(CellType.NUMERIC, value);
    }

    @Override
    public CellType getFunctionResultType(ReadOnlySpreadSheet spreadSheet) {
        return CellType.NUMERIC;
    }
}
