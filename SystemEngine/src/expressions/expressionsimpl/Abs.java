package expressions.expressionsimpl;

import expressions.api.Expression;
import spreadsheet.cell.api.CellType;
import spreadsheet.cell.api.EffectiveValue;
import spreadsheet.cell.impl.EffectiveValueImpl;

public class Abs extends UnaryExpression {

    public Abs(Expression argument) {
        super(argument);
    }

    @Override
    protected EffectiveValue evaluate(EffectiveValue arg) {
        if (arg == null) {
            return new EffectiveValueImpl(CellType.NUMERIC, Double.NaN);}
        try {
            Object value = arg.getValue();
            if (value instanceof Number) {
                Double numericValue = Math.abs(((Number) value).doubleValue());
                return new EffectiveValueImpl(CellType.NUMERIC, numericValue);
            } else {
                return new EffectiveValueImpl(CellType.NUMERIC, Double.NaN);
            }
        } catch (Exception e) {
            return new EffectiveValueImpl(CellType.NUMERIC, Double.NaN);
        }
    }


}
