package shticell.client.sheetpanel.spreadsheet.api;

import dto.SheetDto;
import javafx.scene.control.Label;
public interface SpreadsheetController {
    SheetDto getCurrentSheet();
    void setCurrentSheet(SheetDto sheet);
    void handleCellClick(String cellid);
    void setupCellContextMenu(Label cellLable, String cellid);
    void recalculateGridDimensions();
    void setupHeaderContextMenu(Label cellLabel, int index, boolean isColumn);
}
