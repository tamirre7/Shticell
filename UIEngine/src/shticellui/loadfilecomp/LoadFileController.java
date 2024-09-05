package shticellui.loadfilecomp;

import dto.SheetDto;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import command.api.Engine;
import dto.SaveLoadFileDto;
import javafx.application.Platform;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.Modality;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.geometry.Insets;
import shticellui.spreadsheet.SpreadsheetDisplayController;

import java.io.File;

public class LoadFileController {

    @FXML
    private Button loadFileButton;

    @FXML
    private Label statusLabel;

    @FXML
    private ProgressBar progressBar;

    @FXML
    private TextField fileTextField; // Add the TextField

    private SpreadsheetDisplayController spreadsheetDisplayController;

    private Engine engine;
    private Stage primaryStage;
    private Stage loadingStage;

    public LoadFileController(Engine engine, Stage primaryStage, SpreadsheetDisplayController spreadsheetDisplayController) {
        this.engine = engine;
        this.primaryStage = primaryStage;
        this.spreadsheetDisplayController = spreadsheetDisplayController; // Now assigned correctly
    }


    @FXML
    private void initialize() {
        loadFileButton.setOnAction(event -> handleLoadFile());
        progressBar.setVisible(false);
        fileTextField.setEditable(false); // Make the TextField non-editable
    }

    private void handleLoadFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("XML Files", "*.xml"));
        fileChooser.setTitle("Select Spreadsheet XML File");
        File selectedFile = fileChooser.showOpenDialog(primaryStage);

        if (selectedFile != null) {
            showLoadingPopup();

            try {
                SaveLoadFileDto result = engine.loadFile(selectedFile.getAbsolutePath());
                closeLoadingPopup();

                if (result.isSucceeded()) {
                    fileTextField.setText(selectedFile.getAbsolutePath()); // Update the TextField with the file path
                    SheetDto sheetDto = engine.displayCurrentSpreadsheet();

                    // Pass the SheetDto to the SpreadsheetDisplayController
                    spreadsheetDisplayController.displaySheet(sheetDto);
                } else {
                    showAlert(Alert.AlertType.ERROR, "Error", result.getMessage());
                    statusLabel.setText("Failed to load file: " + selectedFile.getName());
                }
            } catch (Exception e) {
                closeLoadingPopup();
                showAlert(Alert.AlertType.ERROR, "Error", "An unexpected error occurred: " + e.getMessage());
                statusLabel.setText("Error loading file:");
            }

        } else {
            showAlert(Alert.AlertType.WARNING, "No File Selected", "Please select an XML file to load.");
        }
    }

    private void showLoadingPopup() {
        if (loadingStage == null) {
            loadingStage = new Stage();
            loadingStage.initModality(Modality.APPLICATION_MODAL);
            loadingStage.initOwner(primaryStage);

            Label loadingLabel = new Label("Loading...");
            ProgressBar loadingProgressBar = new ProgressBar();
            loadingProgressBar.setPrefWidth(200);

            VBox vbox = new VBox(10, loadingLabel, loadingProgressBar);
            vbox.setPadding(new Insets(10));

            Scene scene = new Scene(vbox);
            loadingStage.setScene(scene);
            loadingStage.setTitle("Loading");
            loadingStage.setWidth(250);
            loadingStage.setHeight(100);
        }

        Platform.runLater(() -> loadingStage.show());
    }

    private void closeLoadingPopup() {
        if (loadingStage != null) {
            Platform.runLater(loadingStage::close);
        }
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
