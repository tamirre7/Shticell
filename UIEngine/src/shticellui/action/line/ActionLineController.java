package shticellui.action.line;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import command.api.Engine;
import dto.SheetDto;
import dto.CellDto;
import shticellui.spreadsheet.SpreadsheetDisplayController;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

import java.util.Map;

public class ActionLineController {

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
    private SpreadsheetDisplayController spreadsheetDisplayController;
    private boolean isViewingOldVersion = false;

    public ActionLineController() {
    }

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

    public void setSpreadsheetDisplayController(SpreadsheetDisplayController spreadsheetDisplayController) {
        this.spreadsheetDisplayController = spreadsheetDisplayController;
    }
    public void setCurrentSheet(SheetDto currentSheet) {this.currentSheet = currentSheet;}

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
        CellDto cell = currentSheet.getCells().get(cellId);
        Map<String,CellDto>activeCells = currentSheet.getCells();
        return activeCells.containsKey(cellId);
    }

    public void updateCellValue(String preBuildOriginalValue) {
        String cellId = cellidTF.getText();
        String newValue;

        if (preBuildOriginalValue == null)
            newValue = originalvalueTF.getText();
        else
            newValue = preBuildOriginalValue;





        if (cellId != null && !cellId.isEmpty() && newValue != null) {
            try {
                // Update cell in the engine
                currentSheet = engine.updateCell(cellId, newValue);
                spreadsheetDisplayController.setCurrentSheet(currentSheet);

                // Update all cells in the display
                spreadsheetDisplayController.updateAllCells(currentSheet.getCells());
                String[] availableVersions = engine.getAvailableVersions();
                populateVersionSelector(availableVersions);
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
        // Load the specified version of the spreadsheet
        currentSheet = engine.displaySheetByVersion(version);
        lastmodverTF.setText(version.toString());

        if (!version.equals(engine.getLatestVersion())) {
            originalvalueTF.setDisable(true);
            updatevalbtn.setDisable(true);
            spreadsheetDisplayController.displayTemporarySheet(currentSheet, true);
        }
        else {
            originalvalueTF.setDisable(false);
            updatevalbtn.setDisable(false);
            spreadsheetDisplayController.setCurrentSheet(currentSheet);
            spreadsheetDisplayController.displayOriginalSheet(true);
        }
    }

    public void populateVersionSelector(String[] availableVersions) {
        versionSelector.getItems().clear();
        versionSelector.getItems().addAll(availableVersions);
    }
    public void clearTextFields()
    {
        cellidTF.clear();
        originalvalueTF.clear();
        lastmodverTF.clear();
    }
    public void disableEditing(){
        updatevalbtn.setDisable(true);
        originalvalueTF.setDisable(true);
        versionSelector.setDisable(true);
    }
    public void enableEditing(){
        updatevalbtn.setDisable(false);
        originalvalueTF.setDisable(false);
        versionSelector.setDisable(false);
    }

}