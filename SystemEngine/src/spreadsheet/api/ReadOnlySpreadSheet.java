package spreadsheet.api;

import spreadsheet.cell.api.Cell;
import spreadsheet.cell.api.EffectiveValue;
import spreadsheet.cell.api.CellIdentifier;

import java.util.Map;

public interface ReadOnlySpreadSheet {
    EffectiveValue getCellEffectiveValue(CellIdentifier identifier);
    String getName();
    int getVersion();
    boolean isValidCellID(CellIdentifier cellID);
    Dimension getSheetDimentions();
    Map<CellIdentifier, Cell> getActiveCells();
    Cell getCell(CellIdentifier identifier);
}
