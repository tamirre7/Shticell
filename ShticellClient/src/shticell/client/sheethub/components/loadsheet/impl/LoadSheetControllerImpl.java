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
import static shticell.client.util.http.HttpClientUtil.showInfoAlert;

public class LoadSheetControllerImpl implements LoadSheetController {

    @FXML
    private Label greetingLabel; // Displays a greeting message to the user.

    private LoginController loginController; // Manages user login information.

    @Override
    @FXML
    // Handles the action when the load button is clicked.
    public void loadButtonClicked(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("XML Files", "*.xml"));
        fileChooser.setTitle("Select Spreadsheet XML File");
        File selectedFile = fileChooser.showOpenDialog(null); // Opens a file dialog for XML file selection.

        if (selectedFile != null) {
            uploadFile(selectedFile); // Uploads the selected file.
        }
    }

    @Override
// Uploads the selected XML file to the server.
    public void uploadFile(File file) {
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("file", file.getName(),
                        RequestBody.create(file, MediaType.parse("application/xml"))) // Prepares the request body.
                .build();

        Request request = new Request.Builder()
                .url(Constants.LOAD_SHEET)
                .post(requestBody)
                .build(); // Builds the HTTP request for file upload.

        HttpClientUtil.runAsync(request, new Callback() {
            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) {
                try {
                    if (!response.isSuccessful()) {
                        String errorMessage = response.body() != null ? response.body().string() : response.message();
                        Platform.runLater(() -> showAlert("Error", "Failed to load file: \n" + errorMessage));
                    } else {
                        Platform.runLater(() -> showInfoAlert("Load Complete", "File loaded successfully!")); // Displays success message.
                    }
                } catch (IOException e) {
                    Platform.runLater(() -> showAlert("Error", "An error occurred while reading the response: \n" + e.getMessage()));
                } finally {
                    response.close(); // Ensure response is closed to avoid connection leaks
                }
            }

            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Platform.runLater(() -> showAlert("Error", "Error: \n" + e.getMessage())); // Handles request failure.
            }
        });
    }
    @Override
    // Sets the greeting label with the user's name.
    public void setGreetingLabel() {
        if (loginController != null) {
            greetingLabel.setText("Hello, " + loginController.getLoggedUserName() + "!"); // Updates greeting message.
        }
    }

    @Override
    // Sets the login controller to manage user login information.
    public void setLoginController(LoginController loginController) {
        this.loginController = loginController;
    }
}
