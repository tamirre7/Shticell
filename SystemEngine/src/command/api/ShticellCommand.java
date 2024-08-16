package command.api;

import spreadsheet.api.SpreadSheet;

public interface ShticellCommand {
    void execute(SpreadSheet spreadSheet);
}