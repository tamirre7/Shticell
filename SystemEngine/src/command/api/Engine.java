package command.api;


import dto.*;

public interface Engine {

    LoadDto loadFile(String path);
    SheetDto displaySpreadsheet();
    CellDto displayCellValue(String cellid);
    CellDto updateCell(String cellid);
    VerDto displayVersions();
    VerDto displaySheetByVersion(String version);
    ExitDto exitSystem();
}