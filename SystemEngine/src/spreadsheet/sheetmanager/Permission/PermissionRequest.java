package spreadsheet.sheetmanager.Permission;

import dto.Permission;

public class PermissionRequest {
    private final Permission permission;
    private String message;
    private final String requester;

    public PermissionRequest(Permission permission,String requester) {
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
}
