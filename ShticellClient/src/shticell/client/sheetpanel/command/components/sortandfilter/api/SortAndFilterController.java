package shticell.client.sheetpanel.command.components.sortandfilter.api;

import shticell.client.sheetpanel.spreadsheet.api.SpreadsheetController;

public interface SortAndFilterController {
    void disableSortAndFilter(boolean versionView);
    void handleFilter();
    void handleResetSortFilter();
    void handleSort();
    void enableSortAndFilter();
    void setSpreadsheetController(SpreadsheetController spreadsheetController);
}
