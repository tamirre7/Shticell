package dto;

public class SaveLoadFileDto {
    private final boolean succeeded; // Indicates whether the save/load operation was successful
    private final String message;     // Message providing additional information about the operation

    // Constructor to initialize the SaveLoadFileDto with success status and message
    public SaveLoadFileDto(boolean succeeded, String message) {
        this.succeeded = succeeded;
        this.message = message;
    }

    // Getter for succeeded
    public boolean isSucceeded() {
        return succeeded; // Returns whether the operation succeeded
    }

    // Getter for message
    public String getMessage() {
        return message; // Returns the message associated with the operation
    }
}
