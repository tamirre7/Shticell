package shticell.client.sheethub.components.commands.components.permissionrequest.api;

import javafx.event.ActionEvent;
import shticell.client.sheethub.components.available.sheets.api.AvailableSheetsController;
import shticell.client.sheethub.components.commands.components.controller.api.CommandsMenuController;
import shticell.client.sheethub.components.login.api.LoginController;

public interface PermissionRequestController {
    // Handles the submission of the permission request
    void handleSubmit(ActionEvent event);

    // Handles the cancellation of the permission request
    void handleCancel(ActionEvent event);

    // Sets the commands menu controller
    void setCommandsMenuController(CommandsMenuController commandsMenuController);

    // Sets the available sheets controller
    void setAvailableSheetsController(AvailableSheetsController availableSheetsController);

    // Populates the sheet names in the UI
    void populateSheetNames();

    // Sets the login controller
    void setLoginController(LoginController loginController);
}
