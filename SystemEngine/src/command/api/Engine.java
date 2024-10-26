package command.api;

import dto.*;
import dto.permission.*;
import spreadsheet.cell.impl.CellIdentifierImpl;
import spreadsheet.range.api.Range;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

public interface Engine {
    // Loads a spreadsheet file from an input stream and associates it with the given username
    SaveLoadFileDto loadFile(InputStream fileContent,String username);

    // Updates a cell value and increments the sheet version
    SheetDto updateCellWithSheetVersionUpdate(String cellid, String originalValue,String modifiedBy,String sheetName,int sheetVersionToEdit);

    // Updates a cell value without incrementing the sheet version (for dynamic updates)
    SheetDto updateCellWithoutSheetVersionUpdate(String cellid, String originalValue,String modifiedBy,String sheetName,int sheetVersionToEdit);

    // Retrieves a specific version of a spreadsheet
    SheetDto displaySheetByVersion(int version,String sheetName);

    // Creates a new named range defined by top-left and bottom-right cells
    SheetDto addRange(String name, CellIdentifierImpl topLeft, CellIdentifierImpl bottomRight,String sheetName);

    // Returns the most recent version number of the specified sheet
    int getLatestVersion(String sheetName);

    // Removes a named range from the spreadsheet
    SheetDto removeRange(String rangeName,String sheetName);

    // Sorts cells within a range based on specified columns
    SheetDto sortRange(Range range, List<String>colsToSort,String sheetName);

    // Creates an empty cell at the specified location
    SheetDto addEmptyCells (List<String> cellId,String sheetName);

    // Sets the visual style for a specific cell
    SheetDto setCellsStyle(List<String> cellids, String style,String sheetName);

    // Filters cells in a range based on column values
    SheetDto filterRangeByColumnsAndValues(Range range, Map<String, List<String>> selectedValuesForColumns,String sheetName);

    // Generates a cell identifier from row and column numbers
    String createCellId(int row, int col);

    // Evaluates a formula or expression and returns its calculated value
    String evaluateOriginalValue(String originalValue,String sheetName);

    // Retrieves all sheets accessible to a specific user
    List<SheetPermissionDto> getAllSheets(String userName);

    // Gets a user's permission level for a specific sheet
    PermissionInfoDto getUserPermissionFromSheet(String username, String sheetName);

    // Retrieves all permission settings for a specific sheet
    List<PermissionInfoDto> getAllSheetPermissions(String sheetName);

    // Submits a new permission request for a sheet
    void permissionRequest(int requestId, String sheetName, Permission permissionType, String message, String username);

    // Approves a pending permission request
    void permissionApproval(PermissionResponseDto responseDto);

    // Denies a pending permission request
    void permissionDenial(PermissionResponseDto responseDto);

    // Gets all sheets owned by a specific user
    List<SheetDto> getOwnedSheets(String username);

    // Retrieves all pending permission requests for a sheet
    List<PermissionRequestDto> getPendingRequests(String sheetName);
}