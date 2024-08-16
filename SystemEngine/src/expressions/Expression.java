package expressions;

import spreadsheet.api.ReadOnlySpreadSheet;
import spreadsheet.cell.api.EffectiveValue;

public interface Expression {

    /**
     * Evaluate the function and return the result.
     *
     * @return the result of the function
     */
    EffectiveValue evaluate(ReadOnlySpreadSheet spreadSheet);
}
