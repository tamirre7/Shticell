package dto.permission;

public enum RequestStatus {
    APPROVED,
    REJECTED,
    PENDING;

    public static RequestStatus fromString(String requestStatus) {
        return RequestStatus.valueOf(requestStatus.toUpperCase());
    }

}
