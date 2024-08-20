package dto;

public class LoadDto {
    private final boolean succeeded;
    private final String message;

    // Constructor
    public LoadDto(boolean succeeded, String message) {
        this.succeeded = succeeded;
        this.message = message;
    }

    // Getter for succeeded
    public boolean isSucceeded() {
        return succeeded;
    }

    // Getter for message
    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return "LoadDto{" +
                "succeeded=" + succeeded +
                ", message='" + message + '\'' +
                '}';
    }
}
