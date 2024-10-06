package dto;

import java.util.List;

public class DataToSortDto {
    private final List<String> columnsToSort;
    private final RangeDto sortRange;
    private final DimensionDto dimensions;

    public DataToSortDto(List<String> columnsToSort, RangeDto sortRange , DimensionDto dimensions) {
        this.columnsToSort = columnsToSort;
        this.sortRange = sortRange;
        this.dimensions = dimensions;
    }

    public List<String> getColumnsToSort() {return columnsToSort;}
    public RangeDto getSortRange() {return sortRange;}
    public DimensionDto getDimensions() {return dimensions;}
}
