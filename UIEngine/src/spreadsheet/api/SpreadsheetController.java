package spreadsheet.api;

import action.line.api.ActionLineController;
import command.components.formulabuilder.FormulaBuilder;
import command.components.sortandfilter.api.SortAndFilterController;
import dto.CellDto;
import dto.SheetDto;
import javafx.scene.control.Label;
import misc.api.MiscController;
import range.api.RangeController;

import java.util.Map;

public interface SpreadsheetController {
    void setFormulaBuilder(FormulaBuilder formulaBuilder);
    void setCurrentSheet(SheetDto currentSheet);
    void setMiscController(MiscController miscController);
    void setActionLineController(ActionLineController actionLineController);
    void setRangeController(RangeController rangeController);
    void setSortAndFilterController(SortAndFilterController sortAndFilterController);
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
    void enableEditing();
    void recalculateGridDimensions();
}
