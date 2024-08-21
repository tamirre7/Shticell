package spreadsheet.sheetimpl;

import spreadsheet.api.Dimentions;
import spreadsheet.api.ReadOnlySpreadSheet;
import spreadsheet.api.SpreadSheet;
import spreadsheet.cell.api.EffectiveValue;
import spreadsheet.cell.api.CellIdentifier;

public class ReadOnlySpreadSheetImpl implements ReadOnlySpreadSheet {
    private final SpreadSheet spreadSheet;

    public ReadOnlySpreadSheetImpl(SpreadSheet spreadSheet) {
        this.spreadSheet = spreadSheet;
    }

    @Override
    public EffectiveValue getCellEffectiveValue(CellIdentifier identifier) {
        var cell = spreadSheet.getCell(identifier);
        return cell != null ? cell.getEffectiveValue() : null;
    }
    public Dimentions getDimentions()
    {
        return spreadSheet.getSheetDimentions();
    }
}
