package dto.permission;

public class PermissionResponseDto {

    private final PermissionRequestDto permissionRequestDto;
    private final boolean isApproved;

    public PermissionResponseDto( PermissionRequestDto permissionRequestDto, boolean isApproved) {
        this.permissionRequestDto = permissionRequestDto;
        this.isApproved = isApproved;
    }

    public boolean isApproved() {
        return isApproved;
    }
    public PermissionRequestDto getPermissionRequestDto() {
        return permissionRequestDto;
    }
}
