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
                spreadSheet.addOrUpdateCell(cell);
            }

            // Return a LoadDto with the populated SpreadSheet
            this.currentSheet = spreadSheet;
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
    public SheetDto displaySpreadsheet() {
        // Check if currentSheet is null
        if (currentSheet == null) {
            throw new IllegalStateException("Current sheet is not available");
        }

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

        return new SheetDto(name, version, cellDtos, dimensions);
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

        CellIdentifierImpl cellIdentifier = CellIdentifierImpl.fromString(cellid);

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
    public CellDto updateCell(String cellid) {
        return null;
    }

    @Override
    public VerDto displayVersions() {
        return null;
    }

    @Override
    public VerDto displaySheetByVersion(String version) {
        return null;
    }

    @Override
    public ExitDto exitSystem() {
        return null;
    }
}


