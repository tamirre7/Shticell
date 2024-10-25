package spreadsheet.api;

import spreadsheet.cell.api.Cell;
import spreadsheet.cell.api.EffectiveValue;
import spreadsheet.cell.api.CellIdentifier;
import spreadsheet.range.api.Range;
import java.util.Map;

public interface ReadOnlySpreadSheet {
    EffectiveValue getCellEffectiveValue(CellIdentifier identifier);
    boolean isValidCellID(String cellID);
    Dimension getSheetDimentions();
    Map<CellIdentifier, Cell> getActiveCells();
    Cell getCell(CellIdentifier identifier);
    Map<String, Range> getRanges();
    Range getRange(String name);
    boolean isCellWithinBounds(CellIdentifier cell);
    boolean isRangeWithinBounds(CellIdentifier topLeft, CellIdentifier bottomRight);
    String getSheetName();
}
