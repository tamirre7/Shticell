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

// Implementation of the SheetManager interface, managing a spreadsheet's permissions and versioning.
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

    // Returns the username of the person who uploaded the spreadsheet.
    @Override
    public String getUploadedBy() {
        return uploadedBy;
    }

    // Returns the name of the spreadsheet.
    @Override
    public String getSheetName() {
        return this.sheetName;
    }

    // Retrieves a spreadsheet by its version number.
    @Override
    public SpreadSheet getSheetByVersion(int version) {
        return versionManager.getSheetByVersion(version);
    }

    // Updates the version of the specified spreadsheet.
    @Override
    public void updateSheetVersion(SpreadSheet sheet) {
        versionManager.updateSheetVersion(sheet);
    }

    // Returns the latest version number of the spreadsheet.
    @Override
    public int getLatestVersion() {
        return versionManager.getLatestVersion();
    }

    // Approves a specified permission request.
    @Override
    public void approvePermission(PermissionRequest request) {
        permissionManager.approvePermission(request);
    }

    // Adds a new pending permission request.
    @Override
    public void addPendingPermissionRequest(PermissionRequest request) {
        permissionManager.addPendingPermissionRequest(request);
    }

    // Denies a specified pending permission request.
    @Override
    public void denyPendingRequest(PermissionRequest request) {
        permissionManager.denyPendingRequest(request);
    }

    // Retrieves the permission level for a specified user.
    @Override
    public Permission getPermission(String username) {
        return permissionManager.getPermission(username);
    }

    // Returns a map of all approved permission requests for the spreadsheet.
    @Override
    public Map<String, List<PermissionRequest>> getApprovedPermissions() {
        return permissionManager.getApprovedPermissions();
    }

    // Returns a map of all pending permission requests for the spreadsheet.
    @Override
    public Map<String, List<PermissionRequest>> getPendingPermissionRequests() {
        return permissionManager.getPendingPermissionRequests();
    }

    // Returns a map of all denied permission requests for the spreadsheet.
    @Override
    public Map<String, List<PermissionRequest>> getDeniedPermissionRequests() {
        return permissionManager.getDeniedPermissionRequests();
    }
}
