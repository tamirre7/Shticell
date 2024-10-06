package dto;

import java.util.List;
import java.util.Map;

public class DataToFilterDto {
    private final RangeDto sortRange;
    private final Map<String, List<String>> selectedValuesForColumns;
    private final DimensionDto sheetDimensions;


    public DataToFilterDto(RangeDto sortRange, Map<String, List<String>> selectedValuesForColumns, DimensionDto selectedDimension) {
        this.sortRange = sortRange;
        this.selectedValuesForColumns = selectedValuesForColumns;
        this.sheetDimensions = selectedDimension;
    }

    public RangeDto getFilterRange() {return sortRange;}
    public Map<String, List<String>> getSelectedValuesForColumns() {return selectedValuesForColumns;}
    public DimensionDto getDimension() {return sheetDimensions;}
}
