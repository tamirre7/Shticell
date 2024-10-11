package shticell.client.sheethub.components.commands.components.controller.impl;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.ListView;
import javafx.scene.layout.BorderPane;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;
import shticell.client.sheethub.components.available.sheets.api.AvailableSheetsController;
import shticell.client.sheethub.components.commands.components.permissionrequest.api.PermissionRequestController;
import shticell.client.sheethub.components.commands.components.controller.api.CommandsMenuController;
import shticell.client.sheethub.components.commands.components.permissionresponse.api.PermissionResponseController;
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
    private AvailableSheetsController availableSheetsController;

    private BorderPane requestPage;
    private BorderPane responsePage;

    @FXML
    private ListView<String> commandsList;

    @FXML
    public void initialize() throws IOException {

        FXMLLoader reqloader = new FXMLLoader(getClass().getResource(Constants.PERMISSION_REQUEST_RESOURCE_LOCATION));
        requestPage = reqloader.load();
        permissionRequestController = reqloader.getController();

        permissionRequestController.setCommandsMenuController(this);
        permissionRequestController.setAvailableSheetsController(availableSheetsController);

        FXMLLoader resploader = new FXMLLoader(getClass().getResource(Constants.PERMISSION_RESPONSE_RESOURCE_LOCATION));
        responsePage = resploader.load();
        permissionResponseController = resploader.getController();

        permissionResponseController.startRequestRefresher();

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
            mainController.switchToPermissionRequestPage(requestPage);
        }
    }
    private void viewResponsePage() {
        if (mainController != null) {
            mainController.switchToPermissionResponsePage(responsePage);

        }
    }

    @FXML
    public void returnToHub(){mainController.switchToLoginPage();}





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
                    Platform.runLater(() -> returnToHub());

                }
            }
        });

    }
}