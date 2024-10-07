package dto;

import java.util.List;
import java.util.Map;

public class DataToFilterDto {
    private final RangeDto sortRange;
    private final Map<String, List<String>> selectedValuesForColumns;
    private final DimensionDto sheetDimensions;
    private final String sheetName;


    public DataToFilterDto(RangeDto sortRange, Map<String, List<String>> selectedValuesForColumns, DimensionDto selectedDimension, String sheetName) {
        this.sortRange = sortRange;
        this.selectedValuesForColumns = selectedValuesForColumns;
        this.sheetDimensions = selectedDimension;
        this.sheetName = sheetName;
    }

    public RangeDto getFilterRange() {return sortRange;}
    public Map<String, List<String>> getSelectedValuesForColumns() {return selectedValuesForColumns;}
    public DimensionDto getDimension() {return sheetDimensions;}
    public String getSheetName() {return sheetName;}
}
