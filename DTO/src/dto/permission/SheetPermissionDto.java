package dto.permission;

import dto.SheetDto;

public class SheetPermissionDto {
    private final SheetDto sheetDto;
    private final Permission userPermission;

    public SheetPermissionDto(SheetDto sheetDto, Permission userPermission) {
       this.sheetDto = sheetDto;
        this.userPermission = userPermission;
    }

    public String getUploadedBy() {return sheetDto.getUploadedBy();}

    public String getSheetName() {
        return sheetDto.getSheetName();
    }

    public String getSize(){return sheetDto.getSheetDimension().getNumRows() + "x" + sheetDto.getSheetDimension().getNumCols();}

    public Permission getUserPermission() {return userPermission;}

    public SheetDto getSheetDto() {return sheetDto;}
}
