package spreadsheet.api;

import spreadsheet.cell.api.CellIdentifier;
import spreadsheet.cell.impl.CellIdentifierImpl;

public interface UpdateSheet {
    void setName(String name);
    void setVersion(int version);
    void removeCell(CellIdentifier identifier);
    SpreadSheet updateCellValueAndCalculate(CellIdentifierImpl cellId, String originalValue) ;
    void updateDependenciesAndInfluences();
    void setAmountOfCellsChangedInVersion(int amountOfCellsChangedInVersion);

    }
