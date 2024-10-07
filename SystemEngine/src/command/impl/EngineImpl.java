package command.impl;

import command.api.Engine;
import dto.*;
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
import spreadsheet.range.impl.RangeImpl;
import spreadsheet.sheetimpl.DimensionImpl;
import spreadsheet.sheetimpl.SpreadSheetImpl;
import spreadsheet.sheetmanager.api.SheetManager;
import spreadsheet.sheetmanager.impl.SheetManagerImpl;
import spreadsheet.util.UpdateResult;
import xml.generated.*;

import java.io.*;
import java.util.*;

import static expressions.parser.FunctionParser.parseExpression;

public class EngineImpl implements Engine {
    private SpreadSheet currentSheet = null;
    private Map<String, SheetManager> sheetMap = new HashMap<>();

    @Override
    public SaveLoadFileDto loadFile(InputStream fileContent) {
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
                return new SaveLoadFileDto(false, "Invalid sheet size: Rows must be between 1 and 50, columns between 1 and 20, but got: Rows: " + numRows + ", Cols: " + numCols);
            }

            // Create Dimensions object
            Dimension sheetDimensions = new DimensionImpl(
                    numRows,
                    numCols,
                    stlSize.getColumnWidthUnits(),
                    stlSize.getRowsHeightUnits()
            );

            // Create a new SpreadSheet instance with the provided dimensions
            SheetManager sheetManager = new SheetManagerImpl(stlSheet.getName());
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

                CellImpl cell = new CellImpl(cellId, stlCell.getSTLOriginalValue(), 1, spreadSheet);
                cell.setEffectiveValue(effectiveValue);

