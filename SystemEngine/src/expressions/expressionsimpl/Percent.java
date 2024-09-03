package expressions.expressionsimpl;

import expressions.api.Expression;
import spreadsheet.cell.api.CellType;
import spreadsheet.cell.api.EffectiveValue;
import spreadsheet.cell.impl.EffectiveValueImpl;

public class Percent extends BinaryExpression{
    public Percent(Expression argument1, Expression argument2) {
        super(argument1, argument2);
    }

    @Override
    protected EffectiveValue evaluate(EffectiveValue arg1, EffectiveValue arg2) {
        if (arg1 == null || arg2 == null) {
            return new EffectiveValueImpl(CellType.NUMERIC, Double.NaN);
        }
        Double part = arg1.extractValueWithExpectation(Double.class);
        Double whole = arg2.extractValueWithExpectation(Double.class);
        if (part == null || whole == null) {
            return new EffectiveValueImpl(CellType.NUMERIC, Double.NaN);
        }
        Double percentage = (part * whole) / 100;
        return new EffectiveValueImpl(CellType.NUMERIC, percentage);
    }
}
