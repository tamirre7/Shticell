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
import shticell.client.sheethub.components.login.api.LoginController;
import shticell.client.sheethub.components.login.impl.LoginControllerImpl;
import shticell.client.util.http.HttpClientUtil;
import shticell.client.util.Constants;

import java.io.File;
import java.io.IOException;

public class LoadSheetControllerImpl implements LoadSheetController {

    @FXML
    private Label greetingLabel;

    private LoginController loginController;
    private AvailableSheetsController availableSheetsController;


    @Override
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

    @Override
    public void uploadFile(File file) {
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("file", file.getName(),
                        RequestBody.create(file, MediaType.parse("application/xml")))
                .build();

        Request request = new Request.Builder()
                .url(Constants.LOAD_SHEET_PAGE)
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
                            showErrorAlert("Error", "Failed to load file: " + response.message())
                    );
                }
            }

            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Platform.runLater(() ->
                        showErrorAlert("Error", "Error: " + e.getMessage())
                );
            }
        });
    }

    private void showErrorAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void setGreetingLabel() {
        if (loginController != null) {
            greetingLabel.setText("Hello, " + loginController.getLoggedUserName() + "!");
        }
    }
    @Override
    public void setLoginSheetController(LoginController loginController) {
        this.loginController = loginController;}

    @Override
    public void setAvailableSheetsController(AvailableSheetsController availableSheetsController) {
        this.availableSheetsController = availableSheetsController;}
}