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
import spreadsheet.cell.impl.CellIdentifierImpl;
import spreadsheet.cell.impl.CellImpl;
import spreadsheet.range.api.Range;
import spreadsheet.range.impl.RangeImpl;
import spreadsheet.sheetimpl.DimensionImpl;
import spreadsheet.sheetimpl.SpreadSheetImpl;
import spreadsheet.util.UpdateResult;
import xml.generated.*;

import java.io.*;
import java.util.*;

import static expressions.parser.FunctionParser.parseExpression;

public class EngineImpl implements Engine {
    private SpreadSheet currentSheet = null;
    private Map<Integer, SpreadSheet> sheetVersionMap = new HashMap<>();

    @Override
    public SaveLoadFileDto loadFile(String path) {
        File file = new File(path);
        if (!path.toLowerCase().endsWith(".xml")) {
            return new SaveLoadFileDto(false, "Invalid file type: Only XML files are supported.");
        }
        if (!file.exists()) {
            return new SaveLoadFileDto(false, "File not found: " + path);
        }

        InputStream inputStream;
        try {
            inputStream = new FileInputStream(file);
            // Initialize JAXB context and unmarshaller
            JAXBContext jaxbContext = JAXBContext.newInstance(STLSheet.class);
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();

            // Unmarshal the XML file to STLSheet object
            STLSheet stlSheet = (STLSheet) unmarshaller.unmarshal(new File(path));

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
            SpreadSheet spreadSheet = new SpreadSheetImpl(stlSheet.getName(), 1, sheetDimensions);

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

            spreadSheet.setAmountOfCellsChangedInVersion(spreadSheet.getActiveCells().size());

            // Update dependencies and influences
            spreadSheet.updateDependenciesAndInfluences();

            // Update the current sheet and version map
            this.currentSheet = spreadSheet;
            sheetVersionMap = new HashMap<>();
            sheetVersionMap.put(1, currentSheet);
            return new SaveLoadFileDto(true, "File loaded successfully.");
        } catch (FileNotFoundException e) {
            return new SaveLoadFileDto(false, "File not found: " + path);
        } catch (JAXBException e) {
            return new SaveLoadFileDto(false, "XML parsing error: " + e.getMessage());
        } catch (Exception e) {
            return new SaveLoadFileDto(false, "An unexpected error occurred: " + e.getMessage());
        }
    }

    @Override
    public SheetDto displayCurrentSpreadsheet() {

        String name = currentSheet.getName();
        int version = currentSheet.getVersion();
        Dimension dimensions = currentSheet.getSheetDimentions();

        // Convert cells from Cell to CellDto
        Map<String, CellDto> cellDtos = new HashMap<>();
        for (Map.Entry<CellIdentifier, Cell> entry : currentSheet.getActiveCells().entrySet()) {
            Cell cell = entry.getValue();
            CellDto cellDto = new CellDto(
                    cell.getIdentifier().toString(),
                    cell.getOriginalValue(),
                    cell.getEffectiveValue().toString(),
                    cell.getLastModifiedVersion(),
                    convertToListOfStrings(cell.getDependencies()),
                    convertToListOfStrings(cell.getInfluences()),
                    cell.getCellStyle(),
                    cell.getCellAlignment()
            );
            cellDtos.put(entry.getKey().toString(), cellDto);
        }

        // Convert Ranges from Ranges to RangesDto
        Map<String, RangeDto> cellsInRangeDto = new HashMap<>();
        for (Map.Entry<String, RangeImpl> entry : currentSheet.getRanges().entrySet()) {
            RangeImpl range = entry.getValue();
            RangeDto rangeDto = new RangeDto(
                    range.getName(),
                    range.getTopLeft().toString(),
                    range.getBottomRight().toString(),
                    convertToListOfStrings(range.getCellsInRange()),
                    range.isActive()
            );
            cellsInRangeDto.put(entry.getKey(), rangeDto);
        }


        return new SheetDto(dimensions.getNumCols(), dimensions.getNumRows(), dimensions.getWidthCol(), dimensions.getHeightRow(), name, version, cellDtos, currentSheet.getAmountOfCellsChangedInVersion(), cellsInRangeDto);
    }


