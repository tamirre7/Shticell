package dto;

import java.util.List;

public class DataToSortDto {
    private final List<String> columnsToSort; // List of columns to be sorted
    private final RangeDto sortRange; // Range of cells to sort
    private final DimensionDto dimensions; // Dimensions of the sheet (rows and columns)
    private final String sheetName; // Name of the sheet

    // Constructor to initialize DataToSortDto with specified parameters
    public DataToSortDto(List<String> columnsToSort, RangeDto sortRange, DimensionDto dimensions, String sheetName) {
        this.columnsToSort = columnsToSort;
        this.sortRange = sortRange;
        this.dimensions = dimensions;
        this.sheetName = sheetName;
    }

    public List<String> getColumnsToSort() {
        return columnsToSort; // Returns the list of columns to be sorted
    }

    public RangeDto getSortRange() {
        return sortRange; // Returns the range of cells to sort
    }

    public DimensionDto getDimensions() {
        return dimensions; // Returns the dimensions of the sheet
    }

    public String getSheetName() {
        return sheetName; // Returns the name of the sheet
    }
}
