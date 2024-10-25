package dto;

import java.util.Map;
import java.util.Collections;

public class SheetDto {
    private final DimensionDto sheetDimension; // Dimensions of the sheet (rows, columns, etc.)
    private final String name;                   // Name of the sheet
    private final int version;                   // Version of the sheet
    private final Map<String, CellDto> cells;   // Map of cells identified by their cell IDs
    private final String uploadedBy;             // Name of the user who uploaded the sheet
    private final Map<String, RangeDto> sheetRanges; // Map of ranges defined in the sheet

    // Constructor to initialize the SheetDto with necessary details
    public SheetDto(DimensionDto sheetDimension, String name, int version, Map<String, CellDto> cells, Map<String, RangeDto> sheetRanges, String uploadedBy) {
        this.sheetDimension = sheetDimension;
        this.name = name;
        this.version = version;
        this.cells = cells;
        this.sheetRanges = sheetRanges;
        this.uploadedBy = uploadedBy;
    }

    // Getters
    public String getUploadedBy() {
        return uploadedBy; // Returns the name of the user who uploaded the sheet
    }

    public String getSheetName() {
        return name; // Returns the name of the sheet
    }

    public Integer getVersion() {
        return version; // Returns the version of the sheet
    }

    public DimensionDto getSheetDimension() {
        return sheetDimension; // Returns the dimensions of the sheet
    }

    // Returns an unmodifiable view of the map of cells to prevent external modification
    public Map<String, CellDto> getCells() {
        return Collections.unmodifiableMap(cells); // Returns the cells map as unmodifiable
    }

    // Returns an unmodifiable view of the map of ranges to prevent external modification
    public Map<String, RangeDto> getSheetRanges() {
        return Collections.unmodifiableMap(sheetRanges); // Returns the ranges map as unmodifiable
    }

    // Returns a string representation of the size of the sheet in "rows x columns" format
    public String getSize() {
        return sheetDimension.getNumRows() + "x" + sheetDimension.getNumCols(); // Returns the size of the sheet
    }
}
