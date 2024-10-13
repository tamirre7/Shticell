package dto.permission;


public class PermissionInfoDto {
    private final String userName; // The name of the user who has the permission
    private final Permission permissionName; // The type of permission (e.g., READ, WRITE)
    private final String sheetName; // The sheet the permission applies to
    private final RequestStatus status; // Status of the permission (e.g., pending or approved)

    public PermissionInfoDto(String userName, Permission permissionName, String sheetName, RequestStatus status) {
        this.userName = userName;
        this.permissionName = permissionName;
        this.sheetName = sheetName;
        this.status = status;
    }

    public String getUsername() {
        return userName;
    }

    public Permission getPermissionType() {
        return permissionName;
    }

    public String getSheetName() {
        return sheetName;
    }

    public RequestStatus getStatus() {
        return status;
    }


}