package dto.permission;

// Data Transfer Object for permission request responses.
public class PermissionResponseDto {

    private final PermissionRequestDto permissionRequestDto; // Original permission request
    private final boolean isApproved; // Indicates if the request is approved

    // Constructs a PermissionResponseDto with the specified permission request and approval status.
    public PermissionResponseDto(PermissionRequestDto permissionRequestDto, boolean isApproved) {
        this.permissionRequestDto = permissionRequestDto;
        this.isApproved = isApproved;
    }

    // Checks if the permission request is approved.
    public boolean isApproved() {
        return isApproved;
    }

    // Gets the original permission request associated with this response.
    public PermissionRequestDto getPermissionRequestDto() {
        return permissionRequestDto;
    }
}
