package command.api;


import dto.*;

public interface Engine {

    SaveLoadFileDto loadFile(String path);
    SheetDto displayCurrentSpreadsheet();
    CellDto displayCellValue(String cellid);
    SheetDto updateCell(String cellid, String originalValue);
    VerDto displayVersions();
    SheetDto displaySheetByVersion(int version);
    SaveLoadFileDto saveState(String path);
    SaveLoadFileDto loadSavedState(String path);
    boolean isFileLoaded();
    ExitDto exitSystem();
}