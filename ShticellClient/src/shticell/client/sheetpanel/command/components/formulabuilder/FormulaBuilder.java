package shticell.client.sheetpanel.command.components.formulabuilder;



import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import shticell.client.sheetpanel.action.line.api.ActionLineController;
import shticell.client.sheetpanel.command.components.formulabuilder.api.FormulaBuilderController;


import java.io.IOException;

import static shticell.client.util.Constants.FORMULA_BUILDER_FXML_RESOURCE_LOCATION;


public class FormulaBuilder {
    ActionLineController actionLineController;

    public void buildFormula() {
        try {
            // Load the FXML file for the formula builder dialog
            FXMLLoader loader = new FXMLLoader(getClass().getResource(FORMULA_BUILDER_FXML_RESOURCE_LOCATION));
            Parent root = loader.load();

            // Get the controller instance
          FormulaBuilderController controller = loader.getController();
          controller.setActionLineController(actionLineController);

            // Show the formula builder window
            Stage stage = new Stage();
            stage.setTitle("Formula Builder");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void setActionLineController(ActionLineController actionLineController) {this.actionLineController = actionLineController;}
}
