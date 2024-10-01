//package loadfilecomp.impl;
//
//import action.line.api.ActionLineController;
//import dto.SheetDto;
//import javafx.fxml.FXML;
//import javafx.scene.control.Button;
//import command.api.Engine;
//import dto.SaveLoadFileDto;
//import javafx.application.Platform;
//import javafx.scene.control.*;
//import javafx.scene.layout.Region;
//import javafx.stage.FileChooser;
//import javafx.stage.Stage;
//import javafx.stage.Modality;
//import javafx.scene.Scene;
//import javafx.scene.layout.VBox;
//import javafx.geometry.Insets;
//import loadfilecomp.api.LoadFileController;
//import spreadsheet.api.SpreadsheetController;
//
//
//import java.io.File;
//
//public class LoadFileControllerImpl implements LoadFileController {
//    @FXML
//    private TextField sheetNameTF;
//    @FXML
//    private Button loadFileButton;
//
//    @FXML
//    private Label statusLabel;
//
//    @FXML
//    private TextField fileTextField;
//
//    private final SpreadsheetController spreadsheetController;
//    private ActionLineController actionLineController;
//
//    private Engine engine;
//    private Stage primaryStage;
//    private Stage loadingStage;
//    private ProgressBar loadingProgressBar;
//
//    public LoadFileControllerImpl(Engine engine, Stage primaryStage, SpreadsheetController spreadsheetController) {
//        this.engine = engine;
//        this.primaryStage = primaryStage;
//        this.spreadsheetController = spreadsheetController;
//    }
//
//
//    @FXML
//    private void initialize() {
//        loadFileButton.setOnAction(event -> handleLoadFile());
//        fileTextField.setEditable(false);
//    }
//
//    @Override
//    public void setActionLineController(ActionLineController actionLineController) {
//        this.actionLineController = actionLineController;
//    }
//
//    @Override
//    public void handleLoadFile() {
//        FileChooser fileChooser = new FileChooser();
//        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("XML Files", "*.xml"));
//        fileChooser.setTitle("Select Spreadsheet XML File");
//        File selectedFile = fileChooser.showOpenDialog(primaryStage);
//
//        if (selectedFile != null) {
//            if (selectedFile.getName().endsWith(".xml")) {
//                showLoadingPopup();
//
//                new Thread(() -> {
//                    try {
//                        // Simulate loading with a delay
//                        updateProgressBar();
//
//                       // SaveLoadFileDto result = engine.loadFile(selectedFile.getAbsolutePath());
//                        Platform.runLater(() -> {
//                            closeLoadingPopup();
//
//                            //if (result.isSucceeded()) {
//                                fileTextField.setText(selectedFile.getAbsolutePath());
//                                SheetDto sheetDto = engine.displayCurrentSpreadsheet();
//                                sheetNameTF.setText(sheetDto.getName());
//                                spreadsheetController.setCurrentSheet(sheetDto);
//                                spreadsheetController.enableEditing();
//                                actionLineController.populateVersionSelector(engine.getLatestVersion());
//                                spreadsheetController.displaySheet(sheetDto);
//                           // } else {
//                             //   showAlert(Alert.AlertType.ERROR, "Error", result.getMessage());
//                                statusLabel.setText("Failed to load file: " + selectedFile.getName());
//                            }
//                        });
//                    } catch (Exception e) {
//                        Platform.runLater(() -> {
//                            closeLoadingPopup();
//                            showAlert(Alert.AlertType.ERROR, "Error", "An unexpected error occurred: " + e.getMessage());
//                            statusLabel.setText("Error loading file:");
//                        });
//                    }
//                }).start();
//            }
//            else
//                showAlert(Alert.AlertType.WARNING, "Error loading File", "Please select an XML file to load.");
//        }
//    }
//
//    @Override
//    public void updateProgressBar() {
//        try {
//            // Initially set progress to 0%
//            Platform.runLater(() -> loadingProgressBar.setProgress(0.0));
//
//            // Wait for 500ms and set progress to 50%
//            Thread.sleep(500);
//            Platform.runLater(() -> loadingProgressBar.setProgress(0.5));
//
//            // Wait for another 1000ms and set progress to 100%
//            Thread.sleep(1000);
//            Platform.runLater(() -> loadingProgressBar.setProgress(1.0));
//
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//    }
//    private void showLoadingPopup() {
//        if (loadingStage == null) {
//            loadingStage = new Stage();
//            loadingStage.initModality(Modality.APPLICATION_MODAL);
//            loadingStage.initOwner(primaryStage);
//
//            Label loadingLabel = new Label("Loading...");
//            loadingProgressBar = new ProgressBar();
//            loadingProgressBar.setPrefWidth(200);
//
//            VBox vbox = new VBox(10, loadingLabel, loadingProgressBar);
//            vbox.setPadding(new Insets(10));
//
//            Scene scene = new Scene(vbox);
//            loadingStage.setScene(scene);
//            loadingStage.setTitle("Loading");
//            loadingStage.setWidth(250);
//            loadingStage.setHeight(100);
//        }
//
//        Platform.runLater(() -> {
//            loadingProgressBar.setProgress(0);  // Start progress at 0
//            loadingStage.show();  // Show the popup
//        });
//    }
//
//    private void closeLoadingPopup() {
//        if (loadingStage != null) {
//            Platform.runLater(() -> loadingStage.close());
//        }
//    }
//
//    private void showAlert(Alert.AlertType alertType, String title, String message) {
//        Alert alert = new Alert(alertType);
//        alert.setTitle(title);
//        alert.setHeaderText(null);
//        alert.setContentText(message);
//
//        // Resize the alert window to fit the content
//        DialogPane dialogPane = alert.getDialogPane();
//        dialogPane.setMinHeight(Region.USE_PREF_SIZE);  // Adjust height to fit content
//        dialogPane.setMinWidth(Region.USE_PREF_SIZE);   // Adjust width to fit content
//
//        alert.showAndWait();
//    }
//
//}
