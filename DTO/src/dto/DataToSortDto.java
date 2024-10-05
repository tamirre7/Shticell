package dto;

import java.util.List;

public class DataToSortDto {
    private final List<String> columnsToSort;
    private final RangeDto sortRange;

    public DataToSortDto(List<String> columnsToSort, RangeDto sortRange) {
        this.columnsToSort = columnsToSort;
        this.sortRange = sortRange;
    }

    public List<String> getColumnsToSort() {return columnsToSort;}
    public RangeDto getSortRange() {return sortRange;}
}
