package shticell.client.sheethub.components.available.sheets.api;

import dto.SheetDto;

import java.util.List;

public interface AvailableSheetsController {
    void handleSheetSelection(SheetDto selectedSheet);
    List<String> getAvailableSheetsNames();
}
