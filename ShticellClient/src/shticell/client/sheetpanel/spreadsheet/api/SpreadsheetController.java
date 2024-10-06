package shticell.client.sheetpanel.spreadsheet.api;


import dto.CellDto;
import dto.SheetDto;
import javafx.scene.control.Label;


import shticell.client.sheetpanel.action.line.api.ActionLineController;
import shticell.client.sheetpanel.command.components.formulabuilder.FormulaBuilder;
import shticell.client.sheetpanel.command.components.sortandfilter.api.SortAndFilterController;
import shticell.client.sheetpanel.editingmanager.api.EditingManager;
import shticell.client.sheetpanel.misc.api.MiscController;
import shticell.client.sheetpanel.range.api.RangeController;
import shticell.client.sheetpanel.spreadsheet.UISheetModel;

import java.util.Map;

public interface SpreadsheetController {
    void setFormulaBuilder(FormulaBuilder formulaBuilder);
    void setCurrentSheet(SheetDto currentSheet);
    void setMiscController(MiscController miscController);
    void setActionLineController(ActionLineController actionLineController);
    void setRangeController(RangeController rangeController);
    void displaySheet(SheetDto sheetDto);
    void displayTemporarySheet(SheetDto sheetDto, boolean versionView);
    void displayOriginalSheet(boolean versionView);
    void handleCellClick(String cellId);
    void setupCellContextMenu(Label cellLabel, String cellId);
    void setupHeaderContextMenu(Label cellLabel, int index, boolean isColumn);
    void updateAllCells(Map<String, CellDto> cells);
    void showCellStyleDialog(Label cellLabel, String cellId);
    void showAlignmentDialog(int index, boolean isColumn);
    SheetDto getCurrentSheet();
    void recalculateGridDimensions();
    void setUiSheetModel(UISheetModel uiSheetModel);
    void setEditingManager(EditingManager editingManager);
    void disableCellClick();
    void enableCellClick();
}
