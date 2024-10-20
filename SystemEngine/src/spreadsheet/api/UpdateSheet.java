package spreadsheet.api;

import spreadsheet.cell.api.CellIdentifier;
import spreadsheet.cell.impl.CellIdentifierImpl;
import spreadsheet.sheetmanager.api.SheetManager;
import spreadsheet.util.UpdateResult;

public interface UpdateSheet {
    void removeCell(CellIdentifier identifier);
    UpdateResult updateCellValueAndCalculate(CellIdentifierImpl cellId, String originalValue, boolean isDynamicUpdate,String modifyingUserName,int currentVersion);
    void updateDependenciesAndInfluences();
    void removeRange(String name);
    void addEmptyCell (CellIdentifierImpl identifier);
    void addRange(String name, CellIdentifierImpl topLeft, CellIdentifierImpl bottomRight);
}
