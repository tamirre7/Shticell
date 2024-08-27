package command.impl;

import command.api.Engine;
import dto.CellDto;
import dto.SheetDto;
import dto.VerDto;
import expressions.api.Expression;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;
import spreadsheet.api.Dimension;
import spreadsheet.api.SpreadSheet;
import spreadsheet.cell.api.Cell;
import spreadsheet.cell.api.CellIdentifier;
import spreadsheet.cell.api.CellType;
import spreadsheet.cell.api.EffectiveValue;
import spreadsheet.cell.impl.CellIdentifierImpl;
import spreadsheet.cell.impl.CellImpl;
import spreadsheet.cell.impl.EffectiveValueImpl;
import spreadsheet.sheetimpl.DimensionImpl;
import spreadsheet.sheetimpl.SpreadSheetImpl;
import spreadsheet.util.UpdateResult;
import xml.generated.*;
import dto.SaveLoadFileDto;
import dto.ExitDto;

import java.io.*;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static expressions.parser.FunctionParser.parseExpression;

public class EngineImpl implements Engine {
    private SpreadSheet currentSheet = null;
    private Map <Integer, SpreadSheet> sheetVersionMap = new HashMap<>();

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
        Map<CellIdentifier, CellDto> cellDtos = new HashMap<>();
        for (Map.Entry<CellIdentifier, Cell> entry : currentSheet.getActiveCells().entrySet()) {
            Cell cell = entry.getValue();
            CellDto cellDto = new CellDto(
                    cell.getIdentifier(),
                    cell.getOriginalValue(),
                    cell.getEffectiveValue(),
                    cell.getLastModifiedVersion(),
                    cell.getDependencies(),
                    cell.getInfluences()
            );
            cellDtos.put(entry.getKey(), cellDto);
        }

        return new SheetDto(name, version, cellDtos, dimensions,currentSheet.getAmountOfCellsChangedInVersion());
    }


    @Override
    public CellDto displayCellValue(String cellid) {

        // Check if cellid is null or empty
        if (cellid == null || cellid.isEmpty()) {
            throw new IllegalArgumentException("Cell ID cannot be null or empty");
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
                    cellIdentifier,                      // The identifier of the cell
                    "EMPTY",                             // Default original value
                    new EffectiveValueImpl(CellType.NOT_INIT,"EMPTY"),                             // Default effective value
                    0,    // Last modified version (could be current version)
                    Collections.emptyList(),              // No dependencies
                    Collections.emptyList()               // No influences
            );
        }


        // Create and return a CellDto
        return new CellDto(
                cell.getIdentifier(),
                cell.getOriginalValue(),
                cell.getEffectiveValue(),
                cell.getLastModifiedVersion(),
                cell.getDependencies(),
                cell.getInfluences()
        );
    }

    @Override
    public void updateCell(String cellid, String originalValue) {

        if (originalValue == null) {
            throw new IllegalArgumentException("Original value must be entered (can also be empty)");
        }

        // Check if cellid is null or empty
        if (cellid == null || cellid.isEmpty()) {
            throw new IllegalArgumentException("Cell ID cannot be null or empty");
        }

        currentSheet.isValidCellID(cellid);
        CellIdentifierImpl cellIdentifier = new CellIdentifierImpl(cellid);

        UpdateResult updateRes;
        SpreadSheet updateSpreadSheet;
        updateRes = currentSheet.updateCellValueAndCalculate(cellIdentifier, originalValue);
         if(updateRes.isSuccess())
         {
             updateSpreadSheet = updateRes.getSheet();
             sheetVersionMap.put(updateSpreadSheet.getVersion(), updateSpreadSheet);
             currentSheet = updateSpreadSheet;
         }
         else {
             throw new RuntimeException(updateRes.getErrorMessage());
         }


        // Retrieve the cell from the currentSheet
        Cell cell = currentSheet.getCell(cellIdentifier);
        if (cell == null) {
            throw new IllegalStateException("Cell not found after update");
        }
    }

    @Override
    public VerDto displayVersions() {

        Map<Integer, SheetDto> versionSheetDtoMap = new HashMap<>();

        for (Map.Entry<Integer, SpreadSheet> entry : sheetVersionMap.entrySet()) {
            Integer version = entry.getKey();
            SpreadSheet spreadSheet = entry.getValue();

            // Convert SpreadSheet to SheetDto
            Map<CellIdentifier, CellDto> cellDtos = new HashMap<>();

            for (Map.Entry<CellIdentifier, Cell> cellEntry : spreadSheet.getActiveCells().entrySet()) {
                Cell cell = cellEntry.getValue();
                CellDto cellDto = new CellDto(
                        cell.getIdentifier(),
                        cell.getOriginalValue(),
                        cell.getEffectiveValue(),
                        cell.getLastModifiedVersion(),
                        cell.getDependencies(),
                        cell.getInfluences()
                );
                cellDtos.put(cellEntry.getKey(), cellDto);
            }

            SheetDto sheetDto = new SheetDto(
                    spreadSheet.getName(),
                    spreadSheet.getVersion(),
                    cellDtos,
                    spreadSheet.getSheetDimentions(),
                    spreadSheet.getAmountOfCellsChangedInVersion()
            );

            versionSheetDtoMap.put(version, sheetDto);
        }

        // Return the version information wrapped in a VerDto
        return new VerDto(versionSheetDtoMap);
    }

    @Override
    public SheetDto displaySheetByVersion(int version) {
        // Retrieve the SpreadSheet from the version map
        SpreadSheet sheet = sheetVersionMap.get(version);
        if (sheet == null) {
            throw new IllegalArgumentException("No spreadsheet found for the specified version");
        }

        // Convert cells from Cell to CellDto
        Map<CellIdentifier, CellDto> cellDtos = new HashMap<>();
        for (Map.Entry<CellIdentifier, Cell> entry : sheet.getActiveCells().entrySet()) {
            Cell cell = entry.getValue();
            CellDto cellDto = new CellDto(
                    cell.getIdentifier(),
                    cell.getOriginalValue(),
                    cell.getEffectiveValue(),
                    cell.getLastModifiedVersion(),
                    cell.getDependencies(),
                    cell.getInfluences()
            );
            cellDtos.put(entry.getKey(), cellDto);
        }

        // Return a SheetDto with the retrieved SpreadSheet
        return new SheetDto(sheet.getName(), sheet.getVersion(), cellDtos, sheet.getSheetDimentions(), currentSheet.getAmountOfCellsChangedInVersion());
    }

    public SaveLoadFileDto saveState(String path)
    {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(path))) {
            oos.writeObject(currentSheet);
            oos.writeObject(sheetVersionMap);
        }  catch (FileNotFoundException e) {
            return new SaveLoadFileDto(false, "File not found: " + path);
        } catch (IOException e) {
            return new SaveLoadFileDto(false, "IO error while saving: " + e.getMessage());
        } catch (Exception e) {
            return new SaveLoadFileDto(false, "An unexpected error occurred: " + e.getMessage());
        }
        return new SaveLoadFileDto(true,"File saved successfully");

    }
    public SaveLoadFileDto loadSavedState(String path)
    {
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
        return new SaveLoadFileDto(true,"File loaded successfully");
    }


    @Override
    public ExitDto exitSystem() {
        return new ExitDto("Exiting application. Goodbye!");
    }
    @Override
    public void checkIfFileLoaded(){
        if (currentSheet == null) {
            throw new IllegalStateException("Current sheet is not available, please load file first");
        }
    }
}


