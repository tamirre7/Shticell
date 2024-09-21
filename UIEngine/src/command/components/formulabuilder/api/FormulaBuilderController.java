package command.components.formulabuilder.api;

import action.line.api.ActionLineController;
import command.api.Engine;

public interface FormulaBuilderController {
    void setup(ActionLineController actionLineController, Engine engine);
    void applyFormula();
    void cancelFormula();
}