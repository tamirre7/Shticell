package shticell.client.sheethub.components.commands.components.controller.impl;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.ListView;
import javafx.scene.layout.BorderPane;
import javafx.scene.web.HTMLEditorSkin;
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

        commandsList.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if ("View Selected Sheet".equals(newValue)) {
                viewSelectedSheet();
            }
            if("Request Permission".equals(newValue)) {
                viewPermissionRequestForm();
            }
            if("Response To Permission Requests".equals(newValue)) {
                viewResponsePage();
            }
            if("Enter Chat".equals(newValue)){
                viewChatPage();
            }

        });
    }

    private void viewSelectedSheet() {
        if (mainController != null) {
            mainController.switchToSheetViewPage();
        }
    }
    private void viewPermissionRequestForm() {
        if (mainController != null) {

            permissionRequestController.populateSheetNames();
            mainController.showPermissionRequestPopup(requestPage);
        }
    }
    private void viewResponsePage() {
        if (mainController != null) {
            mainController.showPermissionResponsePopup(responsePage);

        }
    }
    private void viewChatPage() {
        if (mainController != null) {
            mainController.showChatPopup(chatPage);
        }
    }

    @FXML
    public void returnToHub(){mainController.closePermissionPopup();}


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