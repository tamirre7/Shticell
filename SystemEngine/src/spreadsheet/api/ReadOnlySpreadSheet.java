package spreadsheet.api;

import spreadsheet.cell.api.Cell;
import spreadsheet.cell.api.EffectiveValue;
import spreadsheet.cell.api.CellIdentifier;
import spreadsheet.cell.impl.CellIdentifierImpl;
import spreadsheet.range.impl.RangeImpl;

import java.util.List;
import java.util.Map;

public interface ReadOnlySpreadSheet {
    EffectiveValue getCellEffectiveValue(CellIdentifierImpl identifier);
    String getName();
    int getVersion();
    boolean isValidCellID(String cellID);
    Dimension getSheetDimentions();
    Map<CellIdentifier, Cell> getActiveCells();
    Cell getCell(CellIdentifier identifier);
    int getAmountOfCellsChangedInVersion();
    Map<String, RangeImpl> getRanges();
    RangeImpl getRange(String name);
    boolean isCellWithinBounds(CellIdentifierImpl cell);
    boolean isRangeWithinBounds(CellIdentifierImpl topLeft, CellIdentifierImpl bottomRight);

}
