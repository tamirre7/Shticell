package shticell.client.sheethub.components.available.sheets.impl;

import com.google.gson.Gson;
import dto.PermissionDto;
import dto.SheetDto;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;
import shticell.client.sheethub.components.available.sheets.SheetTableRefresher;
import shticell.client.sheethub.components.available.sheets.api.AvailableSheetsController;
import shticell.client.sheethub.components.permission.table.api.PermissionTableController;
import shticell.client.sheetpanel.spreadsheet.api.SpreadsheetController;
import shticell.client.util.Constants;
import shticell.client.util.http.HttpClientUtil;

import java.io.IOException;
import java.util.Timer;
import java.util.function.Consumer;

import static shticell.client.util.Constants.REFRESH_RATE;
import static shticell.client.util.http.HttpClientUtil.showAlert;

public class AvailableSheetsControllerImpl implements AvailableSheetsController {

    @FXML
    private TableView<SheetDto> sheetsTable;

    @FXML
    private TableColumn<SheetDto, String> uploadedByColumn;

    @FXML
    private TableColumn<SheetDto, String> sheetNameColumn;

    @FXML
    private TableColumn<SheetDto, String> sheetSizeColumn;

    @FXML
    private TableColumn<SheetDto, String> permissionColumn;

    private SheetTableRefresher  tableRefresher;
    private Timer timer;

    private ObservableList<SheetDto> sheetList = FXCollections.observableArrayList();

    private SpreadsheetController spreadsheetController;
    private PermissionTableController permissionTableController;

    @FXML
    public void initialize() {
        // Initialize table columns
        uploadedByColumn.setCellValueFactory(new PropertyValueFactory<>("uploadedBy"));
        sheetNameColumn.setCellValueFactory(new PropertyValueFactory<>("sheetName"));
        sheetSizeColumn.setCellValueFactory(new PropertyValueFactory<>("size"));
        permissionColumn.setCellValueFactory(new PropertyValueFactory<>("permission"));

        permissionColumn.setCellFactory(column -> new TableCell<SheetDto, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getTableRow() == null || getTableRow().getItem() == null) {
                    setText(null);
                }
                else {
                    SheetDto sheetDto = getTableRow().getItem();
                    String sheetName = sheetDto.getSheetName();

                    fetchPermissionForSheet(sheetName,permission ->
                    {
                        Platform.runLater(() -> setText(permission));
                    });
                }
            }
        });


        // Set up row selection behavior
        sheetsTable.setRowFactory(tv -> {
            TableRow<SheetDto> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (!row.isEmpty()) {
                    SheetDto selectedSheet = row.getItem();
                    handleSheetSelection(selectedSheet);
                }
            });
            return row;
        });

        // Bind the observable list to the table
        sheetsTable.setItems(sheetList);
    }

    private void fetchPermissionForSheet(String sheetName, Consumer<String> permissionConsumer) {
        //noinspection ConstantConditions
        String finalUrl = HttpUrl
                .parse(Constants.USER_PERMISSON_FOR_SHEET)
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
                        PermissionDto permissionDto = new Gson().fromJson(responseBody, PermissionDto.class);
                        permissionConsumer.accept(permissionDto.getPermissionType().toString());
                    });
                } else {
                    Platform.runLater(() ->
                            showAlert("Error", "Failed to sort data: " + response.message())

                    );
                }
            }

            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Platform.runLater(() ->
                        showAlert("Error", "An error occurred while sorting: " + e.getMessage())
                );
            }
        });


    }

    @Override
    // Handles what happens when a sheet is selected from the table
    public void handleSheetSelection(SheetDto selectedSheet) {
        if (selectedSheet != null) {
            spreadsheetController.setCurrentSheet(selectedSheet);
            spreadsheetController.displaySheet(selectedSheet);
            permissionTableController.loadPermissionsForSheet(selectedSheet.getSheetName());
        }
    }
    private void updateTable(SheetDto[] availableSheets) {
        Platform.runLater(() -> {
            // Check if the size of available sheets is different from the current list
            if (sheetList.size() != availableSheets.length) {
                // Save the currently selected sheet (if any)
                SheetDto selectedSheet = sheetsTable.getSelectionModel().getSelectedItem();

                // Clear and update the sheet list
                sheetList.clear();
                sheetList.addAll(availableSheets);

                // Try to re-select the previously selected sheet (if still available)
                if (selectedSheet != null) {
                    for (SheetDto sheet : sheetList) {
                        if (sheet.getSheetName().equals(selectedSheet.getSheetName())) {
                            sheetsTable.getSelectionModel().select(sheet);
                            break;
                        }
                    }
                }
            }
        });
    }

    public void startTableRefresher() {
        stopTableRefresher();

        tableRefresher = new SheetTableRefresher(this::updateTable);
        timer = new Timer();
        timer.schedule(tableRefresher, REFRESH_RATE, REFRESH_RATE);
    }

    public void stopTableRefresher() {
        if (tableRefresher != null) {
            tableRefresher.setActive(false);
        }
        if (timer != null) {
            timer.cancel();
            timer.purge();
        }
    }

    public void setSpreadsheetController(SpreadsheetController spreadsheetController) {
        this.spreadsheetController = spreadsheetController;
    }
    public void setPermissionTableController(PermissionTableController permissionTableController) {
        this.permissionTableController = permissionTableController;
    }

}

