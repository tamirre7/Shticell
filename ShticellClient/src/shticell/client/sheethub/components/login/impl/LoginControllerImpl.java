package shticell.client.sheethub.components.login.impl;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;
import shticell.client.sheethub.components.login.api.LoginController;
import shticell.client.sheethub.main.SheetHubMainController;
import shticell.client.util.http.HttpClientUtil;
import shticell.client.util.Constants;

import java.io.IOException;

public class LoginControllerImpl implements LoginController {

    @FXML
    public TextField userNameTextField; // TextField for user input of the username.

    @FXML
    public Label errorLabel; // Label to display error messages.

    private SheetHubMainController sheetHubMainController; // Reference to the main controller for switching views.
    private final StringProperty errorMessageProperty = new SimpleStringProperty(); // Property for binding error messages.
    private String loggedUserName; // Stores the logged-in user's name.

    @FXML
    public void initialize() {
        // Binds the error label to the error message property for dynamic updates.
        errorLabel.textProperty().bind(errorMessageProperty);
    }

    @FXML
    @Override
    public void loginButtonClicked(ActionEvent event) {
        String userName = userNameTextField.getText(); // Retrieve the entered username.

        // Check if the username is empty and update the error message if so.
        if (userName.isEmpty()) {
            errorMessageProperty.set("You can't login with an empty user name.");
            return;
        }

        // Build the final URL with the username as a query parameter for the login request.
        String finalUrl = HttpUrl
                .parse(Constants.LOGIN_PAGE)
                .newBuilder()
                .addQueryParameter("username", userName)
                .build()
                .toString();

        // Execute the asynchronous login request.
        HttpClientUtil.runAsync(finalUrl, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                // Update the error message on the JavaFX application thread if the request fails.
                Platform.runLater(() -> errorMessageProperty.set("Something went wrong: " + e.getMessage()));
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                // Handle the response of the login request.
                if (response.code() != 200) {
                    String responseBody = response.body().string(); // Read the response body.
                    // Update the error message if the response is not successful.
                    Platform.runLater(() -> errorMessageProperty.set(responseBody));
                } else {
                    Platform.runLater(() -> {
                        loggedUserName = userName; // Store the logged-in username.
                        // Switch to the hub page upon successful login.
                        sheetHubMainController.switchToHubPage();
                    });
                }
            }
        });
    }

    @Override
    public String getLoggedUserName() {
        return loggedUserName; // Return the logged-in user's name.
    }

    @FXML
    @Override
    public void quitButtonClicked(ActionEvent e) {
        Platform.exit(); // Exit the application when the quit button is clicked.
    }

    @Override
    public void setSheetHubMainController(SheetHubMainController sheetHubMainController) {
        this.sheetHubMainController = sheetHubMainController; // Set the main controller.
    }
}
