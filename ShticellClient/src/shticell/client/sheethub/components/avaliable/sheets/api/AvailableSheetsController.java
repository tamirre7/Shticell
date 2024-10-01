package shticell.client.sheethub.components.avaliable.sheets.api;

import dto.SheetDto;

public interface AvailableSheetsController {
    void addSheet(SheetDto sheetDto);
    void handleSheetSelection(SheetDto selectedSheet);

}
