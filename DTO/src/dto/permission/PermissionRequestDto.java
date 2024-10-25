package dto.permission;

// Data Transfer Object representing a request for permission on a specific sheet.
public class PermissionRequestDto {

    private final int id; // Unique identifier for the permission request
    private final String sheetName; // Name of the sheet for which permission is requested
    private final Permission permissionType; // Type of permission requested (e.g., OWNER, READER, WRITER)
    private final String message; // Additional message or reason for the permission request
    private final String requester; // Name of the user making the request

    // Constructs a PermissionRequestDto with the specified details.
    public PermissionRequestDto(int id, String sheetName, Permission permissionType, String message, String user) {
        this.id = id;
        this.sheetName = sheetName;
        this.permissionType = permissionType;
        this.message = message;
        this.requester = user;
    }

    // Gets the name of the sheet for which permission is requested.
    public String getSheetName() {
        return sheetName;
    }

    // Gets the type of permission requested.
    public Permission getPermissionType() {
        return permissionType;
    }

    // Gets the message or reason provided for the permission request.
    public String getMessage() {
        return message;
    }

    // Gets the name of the user making the request.
    public String getRequester() {
        return requester;
    }

    // Gets the unique identifier for this permission request.
    public int getId() {
        return id;
    }
}
