package shticellui.misc;

import command.api.Engine;
import dto.SaveLoadFileDto;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import shticellui.skinmanager.SkinManager;

import java.io.File;

public class MiscController {

    @FXML
    private Button saveButton;

    @FXML
    private Button loadButton;

    @FXML
    private ComboBox<String> skinComboBox;
    SkinManager skinManager;

    private Engine engine;
    private Stage primaryStage;

    // Constructor
    public MiscController(Engine engine, Stage primaryStage, SkinManager skinManager) {
        this.engine = engine;
        this.primaryStage = primaryStage;
        this.skinManager = skinManager;
    }

    @FXML
    private void initialize() {
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
    }

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
}
