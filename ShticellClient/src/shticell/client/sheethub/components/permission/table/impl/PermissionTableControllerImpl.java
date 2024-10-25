package shticell.client.sheethub.components.permission.table.impl;

import dto.permission.PermissionInfoDto;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import shticell.client.sheethub.components.permission.table.PermissionInfoDtoProperty;
import shticell.client.sheethub.components.permission.table.PermissionRefresher;
import shticell.client.sheethub.components.permission.table.api.PermissionTableController;
import java.util.List;
import java.util.Timer;
import static shticell.client.util.Constants.REFRESH_RATE;

public class PermissionTableControllerImpl implements PermissionTableController {

    @FXML
    private TableView<PermissionInfoDtoProperty> permissionTable; // Table view for displaying permissions.

    @FXML
    private TableColumn<PermissionInfoDtoProperty, String> userNameColumn; // Column for displaying usernames.

    @FXML
    private TableColumn<PermissionInfoDtoProperty, String> permissionTypeColumn; // Column for displaying permission types.

    @FXML
    private TableColumn<PermissionInfoDtoProperty, String> permissionStatusColumn; // Column for displaying permission statuses.

    private ObservableList<PermissionInfoDtoProperty> permissionList = FXCollections.observableArrayList(); // List of permissions.

    private PermissionRefresher permissionRefresher; // Refresher for updating permissions.
    private Timer timer; // Timer for scheduling permission refreshes.

    @FXML
    private void initialize() {
        // Sets up the cell value factories for each column.
        userNameColumn.setCellValueFactory(cellData -> cellData.getValue().usernameProperty());
        permissionTypeColumn.setCellValueFactory(cellData -> cellData.getValue().permissionTypeProperty().asString());
        permissionStatusColumn.setCellValueFactory(cellData -> cellData.getValue().statusProperty().asString());

        // Binds the permission table to the observable list.
        permissionTable.setItems(permissionList);
    }

    @Override
    public void startRequestRefresher(String sheetName) {
        // Stops any existing refreshers before starting a new one.
        stopRequestRefresher();

        timer = new Timer(); // Initializes the timer.
        permissionRefresher = new PermissionRefresher(this::updatePermissionTable, sheetName); // Creates a new PermissionRefresher.
        timer.schedule(permissionRefresher, REFRESH_RATE, REFRESH_RATE); // Schedules the refresher task.
    }

    private void updatePermissionTable(List<PermissionInfoDto> permissions) {
        // Updates the permission table with new data.
        Platform.runLater(() -> {
            List<PermissionInfoDtoProperty> propertyList = permissions.stream()
                    .map(PermissionInfoDtoProperty::new) // Maps DTOs to properties.
                    .toList();
            permissionList.setAll(propertyList); // Updates the observable list.
        });
    }

    @Override
    public void stopRequestRefresher() {
        // Stops the permission refresher if it exists.
        if (permissionRefresher != null)
            permissionRefresher.setActive(false); // Deactivates the refresher.

        if (timer != null) {
            timer.cancel(); // Cancels the timer.
            timer.purge(); // Clears the timer queue.
        }
    }
}
