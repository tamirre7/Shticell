package shticell.client.sheethub.components.available.sheets.api;

import dto.permission.Permission;
import shticell.client.sheethub.components.available.sheets.SheetPermissionDtoProperty;
import shticell.client.sheethub.components.login.api.LoginController;

import java.util.List;

public interface AvailableSheetsController {
    void handleSheetSelection(SheetPermissionDtoProperty selectedSheet);

    List<String> getAvailableSheetsNames();

    boolean isSheetSelected();

    void updateSheetPermission(String sheetName, Permission newPermission);

    void setLoginController(LoginController loginController);
}
