package expressions.expressionsimpl;
import expressions.api.Expression;
import spreadsheet.cell.api.CellType;
import spreadsheet.cell.api.EffectiveValue;
import spreadsheet.cell.impl.EffectiveValueImpl;

public class Equal extends BinaryExpression {
    public Equal(Expression left, Expression right) {
        super(left, right);
    }

    protected EffectiveValue evaluate(EffectiveValue arg1, EffectiveValue arg2) {
        if (arg1 == null || arg2 == null) {
            return new EffectiveValueImpl(CellType.INVALID_VALUE, "UNKNOWN");
        }
        if (arg1.getCellType() == CellType.INVALID_VALUE || arg2.getCellType() == CellType.INVALID_VALUE) {
            return new EffectiveValueImpl(CellType.INVALID_VALUE, "UNKNOWN");
        }
        if (arg1.getCellType() == arg2.getCellType()) {
            return new EffectiveValueImpl(CellType.BOOLEAN, true);
        }
        return new EffectiveValueImpl(CellType.BOOLEAN, false);
    }
}

