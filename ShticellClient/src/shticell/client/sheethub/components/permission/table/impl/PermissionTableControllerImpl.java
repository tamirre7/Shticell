package shticell.client.sheethub.components.permission.table.impl;

import com.google.gson.Gson;
import dto.PermissionDto;
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
    private TableView<PermissionDto> permissionTable;

    @FXML
    private TableColumn<PermissionDto, String> userNameColumn;

    @FXML
    private TableColumn<PermissionDto, String> permissionTypeColumn;

    @FXML
    private TableColumn<PermissionDto, String> permissionStatusColumn;

    private ObservableList<PermissionDto> permissionList = FXCollections.observableArrayList();
    @FXML
    private void initialize() {
        // Ensure the column types match the data being retrieved
        userNameColumn.setCellValueFactory(new PropertyValueFactory<>("username"));
        permissionTypeColumn.setCellValueFactory(new PropertyValueFactory<>("permissionType"));

        // Set the cell value factory for permissionStatusColumn to Boolean
        permissionStatusColumn.setCellValueFactory(new PropertyValueFactory<>("isPending"));

        // Customize the appearance of the Permission Status column
        permissionStatusColumn.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    // Convert the String to a Boolean and then set the text
                    boolean isPending = Boolean.parseBoolean(item);
                    setText(isPending ? "Pending" : "Approved");
                }
            }
        });

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
                        PermissionDto[] permissions = new Gson().fromJson(responseBody, PermissionDto[].class);
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
