package shticell.client.sheethub.components.commands.components.permissionresponse.api;

import shticell.client.sheethub.components.available.sheets.api.AvailableSheetsController;
import shticell.client.sheethub.components.commands.components.controller.api.CommandsMenuController;


public interface PermissionResponseController {

    // Starts the refresher mechanism for updating permission requests.
    void startRequestRefresher();

    // Stops the refresher mechanism for updating permission requests.
    void stopRequestRefresher();

    // Sets the Commands Menu Controller for handling command menu interactions.
    void setCommandsMenuController(CommandsMenuController commandsMenuController);

    // Initializes the Permission Response Controller.
    void initialize();

    // Sets the Available Sheets Controller for accessing available sheet data.
    void setAvailableSheetsController(AvailableSheetsController availableSheetsController);
}
