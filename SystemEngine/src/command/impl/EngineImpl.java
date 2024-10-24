package command.impl;

import command.api.Engine;
import dto.*;
import dto.permission.*;
import expressions.api.Expression;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;
import spreadsheet.api.Dimension;
import spreadsheet.api.SpreadSheet;
import spreadsheet.cell.api.Cell;
import spreadsheet.cell.api.CellIdentifier;
import spreadsheet.cell.api.EffectiveValue;
import spreadsheet.cell.cellstyle.impl.CellStyleImpl;
import spreadsheet.cell.impl.CellIdentifierImpl;
import spreadsheet.cell.impl.CellImpl;
import spreadsheet.range.api.Range;
import spreadsheet.sheetimpl.DimensionImpl;
import spreadsheet.sheetimpl.SpreadSheetImpl;
import spreadsheet.sheetmanager.permissionmanager.permissionrequest.PermissionRequest;
import spreadsheet.sheetmanager.api.SheetManager;
import spreadsheet.sheetmanager.impl.SheetManagerImpl;
import spreadsheet.util.UpdateResult;
import xml.generated.*;

import java.io.*;
import java.util.*;

import static expressions.parser.FunctionParser.parseExpression;

public class EngineImpl implements Engine {
    private Map<String, SheetManager> sheetMap = new HashMap<>();

    @Override
    public SaveLoadFileDto loadFile(InputStream fileContent,String username) {
        try {
            // Initialize JAXB context and unmarshaller
            JAXBContext jaxbContext = JAXBContext.newInstance(STLSheet.class);
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();

            // Unmarshal the XML file to STLSheet object
            STLSheet stlSheet = (STLSheet) unmarshaller.unmarshal(fileContent);

            String sheetName = stlSheet.getName();
            if (sheetMap.containsKey(sheetName)) {
                return new SaveLoadFileDto(false, "Sheet name '" + sheetName + "' already exists. Each sheet must have a unique name.");
            }

            // Validate sheet dimensions
            STLLayout stlLayout = stlSheet.getSTLLayout();
            STLSize stlSize = stlLayout.getSTLSize();
            int numRows = stlLayout.getRows();
            int numCols = stlLayout.getColumns();

            if (numRows < 1 || numRows > 50 || numCols < 1 || numCols > 20) {
                return new SaveLoadFileDto(false, "Invalid sheet size- \nRows must be between 1 and 50 \nColumns must be between 1 and 20");
            }

            // Create Dimensions object
            Dimension sheetDimensions = new DimensionImpl(
                    numRows,
                    numCols,
                    stlSize.getColumnWidthUnits(),
                    stlSize.getRowsHeightUnits()
            );

            // Create a new SpreadSheet instance with the provided dimensions
            SheetManager sheetManager = new SheetManagerImpl(stlSheet.getName(),username);
            SpreadSheet spreadSheet = new SpreadSheetImpl(sheetDimensions, sheetManager);
            sheetManager.updateSheetVersion(spreadSheet);

            STLRanges stlRanges = stlSheet.getSTLRanges();
            for (STLRange stlRange : stlRanges.getSTLRange()) {
                String startCell = stlRange.getSTLBoundaries().getFrom();
                String endCell = stlRange.getSTLBoundaries().getTo();
                String rangeName = stlRange.getName();

                // Validate the start and end cells
                if (!spreadSheet.isValidCellID(startCell) || !spreadSheet.isValidCellID(endCell)) {
                    return new SaveLoadFileDto(false, "Invalid range: " + startCell + " to " + endCell);
                }
                CellIdentifierImpl startCellId = new CellIdentifierImpl(startCell);
                CellIdentifierImpl endCellId = new CellIdentifierImpl(endCell);

                spreadSheet.addRange(rangeName, startCellId, endCellId);
            }

            // Iterate over the cells and validate their positions
            STLCells stlCells = stlSheet.getSTLCells();
            for (STLCell stlCell : stlCells.getSTLCell()) {
                int row = stlCell.getRow();
                String columnStr = stlCell.getColumn();
                if (columnStr.length() != 1) {
                    return new SaveLoadFileDto(false, "Invalid cell column: " + columnStr);
                }
                char columnChar = columnStr.charAt(0);
                char rowChar = (char) (row + '0');
                String cellID = "" + columnChar + rowChar;

                spreadSheet.isValidCellID(cellID);
                CellIdentifierImpl cellId = new CellIdentifierImpl(cellID);

                // Evaluate the expression and validate function arguments
                Expression expression;
                try {
                    expression = parseExpression(stlCell.getSTLOriginalValue(), spreadSheet);
                } catch (IllegalArgumentException e) {
                    return new SaveLoadFileDto(false, "Invalid expression in cell (" + columnChar + "," + row + "): " + e.getMessage());
                }
                EffectiveValue effectiveValue = expression.evaluate(spreadSheet);

                CellImpl cell = new CellImpl(cellId, stlCell.getSTLOriginalValue(), 1, spreadSheet,username);
                cell.setEffectiveValue(effectiveValue);

                spreadSheet.getActiveCells().put(cell.getIdentifier(), cell);
            }

            // Update dependencies and influences
            spreadSheet.updateDependenciesAndInfluences();

            // Update the map
            sheetMap.put(stlSheet.getName(), sheetManager);

            return new SaveLoadFileDto(true, "File loaded successfully.");
        }  catch (JAXBException e) {
            return new SaveLoadFileDto(false, "XML parsing error: " + e.getMessage());
        } catch (Exception e) {
            return new SaveLoadFileDto(false, e.getMessage());
        }
    }



