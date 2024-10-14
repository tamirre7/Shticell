package spreadsheet.sheetmanager.permissionmanager.impl;

import dto.permission.Permission;
import spreadsheet.sheetmanager.permissionmanager.api.PermissionManager;
import spreadsheet.sheetmanager.permissionmanager.permissionrequest.PermissionRequest;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class PermissionManagerImpl implements PermissionManager, Serializable {
    private Map<String, Permission> approvedPermissionMap = new HashMap<>();
    private Map<String, PermissionRequest> pendingPermissionRequests = new HashMap<>();

    public PermissionManagerImpl(String uploadedBy) {
        this.approvedPermissionMap.put(uploadedBy, Permission.OWNER);
    }

    @Override
    public void ApprovePermission(String username) {
        PermissionRequest request = pendingPermissionRequests.get(username);
        Permission permission = request.getPermission();
        approvedPermissionMap.put(username, permission);
        pendingPermissionRequests.remove(username);
    }

    @Override
    public void addPendingPermissionRequest(String username, PermissionRequest request) {
        if(pendingPermissionRequests.containsKey(username)) {
            throw new IllegalArgumentException("Permission request for this spreadsheet already exists");
        }
        pendingPermissionRequests.put(username, request);
    }

    @Override
    public void removePendingRequest(String username) {
        pendingPermissionRequests.remove(username);
    }

    @Override
    public Permission getPermission(String username) {
        return approvedPermissionMap.getOrDefault(username, Permission.NONE);
    }

    @Override
    public Map<String, Permission> getApprovedPermissions() {
        return new HashMap<>(approvedPermissionMap);
    }

    @Override
    public Map<String, PermissionRequest> getPendingPermissionRequests() {
        return new HashMap<>(pendingPermissionRequests);
    }
}

