package dto;

public class SaveLoadFileDto {
    private final boolean succeeded;
    private final String message;

    // Constructor
    public SaveLoadFileDto(boolean succeeded, String message) {
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

}
