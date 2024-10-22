package shticell.client.sheetpanel.command.components.graphbuilder.builder.api;

import shticell.client.sheetpanel.spreadsheet.api.SpreadsheetController;

public interface GraphBuilderController {
    void buildGraph();
    void setSpreadsheetController(SpreadsheetController spreadsheetController);
    void enableGraphBuild();
    void disableGraphBuild();

}