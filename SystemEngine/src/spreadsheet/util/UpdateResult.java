package spreadsheet.util;
import spreadsheet.api.SpreadSheet;

public class UpdateResult {
    private final SpreadSheet sheet;
    private final String errorMessage;

    public UpdateResult(SpreadSheet sheet, String errorMessage) {
        this.sheet = sheet;
        this.errorMessage = errorMessage;
    }

    public SpreadSheet getSheet() {
        return sheet;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public boolean isSuccess() {
        return errorMessage == null;
    }
}
