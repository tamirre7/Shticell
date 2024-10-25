package shticell.client.sheethub.components.commands.components.permissionresponse;

import dto.permission.Permission;
import dto.permission.PermissionRequestDto;
import javafx.beans.property.*;
/**
 * Property class that encapsulates data from the PermissionRequestDto DTO.
 * Provides JavaFX properties for request ID,sheet name, permission type etc.
 * enabling easy binding to UI components in a JavaFX application.
 */
public class PermissionRequestDtoProperty {
    private final IntegerProperty id;
    private final StringProperty sheetName;
    private final ObjectProperty<Permission> permissionType;
    private final StringProperty message;
    private final StringProperty requester;

    public PermissionRequestDtoProperty(PermissionRequestDto dto) {
        this.id = new SimpleIntegerProperty(dto.getId());
        this.sheetName = new SimpleStringProperty(dto.getSheetName());
        this.permissionType = new SimpleObjectProperty<>(dto.getPermissionType());
        this.message = new SimpleStringProperty(dto.getMessage());
        this.requester = new SimpleStringProperty(dto.getRequester());
    }

    // Getters for properties
    public IntegerProperty idProperty() {
        return id;
    }

    public StringProperty sheetNameProperty() {
        return sheetName;
    }

    public ObjectProperty<Permission> permissionTypeProperty() {
        return permissionType;
    }

    public StringProperty messageProperty() {
        return message;
    }

    public StringProperty requesterProperty() {
        return requester;
    }

    // Getters for values
    public int getId() {
        return id.get();
    }

    public String getSheetName() {
        return sheetName.get();
    }

    public Permission getPermissionType() {
        return permissionType.get();
    }

    public String getMessage() {
        return message.get();
    }

    public String getRequester() {
        return requester.get();
    }

    // Setters
    public void setId(int id) {
        this.id.set(id);
    }

    public void setSheetName(String sheetName) {
        this.sheetName.set(sheetName);
    }

    public void setPermissionType(Permission permissionType) {
        this.permissionType.set(permissionType);
    }

    public void setMessage(String message) {
        this.message.set(message);
    }

    public void setRequester(String requester) {
        this.requester.set(requester);
    }

    // Convert back to PermissionRequestDto if needed
    public PermissionRequestDto toDto() {
        return new PermissionRequestDto(getId(), getSheetName(), getPermissionType(), getMessage(), getRequester());
    }
}
