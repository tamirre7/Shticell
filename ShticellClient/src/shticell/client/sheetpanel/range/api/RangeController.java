package shticell.client.sheetpanel.range.api;

import dto.RangeDto; // Import RangeDto for range data transfer
import shticell.client.sheetpanel.spreadsheet.UISheetModel; // Import UISheetModel for UI sheet model
import shticell.client.sheetpanel.spreadsheet.api.SpreadsheetController; // Import SpreadsheetController for spreadsheet operations

import java.util.Map; // Import Map for handling ranges as key-value pairs

public interface RangeController {
    void handleMouseClick(javafx.scene.input.MouseEvent event); // Handle mouse clicks on range
    void setSpreadsheetController(SpreadsheetController spreadsheetController); // Set spreadsheet controller
    void setUiSheetModel(UISheetModel uiSheetModel); // Set UI sheet model
    void displayRanges(Map<String, RangeDto> ranges); // Display the available ranges
    void handleAddRange(); // Handle adding a new range
    void handleDeleteRange(); // Handle deleting a range
    void disableEditing(); // Disable editing functionality
    void enableEditing(); // Enable editing functionality
    void enableViewOnly(); // Enable view-only mode
}