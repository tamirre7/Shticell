package shticell.client.sheethub.components.permission.table.api;

public interface PermissionTableController {
    void startRequestRefresher(String sheetName);
    void stopRequestRefresher();
}