    private SheetDto convertSheetToSheetDto(SpreadSheet spreadSheet) {


        String name = spreadSheet.getSheetName();
        int version = sheetMap.get(spreadSheet.getSheetName()).getLatestVersion();
        String uploadedBy = sheetMap.get(spreadSheet.getSheetName()).getUploadedBy();
        Dimension dimensions = spreadSheet.getSheetDimentions();
        DimensionDto dimensionDto = new DimensionDto(dimensions.getNumRows(),dimensions.getNumCols(),dimensions.getWidthCol(),dimensions.getHeightRow());

        // Convert cells from Cell to CellDto
        Map<String, CellDto> cellDtos = convertCellsToCellDtos(spreadSheet.getActiveCells());

        // Convert Ranges from Ranges to RangesDto
        Map<String, RangeDto> cellsInRangeDto = convertRangesToRangeDtos(spreadSheet.getRanges());

        return new SheetDto(dimensionDto, name, version, cellDtos, cellsInRangeDto, uploadedBy);
    }

    private List<String> convertToListOfStrings(List<CellIdentifier> cellIdentifiers) {
        List<String> result = new ArrayList<>();
        for (CellIdentifier cellIdentifier : cellIdentifiers) {
            result.add(cellIdentifier.toString());
        }
        return result;
    }
    @Override
     public SheetDto updateCellWithSheetVersionUpdate(String cellid, String originalValue,String modifyingUserName,String sheetName,int sheetVersionToEdit)
    {
        return updateCell(cellid, originalValue, false,modifyingUserName,sheetName,sheetVersionToEdit);
    }

    @Override
    public SheetDto updateCellWithoutSheetVersionUpdate(String cellid, String originalValue,String modifyingUserName,String sheetName,int sheetVersionToEdit)
    {
        return updateCell(cellid, originalValue, true,modifyingUserName,sheetName,sheetVersionToEdit);
    }



