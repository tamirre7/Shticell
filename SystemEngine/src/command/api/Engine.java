package command.api;


import dto.*;
import dto.permission.Permission;
import dto.permission.PermissionInfoDto;
import dto.permission.PermissionRequestDto;
import dto.permission.SheetPermissionDto;
import spreadsheet.cell.impl.CellIdentifierImpl;
import spreadsheet.range.api.Range;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

public interface Engine {

    SaveLoadFileDto loadFile(InputStream fileContent,String username);
    SheetDto updateCellWithSheetVersionUpdate(String cellid, String originalValue,String modifiedBy,String sheetName,int sheetVersionToEdit);
    SheetDto updateCellWithoutSheetVersionUpdate(String cellid, String originalValue,String modifiedBy,String sheetName,int sheetVersionToEdit);
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
    List<SheetPermissionDto> getAllSheets(String userName);
    PermissionInfoDto getUserPermissionFromSheet(String username, String sheetName);
    List<PermissionInfoDto> getAllSheetPermissions(String sheetName);
    void permissionRequest(int requestId, String sheetName, Permission permissionType, String message, String username);
    void permissionApproval(String sheetName,String userName);
    void permissionDenial(String sheetName, String userName);
    List<SheetDto> getOwnedSheets(String username);
    List<PermissionRequestDto> getPendingRequests(String sheetName);

}