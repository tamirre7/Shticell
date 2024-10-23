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

    @FXML private ComboBox<String> sheetNamesBox;
    @FXML
    private ComboBox<String> permissionTypeBox;
    @FXML private TextArea messageField;
    private int idKeeper = 1;

    private CommandsMenuController commandsMenuController;
    private AvailableSheetsController availableSheetsController;
    private LoginController loginController;

    @FXML
    private void initialize() {
        // Set up the ComboBox options
        permissionTypeBox.setItems(FXCollections.observableArrayList("READER", "WRITER"));

    }
    @Override
    public void populateSheetNames() {
        ObservableList<String> sheetNames = FXCollections.observableArrayList();
        List<String> availableSheetsNames = availableSheetsController.getAvailableSheetsNames();
        sheetNames.addAll(availableSheetsNames);
        sheetNamesBox.setItems(sheetNames);
    }

    @FXML
    public void handleSubmit(ActionEvent event){
        if(validateInput()){
            sendPermissionRequest(sheetNamesBox.getValue(), permissionTypeBox.getValue(), messageField.getText());
            commandsMenuController.returnToHub();
        }
        else
        {
            showAlert("Error","Please fill in all required fields.");
        }
    }

    @FXML
    public void handleCancel(ActionEvent event) {
        // Clear all fields
        sheetNamesBox.getSelectionModel().clearSelection();
        permissionTypeBox.getSelectionModel().clearSelection();
        messageField.clear();
        // Return to hub page
        commandsMenuController.returnToHub();
    }


    private boolean validateInput() {
        return (sheetNamesBox.getValue() != null) &&
                (permissionTypeBox.getValue() != null);
    }

    private void sendPermissionRequest(String sheetName, String permissionType, String message) {
        PermissionRequestDto requestParams = new PermissionRequestDto(idKeeper,sheetName,Permission.fromString(permissionType),message, loginController.getLoggedUserName());
        ++idKeeper;

        Gson gson = new Gson();
        String requestParamsJson = gson.toJson(requestParams);
        RequestBody requestBody = RequestBody.create(requestParamsJson, MediaType.parse("application/json"));

        Request request = new Request.Builder()
                .url(Constants.REQUEST_PERMISSION)
                .post(requestBody)
                .build();

        HttpClientUtil.runAsync(request, new Callback() {
            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (!response.isSuccessful()) {
                    Platform.runLater(() -> showAlert("Error", "Failed to add range: " + response.message()));
                }
            }

            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Platform.runLater(() ->
                        showAlert("Error", "Error: " + e.getMessage())
                );
            }
        });


    }

    @Override
    public void setCommandsMenuController(CommandsMenuController commandsMenuController) {
        this.commandsMenuController = commandsMenuController;
    }
    @Override
    public void setAvailableSheetsController(AvailableSheetsController availableSheetsController){this.availableSheetsController = availableSheetsController;}
    @Override
    public void setLoginController(LoginController loginController){
        this.loginController = loginController;
    }




}
