package dto.permission;

// Data Transfer Object representing information about a user's permission for a specific sheet.
public class PermissionInfoDto {

    private final String userName; // The name of the user who has the permission
    private final Permission permissionName; // The type of permission (e.g., OWNER, READER, WRITER)
    private final String sheetName; // The sheet the permission applies to
    private final RequestStatus status; // Status of the permission request (e.g., pending, approved)

    // Constructs a PermissionInfoDto with the specified details.
    public PermissionInfoDto(String userName, Permission permissionName, String sheetName, RequestStatus status) {
        this.userName = userName;
        this.permissionName = permissionName;
        this.sheetName = sheetName;
        this.status = status;
    }

    // Gets the username associated with this permission.
    public String getUsername() {
        return userName;
    }

    // Gets the type of permission.
    public Permission getPermissionType() {
        return permissionName;
    }

    // Gets the name of the sheet associated with this permission.
    public String getSheetName() {
        return sheetName;
    }

    // Gets the status of the permission request.
    public RequestStatus getStatus() {
        return status;
    }
}
