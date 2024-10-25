package shticell.client.sheethub.components.commands.components.controller.impl;

import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.BorderPane;
import javafx.util.Duration;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;
import shticell.client.sheethub.components.available.sheets.api.AvailableSheetsController;
import shticell.client.sheethub.components.commands.components.chat.chatroom.api.ChatRoomController;
import shticell.client.sheethub.components.commands.components.permissionrequest.api.PermissionRequestController;
import shticell.client.sheethub.components.commands.components.controller.api.CommandsMenuController;
import shticell.client.sheethub.components.commands.components.permissionresponse.api.PermissionResponseController;
import shticell.client.sheethub.components.login.api.LoginController;
import shticell.client.sheethub.components.permission.table.api.PermissionTableController;
import shticell.client.sheethub.main.SheetHubMainController;
import shticell.client.util.Constants;
import shticell.client.util.http.HttpClientUtil;

import java.io.IOException;

import static shticell.client.util.http.HttpClientUtil.showAlert;

public class CommandsMenuControllerImpl implements CommandsMenuController {
    // Reference to the main controller of the application
    private SheetHubMainController mainController;
    // Controllers for managing permissions
    private PermissionTableController permissionTableController;
    private PermissionRequestController permissionRequestController;
    private PermissionResponseController permissionResponseController;
    // Controller for chat functionality
    private ChatRoomController chatRoomController;
    // Controller for managing available sheets
    private AvailableSheetsController availableSheetsController;
    // Controller for login functionality
    private LoginController loginController;

    // UI elements for different pages
    private BorderPane requestPage;
    private BorderPane responsePage;
    private BorderPane chatPage;

    @FXML
    private ListView<String> commandsList;

