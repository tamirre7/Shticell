package shticell.client.sheetpanel.action.line.api;

import dto.CellDto;
import shticell.client.sheetpanel.spreadsheet.api.SpreadsheetController;

public interface ActionLineController {
    void setSpreadsheetController(SpreadsheetController spreadsheetControllerI);
    void setCellData(CellDto cellDto, String cellId);
    void updateCellValue(String preBuildOriginalValue);
    void startVersionSelectorRefresher();
    void stopVersionSelectorRefresher();
    void clearTextFields();
    void disableEditing();
    void enableEditing();
    void enableVersionView();
    void setUsernameLabel(String usernameLabel);
    String getLoggedUser();
}
