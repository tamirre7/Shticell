package command.components.sortandfilter.api;

public interface SortAndFilterController {
    void disableSortAndFilter(boolean versionView);
    void handleFilter();
    void handleResetSortFilter();
    void handleSort();
    void enableSortAndFilter();
}
