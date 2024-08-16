package spreadsheet.api;

import spreadsheet.cell.api.Cell;
import spreadsheet.cell.api.CellIdentifier;

import java.util.Map;

public interface SpreadSheet {
   String getName();

    void setName(String name);

    int getVersion();

    Dimentions getSheetDimentions();

    void setVersion(int version);

    Map<CellIdentifier, Cell> getCells();

    void addOrUpdateCell(Cell cell);

    Cell getCell(CellIdentifier identifier);

    void removeCell(CellIdentifier identifier);
}
