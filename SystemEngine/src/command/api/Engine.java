package command.api;


import dto.*;

import java.io.FileNotFoundException;

public interface Engine {

    LoadDto loadFile(String path);
    SheetDto displayCurrentSpreadsheet();
    CellDto displayCellValue(String cellid);
    void updateCell(String cellid, String originalValue);
    VerDto displayVersions();
    SheetDto displaySheetByVersion(int version);
    void checkIfFileLoaded();
    ExitDto exitSystem();
}