package dto;

import java.util.List;
import java.util.Map;

public class DataToFilterDto {
    private final RangeDto sortRange; // Range of cells to sort/filter
    private final Map<String, List<String>> selectedValuesForColumns; // Selected values for each column
    private final DimensionDto sheetDimensions; // Dimensions of the sheet (rows and columns)
    private final String sheetName; // Name of the sheet

    // Constructor to initialize DataToFilterDto with specified parameters
    public DataToFilterDto(RangeDto sortRange, Map<String, List<String>> selectedValuesForColumns, DimensionDto selectedDimension, String sheetName) {
        this.sortRange = sortRange;
        this.selectedValuesForColumns = selectedValuesForColumns;
        this.sheetDimensions = selectedDimension;
        this.sheetName = sheetName;
    }

    public RangeDto getFilterRange() {
        return sortRange; // Returns the range of cells to filter
    }

    public Map<String, List<String>> getSelectedValuesForColumns() {
        return selectedValuesForColumns; // Returns the selected values for each column
    }

    public DimensionDto getDimension() {
        return sheetDimensions; // Returns the dimensions of the sheet
    }

    public String getSheetName() {
        return sheetName; // Returns the name of the sheet
    }
}
