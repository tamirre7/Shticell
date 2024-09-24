package action.line.impl;

import action.line.api.ActionLineController;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import command.api.Engine;
import dto.SheetDto;
import dto.CellDto;
import spreadsheet.api.SpreadsheetController;
import spreadsheet.impl.SpreadsheetControllerImpl;

import java.util.Map;

public class ActionLineControllerImpl implements ActionLineController {

    @FXML
    private TextField cellidTF;
    @FXML
    private TextField originalvalueTF;
    @FXML
    private TextField lastmodverTF;
    @FXML
    private Button updatevalbtn;
    @FXML
    private ComboBox<String> versionSelector;

    private Engine engine;
    private SheetDto currentSheet;
    private SpreadsheetController spreadsheetController;
    private boolean isViewingOldVersion = false;

    public ActionLineControllerImpl() {
    }

    @Override
    public void setEngine(Engine engine) {
        this.engine = engine;
    }

    @FXML
    public void initialize() {
        updatevalbtn.setOnAction(event -> {
            if (!isViewingOldVersion) {
                updateCellValue(null);
            } else {
                showErrorAlert("Read-only Mode", "Cannot update cell in a read-only version.");
            }
        });
        // Listen for version selection changes
        versionSelector.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                loadSpreadsheetVersion(Integer.valueOf(newValue));
            }
        });

    }

    @Override
    public void setSpreadsheetDisplayController(SpreadsheetController spreadsheetController) {
        this.spreadsheetController = spreadsheetController;
    }

    @Override
    public void setCurrentSheet(SheetDto currentSheet) {this.currentSheet = currentSheet;}

    @Override
    public void setCellData(CellDto cellDto, String cellId) {
        cellidTF.setText(cellId);
        if (cellDto != null && isActiveCell(cellDto.getCellId())) {
            originalvalueTF.setText(cellDto.getOriginalValue());
            lastmodverTF.setText(cellDto.getLastModifiedVersion().toString());
        }
        else {
            originalvalueTF.setText("");
            lastmodverTF.setText("");
        }

    }

    private boolean isActiveCell(String cellId) {
        Map<String,CellDto>activeCells = currentSheet.getCells();
        return activeCells.containsKey(cellId);
    }

    @Override
    public void updateCellValue(String preBuildOriginalValue) {
        if (!engine.isFileLoaded())
        {
            showAlert("Error", "A file must be loaded first.");
            return;
        }
        String cellId = cellidTF.getText().toUpperCase();
        String newValue;

        if (preBuildOriginalValue == null)
            newValue = originalvalueTF.getText();
        else
            newValue = preBuildOriginalValue;

        if (cellId != null && !cellId.isEmpty() && newValue != null) {
            try {
                // Update cell in the engine
                currentSheet = engine.updateCellWithSheetVersionUpdate(cellId, newValue);
                spreadsheetController.setCurrentSheet(currentSheet);

                // Update all cells in the display
                spreadsheetController.updateAllCells(currentSheet.getCells());
                int numOfVersions = engine.getLatestVersion();
                populateVersionSelector(numOfVersions);
            } catch (RuntimeException e) {
                showErrorAlert("Error Updating Cell", e.getMessage());
            }
            catch (Exception e) {
                showErrorAlert("Error Updating Cell", e.getMessage());
            }
        } else {
            showErrorAlert("Invalid Input", "Cell ID and value must not be empty.");
        }

    }
    private void showErrorAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void loadSpreadsheetVersion(Integer version) {
        if (!engine.isFileLoaded())
        {
            showAlert("Error", "A file must be loaded first.");
            return;
        }
        // Load the specified version of the spreadsheet
        currentSheet = engine.displaySheetByVersion(version);
        lastmodverTF.setText(version.toString());

        if (!version.equals(engine.getLatestVersion())) {
            originalvalueTF.setDisable(true);
            updatevalbtn.setDisable(true);
            spreadsheetController.displayTemporarySheet(currentSheet, true);
        }
        else {
            originalvalueTF.setDisable(false);
            updatevalbtn.setDisable(false);
            spreadsheetController.setCurrentSheet(currentSheet);
            spreadsheetController.displayOriginalSheet(true);
        }
    }

    @Override
    public void populateVersionSelector(int numOfVersions) {
        versionSelector.getItems().clear();
        for (int i = 1; i <= numOfVersions; i++) {
            versionSelector.getItems().add(String.valueOf(i));
        }
    }
    @Override
    public void clearTextFields()
    {
        cellidTF.clear();
        originalvalueTF.clear();
        lastmodverTF.clear();
    }

    @Override
    public void disableEditing(){
        updatevalbtn.setDisable(true);
        originalvalueTF.setDisable(true);
        versionSelector.setDisable(true);
    }

    @Override
    public void enableEditing(){
        updatevalbtn.setDisable(false);
        originalvalueTF.setDisable(false);
        versionSelector.setDisable(false);
    }
    private void showAlert(String title, String message) {
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}