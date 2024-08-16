package spreadsheet.sheetimpl;

import spreadsheet.api.ReadOnlySpreadSheet;
import spreadsheet.api.SpreadSheet;
import spreadsheet.cell.api.EffectiveValue;
import spreadsheet.cell.impl.CellIdentifierimpl;

public class ReadOnlySpreadSheetImpl implements ReadOnlySpreadSheet {
    private final SpreadSheet spreadSheet;

    public ReadOnlySpreadSheetImpl(SpreadSheet spreadSheet) {
        this.spreadSheet = spreadSheet;
    }

    @Override
    public EffectiveValue getCellEffectiveValue(CellIdentifierimpl identifier) {
        var cell = spreadSheet.getCell(identifier);
        return cell != null ? cell.getEffectiveValue() : null;
    }
}
