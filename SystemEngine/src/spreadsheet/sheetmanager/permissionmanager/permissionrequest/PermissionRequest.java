package spreadsheet.sheetmanager.permissionmanager.permissionrequest;

import dto.permission.Permission;

import java.util.Objects;

// Represents a request for permission, with a unique ID, requested permission type,
// requester information, and an optional message.
public class PermissionRequest {

    private final int id; // Unique identifier for the permission request.
    private final Permission permission;
    private String message;
    private final String requester;

    // Constructs a PermissionRequest with the specified ID, permission type, and requester.
    public PermissionRequest(int id, Permission permission, String requester) {
        this.id = id;
        this.permission = permission;
        this.requester = requester;
    }

    // Retrieves the type of permission requested.
    public Permission getPermission() {
        return permission;
    }

    // Sets an optional message for the permission request.
    public void setMessage(final String message) {
        this.message = message;
    }

    // Retrieves the message associated with the request.
    public String getMessage() {
        return message;
    }

    // Retrieves the requester of the permission.
    public String getRequester() {
        return requester;
    }

    // Retrieves the unique ID of the permission request.
    public int getId() {
        return id;
    }

    // Determines if this PermissionRequest is equal to another object based on ID,
    // permission type, and requester.
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PermissionRequest that = (PermissionRequest) o;
        return id == that.id &&
                permission == that.permission &&
                Objects.equals(requester, that.requester);
    }

    // Generates a hash code based on ID, permission type, and requester.
    @Override
    public int hashCode() {
        return Objects.hash(id, permission, requester);
    }
}
