package spreadsheet.sheetmanager.impl;

import spreadsheet.api.SpreadSheet;
import dto.permission.Permission;
import spreadsheet.sheetmanager.permissionmanager.api.PermissionManager;
import spreadsheet.sheetmanager.permissionmanager.impl.PermissionManagerImpl;
import spreadsheet.sheetmanager.permissionmanager.permissionrequest.PermissionRequest;
import spreadsheet.sheetmanager.api.SheetManager;
import spreadsheet.sheetmanager.versionmanager.api.VersionManager;
import spreadsheet.sheetmanager.versionmanager.impl.VersionManagerImpl;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class SheetManagerImpl implements SheetManager, Serializable {
    private String sheetName;
    private String uploadedBy;
    private PermissionManager permissionManager;
    private VersionManager versionManager;

    public SheetManagerImpl(String sheetName, String uploadedBy) {
        this.sheetName = sheetName;
        this.uploadedBy = uploadedBy;
        this.permissionManager = new PermissionManagerImpl(uploadedBy);
        this.versionManager = new VersionManagerImpl();
    }

    @Override
    public String getUploadedBy() {
        return uploadedBy;
    }

    @Override
    public String getSheetName() {
        return this.sheetName;
    }

    @Override
    public SpreadSheet getSheetByVersion(int version) {
        return versionManager.getSheetByVersion(version);
    }

    @Override
    public void updateSheetVersion(SpreadSheet sheet) {
        versionManager.updateSheetVersion(sheet);
    }

    @Override
    public int getLatestVersion() {
        return versionManager.getLatestVersion();
    }

    @Override
    public void ApprovePermission(PermissionRequest request) {
        permissionManager.ApprovePermission(request);
    }

    @Override
    public void addPendingPermissionRequest(PermissionRequest request) {
        permissionManager.addPendingPermissionRequest(request);
    }

    @Override
    public void denyPendingRequest(PermissionRequest request) {
        permissionManager.denyPendingRequest(request);
    }

    @Override
    public Permission getPermission(String username) {
        return permissionManager.getPermission(username);
    }

    @Override
    public Map<String, Permission> getApprovedPermissions() {
        return permissionManager.getApprovedPermissions();
    }

    @Override
    public Map<String, List<PermissionRequest>> getPendingPermissionRequests() {
        return permissionManager.getPendingPermissionRequests();
    }

    @Override
    public Map<String, List<PermissionRequest>> getDeniedPermissionRequests() {
        return permissionManager.getDeniedPermissionRequests();
    }
}


