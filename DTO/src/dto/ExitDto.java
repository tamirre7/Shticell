package dto;

public class ExitDto {
    private final String message;

    // Constructor
    public ExitDto(String message) {
        this.message = message;
    }

    // Getter
    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return "ExitDto{" +
                "message='" + message + '\'' +
                '}';
    }
}
