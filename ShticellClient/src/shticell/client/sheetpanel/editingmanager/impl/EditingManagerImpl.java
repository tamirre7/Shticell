package shticell.client.sheetpanel.editingmanager.impl;


import dto.permission.Permission;
import shticell.client.sheetpanel.action.line.api.ActionLineController;
import shticell.client.sheetpanel.command.components.sortandfilter.api.SortAndFilterController;
import shticell.client.sheetpanel.editingmanager.api.EditingManager;
import shticell.client.sheetpanel.range.api.RangeController;
import shticell.client.sheetpanel.spreadsheet.api.SpreadsheetController;

public class EditingManagerImpl implements EditingManager {
    private final SpreadsheetController spreadsheetController;
    private final RangeController rangeController;
    private final SortAndFilterController sortAndFilterController;
    private final ActionLineController actionLineController;

    public EditingManagerImpl(SpreadsheetController spreadsheetController,RangeController rangeController, SortAndFilterController sortAndFilterController, ActionLineController actionLineController) {
        this.spreadsheetController = spreadsheetController;
        this.rangeController = rangeController;
        this.sortAndFilterController = sortAndFilterController;
        this.actionLineController = actionLineController;
    }


    @Override
    public void enableSheetViewEditing(Permission permission) {
        if(permission == Permission.OWNER || permission == Permission.WRITER) {
            rangeController.enableEditing();
            sortAndFilterController.enableSortAndFilter();
            spreadsheetController.enableCellClick();
            actionLineController.enableEditing();
        }
        else if(permission == Permission.READER) {
            sortAndFilterController.enableSortAndFilter();
        }
    }

    @Override
    public void disableSheetViewEditing(boolean versionView) {
        spreadsheetController.disableCellClick();
        actionLineController.disableEditing(versionView);
        sortAndFilterController.disableSortAndFilter(versionView);
        rangeController.disableEditing();

    }
}
