package shticell.client.sheethub.components.available.sheets;

import dto.CellDto;
import dto.DimensionDto;
import dto.RangeDto;
import dto.SheetDto;
import javafx.beans.property.*;

import java.util.Collections;
import java.util.Map;

public class SheetDtoProperty {
    private final DimensionDto dimensionDto;
    private final StringProperty sheetName;
    private final StringProperty uploadedBy;
    private final IntegerProperty version;
    private final StringProperty size;
    private final Map<String, CellDto> cells;
    private final Map<String, RangeDto> sheetRanges;

    public SheetDtoProperty(SheetDto sheetDto) {
        this.dimensionDto = sheetDto.getSheetDimension();
        this.sheetName = new SimpleStringProperty(sheetDto.getSheetName());
        this.uploadedBy = new SimpleStringProperty(sheetDto.getUploadedBy());
        this.version = new SimpleIntegerProperty(sheetDto.getVersion());
        this.size = new SimpleStringProperty(sheetDto.getSize());

        // Keep cells and ranges as is, assuming they don't need to be observed directly
        this.cells = Collections.unmodifiableMap(sheetDto.getCells());
        this.sheetRanges = Collections.unmodifiableMap(sheetDto.getSheetRanges());
    }

    public DimensionDto getDimensionDto() {
        return dimensionDto;
    }

    // Getters for JavaFX properties
    public StringProperty sheetNameProperty() {
        return sheetName;
    }

    public StringProperty uploadedByProperty() {
        return uploadedBy;
    }

    public IntegerProperty versionProperty() {
        return version;
    }

    public StringProperty sizeProperty() {
        return size;
    }

    public Map<String, CellDto> getCells() {
        return cells;
    }

    public Map<String, RangeDto> getSheetRanges() {
        return sheetRanges;
    }
}

