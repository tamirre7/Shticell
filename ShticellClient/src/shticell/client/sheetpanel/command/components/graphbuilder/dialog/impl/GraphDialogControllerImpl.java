package shticell.client.sheetpanel.command.components.graphbuilder.dialog.impl;

import shticell.client.sheetpanel.command.components.graphbuilder.dialog.api.GraphDialogController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class GraphDialogControllerImpl implements GraphDialogController {

    @FXML
    private TextField xTopCellField;

    @FXML
    private TextField xBottomCellField;

    @FXML
    private TextField yTopCellField;

    @FXML
    private TextField yBottomCellField;

    private boolean isConfirmed = false;

    @Override
    public String getXTopCell() {
        return xTopCellField.getText();
    }

    @Override
    public String getXBottomCell() {
        return xBottomCellField.getText();
    }

    @Override
    public String getYTopCell() {
        return yTopCellField.getText();
    }

    @Override
    public String getYBottomCell() {
        return yBottomCellField.getText();
    }

    @Override
    public void CloseDialog() {
        isConfirmed = true;
        Stage stage = (Stage) xTopCellField.getScene().getWindow();
        stage.close();
    }
    @Override
    public boolean isConfirmed() {
        return isConfirmed;
    }

}
