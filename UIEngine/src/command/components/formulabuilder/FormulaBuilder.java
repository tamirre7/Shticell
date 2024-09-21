package command.components.formulabuilder;


import command.api.Engine;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import action.line.impl.ActionLineController;

import java.io.IOException;


public class FormulaBuilder {
    ActionLineController actionLineController;
    Engine engine;

    public void buildFormula() {
        try {
            // Load the FXML file for the formula builder dialog
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/shticellui/formulabuilder/formulabuilder.fxml"));
            Parent root = loader.load();

            // Get the controller instance
          FormulaBuilderController controller = loader.getController();

            // Pass the engine and cell data to the controller
            controller.setup(actionLineController,engine);

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
    public void setEngine(Engine engine) {this.engine = engine;}


}
