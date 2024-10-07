package shticell.client.sheetpanel.command.components.formulabuilder.api;

import shticell.client.sheetpanel.action.line.api.ActionLineController;
import shticell.client.sheetpanel.spreadsheet.api.SpreadsheetController;

public interface FormulaBuilderController {
    void setActionLineController(ActionLineController actionLineController);
    void applyFormula();
    void cancelFormula();
    void setSpreadsheetController(SpreadsheetController spreadsheetController);
}
