package command.api;


import dto.*;
import spreadsheet.cell.impl.CellIdentifierImpl;
import spreadsheet.range.api.Range;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

public interface Engine {

    SaveLoadFileDto loadFile(InputStream fileContent,String username);
    SheetDto updateCellWithSheetVersionUpdate(String cellid, String originalValue,String modifiedBy,String sheetName);
    SheetDto updateCellWithoutSheetVersionUpdate(String cellid, String originalValue,String modifiedBy,String sheetName);
    SheetDto displaySheetByVersion(int version,String sheetName);
    SheetDto addRange(String name, CellIdentifierImpl topLeft, CellIdentifierImpl bottomRight,String sheetName);
    int getLatestVersion(String sheetName);
    SheetDto removeRange(String rangeName,String sheetName);
    SheetDto sortRange(Range range, List<String>colsToSort,String sheetName);
    SheetDto addEmptyCell (String cellid,String sheetName);
    SheetDto setCellStyle(String cellid, String style,String sheetName);
    SheetDto filterRangeByColumnsAndValues(Range range, Map<String, List<String>> selectedValuesForColumns,String sheetName);
    String createCellId(int row, int col);
    String evaluateOriginalValue(String originalValue,String sheetName);
    SheetDto[] getAllSheets();
    PermissionDto getUserPermissionFromSheet(String username,String sheetName);
    PermissionDto[] getSheetPermissions(String sheetName);
    void permissionRequest(String sheetName, Permission permissionType, String message,String username);
    void permissionApproval(String sheetName,String userName);
    void permissionDenial(String sheetName, String userName);
}