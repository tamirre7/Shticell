package shticell.client.sheethub.components.commands.components.permissionrequest.api;

import javafx.event.ActionEvent;
import shticell.client.sheethub.components.available.sheets.api.AvailableSheetsController;
import shticell.client.sheethub.components.commands.components.controller.api.CommandsMenuController;
import shticell.client.sheethub.components.login.api.LoginController;

public interface PermissionRequestController {
    void handleSubmit(ActionEvent event);

    void handleCancel(ActionEvent event);

    void setCommandsMenuController(CommandsMenuController commandsMenuController);

    void setAvailableSheetsController(AvailableSheetsController availableSheetsController);
    void populateSheetNames();
    void setLoginController(LoginController loginController);
}
