package shticellui.graphbuilder;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class GraphDialogController {

    @FXML
    private TextField xTopCellField;

    @FXML
    private TextField xBottomCellField;

    @FXML
    private TextField yTopCellField;

    @FXML
    private TextField yBottomCellField;

    @FXML
    private ComboBox<String> graphTypeComboBox;

    private boolean isConfirmed = false;


    public String getXTopCell() {
        return xTopCellField.getText();
    }

    public String getXBottomCell() {
        return xBottomCellField.getText();
    }

    public String getYTopCell() {
        return yTopCellField.getText();
    }

    public String getYBottomCell() {
        return yBottomCellField.getText();
    }

    public String getGraphType() {
        return graphTypeComboBox.getValue();
    }

    public void CloseDialog(ActionEvent actionEvent) {
        isConfirmed = true;
        Stage stage = (Stage) xTopCellField.getScene().getWindow();
        stage.close();
    }

    public boolean isConfirmed() {
        return isConfirmed;
    }

}
