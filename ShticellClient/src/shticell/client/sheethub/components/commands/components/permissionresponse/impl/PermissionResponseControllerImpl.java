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
import shticell.client.sheethub.components.available.sheets.api.AvailableSheetsController;
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

    private CommandsMenuController commandsMenuController; // Controller for commands menu
    private AvailableSheetsController availableSheetsController; // Controller for available sheets

    @FXML private TableView<PermissionRequestDtoProperty> requestTableView; // Table for displaying requests
    @FXML private TextArea messageArea; // Area for showing messages related to requests
    @FXML private Button approveButton; // Button to approve requests
    @FXML private Button denyButton; // Button to deny requests
    @FXML private Button closeButton;

    private RequestRefresher requestRefresher; // Refresher for updating requests
    private Timer timer; // Timer for scheduling the refresher

    private ObservableList<PermissionRequestDtoProperty> requests = FXCollections.observableArrayList(); // List of requests

    @FXML
    public void initialize() {
        requestTableView.setItems(requests); // Set items in the table view

        // Listener for selection changes in the table
        requestTableView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            messageArea.clear(); // Clear message area
            if (newSelection != null && newSelection.getMessage() != null) {
                messageArea.setText(newSelection.getMessage()); // Set message for selected request
            }
        });

        // Disable buttons if no item is selected
        approveButton.disableProperty().bind(requestTableView.getSelectionModel().selectedItemProperty().isNull());
        denyButton.disableProperty().bind(requestTableView.getSelectionModel().selectedItemProperty().isNull());
    }

    @FXML
    private void handleDeny(ActionEvent event) {
        PermissionRequestDtoProperty selectedRequest = requestTableView.getSelectionModel().getSelectedItem(); // Get selected request
        if (selectedRequest != null) {
            PermissionResponseDto response = new PermissionResponseDto(selectedRequest.toDto(), false); // Create denial response
            sendResponseMessage(response); // Send response message
            requests.remove(selectedRequest); // Remove request from list
        }
    }

    @FXML
    private void handleApprove(ActionEvent event) {
        PermissionRequestDtoProperty selectedRequest = requestTableView.getSelectionModel().getSelectedItem(); // Get selected request
        if (selectedRequest != null) {
            PermissionResponseDto response = new PermissionResponseDto(selectedRequest.toDto(), true); // Create approval response
            sendResponseMessage(response); // Send response message
            requests.remove(selectedRequest); // Remove request from list

            // Update the sheet permission for the approved request
            availableSheetsController.updateSheetPermission(selectedRequest.getSheetName(), selectedRequest.getPermissionType());
        }
    }

    @FXML
    private void handleClose(ActionEvent event) {
        commandsMenuController.permissionReturnToHub(); // Return to the commands menu
    }

    private void sendResponseMessage(PermissionResponseDto responseDto) {
        Gson gson = new Gson(); // Gson instance for JSON conversion
        String responseJson = gson.toJson(responseDto); // Convert response DTO to JSON

        RequestBody requestBody = RequestBody.create(responseJson, MediaType.parse("application/json")); // Create request body

        Request request = new Request.Builder()
                .url(Constants.PERMISSION_RESPONSE) // Set request URL
                .post(requestBody) // Set request method to POST
                .build();

        // Execute the request asynchronously
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

    private void updateRequestTable(List<PermissionRequestDto> newRequests) {
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
    public void startRequestRefresher() {
        stopRequestRefresher(); // Stop any existing refresher

        timer = new Timer(); // Create a new timer
        requestRefresher = new RequestRefresher(this::updateRequestTable); // Create a new refresher
        timer.schedule(requestRefresher, REFRESH_RATE, REFRESH_RATE); // Schedule the refresher
    }

    @Override
    public void stopRequestRefresher() {
        if (requestRefresher != null)
            requestRefresher.setActive(false); // Deactivate the refresher

        if (timer != null) {
            timer.cancel(); // Cancel the timer
            timer.purge(); // Purge the timer
        }
    }

    @Override
    public void setCommandsMenuController(CommandsMenuController commandsMenuController) {
        this.commandsMenuController = commandsMenuController; // Set the commands menu controller
    }

    @Override
    public void setAvailableSheetsController(AvailableSheetsController availableSheetsController) {
        this.availableSheetsController = availableSheetsController; // Set the available sheets controller
    }

}
