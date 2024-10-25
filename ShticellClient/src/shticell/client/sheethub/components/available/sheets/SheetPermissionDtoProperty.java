package shticell.client.sheethub.components.available.sheets;

import dto.SheetDto;
import dto.permission.Permission;
import dto.permission.SheetPermissionDto;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
/**
 * Property class that encapsulates data from the SheetPermissionDto DTO.
 * Provides JavaFX properties for sheet name, uploader, size, and user permissions,
 * enabling easy binding to UI components in a JavaFX application.
 */
public class SheetPermissionDtoProperty {
    private final StringProperty sheetName;
    private final StringProperty uploadedBy;
    private final StringProperty size;
    private final ObjectProperty<Permission> userPermission;
    private final SheetDto sheetDto;

    public SheetPermissionDtoProperty(SheetPermissionDto dto) {
        this.sheetDto = dto.getSheetDto();
        this.sheetName = new SimpleStringProperty(dto.getSheetName());
        this.uploadedBy = new SimpleStringProperty(dto.getUploadedBy());
        this.size = new SimpleStringProperty(dto.getSize());
        this.userPermission = new SimpleObjectProperty<>(dto.getUserPermission());
    }

    // Getter and setter methods for properties

    public StringProperty sheetNameProperty() {
        return sheetName;
    }

    public StringProperty uploadedByProperty() {
        return uploadedBy;
    }

    public StringProperty sizeProperty() {
        return size;
    }

    public ObjectProperty<Permission> userPermissionProperty() {
        return userPermission;
    }

    public SheetDto getSheetDto() {
        return sheetDto;
    }
}
