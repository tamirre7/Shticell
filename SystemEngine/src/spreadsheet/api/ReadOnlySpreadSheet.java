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
    Dimentions getSheetDimentions();
    Map<CellIdentifier, Cell> getCells();
    Cell getCell(CellIdentifier identifier);
}
