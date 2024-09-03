package expressions.expressionsimpl;

import expressions.api.Expression;
import spreadsheet.api.ReadOnlySpreadSheet;
import spreadsheet.cell.api.CellType;
import spreadsheet.cell.api.EffectiveValue;
import spreadsheet.cell.impl.EffectiveValueImpl;

public class Concat extends BinaryExpression {

    public Concat(Expression argument1, Expression argument2) {
        super(argument1, argument2);
    }

    @Override
    protected EffectiveValue evaluate(EffectiveValue arg1, EffectiveValue arg2) {
        if (arg1 == null || arg2 == null) {
            return new EffectiveValueImpl(CellType.INVALID_VALUE, "!UNDEFINED!");
        }
        String value1 = arg1.extractValueWithExpectation(String.class);
        String value2 = arg2.extractValueWithExpectation(String.class);
        if (value1 == null || value2 == null || value1.equals("") || value2.equals("")) {
            return new EffectiveValueImpl(CellType.INVALID_VALUE, "!UNDEFINED!");
        }
        String res = value1 + value2;
        return new EffectiveValueImpl(CellType.STRING, res);
    }
}
