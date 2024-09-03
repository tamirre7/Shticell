package expressions.expressionsimpl;
import expressions.api.Expression;
import spreadsheet.cell.api.CellType;
import spreadsheet.cell.api.EffectiveValue;
import spreadsheet.cell.impl.EffectiveValueImpl;

public class If extends TernaryExpression {
   public If(Expression condition, Expression thanExp, Expression elseExp) {
        super(condition, thanExp, elseExp);
    }

    @Override
    protected EffectiveValue evaluate(EffectiveValue conditionValue, EffectiveValue thenValue, EffectiveValue elseValue) {
        if (conditionValue == null || thenValue == null || elseValue == null) {
            return new EffectiveValueImpl(CellType.INVALID_VALUE, "UNKNOWN");
        }

        Boolean condition = conditionValue.extractValueWithExpectation(Boolean.class);

        if (condition == null) {
            return new EffectiveValueImpl(CellType.INVALID_VALUE, "UNKNOWN");
        }

        if (!thenValue.getCellType().equals(elseValue.getCellType())) {
            return new EffectiveValueImpl(CellType.INVALID_VALUE, "UNKNOWN");
        }

        return condition ? thenValue : elseValue;
    }
}
