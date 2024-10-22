package spreadsheet.api;

import spreadsheet.cell.api.CellIdentifier;
import spreadsheet.cell.impl.CellIdentifierImpl;
import spreadsheet.util.UpdateResult;

public interface UpdateSheet {
    UpdateResult updateCellValueAndCalculate(CellIdentifier cellId, String originalValue, boolean isDynamicUpdate,String modifyingUserName,int currentVersion);
    void updateDependenciesAndInfluences();
    void removeRange(String name);
    void addEmptyCell (CellIdentifier identifier);
    void addRange(String name, CellIdentifier topLeft, CellIdentifier bottomRight);
}
