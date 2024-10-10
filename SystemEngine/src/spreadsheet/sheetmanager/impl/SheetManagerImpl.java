package spreadsheet.sheetmanager.impl;

import spreadsheet.api.SpreadSheet;
import dto.Permission;
import spreadsheet.sheetmanager.Permission.PermissionRequest;
import spreadsheet.sheetmanager.api.SheetManager;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class SheetManagerImpl implements SheetManager, Serializable {
    private String sheetName;
    private Map<Integer, SpreadSheet> sheetVersionMap = new HashMap();
    private int latestVersion = 0;
    private String uploadedBy;
    private Map<String, Permission> approvedPermissionMap = new HashMap<>();
    private Map<String, PermissionRequest> pendingPermissionRequests = new HashMap<>();

    public SheetManagerImpl(String sheetName,String uploadedBy) {
        this.sheetName = sheetName;
        this.uploadedBy = uploadedBy;
        this.approvedPermissionMap.put(uploadedBy, Permission.OWNER);
    }
    @Override
    public String getUploadedBy(){return uploadedBy;}
    @Override
    public String getSheetName() {return this.sheetName;}

    @Override
    public SpreadSheet getSheetByVersion(int version) {return sheetVersionMap.get(version);}

    @Override
    public void updateSheetVersion(SpreadSheet sheet) {
        latestVersion +=1;
        sheetVersionMap.put(latestVersion, sheet);
    }

    @Override
    public int getLatestVersion() {return latestVersion;}

    @Override
    public void ApprovePermission(String username) {
       PermissionRequest request = pendingPermissionRequests.get(username);
       Permission permission = request.getPermission();
       approvedPermissionMap.put(username, permission);
       pendingPermissionRequests.remove(username);
    }
    @Override
    public void addPendingPermissionRequest(String username,PermissionRequest request){
        pendingPermissionRequests.put(username, request);

    }
    @Override
    public void removePendingRequest(String username)
    {
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
    public Map<String,PermissionRequest>getPendingPermissionRequests(){
        return new HashMap<>(pendingPermissionRequests);
    }
}

