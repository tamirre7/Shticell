package expressions.expressionsimpl;

import expressions.api.Expression;
import spreadsheet.api.ReadOnlySpreadSheet;
import spreadsheet.cell.api.CellType;
import spreadsheet.cell.api.EffectiveValue;
import spreadsheet.cell.impl.EffectiveValueImpl;

public class Sub extends TernaryExpression {

    public Sub(Expression source, Expression startIndex, Expression endIndex) {
        super(source, startIndex, endIndex);
    }

    @Override
    protected EffectiveValue evaluate(EffectiveValue source, EffectiveValue startIndex, EffectiveValue endIndex) {
        String sourceStr = source.extractValueWithExpectation(String.class);
        Integer startIdx = startIndex.extractValueWithExpectation(Integer.class);
        Integer endIdx = endIndex.extractValueWithExpectation(Integer.class);

        if (sourceStr == null || startIdx == null || endIdx == null ||
                startIdx < 0 || endIdx >= sourceStr.length() || startIdx > endIdx) {
            return new EffectiveValueImpl(CellType.STRING, "!UNDEFINED!");
        }

        String result = sourceStr.substring(startIdx, endIdx + 1);
        return new EffectiveValueImpl(CellType.STRING, result);
    }

    @Override
    public CellType getFunctionResultType(ReadOnlySpreadSheet spreadSheet) {
        return CellType.STRING;
    }
}