    @FXML
    public void initialize() throws IOException {
        // Load the permission request page and its controller
        FXMLLoader reqLoader = new FXMLLoader(getClass().getResource(Constants.PERMISSION_REQUEST_RESOURCE_LOCATION));
        requestPage = reqLoader.load();
        permissionRequestController = reqLoader.getController();
        permissionRequestController.setCommandsMenuController(this);

        // Load the chat room page and its controller
        FXMLLoader chatLoader = new FXMLLoader(getClass().getResource(Constants.CHAT_ROOM_RESOURCE_LOCATION));
        chatPage = chatLoader.load();
        chatRoomController = chatLoader.getController();
        chatRoomController.setCommandsMenuComponent(this);

        // Load the permission response page and its controller
        FXMLLoader respLoader = new FXMLLoader(getClass().getResource(Constants.PERMISSION_RESPONSE_RESOURCE_LOCATION));
        responsePage = respLoader.load();
        permissionResponseController = respLoader.getController();
        permissionResponseController.setCommandsMenuController(this);

        // Set up the commands list with custom cell factory
        commandsList.setCellFactory(lv -> {
            ListCell<String> cell = new ListCell<String>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    // Update the cell display based on item value
                    if (empty || item == null) {
                        setText(null);
                        setGraphic(null);
                    } else {
                        setText(item);
                    }
                }
            };

            // Handle mouse click events on the list cells
            cell.setOnMouseClicked(event -> {
                if (!cell.isEmpty()) {
                    // Change cell background color on click
                    cell.setStyle("-fx-background-color: #2196F3;"); // Set to blue when clicked
                    PauseTransition pause = new PauseTransition(Duration.seconds(0.1));
                    pause.setOnFinished(e -> cell.setStyle(null)); // Reset style after delay
                    pause.play();

                    // Handle the click event
                    handleCellClick(cell.getItem());
                }
            });

            return cell;
        });
    }

    // Method to handle cell click actions
    private void handleCellClick(String item) {
        switch (item) {
            case "View Selected Sheet":
                viewSelectedSheet();
                break;
            case "Request Permission":
                viewPermissionRequestForm();
                break;
            case "Response To Permission Requests":
                viewResponsePage();
                break;
            case "Enter Chat":
                viewChatPage();
                break;
        }
    }

    // View the selected sheet if available
    private void viewSelectedSheet() {
        if (mainController != null) {
            // Check if a sheet is selected before switching views
            if (availableSheetsController.isSheetSelected()) {
                mainController.switchToSheetViewPage();
            } else {
                showAlert("Error", "A sheet must be selected before viewing a sheet");
            }
        }
    }

    // Display the permission request form
    private void viewPermissionRequestForm() {
        if (mainController != null) {
            try {
                FXMLLoader reqLoader = new FXMLLoader(getClass().getResource(Constants.PERMISSION_REQUEST_RESOURCE_LOCATION));
                requestPage = reqLoader.load();
                permissionRequestController = reqLoader.getController();
                permissionRequestController.setCommandsMenuController(this);
                permissionRequestController.setAvailableSheetsController(availableSheetsController);
                permissionRequestController.setLoginController(loginController);
                permissionRequestController.populateSheetNames();
                mainController.showPermissionRequestPopup(requestPage);
            } catch (IOException e) {
                showAlert("Error", "Failed to load permission request form");
            }
        }
    }

    // Display the response page for permission requests
    private void viewResponsePage() {
        if (mainController != null) {
            try {
                FXMLLoader respLoader = new FXMLLoader(getClass().getResource(Constants.PERMISSION_RESPONSE_RESOURCE_LOCATION));
                responsePage = respLoader.load();
                permissionResponseController = respLoader.getController();
                permissionResponseController.setCommandsMenuController(this);
                permissionResponseController.setAvailableSheetsController(availableSheetsController);
                mainController.showPermissionResponsePopup(responsePage);
            } catch (IOException e) {
                showAlert("Error", "Failed to load permission response page");
            }
        }
    }

    // Display the chat page
    private void viewChatPage() {
        if (mainController != null) {
            try {
                FXMLLoader chatLoader = new FXMLLoader(getClass().getResource(Constants.CHAT_ROOM_RESOURCE_LOCATION));
                chatPage = chatLoader.load();
                chatRoomController = chatLoader.getController();
                chatRoomController.setCommandsMenuComponent(this);
                mainController.showChatPopup(chatPage);
            } catch (IOException e) {
                showAlert("Error", "Failed to load chat page");
            }
        }
    }

    // Return to the hub after permission handling
    @Override
    public void permissionReturnToHub() {
        mainController.closePermissionPopup();
    }

    // Return to the hub after chat handling
    @Override
    public void chatReturnToHub() {
        mainController.closeChatPopup();
    }

    // Refresh the command list display
    @Override
    public void refreshList() {
        commandsList.getSelectionModel().clearSelection();
    }

    // Set the main controller
    @Override
    public void setMainController(SheetHubMainController mainController) {
        this.mainController = mainController;
    }

    // Set the permission table controller
    @Override
    public void setPermissionTableController(PermissionTableController permissionTableController) {
        this.permissionTableController = permissionTableController;
    }

    // Set the available sheets controller and update related controllers
    @Override
    public void setAvailableSheetsControllerTableController(AvailableSheetsController availableSheetsController) {
        this.availableSheetsController = availableSheetsController;
        permissionRequestController.setAvailableSheetsController(availableSheetsController);
        permissionResponseController.setAvailableSheetsController(availableSheetsController);
    }

    // Set the login controller and update the permission request controller
    @Override
    public void setLoginController(LoginController loginController) {
        this.loginController = loginController;
        permissionRequestController.setLoginController(loginController);
    }

    // Handle logout button click event
    @FXML
    public void logoutButtonClicked() {
        HttpClientUtil.runAsync(Constants.LOGOUT_PAGE, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                // Handle logout failure (logging, alerting user, etc.)
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                // Check if the logout was successful or redirected
                if (response.isSuccessful() || response.isRedirect()) {
                    HttpClientUtil.removeCookiesOf(Constants.BASE_DOMAIN);
                    Platform.runLater(() -> mainController.switchToLoginPage());
                }
            }
        });
    }

    // Activate the permission request refresher
    @Override
    public void activatePermissionRefresher() {
        permissionResponseController.startRequestRefresher();
    }

    // Deactivate the permission request refresher
    @Override
    public void deactivatePermissionRefresher() {
        permissionResponseController.stopRequestRefresher();
        permissionTableController.stopRequestRefresher();
    }

    // Activate chat refreshers for real-time updates
    @Override
    public void activateChatRefreshers() {
        chatRoomController.setActive();
    }

    // Deactivate chat refreshers
    @Override
    public void deActivateChatRefreshers() {
        chatRoomController.setInActive();
    }
}
