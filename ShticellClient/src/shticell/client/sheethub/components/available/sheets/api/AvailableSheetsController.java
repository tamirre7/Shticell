package shticell.client.sheethub.components.available.sheets.api;

import dto.permission.Permission;
import shticell.client.sheethub.components.available.sheets.SheetPermissionDtoProperty;
import shticell.client.sheethub.components.login.api.LoginController;

import java.util.List;

/**
 * Interface for managing available sheets in the SheetHub application.
 */
public interface AvailableSheetsController {

    // Handles the selection of a sheet.
    void handleSheetSelection(SheetPermissionDtoProperty selectedSheet);

    // Retrieves the names of available sheets.
    List<String> getAvailableSheetsNames();

    // Checks if a sheet is currently selected.
    boolean isSheetSelected();

    // Updates the permission for a specific sheet.
    void updateSheetPermission(String sheetName, Permission newPermission);

    // Sets the login controller to manage user sessions.
    void setLoginController(LoginController loginController);
}
