package dto;



import java.util.Map;
import java.util.Collections;


public class SheetDto {
    private final DimensionDto sheetDimension;
    private final String name;
    private final int version;
    private final Map<String, CellDto> cells;

    private final Map<String,RangeDto> sheetRanges;

    public SheetDto(DimensionDto sheetDimension, String name, int version, Map<String, CellDto> cells, Map<String, RangeDto> sheetRanges) {
        this.name = name;
        this.version = version;
        this.sheetDimension = sheetDimension;
        this.sheetRanges = sheetRanges;
        this.cells = cells;
    }

    // Getters
    public String getSheetName() {
        return name;
    }


    public Integer getVersion() {
        return version;
    }

    public DimensionDto getSheetDimension() {return sheetDimension;}

    // Return an unmodifiable view of the map to prevent external modification
    public Map<String, CellDto> getCells() {
        return Collections.unmodifiableMap(cells);
    }

    public Map<String, RangeDto> getSheetRanges() {return Collections.unmodifiableMap(sheetRanges);}

    public String getSize(){return sheetDimension.getNumRows() + "x" + sheetDimension.getNumCols();}
}


