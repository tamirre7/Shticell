package range.api;

import dto.RangeDto;

import spreadsheet.api.SpreadsheetController;

import java.util.Map;

public interface RangeController {
    void handleMouseClick(javafx.scene.input.MouseEvent event);
    void setSpreadsheetController(SpreadsheetController spreadsheetController);
    //void setUiSheetModel(UISheetModel uiSheetModel);
     void displayRanges(Map<String, RangeDto> ranges);
    void handleAddRange();
    void handleDeleteRange();
    void disableEditing();
    void enableEditing();
    }
