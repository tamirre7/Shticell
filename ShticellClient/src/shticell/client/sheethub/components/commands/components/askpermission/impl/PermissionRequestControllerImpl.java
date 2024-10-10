package shticell.client.sheethub.components.commands.components.askpermission.impl;

import com.google.gson.Gson;
import dto.Permission;
import dto.PermissionDto;
import dto.PermissionRequestDto;
import dto.SheetDto;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;
import shticell.client.sheethub.components.commands.components.askpermission.api.PermissionRequestController;
import shticell.client.sheethub.components.commands.components.controller.api.CommandsMenuController;
import shticell.client.util.Constants;
import shticell.client.util.http.HttpClientUtil;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static shticell.client.util.http.HttpClientUtil.showAlert;

public class PermissionRequestControllerImpl implements PermissionRequestController {

    @FXML private TextField sheetNameField;
    @FXML
    private ComboBox<String> permissionTypeBox;
    @FXML private TextArea messageField;

    private CommandsMenuController commandsMenuController;

    @FXML
    private void initialize() {
        // Set up the ComboBox options
        permissionTypeBox.setItems(FXCollections.observableArrayList("READER", "WRITER"));
    }

    @FXML
    public void handleSubmit(ActionEvent event){
        if(validateInput()){
            sendPermissionRequest(sheetNameField.getText(), permissionTypeBox.getValue(), messageField.getText());
        }
        else
        {
            showAlert("Error","Please fill in all required fields.");
        }
    }

    @FXML
    public void handleCancel(ActionEvent event) {
        // Clear all fields
        sheetNameField.clear();
        permissionTypeBox.getSelectionModel().clearSelection();
        messageField.clear();
        // Return to hub page
        commandsMenuController.returnToHub();
    }


    private boolean validateInput() {
        return !sheetNameField.getText().isEmpty() &&
                permissionTypeBox.getValue() != null;
    }

    private void sendPermissionRequest(String sheetName, String permissionType, String message) {
        PermissionRequestDto requestParams = new PermissionRequestDto(sheetName,Permission.fromString(permissionType),message);

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
}
