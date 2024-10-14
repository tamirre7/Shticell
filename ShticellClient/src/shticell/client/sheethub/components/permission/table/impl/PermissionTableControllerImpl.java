package shticell.client.sheethub.components.permission.table.impl;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import dto.permission.PermissionInfoDto;
import dto.permission.RequestStatus;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;
import shticell.client.sheethub.components.commands.components.permissionresponse.RequestRefresher;
import shticell.client.sheethub.components.permission.table.PermissionInfoDtoProperty;
import shticell.client.sheethub.components.permission.table.PermissionRefresher;
import shticell.client.sheethub.components.permission.table.api.PermissionTableController;
import shticell.client.util.Constants;
import shticell.client.util.http.HttpClientUtil;

import java.io.IOException;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.stream.Collectors;

import static shticell.client.util.Constants.REFRESH_RATE;
import static shticell.client.util.http.HttpClientUtil.showAlert;

public class PermissionTableControllerImpl implements PermissionTableController {
    @FXML
    private TableView<PermissionInfoDtoProperty> permissionTable;

    @FXML
    private TableColumn<PermissionInfoDtoProperty, String> userNameColumn;

    @FXML
    private TableColumn<PermissionInfoDtoProperty, String> permissionTypeColumn;

    @FXML
    private TableColumn<PermissionInfoDtoProperty, String> permissionStatusColumn;

    private ObservableList<PermissionInfoDtoProperty> permissionList = FXCollections.observableArrayList();

    private PermissionRefresher permissionRefresher;
    private Timer timer;

    @FXML
    private void initialize() {
        userNameColumn.setCellValueFactory(cellData -> cellData.getValue().usernameProperty());
        permissionTypeColumn.setCellValueFactory(cellData -> cellData.getValue().permissionTypeProperty().asString());
        permissionStatusColumn.setCellValueFactory(cellData -> cellData.getValue().statusProperty().asString());

        permissionTable.setItems(permissionList);
    }

    @Override
    public void startRequestRefresher(String sheetName) {
        stopRequestRefresher();

        timer = new Timer();
        permissionRefresher = new PermissionRefresher(this::updatePermissionTable,sheetName);
        timer.schedule(permissionRefresher, REFRESH_RATE, REFRESH_RATE);

    }

    private void updatePermissionTable(List<PermissionInfoDto> permissions) {
        Platform.runLater(() -> {
            List<PermissionInfoDtoProperty> propertyList = permissions.stream()
                    .map(PermissionInfoDtoProperty::new)
                    .toList();
            permissionList.setAll(propertyList);
        });
    }
    @Override
    public void stopRequestRefresher() {
        if(permissionRefresher != null)
            permissionRefresher.setActive(false);

        if (timer != null) {
            timer.cancel();
            timer.purge();
        }
    }
}
