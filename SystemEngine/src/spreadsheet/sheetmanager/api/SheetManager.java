package spreadsheet.sheetmanager.api;

import spreadsheet.api.SpreadSheet;
import dto.permission.Permission;
import spreadsheet.sheetmanager.permissionmanager.permissionrequest.PermissionRequest;
import java.util.List;
import java.util.Map;

public interface SheetManager {
    String getSheetName();
    SpreadSheet getSheetByVersion(int version);
    void updateSheetVersion(SpreadSheet sheet);
    int getLatestVersion();
    String getUploadedBy();
    void approvePermission(PermissionRequest request);
    void addPendingPermissionRequest(PermissionRequest request);
    Permission getPermission(String username);
    Map<String, List<PermissionRequest>> getApprovedPermissions();
    Map<String, List<PermissionRequest>>getPendingPermissionRequests();
    Map<String, List<PermissionRequest>> getDeniedPermissionRequests();
    void denyPendingRequest(PermissionRequest request);
}
