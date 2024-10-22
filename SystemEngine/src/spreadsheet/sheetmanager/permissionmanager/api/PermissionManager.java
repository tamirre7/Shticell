package spreadsheet.sheetmanager.permissionmanager.api;

import dto.permission.Permission;
import spreadsheet.sheetmanager.permissionmanager.permissionrequest.PermissionRequest;

import java.util.List;
import java.util.Map;

public interface PermissionManager {
    void ApprovePermission(PermissionRequest requestToApprove);
    void addPendingPermissionRequest(PermissionRequest request);
    void denyPendingRequest(PermissionRequest requestToDeny);
    Permission getPermission(String username);
    Map<String, Permission> getApprovedPermissions();
    Map<String, List<PermissionRequest>> getPendingPermissionRequests();
    Map<String, List<PermissionRequest>> getDeniedPermissionRequests();
}
