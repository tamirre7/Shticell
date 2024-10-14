package spreadsheet.sheetmanager.api;

import spreadsheet.api.SpreadSheet;
import dto.permission.Permission;
import spreadsheet.sheetmanager.permissionmanager.permissionrequest.PermissionRequest;

import java.util.Map;

public interface SheetManager {
    String getSheetName();
    SpreadSheet getSheetByVersion(int version);
    void updateSheetVersion(SpreadSheet sheet);
    int getLatestVersion();
    String getUploadedBy();
    void ApprovePermission(String username);
    void addPendingPermissionRequest(String username,PermissionRequest request);
    Permission getPermission(String username);
    Map<String, Permission> getApprovedPermissions();
    Map<String, PermissionRequest>getPendingPermissionRequests();
    void removePendingRequest(String username);
}
