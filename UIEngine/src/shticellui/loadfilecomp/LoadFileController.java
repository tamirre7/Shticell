package shticellui.loadfilecomp;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import command.api.Engine;
import dto.SaveLoadFileDto;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.Modality;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.geometry.Insets;

import java.io.File;

public class LoadFileController {

    @FXML
    private Button loadFileButton;

    @FXML
    private Label statusLabel;

    @FXML
    private ProgressBar progressBar;

    private Engine engine;
    private Stage primaryStage;
    private Stage loadingStage; // New Stage for loading popup

    // Constructor
    public LoadFileController(Engine engine, Stage primaryStage) {
        this.engine = engine;
        this.primaryStage = primaryStage;
    }

    @FXML
    private void initialize() {
        // Set up the button action
        loadFileButton.setOnAction(event -> handleLoadFile());
        progressBar.setVisible(false); // Initially hide the progress bar
    }

    private void handleLoadFile() {
        // Open a FileChooser dialog to let the user select an XML file
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("XML Files", "*.xml"));
        fileChooser.setTitle("Select Spreadsheet XML File");
        File selectedFile = fileChooser.showOpenDialog(primaryStage);

        if (selectedFile != null) {
            // Show the loading popup
            showLoadingPopup();

            // Create a Task to load the file in the background
            Task<SaveLoadFileDto> loadFileTask = new Task<>() {
                @Override
                protected SaveLoadFileDto call() throws Exception {
                    updateMessage("Loading...");
                    updateProgress(0, 100);

                    // Simulate loading with sleep
                    Thread.sleep(500); // Simulate progress
                    updateProgress(50, 100);
                    Thread.sleep(1000); // Simulate progress
                    updateProgress(100, 100);

                    // Perform the actual file loading
                    return engine.loadFile(selectedFile.getAbsolutePath());
                }
            };

            // Update UI based on Task results
            loadFileTask.setOnSucceeded(event -> {
                SaveLoadFileDto result = loadFileTask.getValue();
                closeLoadingPopup();

                if (result.isSucceeded()) {
                    showAlert(Alert.AlertType.INFORMATION, "File Loaded", "The file was loaded successfully.");
                    statusLabel.setText("Loaded file: " + selectedFile.getName());
                } else {
                    showAlert(Alert.AlertType.ERROR, "Error", result.getMessage());
                    statusLabel.setText("Failed to load file: " + selectedFile.getName());
                }
            });

            loadFileTask.setOnFailed(event -> {
                closeLoadingPopup();
                showAlert(Alert.AlertType.ERROR, "Error", "An unexpected error occurred.");
                statusLabel.setText("Error loading file: " + selectedFile.getName());
            });

            progressBar.progressProperty().bind(loadFileTask.progressProperty());

            // Start the task in a new thread
            new Thread(loadFileTask).start();
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
            loadingProgressBar.setPrefWidth(200); // Set a preferred width for the ProgressBar

            VBox vbox = new VBox(10, loadingLabel, loadingProgressBar);
            vbox.setPadding(new Insets(10));
            vbox.setPrefWidth(250); // Set a preferred width for the VBox

            Scene scene = new Scene(vbox);
            loadingStage.setScene(scene);
            loadingStage.setTitle("Loading");
            loadingStage.setWidth(250); // Set a width for the Stage
            loadingStage.setHeight(100); // Set a height for the Stage
        }

        // Bind the popup's progress bar to the main controller's progress bar
        ProgressBar loadingProgressBar = (ProgressBar) ((VBox) loadingStage.getScene().getRoot()).getChildren().get(1);
        loadingProgressBar.progressProperty().bind(progressBar.progressProperty());

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
