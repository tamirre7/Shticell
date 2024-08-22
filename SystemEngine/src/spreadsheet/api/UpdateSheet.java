package spreadsheet.api;

import spreadsheet.cell.api.Cell;
import spreadsheet.cell.api.CellIdentifier;
import spreadsheet.cell.impl.CellIdentifierImpl;

public interface UpdateSheet {
    void setName(String name);
    void setVersion(int version);
    void addOrUpdateCell(Cell cell);
    void removeCell(CellIdentifier identifier);
    SpreadSheet updateCellValueAndCalculate(CellIdentifierImpl cellId, String originalValue) ;
}
