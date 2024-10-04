package shticell.client.sheetpanel.command.components.formulabuilder.api;

import shticell.client.sheetpanel.action.line.api.ActionLineController;

public interface FormulaBuilderController {
    void setActionLineController(ActionLineController actionLineController);
    void applyFormula();
    void cancelFormula();
}
