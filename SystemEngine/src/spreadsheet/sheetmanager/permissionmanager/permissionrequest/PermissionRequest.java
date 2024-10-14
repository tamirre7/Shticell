package spreadsheet.sheetmanager.permissionmanager.permissionrequest;

import dto.permission.Permission;

public class PermissionRequest {
    private final int id;
    private final Permission permission;
    private String message;
    private final String requester;

    public PermissionRequest(int id,Permission permission,String requester) {
        this.id = id;
        this.permission = permission;
        this.requester = requester;
    }
    public Permission getPermission() {
        return permission;
    }
    public void setMessage(final String message) {
        this.message = message;
    }
    public String getMessage() {
        return message;
    }

    public String getRequester() {
        return requester;
    }

    public int getId() {
        return id;
    }
}
