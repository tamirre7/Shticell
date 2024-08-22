package dto;

import spreadsheet.api.Dimension;
import spreadsheet.cell.api.CellIdentifier;

import java.util.Map;

import java.util.Collections;
import java.util.HashMap;


public class SheetDto {
    private final Dimension sheetDimension;
    private final String name;
    private final int version;
    private final Map<CellIdentifier, CellDto> cells;
    private final int amountOfCellsChangedInVersion;

    public SheetDto(String name, int version, Map<CellIdentifier, CellDto> cells, Dimension sheetDimension, int cellsAmount) {
        this.name = name;
        this.version = version;
        this.sheetDimension = sheetDimension;
        this.amountOfCellsChangedInVersion = cellsAmount;
        this.cells = new HashMap<>();
        for (Map.Entry<CellIdentifier, CellDto> entry : cells.entrySet()) {
            this.cells.put(entry.getKey(), new CellDto(
                    entry.getValue().getCellId(),
                    entry.getValue().getOriginalValue(),
                    entry.getValue().getEffectiveValue(),
                    entry.getValue().getLastModifiedVersion(),
                    entry.getValue().getDependencies(),
                    entry.getValue().getInfluences()
            ));
        }
    }

    // Getters
    public String getName() {
        return name;
    }

    public int getVersion() {
        return version;
    }

    public Dimension getSheetDimentions() {return sheetDimension;}

    // Return an unmodifiable view of the map to prevent external modification
    public Map<CellIdentifier, CellDto> getCells() {
        return Collections.unmodifiableMap(cells);
    }
}


