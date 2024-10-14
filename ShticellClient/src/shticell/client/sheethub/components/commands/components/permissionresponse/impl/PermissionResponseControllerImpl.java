package shticell.client.sheethub.components.commands.components.permissionresponse.impl;

import com.google.gson.Gson;
import dto.permission.PermissionRequestDto;
import dto.permission.PermissionResponseDto;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;
import shticell.client.sheethub.components.commands.components.controller.api.CommandsMenuController;
import shticell.client.sheethub.components.commands.components.permissionresponse.PermissionRequestDtoProperty;
import shticell.client.sheethub.components.commands.components.permissionresponse.RequestRefresher;
import shticell.client.sheethub.components.commands.components.permissionresponse.api.PermissionResponseController;
import shticell.client.util.Constants;
import shticell.client.util.http.HttpClientUtil;

import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.Timer;
import java.util.stream.Collectors;

import static shticell.client.util.Constants.REFRESH_RATE;
import static shticell.client.util.http.HttpClientUtil.showAlert;

public class PermissionResponseControllerImpl implements PermissionResponseController {

    private CommandsMenuController commandsMenuController;

    @FXML private TableView<PermissionRequestDtoProperty> requestTableView;
    @FXML private TextArea messageArea;
    @FXML private Button approveButton;
    @FXML private Button denyButton;
    @FXML private Button closeButton;

    private RequestRefresher requestRefresher;
    private Timer timer;

    private ObservableList<PermissionRequestDtoProperty> requests = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        requestTableView.setItems(requests);

        requestTableView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null && !newSelection.getMessage().isEmpty()) {
                messageArea.setText(newSelection.getMessage());
            }
        });

        approveButton.disableProperty().bind(requestTableView.getSelectionModel().selectedItemProperty().isNull());
        denyButton.disableProperty().bind(requestTableView.getSelectionModel().selectedItemProperty().isNull());
    }

    @FXML
    private void handleDeny(ActionEvent event){
        PermissionRequestDtoProperty selectedRequest = requestTableView.getSelectionModel().getSelectedItem();
        if (selectedRequest != null) {
            PermissionResponseDto response = new PermissionResponseDto(selectedRequest.toDto(),false);
            sendResponseMessage(response);
            requests.remove(selectedRequest);
        }
    }

    @FXML
    private void handleApprove(ActionEvent event){
        PermissionRequestDtoProperty selectedRequest = requestTableView.getSelectionModel().getSelectedItem();
        if (selectedRequest != null) {
            PermissionResponseDto response = new PermissionResponseDto(selectedRequest.toDto(),true);
            sendResponseMessage(response);
            requests.remove(selectedRequest);
        }
    }

    @FXML
    private void handleClose(ActionEvent event){
        commandsMenuController.returnToHub();
    }

    private void sendResponseMessage(PermissionResponseDto responseDto){
        Gson gson = new Gson();
        String responseJson = gson.toJson(responseDto);


        RequestBody requestBody = RequestBody.create(responseJson, MediaType.parse("application/json"));

        Request request = new Request.Builder()
                .url(Constants.PERMISSION_RESPONSE)
                .post(requestBody)
                .build();

        HttpClientUtil.runAsync(request, new Callback() {
            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (!response.isSuccessful()) {
                    Platform.runLater(() ->
                            showAlert("Error", "Failed to sort data: " + response.message())

                    );
                }
            }

            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Platform.runLater(() ->
                        showAlert("Error", "An error occurred while sorting: " + e.getMessage())
                );
            }
        });
    }

    private void updateRequestTable(List<PermissionRequestDto> newRequests){
        Platform.runLater(() -> {
            // Create a set for easy lookup of existing request IDs
            Set<Integer> existingRequestIds = requests.stream()
                    .map(PermissionRequestDtoProperty::getId)
                    .collect(Collectors.toSet());

            // Filter out new requests that are not already in the existing requests
            List<PermissionRequestDtoProperty> filteredNewRequests = newRequests.stream()
                    .filter(request -> !existingRequestIds.contains(request.getId()))
                    .map(PermissionRequestDtoProperty::new)
                    .toList();

            // Update the requests and the table only if there are new requests
            if (!filteredNewRequests.isEmpty()) {
                requests.addAll(filteredNewRequests); // Add new requests to the existing list
                requestTableView.setItems(requests); // Update the table view
            }
        });
    }

    @Override
    public void startRequestRefresher()
    {
        stopRequestRefresher();

        timer = new Timer();
        requestRefresher = new RequestRefresher(this::updateRequestTable);
        timer.schedule(requestRefresher, REFRESH_RATE, REFRESH_RATE);

    }
    @Override
    public void stopRequestRefresher(){
        if(requestRefresher != null)
            requestRefresher.setActive(false);

        if (timer != null) {
            timer.cancel();
            timer.purge();
        }

    }
    @Override
    public void setCommandsMenuController(CommandsMenuController commandsMenuController) {
        this.commandsMenuController = commandsMenuController;
    }

}