                spreadSheet.getActiveCells().put(cell.getIdentifier(), cell);
            }



            // Update dependencies and influences
            spreadSheet.updateDependenciesAndInfluences();

            // Update the current sheet and version map
            this.currentSheet = spreadSheet;
            sheetMap.put(stlSheet.getName(), sheetManager);

            return new SaveLoadFileDto(true, "File loaded successfully.");
        }  catch (JAXBException e) {
            return new SaveLoadFileDto(false, "XML parsing error: " + e.getMessage());
        } catch (Exception e) {
            return new SaveLoadFileDto(false, "An unexpected error occurred: " + e.getMessage());
        }
    }

    @Override
    public SheetDto displayCurrentSpreadsheet() {

        String name = currentSheet.getSheetName();
        int version = sheetMap.get(currentSheet.getSheetName()).getLatestVersion();
        Dimension dimensions = currentSheet.getSheetDimentions();
        DimensionDto dimensionDto = new DimensionDto(dimensions.getNumRows(),dimensions.getNumCols(),dimensions.getWidthCol(),dimensions.getHeightRow());

        // Convert cells from Cell to CellDto
        Map<String, CellDto> cellDtos = convertCellsToCellDtos(currentSheet.getActiveCells());

        // Convert Ranges from Ranges to RangesDto
        Map<String, RangeDto> cellsInRangeDto = convertRangesToRangeDtos(currentSheet.getRanges());

        return new SheetDto(dimensionDto, name, version, cellDtos, cellsInRangeDto);
    }

    private List<String> convertToListOfStrings(List<CellIdentifierImpl> cellIdentifiers) {
        List<String> result = new ArrayList<>();
        for (CellIdentifierImpl cellIdentifier : cellIdentifiers) {
            result.add(cellIdentifier.toString());
        }
        return result;
    }
    @Override
     public SheetDto updateCellWithSheetVersionUpdate(String cellid, String originalValue)
    {
        return updateCell(cellid, originalValue, false);
    }

    @Override
    public SheetDto updateCellWithoutSheetVersionUpdate(String cellid, String originalValue)
    {
        return updateCell(cellid, originalValue, true);
    }



    private SheetDto updateCell(String cellid, String originalValue, boolean isDynamicUpdate) {

        if (originalValue == null) {
            throw new IllegalArgumentException("Original value must be entered (can also be empty)");}

        // Check if cellid is null or empty
        if (cellid == null || cellid.isEmpty()) {
            throw new IllegalArgumentException("Cell ID cannot be empty");}

        currentSheet.isValidCellID(cellid);
        CellIdentifierImpl cellIdentifier = new CellIdentifierImpl(cellid);

        UpdateResult updateRes;
        SpreadSheet updateSpreadSheet;
        updateRes = currentSheet.updateCellValueAndCalculate(cellIdentifier, originalValue, isDynamicUpdate);
        if (updateRes.isSuccess()) {
            updateSpreadSheet = updateRes.getSheet();
            if(!isDynamicUpdate) {sheetMap.get(currentSheet.getSheetName()).updateSheetVersion(updateSpreadSheet);}
            currentSheet = sheetMap.get(currentSheet.getSheetName()).getSheetByVersion(getLatestVersion());
        } else {
            throw new RuntimeException(updateRes.getErrorMessage());}

        // Convert cells from Cell to CellDto
        Map<String, CellDto> cellDtos = convertCellsToCellDtos(currentSheet.getActiveCells());

        // Convert Ranges from Ranges to RangesDto
        Map<String, RangeDto> cellsInRangeDto = convertRangesToRangeDtos(currentSheet.getRanges());
        Dimension dimensions = currentSheet.getSheetDimentions();
        DimensionDto dimensionDto = new DimensionDto(dimensions.getNumRows(),dimensions.getNumCols(),dimensions.getWidthCol(),dimensions.getHeightRow());

        // Return a SheetDto with the retrieved SpreadSheet
        return new SheetDto(dimensionDto, currentSheet.getSheetName(),getLatestVersion(), cellDtos, cellsInRangeDto);

    }

    @Override
    public SheetDto displaySheetByVersion(int version) {
        // Retrieve the SpreadSheet from the version map
        SpreadSheet sheet = sheetMap.get(currentSheet.getSheetName()).getSheetByVersion(version);
        if (sheet == null) {
            throw new IllegalArgumentException("No spreadsheet found for the specified version");}

        // Convert cells from Cell to CellDto
        Map<String, CellDto> cellDtos = convertCellsToCellDtos(sheet.getActiveCells());

        // Convert Ranges from Ranges to RangesDto
        Map<String, RangeDto> cellsInRangeDto = convertRangesToRangeDtos(sheet.getRanges());

        Dimension dimensions = sheet.getSheetDimentions();
        DimensionDto dimensionDto = new DimensionDto(dimensions.getNumRows(),dimensions.getNumCols(),dimensions.getWidthCol(),dimensions.getHeightRow());

        // Return a SheetDto with the retrieved SpreadSheet
        return new SheetDto(dimensionDto, sheet.getSheetName(), version, cellDtos,  cellsInRangeDto);
    }

    @Override
    public SheetDto addRange(String name, CellIdentifierImpl topLeft, CellIdentifierImpl bottomRight) {
        currentSheet.addRange(name, topLeft, bottomRight);
        // Convert cells from Cell to CellDto
        Map<String, CellDto> cellDtos = convertCellsToCellDtos(currentSheet.getActiveCells());

        // Convert Ranges from Ranges to RangesDto
        Map<String, RangeDto> cellsInRangeDto = convertRangesToRangeDtos(currentSheet.getRanges());

        Dimension dimensions = currentSheet.getSheetDimentions();
        DimensionDto dimensionDto = new DimensionDto(dimensions.getNumRows(),dimensions.getNumCols(),dimensions.getWidthCol(),dimensions.getHeightRow());

        // Return a SheetDto with the retrieved SpreadSheet
        return new SheetDto(dimensionDto, currentSheet.getSheetName(),getLatestVersion(), cellDtos, cellsInRangeDto);
    }

    @Override
    public SheetDto removeRange(String rangeName) {
        currentSheet.removeRange(rangeName);
        Map<String, CellDto> cellDtos = convertCellsToCellDtos(currentSheet.getActiveCells());

        // Convert Ranges from Ranges to RangesDto
        Map<String, RangeDto> cellsInRangeDto = convertRangesToRangeDtos(currentSheet.getRanges());

        Dimension dimensions = currentSheet.getSheetDimentions();
        DimensionDto dimensionDto = new DimensionDto(dimensions.getNumRows(),dimensions.getNumCols(),dimensions.getWidthCol(),dimensions.getHeightRow());


        // Return a SheetDto with the retrieved SpreadSheet
        return new SheetDto(dimensionDto, currentSheet.getSheetName(),getLatestVersion(), cellDtos, cellsInRangeDto);
    }

    @Override
    public Integer getLatestVersion() {return sheetMap.get(currentSheet.getSheetName()).getLatestVersion();}

    @Override
    public boolean isFileLoaded() {
        return currentSheet != null;
    }

    @Override
    public SheetDto filterRangeByColumnsAndValues(Range range, Map<String, List<String>> selectedValuesForColumns) {
        SheetDto sheet = displayCurrentSpreadsheet();

        CellIdentifierImpl topLeft = range.getTopLeft();
        CellIdentifierImpl bottomRight = range.getBottomRight();

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
                            "");
                }

                if (rowMatches) {
                    updatedCells.put(cellId, cell);
                } else {
                    updatedCells.put(cellId, new CellDto(cellId, "", "",
                            0, new ArrayList<>(), new ArrayList<>(), ""));
                }
            }
        }

        return new SheetDto(sheet.getSheetDimension(),
                sheet.getSheetName(),
                sheet.getVersion(),
                updatedCells,
                sheet.getSheetRanges());
    }

    @Override
    public SheetDto sortRange(Range range, List<String> colsToSort) {
        // Get the existing SheetDto from the engine
        SheetDto sheet = displayCurrentSpreadsheet();

        // Extract the range boundaries (top-left and bottom-right)
        CellIdentifierImpl topLeft = range.getTopLeft();
        CellIdentifierImpl bottomRight = range.getBottomRight();

        // Create a list to hold all rows within the range
        List<List<CellDto>> rowsInRange = new ArrayList<>();

        // Loop through each row in the range
        for (int row = topLeft.getRow(); row <= bottomRight.getRow(); row++) {
            List<CellDto> rowCells = new ArrayList<>();
            for (int col = (topLeft.getCol() - 'A'); col <= (bottomRight.getCol() - 'A'); col++) {
                String cellId = createCellId(row, col);  // Create a cell identifier
                rowCells.add(sheet.getCells().getOrDefault(cellId, new CellDto(cellId, "", "", 0, new ArrayList<>(), new ArrayList<>(),"")));  // Fetch the cell or empty
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
                        sortedCell.getStyle()
                );

                updatedCells.put(cellId, cellDto);
            }
        }

        // Convert Ranges from Ranges to RangesDto
        Map<String, RangeDto> cellsInRangeDto = convertRangesToRangeDtos(currentSheet.getRanges());

