package shticell.client.sheethub.components.commands.components.controller.api;

import shticell.client.sheethub.components.available.sheets.api.AvailableSheetsController;
import shticell.client.sheethub.components.login.api.LoginController;
import shticell.client.sheethub.components.permission.table.api.PermissionTableController;
import shticell.client.sheethub.main.SheetHubMainController;

public interface CommandsMenuController {
    void setMainController(SheetHubMainController mainController);
    void refreshList();
    void setPermissionTableController(PermissionTableController permissionTableController);
    void permissionReturnToHub();
    void chatReturnToHub();
    void setAvailableSheetsControllerTableController(AvailableSheetsController availableSheetsController);
    void setLoginController(LoginController loginController);
    void activatePermissionRefresher();
    void deactivatePermissionRefresher();
    void activateChatRefreshers();
    void deActivateChatRefreshers();
}
