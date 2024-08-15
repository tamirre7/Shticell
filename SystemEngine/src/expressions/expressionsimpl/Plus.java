package expressions.expressionsimpl;

import expressions.Expression;
import spreadsheet.api.SpreadSheet;
import spreadsheet.cell.api.CellType;
import spreadsheet.cell.api.EffectiveValue;
import spreadsheet.cell.impl.EffectiveValueimpl;

public class Plus extends BinaryExpression {

    public Plus(Expression argument1, Expression argument2) {
        super(argument1, argument2);
    }

    @Override
    protected EffectiveValue evaluate(EffectiveValue arg1, EffectiveValue arg2) {
        // Ensure both arguments are numeric
        Double value1 = arg1.extractValueWithExpectation(Double.class);
        Double value2 = arg2.extractValueWithExpectation(Double.class);

        if (value1 == null || value2 == null) {
            throw new IllegalArgumentException("Plus operation requires numeric values.");
        }

        // Perform addition
        double result = value1 + value2;

        // Return the result wrapped in an EffectiveValue
        return new EffectiveValueimpl(CellType.NUMERIC, result);
    }
}
