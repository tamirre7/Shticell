package expressions.expressionsimpl;

import expressions.api.Expression;
import spreadsheet.api.ReadOnlySpreadSheet;
import spreadsheet.cell.api.CellType;
import spreadsheet.cell.api.EffectiveValue;
import spreadsheet.cell.impl.EffectiveValueImpl;

public class Mod extends BinaryExpression {

    public Mod(Expression argument1, Expression argument2) {
        super(argument1, argument2);
    }

    @Override
    protected EffectiveValue evaluate(EffectiveValue arg1, EffectiveValue arg2) {
        if (arg1 == null || arg2 == null) {
            return new EffectiveValueImpl(CellType.NUMERIC, Double.NaN);
        }
        Double value1 = arg1.extractValueWithExpectation(Double.class);
        Double value2= arg2.extractValueWithExpectation(Double.class);
        if (value1 == null || value2 == null) {
            return new EffectiveValueImpl(CellType.NUMERIC, Double.NaN);
        }
        double res = value1 % value2;
        return new EffectiveValueImpl(CellType.NUMERIC, res);
    }
}
