package dto.permission;

import dto.SheetDto;

public class SheetPermissionDto {
    private final SheetDto sheetDto; // The sheet associated with the permission
    private final Permission userPermission; // The user's permission level for the sheet

    // Constructor
    public SheetPermissionDto(SheetDto sheetDto, Permission userPermission) {
        this.sheetDto = sheetDto;
        this.userPermission = userPermission;
    }

    // Returns the uploader's username
    public String getUploadedBy() {
        return sheetDto.getUploadedBy();
    }

    // Returns the sheet's name
    public String getSheetName() {
        return sheetDto.getSheetName();
    }

    // Returns the size of the sheet as "rows x cols"
    public String getSize() {
        return sheetDto.getSheetDimension().getNumRows() + "x" + sheetDto.getSheetDimension().getNumCols();
    }

    // Returns the user's permission for the sheet
    public Permission getUserPermission() {
        return userPermission;
    }

    // Returns the associated SheetDto
    public SheetDto getSheetDto() {
        return sheetDto;
    }
}
