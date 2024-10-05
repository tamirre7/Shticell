package dto;

import java.util.List;
import java.util.Map;

public class DataToFilterDto {
    private RangeDto sortRange;
    Map<String, List<String>> selectedValuesForColumns;

    public DataToFilterDto(RangeDto sortRange, Map<String, List<String>> selectedValuesForColumns) {
        this.sortRange = sortRange;
        this.selectedValuesForColumns = selectedValuesForColumns;
    }

    public RangeDto getSortRange() {return sortRange;}
    public void setSortRange(RangeDto sortRange) {this.sortRange = sortRange;}
}
