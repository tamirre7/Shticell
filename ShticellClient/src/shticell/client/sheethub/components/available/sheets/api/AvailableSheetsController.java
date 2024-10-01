package shticell.client.sheethub.components.available.sheets.api;

import dto.SheetDto;

public interface AvailableSheetsController {
    void addSheet(SheetDto sheetDto);
    void handleSheetSelection(SheetDto selectedSheet);

}
