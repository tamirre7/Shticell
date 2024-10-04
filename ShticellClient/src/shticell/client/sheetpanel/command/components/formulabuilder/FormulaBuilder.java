package shticell.client.sheetpanel.command.components.formulabuilder;



import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;


import java.io.IOException;


public class FormulaBuilder {

    public void buildFormula() {
        try {
            // Load the FXML file for the formula builder dialog
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/command/components/formulabuilder/formulabuilder.fxml"));
            Parent root = loader.load();

            // Get the controller instance
       //   FormulaBuilderController controller = loader.getController();

            // Show the formula builder window
            Stage stage = new Stage();
            stage.setTitle("Formula Builder");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
