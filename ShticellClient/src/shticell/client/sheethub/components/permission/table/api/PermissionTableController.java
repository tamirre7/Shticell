package shticell.client.sheethub.components.permission.table.api;

public interface PermissionTableController {
    // Starts the process of refreshing permissions for the specified sheet.
    void startRequestRefresher(String sheetName);

    // Stops the process of refreshing permissions.
    void stopRequestRefresher();
}
