package spreadsheet.sheetmanager.permissionmanager.impl;

import dto.permission.Permission;
import spreadsheet.sheetmanager.permissionmanager.api.PermissionManager;
import spreadsheet.sheetmanager.permissionmanager.permissionrequest.PermissionRequest;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// Implementation of the PermissionManager interface, managing user permissions and permission requests.
public class PermissionManagerImpl implements PermissionManager, Serializable {

    // Map to store permissions for each user.
    private Map<String, Permission> sheetPermissionMap = new HashMap<>();
    private Map<String, List<PermissionRequest>> approvedPermissionRequests = new HashMap<>();
    private Map<String, List<PermissionRequest>> pendingPermissionRequests = new HashMap<>();
    private Map<String, List<PermissionRequest>> deniedPermissionRequests = new HashMap<>();

    // Constructs a PermissionManagerImpl with an initial owner user.
    public PermissionManagerImpl(String uploadedBy) {
        this.sheetPermissionMap.put(uploadedBy, Permission.OWNER);
        PermissionRequest ownerRequest = new PermissionRequest(-1, Permission.OWNER, uploadedBy);
        approvedPermissionRequests.computeIfAbsent(uploadedBy, k -> new ArrayList<>()).add(ownerRequest);
    }

    // Approves a pending permission request, transferring it to the approved list.
    @Override
    public void approvePermission(PermissionRequest requestToApprove) {
        String userName = requestToApprove.getRequester();
        List<PermissionRequest> requests = pendingPermissionRequests.get(userName);
        if (requests != null && requests.contains(requestToApprove)) {
            Permission permission = requestToApprove.getPermission();
            approvedPermissionRequests.computeIfAbsent(userName, k -> new ArrayList<>()).add(requestToApprove);
            sheetPermissionMap.put(userName, permission);
            requests.remove(requestToApprove);
            if (requests.isEmpty()) {
                pendingPermissionRequests.remove(userName);
            }
        }
    }

    // Adds a new permission request to the pending list for a specified user.
    @Override
    public void addPendingPermissionRequest(PermissionRequest request) {
        String userName = request.getRequester();
        pendingPermissionRequests.computeIfAbsent(userName, k -> new ArrayList<>()).add(request);
    }

    // Denies a pending permission request, transferring it to the denied list.
    @Override
    public void denyPendingRequest(PermissionRequest requestToDeny) {
        String userName = requestToDeny.getRequester();
        List<PermissionRequest> requests = pendingPermissionRequests.get(userName);
        if (requests != null && requests.contains(requestToDeny)) {
            deniedPermissionRequests.computeIfAbsent(userName, k -> new ArrayList<>()).add(requestToDeny);
            requests.remove(requestToDeny);

            if (requests.isEmpty()) {
                pendingPermissionRequests.remove(userName);
            }
        }
    }

    // Retrieves the permission level for a specified user, returning NONE if the user does not exist.
    @Override
    public Permission getPermission(String username) {
        for (String name : sheetPermissionMap.keySet()) {
            if (name.equalsIgnoreCase(username)) {
                return sheetPermissionMap.get(name);}}
        return Permission.NONE;
    }

    // Returns a map of all approved permission requests by user.
    @Override
    public Map<String, List<PermissionRequest>> getApprovedPermissions() {
        return new HashMap<>(approvedPermissionRequests);
    }

    // Returns a map of all pending permission requests by user.
    @Override
    public Map<String, List<PermissionRequest>> getPendingPermissionRequests() {
        return new HashMap<>(pendingPermissionRequests);
    }

    // Returns a map of all denied permission requests by user.
    @Override
    public Map<String, List<PermissionRequest>> getDeniedPermissionRequests() {
        return new HashMap<>(deniedPermissionRequests);
    }
}
