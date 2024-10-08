package command.components.formulabuilder.api;

import action.line.api.ActionLineController;
import command.api.Engine;

public interface FormulaBuilderController {
    void setActionLineController(ActionLineController actionLineController);
    void applyFormula();
    void cancelFormula();
}