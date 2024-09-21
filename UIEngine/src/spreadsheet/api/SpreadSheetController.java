package spreadsheet.api;

import action.line.impl.ActionLineControllerImpl;
import command.components.formulabuilder.FormulaBuilder;
import command.components.sortandfilter.impl.SortAndFilterControllerImpl;
import dto.CellDto;
import dto.SheetDto;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import misc.impl.MiscControllerImpl;
import range.impl.RangeControllerImpl;

import java.util.List;
import java.util.Map;

public interface SpreadSheetController {
    void setFormulaBuilder(FormulaBuilder formulaBuilder);
    void setCurrentSheet(SheetDto currentSheet);
    void setMiscController(MiscControllerImpl miscController);
    void setActionLineController(ActionLineControllerImpl actionLineController);
    void setRangeController(RangeControllerImpl rangeController);
    void setSortAndFilterController(SortAndFilterControllerImpl sortAndFilterController);
    void displaySheet(SheetDto sheetDto);
    void animateSheetAppearance();
    void clearCells();
    void setupGridDimensions();
    void createCells();
    void displayTemporarySheet(SheetDto sheetDto, boolean versionView);
    void disableCellClick();
    void displayOriginalSheet(boolean versionView);
    void enableEditing();
    void setupCell(Label cellLabel, int col, int row);
    void highlightRange(String topLeft, String bottomRight);
    void clearPreviousRangeHighlight();
    void handleCellClick(String cellId);
    void addPulsingEffect(Node cell);
    void setupCellContextMenu(Label cellLabel, String cellId);
    void resetCellStyle(Label cellLabel, String cellId);
    void setupHeaderContextMenu(Label cellLabel, int index, boolean isColumn);
    void updateAllCells(Map<String, CellDto> cells);
    void updateCell(String cellId, CellDto cellDto);
    void applyStyle(Label cellLabel, String cellId);
    void showCellStyleDialog(Label cellLabel, String cellId);
    void showResizeDialog(int index, boolean isColumn);
    void showAlignmentDialog(int index, boolean isColumn);
    String toRgbString(Color color);
    void clearPreviousHighlights();
    void clearHighlights(List<String> cellIds);
    void highlightDependenciesAndInfluences(CellDto cellDto);
    void highlightCells(List<String> cellIds, String color);
    SheetDto getCurrentSheet();
    void openSliderDialog(Label cellLabel, String cellID);
    void showSliderDialog(Label cellLabel, String cellID, double min, double max, double step);


}