    @Override
    public CellDto displayCellValue(String cellid) {

        // Check if cellid is null or empty
        if (cellid == null || cellid.isEmpty()) {
            throw new IllegalArgumentException("Cell ID cannot be empty");
        }

        // Check if currentSheet is null
        if (currentSheet == null) {
            throw new IllegalStateException("Current sheet is not available");
        }

        currentSheet.isValidCellID(cellid);
        CellIdentifierImpl cellIdentifier = new CellIdentifierImpl(cellid);

        // Retrieve the cell from the currentSheet
        Cell cell = currentSheet.getCell(cellIdentifier);

        if (cell == null || cell.getOriginalValue() == null) {
            return new CellDto(
                    cellIdentifier.toString(),                      // The identifier of the cell
                    "",                             // Default original value
                    "",                             // Default effective value
                    0,    // Last modified version (could be current version)
                    Collections.emptyList(),              // No dependencies
                    Collections.emptyList(),
                    "",
                    "-fx-alignment: center;"// No influences
            );
        }


        // Create and return a CellDto
        return new CellDto(
                cell.getIdentifier().toString(),
                cell.getOriginalValue(),
                cell.getEffectiveValue().toString(),
                cell.getLastModifiedVersion(),
                convertToListOfStrings(cell.getDependencies()),
                convertToListOfStrings(cell.getInfluences()),
                cell.getCellStyle(),
                cell.getCellAlignment()
        );
    }

    private List<String> convertToListOfStrings(List<CellIdentifierImpl> cellIdentifiers) {
        List<String> result = new ArrayList<>();
        for (CellIdentifierImpl cellIdentifier : cellIdentifiers) {
            result.add(cellIdentifier.toString());
        }
        return result;
    }

    @Override
    public SheetDto updateCell(String cellid, String originalValue) {

        if (originalValue == null) {
            throw new IllegalArgumentException("Original value must be entered (can also be empty)");
        }

        // Check if cellid is null or empty
        if (cellid == null || cellid.isEmpty()) {
            throw new IllegalArgumentException("Cell ID cannot be empty");
        }

        currentSheet.isValidCellID(cellid);
        CellIdentifierImpl cellIdentifier = new CellIdentifierImpl(cellid);

        UpdateResult updateRes;
        SpreadSheet updateSpreadSheet;
        updateRes = currentSheet.updateCellValueAndCalculate(cellIdentifier, originalValue);
        if (updateRes.isSuccess()) {
            updateSpreadSheet = updateRes.getSheet();
            sheetVersionMap.put(updateSpreadSheet.getVersion(), updateSpreadSheet);
            currentSheet = updateSpreadSheet;
        } else {
            throw new RuntimeException(updateRes.getErrorMessage());
        }

        // Convert cells from Cell to CellDto
        Map<String, CellDto> cellDtos = new HashMap<>();
        for (Map.Entry<CellIdentifier, Cell> entry : currentSheet.getActiveCells().entrySet()) {
            Cell sheetCells = entry.getValue();
            CellDto cellDto = new CellDto(
                    sheetCells.getIdentifier().toString(),
                    sheetCells.getOriginalValue(),
                    sheetCells.getEffectiveValue().toString(),
                    sheetCells.getLastModifiedVersion(),
                    convertToListOfStrings(sheetCells.getDependencies()),
                    convertToListOfStrings(sheetCells.getInfluences()),
                    sheetCells.getCellStyle(),
                    sheetCells.getCellAlignment()
            );
            cellDtos.put(entry.getKey().toString(), cellDto);
        }

        // Convert Ranges from Ranges to RangesDto
        Map<String, RangeDto> cellsInRangeDto = new HashMap<>();
        for (Map.Entry<String, RangeImpl> entry : currentSheet.getRanges().entrySet()) {
            RangeImpl range = entry.getValue();
            RangeDto rangeDto = new RangeDto(
                    range.getName(),
                    range.getTopLeft().toString(),
                    range.getBottomRight().toString(),
                    convertToListOfStrings(range.getCellsInRange()),
                    range.isActive()
            );
            cellsInRangeDto.put(entry.getKey(), rangeDto);
        }

        // Return a SheetDto with the retrieved SpreadSheet
        return new SheetDto(currentSheet.getSheetDimentions().getNumRows(), currentSheet.getSheetDimentions().getNumRows(),
                currentSheet.getSheetDimentions().getWidthCol(), currentSheet.getSheetDimentions().getHeightRow(), currentSheet.getName(),
                currentSheet.getVersion(), cellDtos, currentSheet.getAmountOfCellsChangedInVersion(), cellsInRangeDto);

    }


