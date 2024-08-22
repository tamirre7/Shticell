package spreadsheet.api;

import spreadsheet.cell.api.Cell;
import spreadsheet.cell.api.CellIdentifier;

public interface UpdateSheet {
    void setName(String name);
    void setVersion(int version);
    void addOrUpdateCell(Cell cell);
    void removeCell(CellIdentifier identifier);
}
