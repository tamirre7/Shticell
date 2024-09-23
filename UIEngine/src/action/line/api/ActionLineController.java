package action.line.api;

import command.api.Engine;
import dto.CellDto;
import dto.SheetDto;
import spreadsheet.api.SpreadsheetController;
import spreadsheet.impl.SpreadsheetControllerImpl;

public interface ActionLineController {
    void setEngine(Engine engine);
    void setSpreadsheetDisplayController(SpreadsheetController spreadsheetControllerI);
    void setCurrentSheet(SheetDto currentSheet);
    void setCellData(CellDto cellDto, String cellId);
    void updateCellValue(String preBuildOriginalValue);
    void populateVersionSelector(int numOfVersions);
    void clearTextFields();
    void disableEditing();
    void enableEditing();

    }

