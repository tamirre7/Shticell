package shticell.client.sheetpanel.command.components.sortandfilter.api;

import shticell.client.sheetpanel.spreadsheet.api.SpreadsheetController;

public interface SortAndFilterController {
    void disableSortAndFilter();
    void handleFilter();
    void handleResetSortFilter();
    void handleSort();
    void enableSortAndFilter();
    void enableResetOnly();
    void setSpreadsheetController(SpreadsheetController spreadsheetController);
}
