package shticell.client.sheetpanel.editingmanager.api;

import dto.permission.Permission;

public interface EditingManager {
    // Enables editing capabilities based on user permission
    void enableSheetViewEditing(Permission permission);
    // Enables view for the sheet state
    void enableSheetStateView();
    // Enables read-only access for version viewing
    void enableVersionViewRead();
}