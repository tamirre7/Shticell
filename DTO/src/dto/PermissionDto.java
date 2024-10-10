package dto;


public class PermissionDto {
    private final String userName; // The name of the user who has the permission
    private final Permission permissionName; // The type of permission (e.g., READ, WRITE)
    private final String sheetName; // The sheet the permission applies to
    private final boolean isPending; // Status of the permission (e.g., pending or approved)

    public PermissionDto(String userName, Permission permissionName, String sheetName, boolean isPending) {
        this.userName = userName;
        this.permissionName = permissionName;
        this.sheetName = sheetName;
        this.isPending = isPending;
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

    public boolean getIsPending() {
        return isPending;
    }


}