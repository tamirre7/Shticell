package dto;

public enum Permission {
    OWNER,
    READER,
    WRITER,
    NONE;

    public static Permission fromString(String permissionString) {
        try {
            return Permission.valueOf(permissionString.toUpperCase());
        } catch (IllegalArgumentException e) {
            return NONE;
        }
    }
}
