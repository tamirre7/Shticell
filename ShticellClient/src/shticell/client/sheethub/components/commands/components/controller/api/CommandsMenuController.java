package shticell.client.sheethub.components.commands.components.controller.api;

import shticell.client.sheethub.main.SheetHubMainController;
import shticell.client.sheetpanel.spreadsheet.api.SpreadsheetController;

public interface CommandsMenuController {
    void setMainController(SheetHubMainController mainController);
    void refreshList();
}
