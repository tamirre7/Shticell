package expressions.expressionsimpl;
import expressions.api.Expression;
import spreadsheet.cell.api.CellType;
import spreadsheet.cell.api.EffectiveValue;
import spreadsheet.cell.impl.EffectiveValueImpl;

public class Less extends BinaryExpression {
    public Less(Expression left, Expression right) {
        super(left, right);
    }

    protected EffectiveValue evaluate(EffectiveValue arg1, EffectiveValue arg2) {
        if (arg1 == null || arg2 == null) {
            return new EffectiveValueImpl(CellType.NUMERIC, Double.NaN);
        }
        // Ensure both arguments are numeric
        Double value1 = arg1.extractValueWithExpectation(Double.class);
        Double value2 = arg2.extractValueWithExpectation(Double.class);
        if (value1 == null || value2 == null) {
            return new EffectiveValueImpl(CellType.NUMERIC, Double.NaN);
        }

        // Perform addition
        boolean result = value1 <= value2;

        // Return the result wrapped in an EffectiveValue
        return new EffectiveValueImpl(CellType.BOOLEAN, result);
    }
}