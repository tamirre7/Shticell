package spreadsheet.sheetmanager.permissionmanager.api;

import dto.permission.Permission;
import spreadsheet.sheetmanager.permissionmanager.permissionrequest.PermissionRequest;

import java.util.Map;

public interface PermissionManager {
    void ApprovePermission(String username);
    void addPendingPermissionRequest(String username, PermissionRequest request);
    void removePendingRequest(String username);
    Permission getPermission(String username);
    Map<String, Permission> getApprovedPermissions();
    Map<String, PermissionRequest> getPendingPermissionRequests();
}
