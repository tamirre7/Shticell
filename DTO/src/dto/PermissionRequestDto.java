package dto;

public class PermissionRequestDto {
    private final String sheetName;
    private final Permission permissionType;
    private final String message;

    public PermissionRequestDto(String sheetName, Permission permissionType, String message) {
        this.sheetName = sheetName;
        this.permissionType = permissionType;
        this.message = message;
    }
    public String getSheetName() {
        return sheetName;
    }
    public Permission getPermissionType() {
        return permissionType;
    }
    public String getMessage() {
        return message;
    }
}