// Return the new SheetDto
        return new SheetDto(
                sheet.getSheetDimension(),
                sheet.getSheetName(),
                sheet.getVersion(),
                updatedCells,
                cellsInRangeDto
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
    public SheetDto addEmptyCell (String cellId) {
        CellIdentifierImpl cellIdentifier = new CellIdentifierImpl(cellId);
        currentSheet.addEmptyCell(cellIdentifier);
        // Convert cells from Cell to CellDto
        Map<String, CellDto> cellDtos = convertCellsToCellDtos(currentSheet.getActiveCells());

        // Convert Ranges from Ranges to RangesDto
        Map<String, RangeDto> cellsInRangeDto = convertRangesToRangeDtos(currentSheet.getRanges());

        Dimension dimensions = currentSheet.getSheetDimentions();
        DimensionDto dimensionDto = new DimensionDto(dimensions.getNumRows(),dimensions.getNumCols(),dimensions.getWidthCol(),dimensions.getHeightRow());

        // Return a SheetDto with the SpreadSheet
        return new SheetDto(dimensionDto, currentSheet.getSheetName(), getLatestVersion(), cellDtos, cellsInRangeDto);
    }

    @Override
    public SheetDto setCellStyle(String cellid, String style) {
        CellIdentifierImpl cellIdentifier = new CellIdentifierImpl(cellid);
        currentSheet.getCell(cellIdentifier).setCellStyle(new CellStyleImpl(style));

        // Convert cells from Cell to CellDto
        Map<String, CellDto> cellDtos = convertCellsToCellDtos(currentSheet.getActiveCells());

        // Convert Ranges from Ranges to RangesDto
        Map<String, RangeDto> cellsInRangeDto = convertRangesToRangeDtos(currentSheet.getRanges());

        Dimension dimensions = currentSheet.getSheetDimentions();
        DimensionDto dimensionDto = new DimensionDto(dimensions.getNumRows(),dimensions.getNumCols(),dimensions.getWidthCol(),dimensions.getHeightRow());

        // Return a SheetDto with the retrieved SpreadSheet
        return new SheetDto(dimensionDto, currentSheet.getSheetName(), getLatestVersion(), cellDtos, cellsInRangeDto);

    }

    @Override
    public String evaluateOriginalValue(String originalValue)
    {
        Expression expression = parseExpression(originalValue, currentSheet);
        EffectiveValue newEffectiveValue = expression.evaluate(currentSheet);
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
                    sheetCell.getCellStyle().getStyle()
            );
            cellDtos.put(entry.getKey().toString(), cellDto);
        }
        return cellDtos;
    }
    private Map<String, RangeDto> convertRangesToRangeDtos(Map<String, RangeImpl> ranges) {
        Map<String, RangeDto> rangeDtos = new HashMap<>();
        for (Map.Entry<String, RangeImpl> entry : ranges.entrySet()) {
            RangeImpl range = entry.getValue();
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
    public SheetDto setCurrentSheet(String sheetName) {
       SheetManager sheetManager = sheetMap.get(sheetName);
       currentSheet = sheetManager.getSheetByVersion(getLatestVersion());
        // Convert cells from Cell to CellDto
        Map<String, CellDto> cellDtos = convertCellsToCellDtos(currentSheet.getActiveCells());

        // Convert Ranges from Ranges to RangesDto
        Map<String, RangeDto> cellsInRangeDto = convertRangesToRangeDtos(currentSheet.getRanges());

        Dimension dimensions = currentSheet.getSheetDimentions();
        DimensionDto dimensionDto = new DimensionDto(dimensions.getNumRows(),dimensions.getNumCols(),dimensions.getWidthCol(),dimensions.getHeightRow());

        // Return a SheetDto with the retrieved SpreadSheet
        return new SheetDto(dimensionDto, currentSheet.getSheetName(), getLatestVersion(), cellDtos, cellsInRangeDto);
    }
    @Override
    public SheetDto[] getAllSheets()
    {
        List<SheetDto> sheetDtos = new ArrayList<>();
        for(Map.Entry<String,SheetManager> entry : sheetMap.entrySet()) {
            SheetManager sheetManager = entry.getValue();
            SpreadSheet sheet = sheetManager.getSheetByVersion(sheetManager.getLatestVersion());

            Map<String, CellDto> cellDtos = convertCellsToCellDtos(sheet.getActiveCells());

            // Convert Ranges from Ranges to RangesDto
            Map<String, RangeDto> cellsInRangeDto = convertRangesToRangeDtos(sheet.getRanges());

            Dimension dimensions = sheet.getSheetDimentions();
            DimensionDto dimensionDto = new DimensionDto(dimensions.getNumRows(),dimensions.getNumCols(),dimensions.getWidthCol(),dimensions.getHeightRow());

            // Return a SheetDto with the retrieved SpreadSheet
            sheetDtos.add(new SheetDto(dimensionDto, sheet.getSheetName(), getLatestVersion(), cellDtos, cellsInRangeDto));

        }

        return sheetDtos.toArray(new SheetDto[0]);
    }
}