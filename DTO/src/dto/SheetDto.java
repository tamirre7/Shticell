package dto;



import java.sql.SQLData;
import java.util.Map;

import java.util.Collections;
import java.util.HashMap;


public class SheetDto {
    private final int numRows;
    private final int numCols;
    private final int widthCol;
    private final int heightRow;
    private final String name;
    private final int version;
    private final Map<String, CellDto> cells;
    private final int amountOfCellsChangedInVersion;
    private final Map<String,RangeDto> sheetRanges;

    public SheetDto(int numCols, int numRows, int widthCol, int heightRow, String name, int version, Map<String, CellDto> cells, int cellsAmount, Map<String, RangeDto> sheetRanges) {
        this.name = name;
        this.version = version;
        this.numRows = numRows;
        this.numCols = numCols;
        this.widthCol = widthCol;
        this.heightRow = heightRow;
        this.amountOfCellsChangedInVersion = cellsAmount;
        this.sheetRanges = sheetRanges;
        this.cells = cells;
//        for (Map.Entry<String, CellDto> entry : cells.entrySet()) {
//            this.cells.put(entry.getKey(), new CellDto(
//                    entry.getValue().getCellId(),
//                    entry.getValue().getOriginalValue(),
//                    entry.getValue().getEffectiveValue(),
//                    entry.getValue().getLastModifiedVersion(),
//                    entry.getValue().getDependencies(),
//                    entry.getValue().getInfluences()
//            ));
//        }
    }

    // Getters
    public String getName() {
        return name;
    }

    public int getAmountOfCellsChangedInVersion() {return amountOfCellsChangedInVersion;}

    public Integer getVersion() {
        return version;
    }

    public int getHeightRow() { return heightRow; }
    public int getWidthCol() { return widthCol; }
    public int getNumCols() { return numCols; }
    public int getNumRows() { return numRows; }

    // Return an unmodifiable view of the map to prevent external modification
    public Map<String, CellDto> getCells() {
        return Collections.unmodifiableMap(cells);
    }

    public Map<String, RangeDto> getSheetRanges() {return Collections.unmodifiableMap(sheetRanges);}
}