    private SheetDto updateCell(String cellid, String originalValue, boolean isDynamicUpdate,String modifyingUserName,String sheetName,int sheetVersionToEdit) {
        SheetManager relevantManager = sheetMap.get(sheetName);
        if(sheetVersionToEdit != relevantManager.getLatestVersion() && !isDynamicUpdate)
        {
            throw new IllegalArgumentException("Cell update must be performed on the most recent sheet version");
        }
        SpreadSheet relevantSheet = relevantManager.getSheetByVersion(relevantManager.getLatestVersion());

        if (originalValue == null) {
            throw new IllegalArgumentException("Original value must be entered (can also be empty)");}

        // Check if cellid is null or empty
        if (cellid == null || cellid.isEmpty()) {
            throw new IllegalArgumentException("Cell ID cannot be empty");}

        relevantSheet.isValidCellID(cellid);
        CellIdentifierImpl cellIdentifier = new CellIdentifierImpl(cellid);

        UpdateResult updateRes;
        SpreadSheet updateSpreadSheet;
        updateRes = relevantSheet.updateCellValueAndCalculate(cellIdentifier, originalValue, isDynamicUpdate,modifyingUserName,relevantManager.getLatestVersion());
        if (updateRes.isSuccess()) {
            updateSpreadSheet = updateRes.getSheet();
            if(!isDynamicUpdate) {sheetMap.get(relevantSheet.getSheetName()).updateSheetVersion(updateSpreadSheet);}
            relevantSheet = updateSpreadSheet;
        } else {
            throw new RuntimeException(updateRes.getErrorMessage());}

        return convertSheetToSheetDto(relevantSheet);

    }

    @Override
    public SheetDto displaySheetByVersion(int version,String sheetName) {
        SheetManager relevantManager = sheetMap.get(sheetName);
        SpreadSheet relevantSheet = relevantManager.getSheetByVersion(relevantManager.getLatestVersion());

        // Retrieve the SpreadSheet from the version map
        SpreadSheet sheet = sheetMap.get(relevantSheet.getSheetName()).getSheetByVersion(version);
        if (sheet == null) {
            throw new IllegalArgumentException("No spreadsheet found for the specified version");}

       return convertSheetToSheetDto(sheet);
    }

    @Override
    public SheetDto addRange(String name, CellIdentifierImpl topLeft, CellIdentifierImpl bottomRight,String sheetName) {
        SheetManager relevantManager = sheetMap.get(sheetName);
        SpreadSheet relevantSheet = relevantManager.getSheetByVersion(relevantManager.getLatestVersion());

        relevantSheet.addRange(name, topLeft, bottomRight);
        // Convert cells from Cell to CellDto
        Map<String, CellDto> cellDtos = convertCellsToCellDtos(relevantSheet.getActiveCells());

        // Convert Ranges from Ranges to RangesDto
        Map<String, RangeDto> cellsInRangeDto = convertRangesToRangeDtos(relevantSheet.getRanges());

        Dimension dimensions = relevantSheet.getSheetDimentions();
        DimensionDto dimensionDto = new DimensionDto(dimensions.getNumRows(),dimensions.getNumCols(),dimensions.getWidthCol(),dimensions.getHeightRow());

        // Return a SheetDto with the retrieved SpreadSheet
        return new SheetDto(dimensionDto, relevantSheet.getSheetName(), relevantManager.getLatestVersion(), cellDtos, cellsInRangeDto,relevantManager.getUploadedBy());
    }

    @Override
    public SheetDto removeRange(String rangeName,String sheetName) {
        SheetManager relevantManager = sheetMap.get(sheetName);
        SpreadSheet relevantSheet = relevantManager.getSheetByVersion(relevantManager.getLatestVersion());

        relevantSheet.removeRange(rangeName);
        Map<String, CellDto> cellDtos = convertCellsToCellDtos(relevantSheet.getActiveCells());

        // Convert Ranges from Ranges to RangesDto
        Map<String, RangeDto> cellsInRangeDto = convertRangesToRangeDtos(relevantSheet.getRanges());

        Dimension dimensions = relevantSheet.getSheetDimentions();
        DimensionDto dimensionDto = new DimensionDto(dimensions.getNumRows(),dimensions.getNumCols(),dimensions.getWidthCol(),dimensions.getHeightRow());


        // Return a SheetDto with the retrieved SpreadSheet
        return new SheetDto(dimensionDto, relevantSheet.getSheetName(), relevantManager.getLatestVersion(), cellDtos, cellsInRangeDto,relevantManager.getUploadedBy());
    }

