package shticell.client.sheetpanel.action.line.api;

import dto.CellDto;
import dto.SheetDto;
import shticell.client.sheetpanel.spreadsheet.api.SpreadsheetController;

public interface ActionLineController {
    void setSpreadsheetController(SpreadsheetController spreadsheetControllerI);
    void setCellData(CellDto cellDto, String cellId);
    void updateCellValue(String preBuildOriginalValue);
    void startVersionSelectorRefresher();
    void stopVersionSelectorRefresher();
    void clearTextFields();
    void disableEditing(boolean versionView);
    void enableEditing(boolean versionView);
    void setUsernameLabel(String usernameLabel);
    String getLoggedUser();
}
