package shticell.client.sheethub.components.avaliable.sheets.impl;

import dto.SheetDto;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import shticell.client.sheethub.components.avaliable.sheets.api.AvailableSheetsController;

import java.util.HashMap;
import java.util.Map;

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
    }

    // Adds a new sheet to the map and the observable list
    public void addSheet(SheetDto sheetDto) {
        sheetList.add(sheetDto);
    }

    @Override
    // Handles what happens when a sheet is selected from the table
    public void handleSheetSelection(SheetDto selectedSheet) {
        if (selectedSheet != null) {


        }
    }
}
