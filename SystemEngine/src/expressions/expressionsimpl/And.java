package expressions.expressionsimpl;
import expressions.api.Expression;
import spreadsheet.cell.api.CellType;
import spreadsheet.cell.api.EffectiveValue;
import spreadsheet.cell.impl.EffectiveValueImpl;

public class And extends BinaryExpression {
    public And(Expression left, Expression right) {
        super(left, right);
    }

    @Override
    protected EffectiveValue evaluate(EffectiveValue arg1, EffectiveValue arg2) {
        if (arg1 == null || arg2 == null) {
            return new EffectiveValueImpl(CellType.INVALID_VALUE, "UNKNOWN");
        }
        // Ensure both arguments are boolean
        Boolean value1 = arg1.extractValueWithExpectation(Boolean.class);
        Boolean value2 = arg2.extractValueWithExpectation(Boolean.class);
        if (value1 == null || value2 == null) {
            return new EffectiveValueImpl(CellType.INVALID_VALUE, "UNKNOWN");
        }

        // Perform addition
        boolean result = value1 && value2;

        // Return the result wrapped in an EffectiveValue
        return new EffectiveValueImpl(CellType.BOOLEAN, result);
    }
}
