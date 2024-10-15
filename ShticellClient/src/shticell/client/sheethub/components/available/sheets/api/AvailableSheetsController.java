package shticell.client.sheethub.components.available.sheets.api;

import dto.SheetDto;
import dto.permission.Permission;
import shticell.client.sheethub.components.available.sheets.SheetDtoProperty;

import java.util.List;

public interface AvailableSheetsController {
    void handleSheetSelection(SheetDtoProperty selectedSheet);
    List<String> getAvailableSheetsNames();
    void updateSheetPermission(String sheetName, Permission newPermission);
}
