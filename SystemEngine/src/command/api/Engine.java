package command.api;


import dto.*;

public interface Engine {

    LoadDto loadFile(String path);
    SheetDto displaySpreadsheet();
    CellDto displayCellValue(String cellid);
    CellDto updateCell(String cellid, String originalValue);
    VerDto displayVersions();
    SheetDto displaySheetByVersion(int version);
    ExitDto exitSystem();
}