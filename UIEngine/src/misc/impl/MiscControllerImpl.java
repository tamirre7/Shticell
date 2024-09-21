package misc.impl;

import action.line.impl.ActionLineController;
import command.api.Engine;
import dto.SaveLoadFileDto;
import dto.SheetDto;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import skinmanager.SkinManager;
import spreadsheet.impl.SpreadsheetControllerImpl;

import java.io.File;

public class MiscControllerImpl {

    @FXML
    private Button saveButton;

    @FXML
    private CheckBox animationsCheckBox;

    @FXML
    private Button loadButton;

    @FXML
    private ComboBox<String> skinComboBox;
    SkinManager skinManager;

    private Engine engine;
    private Stage primaryStage;
    private SpreadsheetControllerImpl spreadsheetControllerImpl;
    private ActionLineController actionLineController;

    // Constructor
    public MiscController(Engine engine, Stage primaryStage, SkinManager skinManager, SpreadsheetControllerImpl spreadsheetControllerImpl, ActionLineController actionLineController) {
        this.engine = engine;
        this.primaryStage = primaryStage;
        this.skinManager = skinManager;
        this.spreadsheetControllerImpl = spreadsheetControllerImpl;
        this.actionLineController = actionLineController;
    }

    @FXML
    private void initialize() {
        animationsCheckBox.setSelected(true);

        // Add skin options
        skinComboBox.getItems().addAll("Default", "Dark", "Colorful");
        skinComboBox.setValue("Default"); // Set default skin

        skinComboBox.setOnAction(event -> {
            String selectedSkin = skinComboBox.getValue();
            if (selectedSkin != null) {
                applySkin(selectedSkin);
            }
        });

        saveButton.setOnAction(event -> handleSaveState());
        loadButton.setOnAction(event -> handleLoadState());
    }

    public boolean areAnimationsEnabled() {
        return animationsCheckBox.isSelected();
    }

    @Override
    private void handleSaveState() {
        if (!engine.isFileLoaded())
        {
            showAlert("Error", "A file must be loaded before saving.");
            return;
        }
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Serialized Files", "*.ser"));
        fileChooser.setTitle("Save System State");
        File file = fileChooser.showSaveDialog(primaryStage);

        if (file != null) {
            SaveLoadFileDto result = engine.saveState(file.getAbsolutePath());
            if (result.isSucceeded()) {
                showAlert("Success", result.getMessage());
            } else {
                showAlert("Error", result.getMessage());
            }
        }
    }

    @Override
    private void handleLoadState() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Serialized Files", "*.ser"));
        fileChooser.setTitle("Load System State");
        File file = fileChooser.showOpenDialog(primaryStage);

        if (file != null) {
            SaveLoadFileDto result = engine.loadSavedState(file.getAbsolutePath());
            if (result.isSucceeded()) {
                showAlert("Success", result.getMessage());
            } else {
                showAlert("Error", result.getMessage());
            }
        }
        SheetDto sheetDto = engine.displayCurrentSpreadsheet();
        actionLineController.populateVersionSelector(engine.getLatestVersion());
        spreadsheetControllerImpl.setCurrentSheet(sheetDto);
        spreadsheetControllerImpl.enableEditing();
        spreadsheetControllerImpl.displaySheet(sheetDto);
    }

    @Override
    private void applySkin(String skinFileName) {
        skinManager.applySkin(primaryStage.getScene(), skinFileName);
    }

    private void showAlert(String title, String message) {
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @Override
    public void disableEditing() {
        saveButton.setDisable(true);
    }

    @Override
    public void enableEditing() {
        saveButton.setDisable(false);
    }
}
