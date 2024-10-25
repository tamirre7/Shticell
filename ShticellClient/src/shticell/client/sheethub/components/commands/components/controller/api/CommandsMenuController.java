package shticell.client.sheethub.components.commands.components.controller.api;

import shticell.client.sheethub.components.available.sheets.api.AvailableSheetsController;
import shticell.client.sheethub.components.login.api.LoginController;
import shticell.client.sheethub.components.permission.table.api.PermissionTableController;
import shticell.client.sheethub.main.SheetHubMainController;

public interface CommandsMenuController {
    // Set the main controller for the application
    void setMainController(SheetHubMainController mainController);

    // Refresh the list of available commands or options in the menu
    void refreshList();

    // Set the controller responsible for managing the permission table
    void setPermissionTableController(PermissionTableController permissionTableController);

    // Return to the main hub after handling permission-related tasks
    void permissionReturnToHub();

    // Return to the main hub after handling chat-related tasks
    void chatReturnToHub();

    // Set the controller for managing available sheets in the table
    void setAvailableSheetsControllerTableController(AvailableSheetsController availableSheetsController);

    // Set the login controller for managing user login functionalities
    void setLoginController(LoginController loginController);

    // Activate the permission refresher to update permission-related information
    void activatePermissionRefresher();

    // Deactivate the permission refresher
    void deactivatePermissionRefresher();

    // Activate the chat refreshers to keep chat-related information updated
    void activateChatRefreshers();

    // Deactivate the chat refreshers
    void deActivateChatRefreshers();
}
