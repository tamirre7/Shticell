package spreadsheet.api;

import spreadsheet.cell.api.Cell;
import spreadsheet.cell.impl.CellIdentifierimpl;

import java.util.Map;

public interface SpreadSheet {
   String getName();

    void setName(String name);

    int getVersion();

    Dimentions getSheetDimentions();

    void setVersion(int version);

    Map<CellIdentifierimpl, Cell> getCells();

    void addOrUpdateCell(Cell cell);

    Cell getCell(CellIdentifierimpl identifier);

    void removeCell(CellIdentifierimpl identifier);
}
