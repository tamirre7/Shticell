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
import spreadsheet.cell.api.EffectiveValue;
import spreadsheet.cell.impl.CellIdentifierImpl;
import spreadsheet.cell.impl.CellImpl;
import spreadsheet.sheetimpl.DimensionImpl;
import spreadsheet.sheetimpl.SpreadSheetImpl;
import xml.generated.*;
import dto.LoadDto;
import dto.ExitDto;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static expressions.parser.FunctionParser.parseExpression;

public class EngineImpl implements Engine {
    private SpreadSheet currentSheet = null;
    private Map <Integer, SpreadSheet> sheetVersionMap = new HashMap<>();

    @Override
    public LoadDto loadFile(String path) {
        try {
            // Initialize JAXB context and unmarshaller
            JAXBContext jaxbContext = JAXBContext.newInstance(STLSheet.class);
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();

            // Unmarshal the XML file to STLSheet object
            STLSheet stlSheet = (STLSheet) unmarshaller.unmarshal(new File(path));

            // Get the layout and size information from the STLSheet
            STLLayout stlLayout = stlSheet.getSTLLayout();
            STLSize stlSize = stlLayout.getSTLSize();

            // Create Dimentions object
            Dimension sheetDimensions = new DimensionImpl(
                    stlLayout.getRows(),
                    stlLayout.getColumns(),
                    stlSize.getRowsHeightUnits(),
                    stlSize.getColumnWidthUnits()
            );

            // Create a new SpreadSheet instance with the provided dimensions
            SpreadSheet spreadSheet = new SpreadSheetImpl(stlSheet.getName(),1,sheetDimensions);

            // Iterate over the cells and add them to the spreadsheet
            STLCells stlCells = stlSheet.getSTLCells();
            for (STLCell stlCell : stlCells.getSTLCell()) {
                // Convert column string to char
                char columnChar = stlCell.getColumn().charAt(0);

                // Create a CellIdentifierImpl instance with row and column
                CellIdentifierImpl cellId = new CellIdentifierImpl(stlCell.getRow(), columnChar);

                Expression expression = parseExpression(stlCell.getSTLOriginalValue(), spreadSheet);
                EffectiveValue effectiveValue = expression.evaluate(spreadSheet);

                // Initialize dependencies and influences as empty lists or based on STLCell data
                List<CellIdentifierImpl> dependencies = new ArrayList<>();
                List<CellIdentifierImpl> influences = new ArrayList<>();

                // Create a CellImpl instance with the cell identifier, original value, effective value,
                // last modified version (if known, e.g., 0 for new cells), and lists for dependencies and influences
                CellImpl cell = new CellImpl(
                        cellId,
                        stlCell.getSTLOriginalValue(),
                        1, // Assuming lastModifiedVersion is 0 for new cells
                        spreadSheet
                );

                // Add the cell to the spreadsheet
                spreadSheet.getActiveCells().put(cell.getIdentifier(), cell);
            }

            // Return a LoadDto with the populated SpreadSheet
            this.currentSheet = spreadSheet;
            sheetVersionMap.put(1,currentSheet);
            return new LoadDto(true,"yay");
        } catch (JAXBException e) {
            // Handle JAXB exceptions
            e.printStackTrace();
            return null;
        } catch (Exception e) {
            // Handle other exceptions
            e.printStackTrace();
            return null;
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

        CellIdentifierImpl cellIdentifier = new CellIdentifierImpl(cellid);

        currentSheet.isValidCellID(cellIdentifier);

        // Retrieve the cell from the currentSheet
        Cell cell = currentSheet.getCell(cellIdentifier);

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
    public CellDto updateCell(String cellid, String originalValue) {

        if (originalValue == null) {
            throw new IllegalArgumentException("Original value must be entered (can also be empty)");
        }

        // Check if cellid is null or empty
        if (cellid == null || cellid.isEmpty()) {
            throw new IllegalArgumentException("Cell ID cannot be null or empty");
        }

        // Check if currentSheet is null
        if (currentSheet == null) {
            throw new IllegalStateException("Current sheet is not available");
        }

        CellIdentifierImpl cellIdentifier = new CellIdentifierImpl(cellid);
        currentSheet.isValidCellID(cellIdentifier);

        SpreadSheet updatedSheet;
        try {
            updatedSheet = currentSheet.updateCellValueAndCalculate(cellIdentifier, originalValue);
        } catch (Exception e) {
            // Handle exceptions from the update process
            throw new RuntimeException("Failed to update cell value: " + e.getMessage(), e);
        }

        if (!updatedSheet.equals(currentSheet)) {
            sheetVersionMap.put(updatedSheet.getVersion(), updatedSheet);
            currentSheet = updatedSheet;
        }

        // Retrieve the cell from the currentSheet
        Cell cell = currentSheet.getCell(cellIdentifier);
        if (cell == null) {
            throw new IllegalStateException("Cell not found after update");
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


