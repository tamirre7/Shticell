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
    private SheetHubMainController mainController;
    private PermissionTableController permissionTableController;
    private PermissionRequestController permissionRequestController;
    private PermissionResponseController permissionResponseController;
    private ChatRoomController chatRoomController;
    private AvailableSheetsController availableSheetsController;
    private LoginController loginController;

    private BorderPane requestPage;
    private BorderPane responsePage;
    private BorderPane chatPage;

    @FXML
    private ListView<String> commandsList;

    @FXML
    public void initialize() throws IOException {

        FXMLLoader reqLoader = new FXMLLoader(getClass().getResource(Constants.PERMISSION_REQUEST_RESOURCE_LOCATION));
        requestPage = reqLoader.load();
        permissionRequestController = reqLoader.getController();

        permissionRequestController.setCommandsMenuController(this);

        FXMLLoader respLoader = new FXMLLoader(getClass().getResource(Constants.CHAT_ROOM_RESOURCE_LOCATION));
        chatPage = respLoader.load();
        chatRoomController = respLoader.getController();
        chatRoomController.setCommandsMenuComponent(this);

        FXMLLoader chatLoader = new FXMLLoader(getClass().getResource(Constants.PERMISSION_RESPONSE_RESOURCE_LOCATION));
        responsePage = chatLoader.load();
        permissionResponseController = chatLoader.getController();
        permissionResponseController.setCommandsMenuController(this);

        commandsList.setCellFactory(lv -> {
            ListCell<String> cell = new ListCell<String>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                        setGraphic(null);
                    } else {
                        setText(item);
                    }
                }
            };

            cell.setOnMouseClicked(event -> {
                if (!cell.isEmpty()) {
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

    private void viewSelectedSheet() {
        if (mainController != null) {
            if(availableSheetsController.isSheetSelected())
            mainController.switchToSheetViewPage();
            else{showAlert("Error", "A sheet must be selected before viewing a sheet");}
        }
    }
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

    private void clearSelection() {
        Platform.runLater(() -> commandsList.getSelectionModel().clearSelection());
    }

    @Override
    public void permissionReturnToHub(){mainController.closePermissionPopup();}

    @Override
    public void chatReturnToHub(){mainController.closeChatPopup();}

    @Override
    public void refreshList()
    {
        commandsList.getSelectionModel().clearSelection();
    }

    @Override
    public void setMainController(SheetHubMainController mainController) {
        this.mainController = mainController;
    }

    @Override
    public void setPermissionTableController(PermissionTableController permissionTableController) {
        this.permissionTableController = permissionTableController;
    }
    @Override
    public void setAvailableSheetsControllerTableController(AvailableSheetsController availableSheetsController) {
        this.availableSheetsController = availableSheetsController;
        permissionRequestController.setAvailableSheetsController(availableSheetsController);
        permissionResponseController.setAvailableSheetsController(availableSheetsController);
    }
    @Override
    public void setLoginController(LoginController loginController) {
        this.loginController = loginController;
        permissionRequestController.setLoginController(loginController);
    }
    @FXML
    public void logoutButtonClicked(){
        HttpClientUtil.runAsync(Constants.LOGOUT_PAGE, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {

            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.isSuccessful() || response.isRedirect()) {
                    HttpClientUtil.removeCookiesOf(Constants.BASE_DOMAIN);
                    Platform.runLater(() -> mainController.switchToLoginPage());

                }
            }
        });
    }

    @Override
    public void activatePermissionRefresher()
    {
        permissionResponseController.startRequestRefresher();
    }
    @Override
    public void deactivatePermissionRefresher(){
        permissionResponseController.stopRequestRefresher();
        permissionTableController.stopRequestRefresher();
    }
    @Override
    public void activateChatRefreshers(){
        chatRoomController.setActive();
    }
    @Override
    public void deActivateChatRefreshers(){
        chatRoomController.setInActive();
    }
}