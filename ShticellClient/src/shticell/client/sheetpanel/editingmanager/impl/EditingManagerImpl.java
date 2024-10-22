package shticell.client.sheetpanel.editingmanager.impl;


import dto.permission.Permission;
import shticell.client.sheetpanel.action.line.api.ActionLineController;
import shticell.client.sheetpanel.command.components.dynamicanalysis.api.DynamicAnalysisController;
import shticell.client.sheetpanel.command.components.graphbuilder.builder.api.GraphBuilderController;
import shticell.client.sheetpanel.command.components.sortandfilter.api.SortAndFilterController;
import shticell.client.sheetpanel.editingmanager.api.EditingManager;
import shticell.client.sheetpanel.range.api.RangeController;
import shticell.client.sheetpanel.spreadsheet.api.SpreadsheetController;

public class EditingManagerImpl implements EditingManager {
    private final SpreadsheetController spreadsheetController;
    private final RangeController rangeController;
    private final SortAndFilterController sortAndFilterController;
    private final ActionLineController actionLineController;
    private final DynamicAnalysisController dynamicAnalysisController;
    private final GraphBuilderController graphBuilderController;

    public EditingManagerImpl(SpreadsheetController spreadsheetController,RangeController rangeController, SortAndFilterController sortAndFilterController, ActionLineController actionLineController,DynamicAnalysisController dynamicAnalysisController, GraphBuilderController graphBuilderController) {
        this.spreadsheetController = spreadsheetController;
        this.rangeController = rangeController;
        this.sortAndFilterController = sortAndFilterController;
        this.actionLineController = actionLineController;
        this.dynamicAnalysisController = dynamicAnalysisController;
        this.graphBuilderController = graphBuilderController;
    }


    @Override
    public void enableSheetViewEditing(Permission permission) {
        disableFullEditing();
        switch (permission) {
            case Permission.OWNER:
            case Permission.WRITER:
                enableFullEditing();
                break;
            case Permission.READER:
                enableReaderView();
                break;
        }
    }

    private void enableFullEditing() {
        spreadsheetController.enableCellClick();
        actionLineController.enableEditing();
        sortAndFilterController.enableSortAndFilter();
        rangeController.enableEditing();
        dynamicAnalysisController.enableDynamicAnalysis();
        graphBuilderController.enableGraphBuild();
    }

    private void enableReaderView() {
        spreadsheetController.enableCellClick();
        actionLineController.enableVersionView();
        sortAndFilterController.enableSortAndFilter();
        rangeController.enableViewOnly();
        dynamicAnalysisController.enableDynamicAnalysis();
        graphBuilderController.enableGraphBuild();
    }
    @Override
    public void enableVersionViewRead(){
        disableFullEditing();
        enableReaderView();
    }
    @Override
    public void enableSheetStateView(){
        disableFullEditing();
        spreadsheetController.enableCellClick();
        actionLineController.disableEditing();
        sortAndFilterController.enableResetOnly();
        rangeController.disableEditing();
        dynamicAnalysisController.enableDynamicAnalysis();
        graphBuilderController.enableGraphBuild();
    }


    private void disableFullEditing() {
        spreadsheetController.disableCellClick();
        actionLineController.disableEditing();
        sortAndFilterController.disableSortAndFilter();
        rangeController.disableEditing();
        dynamicAnalysisController.disableDynamicAnalysis();
        graphBuilderController.disableGraphBuild();

    }
}