    @Override
    public SheetDto displaySheetByVersion(int version) {
        // Retrieve the SpreadSheet from the version map
        SpreadSheet sheet = sheetVersionMap.get(version);
        if (sheet == null) {
            throw new IllegalArgumentException("No spreadsheet found for the specified version");
        }

        // Convert cells from Cell to CellDto
        Map<String, CellDto> cellDtos = new HashMap<>();
        for (Map.Entry<CellIdentifier, Cell> entry : sheet.getActiveCells().entrySet()) {
            Cell cell = entry.getValue();
            CellDto cellDto = new CellDto(
                    cell.getIdentifier().toString(),
                    cell.getOriginalValue(),
                    cell.getEffectiveValue().toString(),
                    cell.getLastModifiedVersion(),
                    convertToListOfStrings(cell.getDependencies()),
                    convertToListOfStrings(cell.getInfluences()),
                    cell.getCellStyle(),
                    cell.getCellAlignment()
            );
            cellDtos.put(entry.getKey().toString(), cellDto);
        }

        Map<String, RangeDto> cellsInRangeDto = new HashMap<>();
        for (Map.Entry<String, RangeImpl> rangeEntry : currentSheet.getRanges().entrySet()) {
            RangeImpl range = rangeEntry.getValue();
            RangeDto rangeDto = new RangeDto(
                    range.getName(),
                    range.getTopLeft().toString(),
                    range.getBottomRight().toString(),
                    convertToListOfStrings(range.getCellsInRange()),
                    range.isActive()
            );
            cellsInRangeDto.put(rangeEntry.getKey().toString(), rangeDto);
        }


        // Return a SheetDto with the retrieved SpreadSheet
        return new SheetDto(sheet.getSheetDimentions().getNumRows(), sheet.getSheetDimentions().getNumRows(), sheet.getSheetDimentions().getWidthCol(),
                sheet.getSheetDimentions().getHeightRow(), sheet.getName(), sheet.getVersion(), cellDtos, currentSheet.getAmountOfCellsChangedInVersion(), cellsInRangeDto);
    }

