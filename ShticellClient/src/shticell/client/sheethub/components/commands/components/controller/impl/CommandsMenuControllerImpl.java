package shticell.client.sheethub.components.commands.components.controller.impl;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;
import shticell.client.sheethub.components.commands.components.controller.api.CommandsMenuController;
import shticell.client.sheethub.main.SheetHubMainController;
import shticell.client.sheetpanel.main.SheetViewMainController;
import shticell.client.sheetpanel.spreadsheet.api.SpreadsheetController;
import shticell.client.util.Constants;
import shticell.client.util.http.HttpClientUtil;

import java.io.IOException;

public class CommandsMenuControllerImpl implements CommandsMenuController {
    private SheetHubMainController mainController;

    @FXML
    private ListView<String> commandsList;

    @FXML
    public void initialize() {
        commandsList.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if ("View Selected Sheet".equals(newValue)) {
                viewSelectedSheet();
            }
            // Add other command handlers here
        });
    }

    private void viewSelectedSheet() {
        if (mainController != null) {
            mainController.switchToSheetViewPage();
        }
    }
    @Override
    public void refreshList()
    {
        commandsList.getSelectionModel().clearSelection();
    }

    @Override
    public void setMainController(SheetHubMainController mainController) {
        this.mainController = mainController;
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


}