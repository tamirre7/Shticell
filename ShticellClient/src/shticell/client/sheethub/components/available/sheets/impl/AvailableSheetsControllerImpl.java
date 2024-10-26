package shticell.client.sheethub.components.available.sheets.impl;

import dto.permission.Permission;
import dto.SheetDto;
import dto.permission.SheetPermissionDto;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;
import shticell.client.sheethub.components.available.sheets.SheetPermissionDtoProperty;
import shticell.client.sheethub.components.available.sheets.SheetTableRefresher;
import shticell.client.sheethub.components.available.sheets.api.AvailableSheetsController;
import shticell.client.sheethub.components.login.api.LoginController;
import shticell.client.sheethub.components.permission.table.api.PermissionTableController;
import shticell.client.sheetpanel.spreadsheet.api.SpreadsheetController;
import java.util.List;
import java.util.Timer;
import java.util.stream.Collectors;
import static shticell.client.util.Constants.REFRESH_RATE;

public class AvailableSheetsControllerImpl implements AvailableSheetsController {

    @FXML
    private TableView<SheetPermissionDtoProperty> sheetsTable;

    @FXML
    private TableColumn<SheetPermissionDtoProperty, String> uploadedByColumn;

    @FXML
    private TableColumn<SheetPermissionDtoProperty, String> sheetNameColumn;

    @FXML
    private TableColumn<SheetPermissionDtoProperty, String> sheetSizeColumn;

    @FXML
    private TableColumn<SheetPermissionDtoProperty, String> permissionColumn;

    private SheetTableRefresher tableRefresher;
    private Timer timer;

    private LoginController loginController;
    private ObservableList<SheetPermissionDtoProperty> sheetList = FXCollections.observableArrayList();
    private SpreadsheetController spreadsheetController;
    private PermissionTableController permissionTableController;

    @FXML
    public void initialize() {
        // Bind columns to properties
        uploadedByColumn.setCellValueFactory(cellData -> cellData.getValue().uploadedByProperty());
        sheetNameColumn.setCellValueFactory(cellData -> cellData.getValue().sheetNameProperty());
        sheetSizeColumn.setCellValueFactory(cellData -> cellData.getValue().sizeProperty());
        permissionColumn.setCellValueFactory(cellData -> cellData.getValue().userPermissionProperty().asString());

        // Bind the observable list to the table
        sheetsTable.setItems(sheetList);

        // Set up selection behavior using property-based approach
        sheetsTable.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                handleSheetSelection(newValue);
            }
        });
    }

    // Updates the permission for the specified sheet.
    @Override
    public void updateSheetPermission(String sheetName, Permission newPermission) {
        Platform.runLater(() -> {
            for (SheetPermissionDtoProperty sheet : sheetList) {
                if (sheet.sheetNameProperty().get().equals(sheetName)) {
                    sheet.userPermissionProperty().set(newPermission);
                    break;
                }
            }
        });
    }

    // Handles the selection of a sheet and updates related components.
    @Override
    public void handleSheetSelection(SheetPermissionDtoProperty selectedSheet) {
        if (selectedSheet != null) {
            SheetDto sheetDto = selectedSheet.getSheetDto();
            spreadsheetController.setCurrentSheet(sheetDto);
            spreadsheetController.displaySheet(sheetDto);
            permissionTableController.startRequestRefresher(selectedSheet.sheetNameProperty().get());
        }
    }

    // Updates the table with the list of available sheets.
    private void updateTable(List<SheetPermissionDto> availableSheets) {
        Platform.runLater(() -> {
            // Save the currently selected sheet (if any)
            SheetPermissionDtoProperty selectedSheet = sheetsTable.getSelectionModel().getSelectedItem();

            // Clear and update the sheet list
            sheetList.clear();
            sheetList.addAll(availableSheets.stream()
                    .map(SheetPermissionDtoProperty::new)
                    .collect(Collectors.toList()));

            // Re-select the previously selected sheet
            if (selectedSheet != null) {
                for (SheetPermissionDtoProperty sheet : sheetList) {
                    if (sheet.sheetNameProperty().get().equals(selectedSheet.sheetNameProperty().get())) {
                        sheetsTable.getSelectionModel().select(sheet);
                        break;
                    }
                }
            }
        });
    }

    // Starts the table refresher to periodically update the sheet list.
    public void startTableRefresher() {
        stopTableRefresher();

        tableRefresher = new SheetTableRefresher(this::updateTable);
        timer = new Timer();
        timer.schedule(tableRefresher, REFRESH_RATE, REFRESH_RATE);
    }

    // Stops the table refresher.
    public void stopTableRefresher() {
        if (tableRefresher != null) {
            tableRefresher.setActive(false);
        }
        if (timer != null) {
            timer.cancel();
            timer.purge();
        }
    }

    // Sets the spreadsheet controller for managing sheets.
    public void setSpreadsheetController(SpreadsheetController spreadsheetController) {
        this.spreadsheetController = spreadsheetController;
    }

    // Sets the permission table controller for managing permissions.
    public void setPermissionTableController(PermissionTableController permissionTableController) {
        this.permissionTableController = permissionTableController;
    }

    // Retrieves the names of available sheets excluding those uploaded by the logged-in user.
    @Override
    public List<String> getAvailableSheetsNames() {
        return sheetList.stream()
                .filter(sheet -> !sheet.uploadedByProperty().get().equals(loginController.getLoggedUserName()))
                .map(sheet -> sheet.sheetNameProperty().get())
                .collect(Collectors.toList());
    }

    // Checks if a sheet is currently selected.
    @Override
    public boolean isSheetSelected() {
        return spreadsheetController.getCurrentSheet() != null;
    }

    // Sets the login controller for managing user sessions.
    @Override
    public void setLoginController(LoginController loginController) {
        this.loginController = loginController;
    }
}