    public SaveLoadFileDto saveState(String path) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(path))) {
            oos.writeObject(currentSheet);
            oos.writeObject(sheetVersionMap);
        } catch (FileNotFoundException e) {
            return new SaveLoadFileDto(false, "File not found: " + path);
        } catch (IOException e) {
            return new SaveLoadFileDto(false, "IO error while saving: " + e.getMessage());
        } catch (Exception e) {
            return new SaveLoadFileDto(false, "An unexpected error occurred: " + e.getMessage());
        }
        return new SaveLoadFileDto(true, "File saved successfully");

    }

    public SaveLoadFileDto loadSavedState(String path) {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(path))) {
            currentSheet = (SpreadSheet) ois.readObject();
            sheetVersionMap = (Map<Integer, SpreadSheet>) ois.readObject();
            System.out.println("System state loaded successfully.");
        } catch (FileNotFoundException e) {
            return new SaveLoadFileDto(false, "File not found: " + path);
        } catch (IOException e) {
            return new SaveLoadFileDto(false, "IO error while loading: " + e.getMessage());
        } catch (ClassNotFoundException e) {
            return new SaveLoadFileDto(false, "Class not found during loading: " + e.getMessage());
        } catch (Exception e) {
            return new SaveLoadFileDto(false, "An unexpected error occurred: " + e.getMessage());
        }
        return new SaveLoadFileDto(true, "File loaded successfully");
    }

    @Override
    public RangeDto addRange(String name, CellIdentifierImpl topLeft, CellIdentifierImpl bottomRight) {
        currentSheet.addRange(name, topLeft, bottomRight);
        RangeImpl addedRange = currentSheet.getRange(name);
        return new RangeDto(addedRange.getName(), addedRange.getTopLeft().toString(), addedRange.getBottomRight().toString(), convertToListOfStrings(addedRange.getCellsInRange()), addedRange.isActive());
    }

    @Override
    public void removeRange(String rangeName) {
        currentSheet.removeRange(rangeName);
    }

    @Override
    public RangeDto getRange(String rangeName) {
        RangeImpl range = currentSheet.getRange(rangeName);
        return new RangeDto(range.getName(), range.getTopLeft().toString(), range.getBottomRight().toString(), convertToListOfStrings(range.getCellsInRange()), range.isActive());
    }

    @Override
    public String[] getAvailableVersions() {
        return sheetVersionMap.keySet().stream()
                .map(String::valueOf)  // Convert Integer to String
                .toArray(String[]::new);
    }

    @Override
    public Integer getLatestVersion() {
        return sheetVersionMap.size();
    }

    @Override
    public boolean isFileLoaded() {
        if (currentSheet == null) {
            return false;
        }
        return true;
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
                rowCells.add(sheet.getCells().getOrDefault(cellId, new CellDto(cellId, "", "", 0, new ArrayList<>(), new ArrayList<>(),"","-fx-alignment: center;")));  // Fetch the cell or empty
            }
            rowsInRange.add(rowCells);  // Add the row to the list
        }

        int countOfHeaders = 0;

        // Filter out header rows (non-numeric values in all columnsToSort)
        List<List<CellDto>> rowsWithoutHeaders = new ArrayList<>();
        for (List<CellDto> row : rowsInRange) {
            if (!isHeaderRow(row, colsToSort, topLeft.getCol()) || countOfHeaders == 1) {
                rowsWithoutHeaders.add(row);// Add only rows that are not headers
            }
            else
                countOfHeaders++;
        }

        // Sort the remaining rows based on the provided column order
        rowsWithoutHeaders.sort((row1, row2) -> {
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

        for (int rowIndex = 0; rowIndex < rowsWithoutHeaders.size(); rowIndex++) {
            List<CellDto> sortedRow = rowsWithoutHeaders.get(rowIndex);
            for (int col = 0; col < sortedRow.size(); col++) {
                char currentCol = (char) (topLeft.getCol() + col);
                int currentRow = topLeft.getRow() + rowIndex+countOfHeaders;
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
                        sortedCell.getAlignment()

                );

                updatedCells.put(cellId, cellDto);
            }
        }

// Convert ranges (same as in displaySheetByVersion)
        Map<String, RangeDto> cellsInRangeDto = new HashMap<>();
        for (Map.Entry<String, RangeImpl> rangeEntry : currentSheet.getRanges().entrySet()) {
            RangeImpl rangeEntryValue = rangeEntry.getValue();
            RangeDto rangeDto = new RangeDto(
                    range.getName(),
                    range.getTopLeft().toString(),
                    range.getBottomRight().toString(),
                    convertToListOfStrings(rangeEntryValue.getCellsInRange()),
                    rangeEntryValue.isActive()
            );
            cellsInRangeDto.put(rangeEntry.getKey(), rangeDto);
        }

// Return the new SheetDto
        return new SheetDto(
                sheet.getNumRows(),
                sheet.getNumRows(),
                sheet.getWidthCol(),
                sheet.getHeightRow(),
                sheet.getName(),
                sheet.getVersion(),
                updatedCells,
                currentSheet.getAmountOfCellsChangedInVersion(),
                cellsInRangeDto
        );

    }

    // Helper method to create cell IDs based on row and column numbers
    private String createCellId(int row, int col) {
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

    // Helper method to check if the row contains headers (non-numeric values)
    private boolean isHeaderRow(List<CellDto> row, List<String> colsToSort, char colOffset) {
        for (String colToSort : colsToSort) {
            int colIndex = colToSort.charAt(0) - colOffset ;  // Convert column letter to index
            String value = row.get(colIndex).getEffectiveValue();
            if (parseNumericValue(value) != null || Objects.equals(value, "")) {
                return false;  // If it's numeric, it's not a header row
            }
        }
        return true;  // All values are non-numeric, so it's a header row
    }

    @Override
    public SheetDto addCell (String cellId) {
        CellIdentifierImpl cellIdentifier = new CellIdentifierImpl(cellId);
        currentSheet.getCellEffectiveValue(cellIdentifier);

        Map<String, CellDto> cellDtos = new HashMap<>();
        for (Map.Entry<CellIdentifier, Cell> entry : currentSheet.getActiveCells().entrySet()) {
            Cell cell = entry.getValue();
            CellDto cellDto = new CellDto(
                    cell.getIdentifier().toString(),
                    cell.getOriginalValue(),
                    cell.getEffectiveValue().toString(),
                    cell.getLastModifiedVersion(),
                    convertToListOfStrings(cell.getDependencies()),
                    convertToListOfStrings(cell.getInfluences()),
                    "",
                    "-fx-alignment: center;"
            );
            cellDtos.put(entry.getKey().toString(), cellDto);
        }

        Map<String, RangeDto> cellsInRangeDto = new HashMap<>();
        for (Map.Entry<String, RangeImpl> rangeEntry : currentSheet.getRanges().entrySet()) {
            RangeImpl range = rangeEntry.getValue();
            RangeDto rangeDto = new RangeDto(
                    range.getName(),
                    range.getTopLeft().toString(),
                    range.getBottomRight().toString(),
                    convertToListOfStrings(range.getCellsInRange()),
                    range.isActive()
            );
            cellsInRangeDto.put(rangeEntry.getKey().toString(), rangeDto);
        }


        // Return a SheetDto with the SpreadSheet
        return new SheetDto(currentSheet.getSheetDimentions().getNumRows(), currentSheet.getSheetDimentions().getNumRows(), currentSheet.getSheetDimentions().getWidthCol(),
                currentSheet.getSheetDimentions().getHeightRow(), currentSheet.getName(), currentSheet.getVersion(), cellDtos, currentSheet.getAmountOfCellsChangedInVersion(), cellsInRangeDto);
    }

    @Override
    public SheetDto setCellStyle(String cellid, String style) {
        CellIdentifierImpl cellIdentifier = new CellIdentifierImpl(cellid);
        currentSheet.getCell(cellIdentifier).setCellStyle(style);

        Map<String, CellDto> cellDtos = new HashMap<>();
        for (Map.Entry<CellIdentifier, Cell> entry : currentSheet.getActiveCells().entrySet()) {
            Cell cell = entry.getValue();
            CellDto cellDto = new CellDto(
                    cell.getIdentifier().toString(),
                    cell.getOriginalValue(),
                    cell.getEffectiveValue().toString(),
                    cell.getLastModifiedVersion(),
                    convertToListOfStrings(cell.getDependencies()),
                    convertToListOfStrings(cell.getInfluences()),
                    cell.getCellStyle(),
                    cell.getCellAlignment()
            );
            cellDtos.put(entry.getKey().toString(), cellDto);
        }

        Map<String, RangeDto> cellsInRangeDto = new HashMap<>();
        for (Map.Entry<String, RangeImpl> rangeEntry : currentSheet.getRanges().entrySet()) {
            RangeImpl range = rangeEntry.getValue();
            RangeDto rangeDto = new RangeDto(
                    range.getName(),
                    range.getTopLeft().toString(),
                    range.getBottomRight().toString(),
                    convertToListOfStrings(range.getCellsInRange()),
                    range.isActive()
            );
            cellsInRangeDto.put(rangeEntry.getKey().toString(), rangeDto);
        }


        // Return a SheetDto with the retrieved SpreadSheet
        return new SheetDto(currentSheet.getSheetDimentions().getNumRows(), currentSheet.getSheetDimentions().getNumRows(), currentSheet.getSheetDimentions().getWidthCol(),
                currentSheet.getSheetDimentions().getHeightRow(), currentSheet.getName(), currentSheet.getVersion(), cellDtos, currentSheet.getAmountOfCellsChangedInVersion(), cellsInRangeDto);

    }

    @Override
    public SheetDto setCellAlignment(String cellid, String alignment) {
        CellIdentifierImpl cellIdentifier = new CellIdentifierImpl(cellid);
        currentSheet.getCell(cellIdentifier).setCellAlignment(alignment);

        Map<String, CellDto> cellDtos = new HashMap<>();
        for (Map.Entry<CellIdentifier, Cell> entry : currentSheet.getActiveCells().entrySet()) {
            Cell cell = entry.getValue();
            CellDto cellDto = new CellDto(
                    cell.getIdentifier().toString(),
                    cell.getOriginalValue(),
                    cell.getEffectiveValue().toString(),
                    cell.getLastModifiedVersion(),
                    convertToListOfStrings(cell.getDependencies()),
                    convertToListOfStrings(cell.getInfluences()),
                    cell.getCellStyle(),
                    cell.getCellAlignment()
            );
            cellDtos.put(entry.getKey().toString(), cellDto);
        }

        Map<String, RangeDto> cellsInRangeDto = new HashMap<>();
        for (Map.Entry<String, RangeImpl> rangeEntry : currentSheet.getRanges().entrySet()) {
            RangeImpl range = rangeEntry.getValue();
            RangeDto rangeDto = new RangeDto(
                    range.getName(),
                    range.getTopLeft().toString(),
                    range.getBottomRight().toString(),
                    convertToListOfStrings(range.getCellsInRange()),
                    range.isActive()
            );
            cellsInRangeDto.put(rangeEntry.getKey().toString(), rangeDto);
        }


        // Return a SheetDto with the retrieved SpreadSheet
        return new SheetDto(currentSheet.getSheetDimentions().getNumRows(), currentSheet.getSheetDimentions().getNumRows(), currentSheet.getSheetDimentions().getWidthCol(),
                currentSheet.getSheetDimentions().getHeightRow(), currentSheet.getName(), currentSheet.getVersion(), cellDtos, currentSheet.getAmountOfCellsChangedInVersion(), cellsInRangeDto);

    }

}

