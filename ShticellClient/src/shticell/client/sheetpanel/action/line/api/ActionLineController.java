package shticell.client.sheetpanel.action.line.api;

import dto.CellDto;
import shticell.client.sheetpanel.spreadsheet.api.SpreadsheetController;

public interface ActionLineController {
    void setSpreadsheetController(SpreadsheetController spreadsheetControllerI); // Set the spreadsheet controller
    void setCellData(CellDto cellDto, String cellId); // Set data for a specific cell
    void updateCellValue(String preBuildOriginalValue); // Update the value of a cell
    void startVersionSelectorRefresher(); // Start refreshing the version selector
    void stopVersionSelectorRefresher(); // Stop refreshing the version selector
    void clearTextFields(); // Clear input text fields
    void disableEditing(); // Disable editing
    void enableEditing(); // Enable editing
    void enableVersionView(); // Enable view for versions
    void setUsernameLabel(String usernameLabel); // Set the username label
}