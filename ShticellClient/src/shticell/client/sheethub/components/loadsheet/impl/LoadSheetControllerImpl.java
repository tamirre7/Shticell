package shticell.client.sheethub.components.loadsheet.impl;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.stage.FileChooser;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;
import shticell.client.sheethub.components.loadsheet.api.LoadSheetController;
import shticell.client.sheethub.components.login.api.LoginController;
import shticell.client.util.http.HttpClientUtil;
import shticell.client.util.Constants;

import java.io.File;
import java.io.IOException;

import static shticell.client.util.http.HttpClientUtil.showAlert;

public class LoadSheetControllerImpl implements LoadSheetController {

    @FXML
    private Label greetingLabel;

    private LoginController loginController;



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
                .url(Constants.LOAD_SHEET)
                .post(requestBody)
                .build();

        HttpClientUtil.runAsync(request, new Callback() {
            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (!response.isSuccessful()) {
                    String errorMessage = response.body() != null ? response.body().string() : response.message();
                    Platform.runLater(() -> showAlert("Error", "Failed to load file: \n" + errorMessage)
                    );
                }
            }

            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Platform.runLater(() ->
                        showAlert("Error", "Error: \n" + e.getMessage())
                );
            }
        });
    }

    @Override
    public void setGreetingLabel() {
        if (loginController != null) {
            greetingLabel.setText("Hello, " + loginController.getLoggedUserName() + "!");
        }
    }
    @Override
    public void setLoginController(LoginController loginController) {
        this.loginController = loginController;}

}