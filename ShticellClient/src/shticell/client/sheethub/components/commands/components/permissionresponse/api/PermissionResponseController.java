package shticell.client.sheethub.components.commands.components.permissionresponse.api;

import shticell.client.sheethub.components.available.sheets.api.AvailableSheetsController;
import shticell.client.sheethub.components.commands.components.controller.api.CommandsMenuController;

public interface PermissionResponseController {
    void startRequestRefresher();
    void stopRequestRefresher();
    void setCommandsMenuController(CommandsMenuController commandsMenuController);
    void initialize();
    void setAvailableSheetsController(AvailableSheetsController availableSheetsController);
}
