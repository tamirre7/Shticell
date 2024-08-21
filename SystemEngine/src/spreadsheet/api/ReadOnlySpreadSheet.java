package spreadsheet.api;

import spreadsheet.cell.api.EffectiveValue;
import spreadsheet.cell.api.CellIdentifier;

public interface ReadOnlySpreadSheet {
    EffectiveValue getCellEffectiveValue(CellIdentifier identifier);
    Dimentions getDimentions();

}
