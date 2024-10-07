package shticell.client.sheethub.components.available.sheets.impl;

import com.google.gson.Gson;
import dto.SheetDto;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;
import shticell.client.sheethub.components.available.sheets.api.AvailableSheetsController;
import shticell.client.sheetpanel.spreadsheet.api.SpreadsheetController;
import shticell.client.util.Constants;
import shticell.client.util.http.HttpClientUtil;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicReference;

import static shticell.client.util.http.HttpClientUtil.extractSheetFromResponseBody;
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

    private ObservableList<SheetDto> sheetList = FXCollections.observableArrayList();

    SpreadsheetController spreadsheetController;

    @FXML
    public void initialize() {
        // Initialize table columns
        uploadedByColumn.setCellValueFactory(new PropertyValueFactory<>("uploadedBy"));
        sheetNameColumn.setCellValueFactory(new PropertyValueFactory<>("sheetName"));
        sheetSizeColumn.setCellValueFactory(new PropertyValueFactory<>("size"));
        permissionColumn.setCellValueFactory(new PropertyValueFactory<>("permission"));

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

         loadAvailableSheets();
    }
    private void loadAvailableSheets() {
        Request request = new Request.Builder()
                .url(Constants.GET_AVAILABLE_SHEETS)  // Replace with the correct URL for fetching sheets
                .get()
                .build();


        HttpClientUtil.runAsync(request, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {

                Platform.runLater(() ->
                        showAlert("Error", e.getMessage())
                );
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.code() != 200) {
                    String responseBody = response.body().string();
                    Platform.runLater(() ->
                            showAlert("Error", responseBody)
                    );
                } else {
                    String responseBody = response.body().string();
                    SheetDto[] availableSheets = new Gson().fromJson(responseBody, SheetDto[].class);
                    Platform.runLater(() -> {
                        sheetList.clear();  // Clear any old data
                        sheetList.addAll(availableSheets);  // Add the new sheets to the list
                    });
                }
            }
        });
    }

    // Adds a new sheet to the map and the observable list
    public void addSheet(SheetDto sheetDto) {
        sheetList.add(sheetDto);
    }

    @Override
    // Handles what happens when a sheet is selected from the table
    public void handleSheetSelection(SheetDto selectedSheet) {
        if (selectedSheet != null) {
            Gson gson = new Gson();
            String sheetToSetJson = gson.toJson(selectedSheet.getSheetName());

            RequestBody requestBody = RequestBody.create(sheetToSetJson, MediaType.parse("application/json"));

            Request request = new Request.Builder()
                    .url(Constants.SET_SHEET)
                    .post(requestBody)
                    .build();

            HttpClientUtil.runAsync(request, new Callback() {
                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                    if (response.isSuccessful()) {
                        String responseBody = response.body().string();
                        Platform.runLater(() -> {
                            SheetDto currentSheet = HttpClientUtil.extractSheetFromResponseBody(responseBody);
                            spreadsheetController.setCurrentSheet(currentSheet);
                            spreadsheetController.displaySheet(currentSheet);
                        });
                    }


                    else {
                        showAlert("Error", "Failed to delete range: " + response.message());
                    }

                }

                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    Platform.runLater(() ->
                            showAlert("Error", "Error: " + e.getMessage())
                    );
                }
            });

        }
    }
    public void setSpreadsheetController(SpreadsheetController spreadsheetController) {
        this.spreadsheetController = spreadsheetController;
    }
}

