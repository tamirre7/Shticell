package dto;

import java.util.List;

public class DataToSortDto {
    private final List<String> columnsToSort;
    private final RangeDto sortRange;
    private final DimensionDto dimensions;
    private final String sheetName;

    public DataToSortDto(List<String> columnsToSort, RangeDto sortRange , DimensionDto dimensions,String sheetName) {
        this.columnsToSort = columnsToSort;
        this.sortRange = sortRange;
        this.dimensions = dimensions;
        this.sheetName = sheetName;
    }

    public List<String> getColumnsToSort() {return columnsToSort;}
    public RangeDto getSortRange() {return sortRange;}
    public DimensionDto getDimensions() {return dimensions;}
    public String getSheetName() {return sheetName;}
}
