package spreadsheet.range.api;
import spreadsheet.cell.api.Cell;
import spreadsheet.cell.api.CellIdentifier;
import spreadsheet.cell.impl.CellIdentifierImpl;

import java.util.List;

public interface Range {
    CellIdentifier getTopLeft();
    CellIdentifier getBottomRight();
    void setActive(boolean active);
    void initRangeList();
    boolean isActive();
    String getName();
    List<CellIdentifier> getCellsInRange();
}
