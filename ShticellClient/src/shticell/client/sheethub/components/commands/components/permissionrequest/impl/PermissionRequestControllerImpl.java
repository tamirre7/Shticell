package shticell.client.sheethub.components.commands.components.permissionrequest.impl;

import com.google.gson.Gson;
import dto.permission.Permission;
import dto.permission.PermissionRequestDto;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;
import shticell.client.sheethub.components.available.sheets.api.AvailableSheetsController;
import shticell.client.sheethub.components.commands.components.permissionrequest.api.PermissionRequestController;
import shticell.client.sheethub.components.commands.components.controller.api.CommandsMenuController;
import shticell.client.sheethub.components.login.api.LoginController;
import shticell.client.util.Constants;
import shticell.client.util.http.HttpClientUtil;

import java.io.IOException;
import java.util.List;

import static shticell.client.util.http.HttpClientUtil.showAlert;

public class PermissionRequestControllerImpl implements PermissionRequestController {

    @FXML
    private ComboBox<String> sheetNamesBox; // ComboBox for selecting sheet names

    @FXML
    private ComboBox<String> permissionTypeBox; // ComboBox for selecting permission type (READER or WRITER)

    @FXML
    private TextArea messageField; // TextArea for entering an optional message

    private int idKeeper = 1; // Unique identifier for permission requests

    private CommandsMenuController commandsMenuController; // Controller for the commands menu
    private AvailableSheetsController availableSheetsController; // Controller for available sheets
    private LoginController loginController; // Controller for login functionality

    @FXML
    private void initialize() {
        // Set up the ComboBox options for permission types
        permissionTypeBox.setItems(FXCollections.observableArrayList("READER", "WRITER"));
    }

    @Override
    public void populateSheetNames() {
        // Populate the sheetNamesBox with available sheet names
        ObservableList<String> sheetNames = FXCollections.observableArrayList();
        List<String> availableSheetsNames = availableSheetsController.getAvailableSheetsNames();
        sheetNames.addAll(availableSheetsNames); // Add available sheet names to the list
        sheetNamesBox.setItems(sheetNames); // Set items in the ComboBox
    }

    @FXML
    public void handleSubmit(ActionEvent event) {
        // Handle the submit action for permission requests
        if (validateInput()) {
            // If input is valid, send the permission request
            sendPermissionRequest(sheetNamesBox.getValue(), permissionTypeBox.getValue(), messageField.getText());
            commandsMenuController.permissionReturnToHub(); // Return to the hub after submission
        } else {
            // Show an alert if required fields are missing
            showAlert("Error", "Please fill in all required fields.");
        }
    }

    @FXML
    public void handleCancel(ActionEvent event) {
        // Handle the cancel action for permission requests
        // Clear all fields
        sheetNamesBox.getSelectionModel().clearSelection();
        permissionTypeBox.getSelectionModel().clearSelection();
        messageField.clear();
        // Return to hub page
        commandsMenuController.permissionReturnToHub();
    }

    private boolean validateInput() {
        // Validate input fields to ensure they are not null
        return (sheetNamesBox.getValue() != null) &&
                (permissionTypeBox.getValue() != null);
    }

    private void sendPermissionRequest(String sheetName, String permissionType, String message) {
        // Create a PermissionRequestDto object with request parameters
        PermissionRequestDto requestParams = new PermissionRequestDto(
                idKeeper,
                sheetName,
                Permission.fromString(permissionType), // Convert permission type to Permission enum
                message,
                loginController.getLoggedUserName() // Get the logged-in user's name
        );
        ++idKeeper; // Increment the idKeeper for the next request

        Gson gson = new Gson();
        String requestParamsJson = gson.toJson(requestParams); // Convert request parameters to JSON
        RequestBody requestBody = RequestBody.create(requestParamsJson, MediaType.parse("application/json")); // Create request body

        Request request = new Request.Builder()
                .url(Constants.REQUEST_PERMISSION) // Set the URL for the request
                .post(requestBody) // Specify the request method and body
                .build();

        // Run the request asynchronously
        HttpClientUtil.runAsync(request, new Callback() {
            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (!response.isSuccessful()) {
                    // Handle unsuccessful responses
                    Platform.runLater(() -> showAlert("Error", "Failed to add range: " + response.message()));
                }
            }

            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                // Handle request failure
                Platform.runLater(() -> showAlert("Error", "Error: " + e.getMessage()));
            }
        });
    }

    @Override
    public void setCommandsMenuController(CommandsMenuController commandsMenuController) {
        this.commandsMenuController = commandsMenuController; // Set the commands menu controller
    }

    @Override
    public void setAvailableSheetsController(AvailableSheetsController availableSheetsController) {
        this.availableSheetsController = availableSheetsController; // Set the available sheets controller
    }

    @Override
    public void setLoginController(LoginController loginController) {
        this.loginController = loginController; // Set the login controller
    }
}
