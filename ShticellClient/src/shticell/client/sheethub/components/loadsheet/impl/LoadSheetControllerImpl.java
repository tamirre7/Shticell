package shticell.client.sheethub.components.loadsheet.impl;

import dto.SheetDto;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.FileChooser;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;
import shticell.client.sheethub.components.avaliable.sheets.api.AvailableSheetsController;
import shticell.client.sheethub.components.loadsheet.api.LoadSheetController;
import shticell.client.util.http.HttpClientUtil;
import shticell.client.util.Constants;

import java.io.File;
import java.io.IOException;

public class LoadSheetControllerImpl {
    @FXML
    private Label greetingLabel;

    private LoadSheetController loadSheetController;
    private AvailableSheetsController availableSheetsController;

    @FXML
    public void initialize() {
        setGreetingLabel();
    }

    @FXML
    public void loadButtonClicked(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("XML Files", "*.xml"));
        fileChooser.setTitle("Select Spreadsheet XML File");
        File selectedFile = fileChooser.showOpenDialog(null);

        if (selectedFile != null) {
            uploadFile(selectedFile);
        }
    }

    private void uploadFile(File file) {
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("file", file.getName(),
                        RequestBody.create(file, MediaType.parse("application/xml")))
                .build();

        Request request = new Request.Builder()
                .url(Constants.LOAD_SHEET_ENDPOINT)
                .post(requestBody)
                .build();

        HttpClientUtil.runAsync(request, new Callback() {
            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseBody = response.body().string();
                    Platform.runLater(() -> {
                        SheetDto newSheet = HttpClientUtil.extractSheetFromResponse(responseBody);
                        availableSheetsController.addSheet(newSheet);
                    });
                } else {
                    Platform.runLater(() ->
                            showAlert(AlertType.ERROR, "Error", "Failed to load file: " + response.message())
                    );
                }
            }

            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Platform.runLater(() ->
                        showAlert(AlertType.ERROR, "Error", "Error: " + e.getMessage())
                );
            }
        });
    }

    private void showAlert(AlertType alertType, String title, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void setGreetingLabel() {
        if (loadSheetController != null) {
            greetingLabel.setText("Hello, " + loadSheetController.getLoggedUserName() + "!");
        }
    }

    public void setLoadSheetController(LoadSheetController loadSheetController) {this.loadSheetController = loadSheetController;}
    public void setAvailableSheetsController(AvailableSheetsController availableSheetsController) {this.availableSheetsController = availableSheetsController;}
}