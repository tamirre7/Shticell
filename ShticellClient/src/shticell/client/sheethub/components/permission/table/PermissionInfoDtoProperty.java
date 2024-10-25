package shticell.client.sheethub.components.permission.table;

import dto.permission.Permission;
import dto.permission.PermissionInfoDto;
import dto.permission.RequestStatus;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
/**
 * Property class that encapsulates data from the PermissionInfoDto DTO.
 * Provides JavaFX properties for username,permission type,sheet name and the request status,
 * enabling easy binding to UI components in a JavaFX application.
 */
public class PermissionInfoDtoProperty {
    private final StringProperty username;
    private final ObjectProperty<Permission> permissionType;
    private final StringProperty sheetName;
    private final ObjectProperty<RequestStatus> status;

    public PermissionInfoDtoProperty(PermissionInfoDto permissionInfoDto) {
        this.sheetName = new SimpleStringProperty(permissionInfoDto.getSheetName());
        this.username = new SimpleStringProperty(permissionInfoDto.getUsername());
        this.permissionType = new SimpleObjectProperty<>(permissionInfoDto.getPermissionType());
        this.status = new SimpleObjectProperty<>(permissionInfoDto.getStatus());
    }

    public StringProperty usernameProperty() {
        return username;
    }

    public ObjectProperty<Permission> permissionTypeProperty() {
        return permissionType;
    }

    public ObjectProperty<RequestStatus> statusProperty() {
        return status;
    }

    // Getter methods
    public String getUsername() {
        return username.get();
    }

    public Permission getPermissionType() {
        return permissionType.get();
    }

    public RequestStatus getStatus() {
        return status.get();
    }

    // Setter methods
    public void setUsername(String username) {
        this.username.set(username);
    }

    public void setPermissionType(Permission permissionType) {
        this.permissionType.set(permissionType);
    }

    public void setStatus(RequestStatus status) {
        this.status.set(status);
    }
}