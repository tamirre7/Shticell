package shticellui.misc;

import command.api.Engine;
import dto.SaveLoadFileDto;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import java.io.File;

public class MiscController {

    @FXML
    private Button saveButton;

    @FXML
    private Button loadButton;

    private Engine engine;
    private Stage primaryStage;

    // Constructor
    public MiscController(Engine engine, Stage primaryStage) {
        this.engine = engine;
        this.primaryStage = primaryStage;
    }

    @FXML
    private void initialize() {
        System.out.println("MiscController initialized");
        if (saveButton != null && loadButton != null) {
            saveButton.setOnAction(event -> handleSaveState());
            loadButton.setOnAction(event -> handleLoadState());
        } else {
            System.out.println("Buttons are not initialized.");
        }
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

    private void showAlert(String title, String message) {
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
