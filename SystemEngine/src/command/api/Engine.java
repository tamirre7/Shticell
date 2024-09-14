package command.api;


import dto.*;
import spreadsheet.cell.impl.CellIdentifierImpl;
import spreadsheet.range.api.Range;

import java.util.List;

public interface Engine {

    SaveLoadFileDto loadFile(String path);
    SheetDto displayCurrentSpreadsheet();
    CellDto displayCellValue(String cellid);
    SheetDto updateCell(String cellid, String originalValue);
    SheetDto displaySheetByVersion(int version);
    SaveLoadFileDto saveState(String path);
    SaveLoadFileDto loadSavedState(String path);
    boolean isFileLoaded();
    RangeDto addRange(String name, CellIdentifierImpl topLeft, CellIdentifierImpl bottomRight);
    void removeRange(String rangeName);
    RangeDto getRange(String rangeName);
    String[] getAvailableVersions();
    Integer getLatestVersion();
    SheetDto sortRange(Range range, List<String>colsToSort);
}