package shticell.client.sheethub.components.commands.components.controller.impl;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import shticell.client.sheethub.components.commands.components.controller.api.CommandsMenuController;
import shticell.client.sheethub.main.SheetHubMainController;
import shticell.client.sheetpanel.main.SheetViewMainController;
import shticell.client.util.Constants;

import java.io.IOException;

public class CommandsMenuControllerImpl implements CommandsMenuController {
    private SheetHubMainController mainController;
    private SheetViewMainController sheetViewController;
    private ScrollPane sheetViewPane;

    @FXML
    private ListView<String> commandsList;

    @FXML
    public void initialize() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(Constants.SHEET_VIEW_MAIN_PAGE_FXML_RESOURCE_LOCATION));
        sheetViewPane = loader.load();
        sheetViewController = loader.getController();

        commandsList.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if ("View Selected Sheet".equals(newValue)) {
                viewSelectedSheet();
            }
            // Add other command handlers here
        });
    }

    private void viewSelectedSheet() {
        if (mainController != null && sheetViewPane != null) {
            mainController.switchToSheetViewPage(sheetViewPane);
        }
    }

    @Override
    public void setMainController(SheetHubMainController mainController) {
        this.mainController = mainController;
    }
}