package dto.permission;

public enum RequestStatus {
    APPROVED,
    DENIED,
    PENDING;

    public static RequestStatus fromString(String requestStatus) {
        return RequestStatus.valueOf(requestStatus.toUpperCase());
    }

}
