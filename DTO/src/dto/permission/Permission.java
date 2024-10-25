package dto.permission;

// Enum representing different levels of permissions for users.
public enum Permission {
    // Full control over the resource.
    OWNER,

    // Read-only access to the resource.
    READER,

    // Read and write access to the resource.
    WRITER,

    // No permissions.
    NONE;

    // Converts a string to a corresponding Permission enum value.
    // If the string does not match any defined permission, defaults to NONE.
    public static Permission fromString(String permissionString) {
        try {
            return Permission.valueOf(permissionString.toUpperCase());
        } catch (IllegalArgumentException e) {
            return NONE;
        }
    }
}
