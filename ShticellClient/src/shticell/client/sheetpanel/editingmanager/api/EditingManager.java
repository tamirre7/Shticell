package shticell.client.sheetpanel.editingmanager.api;

import dto.permission.Permission;

public interface EditingManager {
    void enableSheetViewEditing(Permission permission);
    void disableSheetViewEditing(boolean versionView);
}