    @Override
    public SheetDto filterRangeByColumnsAndValues(Range range, Map<String, List<String>> selectedValuesForColumns,String sheetName) {

        SheetManager relevantManager = sheetMap.get(sheetName);
        SpreadSheet relevantSheet = relevantManager.getSheetByVersion(relevantManager.getLatestVersion());

        // Get the existing SheetDto from the engine
        SheetDto sheet = convertSheetToSheetDto(relevantSheet);

        CellIdentifier topLeft = range.getTopLeft();
        CellIdentifier bottomRight = range.getBottomRight();

        Map<String, CellDto> updatedCells = new HashMap<>(sheet.getCells());
        for (int row = topLeft.getRow(); row <= bottomRight.getRow(); row++) {
            boolean rowMatches = true;

            for (Map.Entry<String, List<String>> entry : selectedValuesForColumns.entrySet()) {
                String column = entry.getKey();
                List<String> selectedValues = entry.getValue();

                int colIndex = column.charAt(0) - 'A';
                CellDto cell = sheet.getCells().get(createCellId(row, colIndex));
                if (cell == null) {
                    rowMatches = false;
                    break;
                }
                else {
                    if (!selectedValues.contains(cell.getEffectiveValue())) {
                        rowMatches = false;
                        break;
                    }
                }
            }

            for (int col = topLeft.getCol() - 'A'; col <= bottomRight.getCol() - 'A'; col++) {
                String cellId = createCellId(row, col);
                CellDto cell = sheet.getCells().get(cellId);
                if (cell == null) {
                    cell = new CellDto(cellId,"","",0,new ArrayList<>(),new ArrayList<>(),
                            "","John Dow");
                }

                if (rowMatches) {
                    updatedCells.put(cellId, cell);
                } else {
                    updatedCells.put(cellId, new CellDto(cellId, "", "",
                            0, new ArrayList<>(), new ArrayList<>(), "",""));
                }
            }
        }


        return new SheetDto(sheet.getSheetDimension(),
                sheet.getSheetName(),
                sheet.getVersion(),
                updatedCells,
                sheet.getSheetRanges(),
                sheet.getUploadedBy());
    }

