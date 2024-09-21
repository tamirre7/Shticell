package command.api;


import dto.*;
import spreadsheet.cell.impl.CellIdentifierImpl;
import spreadsheet.range.api.Range;

import java.util.List;
import java.util.Map;

public interface Engine {

    SaveLoadFileDto loadFile(String path);
    SheetDto displayCurrentSpreadsheet();
    SheetDto updateCell(String cellid, String originalValue);
    SheetDto displaySheetByVersion(int version);
    SaveLoadFileDto saveState(String path);
    SaveLoadFileDto loadSavedState(String path);
    boolean isFileLoaded();
    RangeDto addRange(String name, CellIdentifierImpl topLeft, CellIdentifierImpl bottomRight);
    void removeRange(String rangeName);
    Integer getLatestVersion();
    SheetDto sortRange(Range range, List<String>colsToSort);
    SheetDto addEmptyCell (String cellid);
    SheetDto setCellStyle(String cellid, String style);
    SheetDto filterRangeByColumnsAndValues(Range range, Map<String, List<String>> selectedValuesForColumns);
    String createCellId(int row, int col);
    String evaluateOriginalValue(String originalValue);
}