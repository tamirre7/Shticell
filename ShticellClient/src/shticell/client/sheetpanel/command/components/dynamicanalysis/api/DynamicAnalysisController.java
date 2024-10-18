package shticell.client.sheetpanel.command.components.dynamicanalysis.api;

import dto.SheetDto;
import javafx.scene.control.Slider;
import shticell.client.sheetpanel.spreadsheet.impl.SpreadsheetControllerImpl;

import java.util.List;

public interface DynamicAnalysisController {
    void handleAnalysisButtonPress();
    void openSliderSetupDialog(List<String> cellIds);
    void showMultiCellSliderDialog(List<String> cellIds, double min, double max, double step);
    void sendDynamicAnalysisUpdateRequest(String cellId, String cellOriginalValue, Slider slider, SheetDto sheetToUpdate);
    void setSpreadsheetController(SpreadsheetControllerImpl spreadsheetController);

}
