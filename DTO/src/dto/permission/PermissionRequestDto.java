package dto.permission;

public class PermissionRequestDto {
    private final int id;
    private final String sheetName;
    private final Permission permissionType;
    private final String message;
    private final String requester;

    public PermissionRequestDto(int id,String sheetName, Permission permissionType, String message, String user) {
        this.id = id;
        this.sheetName = sheetName;
        this.permissionType = permissionType;
        this.message = message;
        this.requester = user;
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
    public String getRequester() {
        return requester;
    }
    public int getId() {
        return id;
    }
}
