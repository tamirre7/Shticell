package spreadsheet.sheetmanager.permissionmanager.impl;

import dto.permission.Permission;
import dto.permission.RequestStatus;
import spreadsheet.sheetmanager.permissionmanager.api.PermissionManager;
import spreadsheet.sheetmanager.permissionmanager.permissionrequest.PermissionRequest;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PermissionManagerImpl implements PermissionManager, Serializable {
    private Map<String, Permission> sheetPermissionMap = new HashMap<>();
    private Map<String, List<PermissionRequest>> approvedPermissionRequests = new HashMap<>();
    private Map<String, List<PermissionRequest>> pendingPermissionRequests = new HashMap<>();
    private Map<String, List<PermissionRequest>> deniedPermissionRequests = new HashMap<>();

    public PermissionManagerImpl(String uploadedBy) {
        this.sheetPermissionMap.put(uploadedBy, Permission.OWNER);
        PermissionRequest ownerRequest = new PermissionRequest(-1,Permission.OWNER,uploadedBy);
        approvedPermissionRequests.computeIfAbsent(uploadedBy, k -> new ArrayList<>()).add(ownerRequest);
    }

    @Override
    public void ApprovePermission(PermissionRequest requestToApprove) {
      String userName = requestToApprove.getRequester();
      List<PermissionRequest> requests = pendingPermissionRequests.get(userName);
      if(requests != null && requests.contains(requestToApprove)) {
          Permission permission = requestToApprove.getPermission();
          approvedPermissionRequests.computeIfAbsent(userName, k -> new ArrayList<>()).add(requestToApprove);
          sheetPermissionMap.put(userName, permission);
          requests.remove(requestToApprove);

          if(requests.isEmpty()) {
              pendingPermissionRequests.remove(userName);
          }
      }
    }

    @Override
    public void addPendingPermissionRequest(PermissionRequest request) {
        String userName = request.getRequester();
       pendingPermissionRequests.computeIfAbsent(userName, k -> new ArrayList<>()).add(request);
    }

    @Override
    public void denyPendingRequest(PermissionRequest requestToDeny) {
        String userName = requestToDeny.getRequester();
        List<PermissionRequest> requests = pendingPermissionRequests.get(userName);
        if(requests != null && requests.contains(requestToDeny)) {
            deniedPermissionRequests.computeIfAbsent(userName, k -> new ArrayList<>()).add(requestToDeny);
            requests.remove(requestToDeny);

            if(requests.isEmpty()) {
                pendingPermissionRequests.remove(userName);
            }
        }

    }

    @Override
    public Permission getPermission(String username) {
        return sheetPermissionMap.getOrDefault(username, Permission.NONE);
    }

    @Override
    public Map<String, List<PermissionRequest>> getApprovedPermissions() {
        return new HashMap<>(approvedPermissionRequests);
    }

    @Override
    public Map<String, List<PermissionRequest>> getPendingPermissionRequests() {
        return new HashMap<>(pendingPermissionRequests);
    }

    @Override
    public Map<String, List<PermissionRequest>> getDeniedPermissionRequests() {
        return new HashMap<>(deniedPermissionRequests);
    }
}