    @Override
    public SheetDto sortRange(Range range, List<String> colsToSort,String sheetName) {
        SheetManager relevantManager = sheetMap.get(sheetName);
        SpreadSheet relevantSheet = relevantManager.getSheetByVersion(relevantManager.getLatestVersion());

        // Get the existing SheetDto from the engine
        SheetDto sheet = convertSheetToSheetDto(relevantSheet);

        // Extract the range boundaries (top-left and bottom-right)
        CellIdentifier topLeft = range.getTopLeft();
        CellIdentifier bottomRight = range.getBottomRight();

        // Create a list to hold all rows within the range
        List<List<CellDto>> rowsInRange = new ArrayList<>();

        // Loop through each row in the range
        for (int row = topLeft.getRow(); row <= bottomRight.getRow(); row++) {
            List<CellDto> rowCells = new ArrayList<>();
            for (int col = (topLeft.getCol() - 'A'); col <= (bottomRight.getCol() - 'A'); col++) {
                String cellId = createCellId(row, col);  // Create a cell identifier
                rowCells.add(sheet.getCells().getOrDefault(cellId, new CellDto(cellId, "", "", 0, new ArrayList<>(), new ArrayList<>(),"","")));  // Fetch the cell or empty
            }
            rowsInRange.add(rowCells);  // Add the row to the list
        }

        // Sort the remaining rows based on the provided column order
        rowsInRange.sort((row1, row2) -> {
            for (String colToSort : colsToSort) {
                int colIndex = colToSort.charAt(0) - topLeft.getCol();  // Convert column letter to index
                CellDto cell1 = row1.get(colIndex);
                CellDto cell2 = row2.get(colIndex);
                Double value1 = 0.0;
                Double value2 = 0.0;
                if (Objects.equals(cell1.getEffectiveValue(), ""))
                    value1 = Double.MAX_VALUE;
                if (Objects.equals(cell2.getEffectiveValue(), ""))
                    value2 = Double.MAX_VALUE;
                if (value1 == 0.0)
                    value1 = parseNumericValue(cell1.getEffectiveValue());
                if (value2 == 0.0)
                    value2 = parseNumericValue(cell2.getEffectiveValue());

                // If one of the values is not numeric, skip this column for sorting
                if (value1 == null || value2 == null) {
                    throw new IllegalArgumentException("Column " + colToSort + " contains non-numeric values and cannot be used for sorting.");
                }

                int compareResult = value1.compareTo(value2);
                if (compareResult != 0) {
                    return compareResult;  // If not equal, return the result
                }
            }
            return 0;  // If all columns are equal, maintain the original order (stable sort)
        });

        // After sorting, update the cells in the sheet
        Map<String, CellDto> updatedCells = new HashMap<>(sheet.getCells());

        for (int rowIndex = 0; rowIndex < rowsInRange.size(); rowIndex++) {
            List<CellDto> sortedRow = rowsInRange.get(rowIndex);
            for (int col = 0; col < sortedRow.size(); col++) {
                char currentCol = (char) (topLeft.getCol() + col);
                int currentRow = topLeft.getRow() + rowIndex;
                String cellId = String.valueOf(currentCol) + currentRow;
                CellDto sortedCell = sortedRow.get(col);
                // Construct a new CellDto for each sorted cell
                CellDto cellDto = new CellDto(
                        cellId,
                        sortedCell.getOriginalValue(),
                        sortedCell.getEffectiveValue(),
                        sortedCell.getLastModifiedVersion(),
                        sortedCell.getDependencies(),  // Assuming this remains the same
                        sortedCell.getInfluences(),
                        sortedCell.getStyle(),
                        sortedCell.getModifiedBy()
                );

                updatedCells.put(cellId, cellDto);
            }
        }

        // Convert Ranges from Ranges to RangesDto
        Map<String, RangeDto> cellsInRangeDto = convertRangesToRangeDtos(relevantSheet.getRanges());

// Return the new SheetDto
        return new SheetDto(
                sheet.getSheetDimension(),
                sheet.getSheetName(),
                sheet.getVersion(),
                updatedCells,
                cellsInRangeDto,
                sheet.getUploadedBy()
        );

    }

    // Helper method to create cell IDs based on row and column numbers
    public String createCellId(int row, int col) {
        return String.valueOf((char) ('A' + col)) + (row);
    }

    // Helper method to parse numeric values from cell effective values
    private Double parseNumericValue(String value) {
        try {
            return value != null ? Double.parseDouble(value) : null;
        } catch (NumberFormatException e) {
            return null;  // Return null if the value is not numeric
        }
    }

    @Override
    public SheetDto addEmptyCell (String cellId,String sheetName) {
        SheetManager relevantManager = sheetMap.get(sheetName);
        SpreadSheet relevantSheet = relevantManager.getSheetByVersion(relevantManager.getLatestVersion());
        CellIdentifierImpl cellIdentifier = new CellIdentifierImpl(cellId);
        relevantSheet.addEmptyCell(cellIdentifier);
        return convertSheetToSheetDto(relevantSheet);
    }

    @Override
    public SheetDto setCellStyle(String cellid, String style,String sheetName) {
        SheetManager relevantManager = sheetMap.get(sheetName);
        SpreadSheet relevantSheet = relevantManager.getSheetByVersion(relevantManager.getLatestVersion());
        CellIdentifierImpl cellIdentifier = new CellIdentifierImpl(cellid);
        relevantSheet.getCell(cellIdentifier).setCellStyle(new CellStyleImpl(style));

        return convertSheetToSheetDto(relevantSheet);
    }

