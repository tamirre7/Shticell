package range.api;

import dto.RangeDto;
import spreadsheet.impl.SpreadsheetControllerImpl;

import java.util.Map;

public interface RangeController {
    void handleMouseClick(javafx.scene.input.MouseEvent event);
    void setSpreadsheetDisplayController(SpreadsheetControllerImpl spreadsheetControllerImpl);
    void displayRanges(Map<String, RangeDto> ranges);
    void handleAddRange();
    void handleDeleteRange();
    void disableEditing();
    void enableEditing();
    }
