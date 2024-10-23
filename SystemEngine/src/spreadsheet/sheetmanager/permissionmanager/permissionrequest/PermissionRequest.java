package spreadsheet.sheetmanager.permissionmanager.permissionrequest;

import dto.permission.Permission;

import java.util.Objects;

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


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PermissionRequest that = (PermissionRequest) o;
        return id == that.id &&
                permission == that.permission &&
                Objects.equals(requester, that.requester);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, permission, requester);
    }
}
