package shticellui.sortandfilter;

import command.api.Engine;
import dto.SheetDto;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import shticellui.spreadsheet.SpreadsheetDisplayController;
import spreadsheet.api.Dimension;
import spreadsheet.cell.impl.CellIdentifierImpl;
import spreadsheet.range.api.Range;
import spreadsheet.range.impl.RangeImpl;
import spreadsheet.sheetimpl.DimensionImpl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SortAndFilterController {
    List<String> columnsToSortOrFilter;
    Range sortOrFilterRange;
    Dimension sheetDimension;
    SpreadsheetDisplayController spreadsheetDisplayController;
    private final Engine engine;

    public SortAndFilterController(Engine engine, SpreadsheetDisplayController spreadsheetDisplayController) {
        this.engine = engine;
        this.spreadsheetDisplayController = spreadsheetDisplayController;
    }

    @FXML
    public void initialize() {

    }


    @FXML
    public void handleSort() {
        sortAndFilterDialog();

        if (sortOrFilterRange == null || columnsToSortOrFilter == null || columnsToSortOrFilter.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Error", "Please select a valid range and columns.");
            return;
        }

        try {
            // Call the Engine to sort the selected range based on the chosen columns
            SheetDto sortedSheet = engine.sortRange(sortOrFilterRange, columnsToSortOrFilter);
            spreadsheetDisplayController.displaySheet(sortedSheet);


        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Sort Error", "An error occurred while sorting: " + e.getMessage());
        }
    }



    @FXML
    public void handleFilter() {

    }
    @FXML
    public void handleResetSortFilter() {
        spreadsheetDisplayController.displayOriginaSheet();
    }


    public void initDimension() {
        SheetDto sheet = engine.displayCurrentSpreadsheet();
        this.sheetDimension = new DimensionImpl(sheet.getNumRows(), sheet.getNumCols(), sheet.getWidthCol(), sheet.getHeightRow());
    }

    private void sortAndFilterDialog() {
        initDimension();

        String rangeName = "sortAndFilterRange";

        // Prompt for top-left cell identifier
        TextInputDialog topLeftDialog = new TextInputDialog();
        topLeftDialog.setTitle("Top-Left Cell");
        topLeftDialog.setHeaderText("Enter top-left cell (example: A1):");
        Optional<String> topLeftResult = topLeftDialog.showAndWait();

        // If the user cancels the top-left cell dialog, stop the process
        if (topLeftResult.isEmpty()) {
            return;
        }

        String StrTopLeft = topLeftResult.get().toUpperCase();

        // Prompt for bottom-right cell identifier
        TextInputDialog bottomRightDialog = new TextInputDialog();
        bottomRightDialog.setTitle("Bottom-Right Cell");
        bottomRightDialog.setHeaderText("Enter bottom-right cell (example: B2):");
        Optional<String> bottomRightResult = bottomRightDialog.showAndWait();

        // If the user cancels the bottom-right cell dialog, stop the process
        if (!bottomRightResult.isPresent()) {
            return;
        }
        String StrBottomRight = bottomRightResult.get().toUpperCase();

        try {
            // If both cells are provided, process the range creation
            CellIdentifierImpl topLeft = new CellIdentifierImpl(StrTopLeft);
            CellIdentifierImpl bottomRight = new CellIdentifierImpl(StrBottomRight);
            Range range = new RangeImpl(rangeName, topLeft, bottomRight, sheetDimension);
            sortOrFilterRange = range;

        } catch (IllegalArgumentException e) {
            showAlert(Alert.AlertType.WARNING, "Error - out of range", e.getMessage());
        }

// Create a ComboBox for column selection
        ComboBox<String> columnComboBox = new ComboBox<>();
        for (int i = 0; i < sheetDimension.getNumCols(); i++) {
            String columnName = String.valueOf((char) ('A' + i));
            columnComboBox.getItems().add(columnName);
        }
        columnComboBox.setPromptText("Select a column");

// Create a ListView to display selected columns in order
        ListView<String> selectedColumnsListView = new ListView<>();
        ObservableList<String> selectedColumns = FXCollections.observableArrayList();
        selectedColumnsListView.setItems(selectedColumns);
        selectedColumnsListView.setPrefHeight(130);

// Create an "Add" button to add selected columns
        Button addButton = new Button("Add");
        addButton.setDisable(true);  // Initially disable the button

// Enable the button when a column is selected
        columnComboBox.valueProperty().addListener((obs, oldVal, newVal) -> {
            addButton.setDisable(newVal == null || selectedColumns.contains(newVal));
        });

// Handle adding the selected column
        addButton.setOnAction(e -> {
            String selectedColumn = columnComboBox.getValue();
            if (selectedColumn != null && !selectedColumns.contains(selectedColumn)) {
                selectedColumns.add(selectedColumn);
            }
            columnComboBox.setValue(null);  // Reset the ComboBox for the next selection
        });

// Create a "Remove" button to remove a column from the selection
        Button removeButton = new Button("Remove Selected");
        removeButton.setDisable(true);  // Initially disable the button

// Enable the remove button only when a column is selected from the list
        selectedColumnsListView.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            removeButton.setDisable(newVal == null);
        });

// Handle removing the selected column
        removeButton.setOnAction(e -> {
            String selectedColumn = selectedColumnsListView.getSelectionModel().getSelectedItem();
            if (selectedColumn != null) {
                selectedColumns.remove(selectedColumn);
            }
        });

// Create a dialog to wrap the selection process
        Dialog<List<String>> columnDialog = new Dialog<>();
        columnDialog.setTitle("Select Columns");
        columnDialog.setHeaderText("Select columns (in order):");

// Set dialog buttons
        ButtonType okButtonType = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
        columnDialog.getDialogPane().getButtonTypes().addAll(okButtonType, ButtonType.CANCEL);

// Layout for the ComboBox, ListView, and buttons
        VBox layout = new VBox(10);
        layout.getChildren().addAll(new Label("Choose a column and click 'Add':"), columnComboBox, addButton, new Label("Selected columns:"), selectedColumnsListView, removeButton);
        layout.setPadding(new Insets(10));

// Set content of the dialog
        columnDialog.getDialogPane().setContent(layout);

// Convert the result to the list of selected columns
        columnDialog.setResultConverter(dialogButton -> {
            if (dialogButton == okButtonType) {
                return new ArrayList<>(selectedColumns);
            }
            return null;
        });

// Show the dialog and capture the result
        Optional<List<String>> result = columnDialog.showAndWait();

// If the user cancels or no columns are selected, stop the process
        if (result.isEmpty() || result.get().isEmpty()) {
            return;
        }

// Use the selected columns in order
        List<String> columns = result.get();
        this.columnsToSortOrFilter = columns;
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}