    @Override
    public int getLatestVersion(String sheetName){
        SheetManager relevantManager = sheetMap.get(sheetName);
        return relevantManager.getLatestVersion();
    }

    @Override
    public String evaluateOriginalValue(String originalValue,String sheetName)
    {
        SheetManager relevantManager = sheetMap.get(sheetName);
        SpreadSheet relevantSheet = relevantManager.getSheetByVersion(relevantManager.getLatestVersion());
        Expression expression = parseExpression(originalValue, relevantSheet);
        EffectiveValue newEffectiveValue = expression.evaluate(relevantSheet);
        return newEffectiveValue.getValue().toString();
    }

    private Map<String, CellDto> convertCellsToCellDtos(Map<CellIdentifier, Cell> activeCells) {
        Map<String, CellDto> cellDtos = new HashMap<>();
        for (Map.Entry<CellIdentifier, Cell> entry : activeCells.entrySet()) {
            Cell sheetCell = entry.getValue();
            CellDto cellDto = new CellDto(
                    sheetCell.getIdentifier().toString(),
                    sheetCell.getOriginalValue(),
                    sheetCell.getEffectiveValue().toString(),
                    sheetCell.getLastModifiedVersion(),
                    convertToListOfStrings(sheetCell.getDependencies()),
                    convertToListOfStrings(sheetCell.getInfluences()),
                    sheetCell.getCellStyle().getStyle(),
                    sheetCell.getModifiedBy()
            );
            cellDtos.put(entry.getKey().toString(), cellDto);
        }
        return cellDtos;
    }
    private Map<String, RangeDto> convertRangesToRangeDtos(Map<String, Range> ranges) {
        Map<String, RangeDto> rangeDtos = new HashMap<>();
        for (Map.Entry<String, Range> entry : ranges.entrySet()) {
            Range range = entry.getValue();
            RangeDto rangeDto = new RangeDto(
                    range.getName(),
                    range.getTopLeft().toString(),
                    range.getBottomRight().toString(),
                    convertToListOfStrings(range.getCellsInRange()),
                    range.isActive()
            );
            rangeDtos.put(entry.getKey(), rangeDto);
        }
        return rangeDtos;
    }


    @Override
    public List<SheetPermissionDto>getAllSheets(String userName)
    {
        List<SheetPermissionDto> sheetPermissionsDtos = new ArrayList<>();
        for(Map.Entry<String,SheetManager> entry : sheetMap.entrySet()) {
            SheetManager sheetManager = entry.getValue();
            SpreadSheet sheet = sheetManager.getSheetByVersion(sheetManager.getLatestVersion());

            SheetDto sheetDto = convertSheetToSheetDto(sheet);
            Permission userPermission = sheetManager.getPermission(userName);

            sheetPermissionsDtos.add(new SheetPermissionDto(sheetDto,userPermission));
        }

        return sheetPermissionsDtos;
    }

    @Override
    public PermissionInfoDto getUserPermissionFromSheet(String username, String sheetName)
    {
        SheetManager relevantManager = sheetMap.get(sheetName);
        return new PermissionInfoDto(username,relevantManager.getPermission(username),sheetName, RequestStatus.APPROVED);
    }

