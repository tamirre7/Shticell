package shticell.client.sheethub.components.permission.table.impl;

import com.google.gson.Gson;
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
import shticell.client.sheethub.components.permission.table.api.PermissionTableController;
import shticell.client.util.Constants;
import shticell.client.util.http.HttpClientUtil;

import java.io.IOException;

import static shticell.client.util.http.HttpClientUtil.showAlert;

public class PermissionTableControllerImpl implements PermissionTableController {
    @FXML
    private TableView<PermissionInfoDto> permissionTable;

    @FXML
    private TableColumn<PermissionInfoDto, String> userNameColumn;

    @FXML
    private TableColumn<PermissionInfoDto, String> permissionTypeColumn;

    @FXML
    private TableColumn<PermissionInfoDto, String> permissionStatusColumn;

    private ObservableList<PermissionInfoDto> permissionList = FXCollections.observableArrayList();
    @FXML
    private void initialize() {
        // Ensure the column types match the data being retrieved
        userNameColumn.setCellValueFactory(new PropertyValueFactory<>("username"));
        permissionTypeColumn.setCellValueFactory(new PropertyValueFactory<>("permissionType"));
        permissionStatusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));

    // Bind the permission list to the table
        permissionTable.setItems(permissionList);
    }
    @Override
    public void loadPermissionsForSheet(String sheetName){
        String finalUrl = HttpUrl
                .parse(Constants.SHEET_PERMISSIONS)
                .newBuilder()
                .addQueryParameter("sheetName", sheetName)
                .build()
                .toString();

        HttpClientUtil.runAsync(finalUrl, new Callback() {
            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseBody = response.body().string();
                    Platform.runLater(() -> {
                        PermissionInfoDto[] permissions = new Gson().fromJson(responseBody, PermissionInfoDto[].class);
                        permissionList.setAll(permissions); // Update the ObservableList
                    });
                } else {
                    Platform.runLater(() ->
                            showAlert("Error", "Failed to load permissions: " + response.message())
                    );
                }
            }

            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Platform.runLater(() ->
                        showAlert("Error", "An error occurred while loading permissions: " + e.getMessage())
                );
            }
        });
    }
}
