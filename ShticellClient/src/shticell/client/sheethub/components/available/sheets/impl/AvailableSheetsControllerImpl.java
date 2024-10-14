package shticell.client.sheethub.components.available.sheets.impl;

import com.google.gson.Gson;
import dto.permission.PermissionInfoDto;
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
import shticell.client.sheethub.components.available.sheets.SheetDtoProperty;
import shticell.client.sheethub.components.available.sheets.SheetTableRefresher;
import shticell.client.sheethub.components.available.sheets.api.AvailableSheetsController;
import shticell.client.sheethub.components.permission.table.api.PermissionTableController;
import shticell.client.sheetpanel.spreadsheet.api.SpreadsheetController;
import shticell.client.util.Constants;
import shticell.client.util.http.HttpClientUtil;

import java.io.IOException;
import java.util.List;
import java.util.Timer;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static shticell.client.util.Constants.REFRESH_RATE;
import static shticell.client.util.http.HttpClientUtil.showAlert;

public class AvailableSheetsControllerImpl implements AvailableSheetsController {

    @FXML
    private TableView<SheetDtoProperty> sheetsTable;

    @FXML
    private TableColumn<SheetDtoProperty, String> uploadedByColumn;

    @FXML
    private TableColumn<SheetDtoProperty, String> sheetNameColumn;

    @FXML
    private TableColumn<SheetDtoProperty, String> sheetSizeColumn;

    @FXML
    private TableColumn<SheetDtoProperty, String> permissionColumn;

    private SheetTableRefresher  tableRefresher;
    private Timer timer;

    private ObservableList<SheetDtoProperty> sheetList = FXCollections.observableArrayList();

    private SpreadsheetController spreadsheetController;
    private PermissionTableController permissionTableController;

    @FXML
    public void initialize() {
        // Set cell value factories to bind to the JavaFX properties
        uploadedByColumn.setCellValueFactory(cellData -> cellData.getValue().uploadedByProperty());
        sheetNameColumn.setCellValueFactory(cellData -> cellData.getValue().sheetNameProperty());
        sheetSizeColumn.setCellValueFactory(cellData -> cellData.getValue().sizeProperty());

        permissionColumn.setCellFactory(column -> new TableCell<SheetDtoProperty, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getTableRow() == null || getTableRow().getItem() == null) {
                    setText(null);
                } else {
                    SheetDtoProperty sheetDtoProperty = getTableRow().getItem();
                    String sheetName = sheetDtoProperty.sheetNameProperty().get();

                    fetchPermissionForSheet(sheetName, permission -> {
                        Platform.runLater(() -> setText(permission));
                    });
                }
            }
        });

        // Set up selection behavior using property-based approach
        sheetsTable.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                handleSheetSelection(newValue);
            }
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
                        PermissionInfoDto permissionInfoDto = new Gson().fromJson(responseBody, PermissionInfoDto.class);
                        permissionConsumer.accept(permissionInfoDto.getPermissionType().toString());
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
    public void handleSheetSelection(SheetDtoProperty selectedSheet) {
        if (selectedSheet != null) {
            SheetDto sheetDto = convertToSheetDto(selectedSheet);
            spreadsheetController.setCurrentSheet(sheetDto);
            spreadsheetController.displaySheet(sheetDto);
            permissionTableController.stopRequestRefresher();
            permissionTableController.startRequestRefresher(selectedSheet.sheetNameProperty().get());
        }
    }
    private void updateTable(List<SheetDto> availableSheets) {
        Platform.runLater(() -> {
            // Check if the size of available sheets is different from the current list
            if (sheetList.size() != availableSheets.size()) {
                // Save the currently selected sheet (if any)
                SheetDtoProperty selectedSheet = sheetsTable.getSelectionModel().getSelectedItem();

                // Clear and update the sheet list
                sheetList.clear();
                sheetList.addAll(availableSheets.stream().map(this::convertToSheetDtoProperty).collect(Collectors.toList()));

                // Re-select the previously selected sheet
                if (selectedSheet != null) {
                    for (SheetDtoProperty sheet : sheetList) {
                        if (sheet.sheetNameProperty().get().equals(selectedSheet.sheetNameProperty().get())) {
                            sheetsTable.getSelectionModel().select(sheet);
                            break;
                        }
                    }
                }
            } else {
                // Update existing items
                for (int i = 0; i < availableSheets.size(); i++) {
                    SheetDto newSheet = availableSheets.get(i);
                    SheetDtoProperty existingSheet = sheetList.get(i);
                    updateSheetDtoProperty(existingSheet, newSheet);
                }
            }
        });
    }
    private void updateSheetDtoProperty(SheetDtoProperty existingSheet, SheetDto newSheet) {
        existingSheet.sheetNameProperty().set(newSheet.getSheetName());
        existingSheet.uploadedByProperty().set(newSheet.getUploadedBy());
        existingSheet.versionProperty().set(newSheet.getVersion());
        existingSheet.sizeProperty().set(newSheet.getSize());
    }
    private SheetDto convertToSheetDto(SheetDtoProperty sheetDtoProperty) {
        return new SheetDto(
                sheetDtoProperty.getDimensionDto(),
                sheetDtoProperty.sheetNameProperty().get(),
                sheetDtoProperty.versionProperty().get(),
                sheetDtoProperty.getCells(),
                sheetDtoProperty.getSheetRanges(),
                sheetDtoProperty.uploadedByProperty().get()
        );
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
    @Override
    public List<String> getAvailableSheetsNames() {
        return sheetList.stream()
                .map(sheet -> sheet.sheetNameProperty().get())
                .collect(Collectors.toList());
    }
    private SheetDtoProperty convertToSheetDtoProperty(SheetDto sheetDto) {
        return new SheetDtoProperty(sheetDto);
    }
}

