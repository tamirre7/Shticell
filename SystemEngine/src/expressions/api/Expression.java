package expressions.api;

import spreadsheet.api.ReadOnlySpreadSheet;
import spreadsheet.cell.api.CellType;
import spreadsheet.cell.api.EffectiveValue;

public interface Expression {

    CellType getFunctionResultType(ReadOnlySpreadSheet spreadSheet);
    EffectiveValue evaluate(ReadOnlySpreadSheet spreadSheet);
}
