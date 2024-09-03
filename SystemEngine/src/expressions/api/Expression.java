package expressions.api;

import spreadsheet.api.ReadOnlySpreadSheet;
import spreadsheet.cell.api.CellType;
import spreadsheet.cell.api.EffectiveValue;

public interface Expression {

    EffectiveValue evaluate(ReadOnlySpreadSheet spreadSheet);
}