    @Override
    public List<PermissionInfoDto> getAllSheetPermissions(String sheetName) {
        SheetManager relevantManager = sheetMap.get(sheetName);

        Map<String, List<PermissionRequest>> approvedPermissions = relevantManager.getApprovedPermissions();
        Map<String, List<PermissionRequest>> pendingPermissions = relevantManager.getPendingPermissionRequests();
        Map<String, List<PermissionRequest>> deniedPermissions = relevantManager.getDeniedPermissionRequests();

        // Create a list to hold all PermissionInfoDto objects
        List<PermissionInfoDto> permissionInfoDtos = new ArrayList<>();

        // Add approved permissions to the list
        for (Map.Entry<String, List<PermissionRequest>> entry : approvedPermissions.entrySet()) {
            String userName = entry.getKey();
            List<PermissionRequest> requests = entry.getValue();

            for (PermissionRequest request : requests) {
                Permission permission = request.getPermission();
                permissionInfoDtos.add(new PermissionInfoDto(userName, permission, sheetName, RequestStatus.APPROVED));
            }
        }

        // Add pending permissions to the list
        for (Map.Entry<String, List<PermissionRequest>> entry : pendingPermissions.entrySet()) {
            String userName = entry.getKey();
            List<PermissionRequest> requests = entry.getValue();

            // Add each pending request for the user
            for (PermissionRequest request : requests) {
                Permission permission = request.getPermission();
                permissionInfoDtos.add(new PermissionInfoDto(userName, permission, sheetName, RequestStatus.PENDING));
            }
        }

        // Add denied permissions to the list
        for (Map.Entry<String, List<PermissionRequest>> entry : deniedPermissions.entrySet()) {
            String userName = entry.getKey();
            List<PermissionRequest> requests = entry.getValue();

            // Add each denied request for the user
            for (PermissionRequest request : requests) {
                Permission permission = request.getPermission();
                permissionInfoDtos.add(new PermissionInfoDto(userName, permission, sheetName, RequestStatus.REJECTED));
            }
        }

        return permissionInfoDtos;
    }

    @Override
    public void permissionRequest(int requestId,String sheetName, Permission permissionType, String message,String username){
        SheetManager relevantManager = sheetMap.get(sheetName);

        PermissionRequest request = new PermissionRequest(requestId,permissionType,username);
        if(!message.isEmpty()){request.setMessage(message);}
        relevantManager.addPendingPermissionRequest(request);
    }
    @Override
    public void permissionApproval(PermissionResponseDto responseDto) {
        PermissionRequestDto requestDto = responseDto.getPermissionRequestDto();
        PermissionRequest request = new PermissionRequest(requestDto.getId(), requestDto.getPermissionType(), requestDto.getRequester());
        SheetManager relevantManager = sheetMap.get(requestDto.getSheetName());
        relevantManager.ApprovePermission(request);
    }

    @Override
    public void permissionDenial(PermissionResponseDto responseDto)
    {
        PermissionRequestDto requestDto = responseDto.getPermissionRequestDto();
        PermissionRequest request = new PermissionRequest(requestDto.getId(),requestDto.getPermissionType(),requestDto.getRequester());
        SheetManager relevantManager = sheetMap.get(requestDto.getSheetName());
        relevantManager.denyPendingRequest(request);
    }

    @Override
    public List<SheetDto> getOwnedSheets(String username) {
        List<SheetDto> ownedSheets = new ArrayList<>();

        for (Map.Entry<String,SheetManager> entry : sheetMap.entrySet()) {
            SheetManager manager = entry.getValue();
            if (manager.getUploadedBy().equals(username))
                ownedSheets.add(convertSheetToSheetDto(manager.getSheetByVersion(manager.getLatestVersion())));
        }
        return ownedSheets;
    }
    @Override
    public List<PermissionRequestDto> getPendingRequests(String sheetName) {
        SheetManager relevantManager = sheetMap.get(sheetName);
        Map<String, List<PermissionRequest>> pendingPermissions = relevantManager.getPendingPermissionRequests();
        List<PermissionRequestDto> pendingRequests = new ArrayList<>();

        for (Map.Entry<String, List<PermissionRequest>> entry : pendingPermissions.entrySet()) {
            String userName = entry.getKey();
            List<PermissionRequest> requests = entry.getValue();

            // Convert each request in the list to a DTO
            for (PermissionRequest request : requests) {
                PermissionRequestDto permissionRequestDto = new PermissionRequestDto(
                        request.getId(),
                        sheetName,
                        request.getPermission(),
                        request.getMessage(),
                        userName
                );
                pendingRequests.add(permissionRequestDto);
            }
        }
        return pendingRequests;
    }


}