package spreadsheet.api;

import spreadsheet.cell.api.CellIdentifier;
import spreadsheet.cell.impl.CellIdentifierImpl;
import spreadsheet.util.UpdateResult;

public interface UpdateSheet {
    void setName(String name);
    void setVersion(int version);
    void removeCell(CellIdentifier identifier);
    UpdateResult updateCellValueAndCalculate(CellIdentifierImpl cellId, String originalValue) ;
    void updateDependenciesAndInfluences();
    void setAmountOfCellsChangedInVersion(int amountOfCellsChangedInVersion);
    void removeRange(String name);
    void addRange(String name, CellIdentifierImpl topLeft, CellIdentifierImpl bottomRight);
}
