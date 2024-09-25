package command.components.sortandfilter.impl;

import command.api.Engine;
import command.components.sortandfilter.api.SortAndFilterController;
import dto.SheetDto;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import spreadsheet.api.SpreadsheetController;
import spreadsheet.api.Dimension;
import spreadsheet.cell.impl.CellIdentifierImpl;
import spreadsheet.range.api.Range;
import spreadsheet.range.impl.RangeImpl;
import spreadsheet.sheetimpl.DimensionImpl;
import dto.CellDto;
import javafx.scene.layout.Region;

import java.util.*;
import java.util.stream.Collectors;

public class SortAndFilterControllerImpl implements SortAndFilterController {
    @FXML
    private Button sortButton;
    @FXML
    private Button filterButton;
    @FXML
    private Button resetButton;
    List<String> columnsToSortOrFilter;
    Range sortOrFilterRange;
    Dimension sheetDimension;
    SpreadsheetController spreadsheetController;
    private Engine engine;

    public SortAndFilterControllerImpl(Engine engine, SpreadsheetController spreadsheetController) {
        this.engine = engine;
        this.spreadsheetController = spreadsheetController;
    }


    @FXML
    public void handleSort() {
        if (!engine.isFileLoaded())
        {
            showAlert(Alert.AlertType.ERROR, "Error", "A file must be loaded first.");
            return;
        }

        sortAndFilterDialog();

        if (sortOrFilterRange == null || columnsToSortOrFilter == null || columnsToSortOrFilter.isEmpty()) {
            return;
        }

        try {
            // Call the Engine to sort the selected range based on the chosen columns
            SheetDto sortedSheet = engine.sortRange(sortOrFilterRange, columnsToSortOrFilter);
            spreadsheetController.displayTemporarySheet(sortedSheet,false);


        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Sort Error", "An error occurred while sorting: " + e.getMessage());
        }

    }
    @Override
    public void disableSortAndFilter(boolean versionView) {
        sortButton.setDisable(true);
        filterButton.setDisable(true);
        if (versionView) {resetButton.setDisable(true);}

    }


    @FXML
    @Override
    public void handleFilter() {
        if (!engine.isFileLoaded())
        {
            showAlert(Alert.AlertType.ERROR,"Error", "A file must be loaded first.");
            return;
        }
        sortAndFilterDialog();

        if (sortOrFilterRange == null || columnsToSortOrFilter == null || columnsToSortOrFilter.isEmpty()) {
            return;
        }

        Map<String, List<String>> selectedValuesForColumns = new HashMap<>();

        for (String column : columnsToSortOrFilter) {
            List<String> uniqueValues = getUniqueValuesForColumn(sortOrFilterRange, column);

            if (uniqueValues.isEmpty()) {
                showAlert(Alert.AlertType.WARNING, "Error", "No values found in the selected column: " + column);
                continue;
            }

            Dialog<List<String>> filterDialog = new Dialog<>();
            filterDialog.setTitle("Filter Criteria for Column " + column);
            filterDialog.setHeaderText("Select values to filter by for column " + column);

            VBox vbox = new VBox();
            List<CheckBox> checkBoxes = new ArrayList<>();
            for (String value : uniqueValues) {
                CheckBox checkBox = new CheckBox(value);
                checkBoxes.add(checkBox);
                vbox.getChildren().add(checkBox);
            }
            filterDialog.getDialogPane().setContent(vbox);

            ButtonType okButtonType = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
            filterDialog.getDialogPane().getButtonTypes().addAll(okButtonType, ButtonType.CANCEL);

            filterDialog.setResultConverter(dialogButton -> {
                if (dialogButton == okButtonType) {
                    return checkBoxes.stream()
                            .filter(CheckBox::isSelected)
                            .map(CheckBox::getText)
                            .collect(Collectors.toList());
                }
                return null;
            });

            Optional<List<String>> selectedValuesResult = filterDialog.showAndWait();

            if (selectedValuesResult.isPresent()) {
                selectedValuesForColumns.put(column, selectedValuesResult.get());
            }
        }

        if (!selectedValuesForColumns.isEmpty()) {
            try {
                SheetDto filteredSheet = engine.filterRangeByColumnsAndValues(sortOrFilterRange, selectedValuesForColumns);
                spreadsheetController.displayTemporarySheet(filteredSheet,false);
            } catch (Exception e) {
                showAlert(Alert.AlertType.ERROR, "Filter Error", "An error occurred while filtering: " + e.getMessage());
            }
        }
    }

    private List<String> getUniqueValuesForColumn(Range range, String column) {
        Set<String> uniqueValues = new HashSet<>();
        SheetDto sheet = engine.displayCurrentSpreadsheet();

        CellIdentifierImpl topLeft = range.getTopLeft();
        CellIdentifierImpl bottomRight = range.getBottomRight();
        int colIndex = column.charAt(0) - 'A';

        for (int row = topLeft.getRow(); row <= bottomRight.getRow(); row++) {
            CellDto cell = sheet.getCells().get(engine.createCellId(row,colIndex));
            if (cell != null)
                uniqueValues.add(cell.getEffectiveValue());
        }

        return new ArrayList<>(uniqueValues);
    }


    @FXML
    @Override
    public void handleResetSortFilter() {
        if (!engine.isFileLoaded())
        {
            showAlert(Alert.AlertType.ERROR, "Error", "A file must be loaded first.");
            return;
        }
        enableSortAndFilter();
        spreadsheetController.displayOriginalSheet(false);
    }
    @Override
    public void enableSortAndFilter() {
        sortButton.setDisable(false);
        filterButton.setDisable(false);
        resetButton.setDisable(false);
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
            return; // User canceled, do nothing
        }

        String StrTopLeft = topLeftResult.get().toUpperCase();
        if (StrTopLeft.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Error", "Cell ID cannot be empty.");
            return;
        }

        // Prompt for bottom-right cell identifier
        TextInputDialog bottomRightDialog = new TextInputDialog();
        bottomRightDialog.setTitle("Bottom-Right Cell");
        bottomRightDialog.setHeaderText("Enter bottom-right cell (example: B2):");
        Optional<String> bottomRightResult = bottomRightDialog.showAndWait();

        // If the user cancels the bottom-right cell dialog, stop the process
        if (bottomRightResult.isEmpty()) {
            return; // User canceled, do nothing
        }

        String StrBottomRight = bottomRightResult.get().toUpperCase();
        if (StrBottomRight.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Error", "Cell ID cannot be empty.");
            return;
        }

        try {
            // If both cells are provided, process the range creation
            CellIdentifierImpl topLeft = new CellIdentifierImpl(StrTopLeft);
            CellIdentifierImpl bottomRight = new CellIdentifierImpl(StrBottomRight);
            Range range = new RangeImpl(rangeName, topLeft, bottomRight, sheetDimension);
            sortOrFilterRange = range;

        } catch (IllegalArgumentException e) {
            showAlert(Alert.AlertType.WARNING, "Error - out of range", e.getMessage());
            return;
        }

        char leftCol = StrTopLeft.charAt(0);
        char rightCol = StrBottomRight.charAt(0);
        int leftColNumber = leftCol - 'A' + 1;
        int rightColNumber = rightCol - 'A' + 1;
        int numOfColumns = rightColNumber - leftColNumber + 1;
        // Create a ComboBox for column selection
        ComboBox<String> columnComboBox = new ComboBox<>();
        for (int i = 0; i < numOfColumns; i++) {
            String columnName = String.valueOf((char) (leftCol + i));
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
        if (result.isEmpty() || result.get() == null) {
            return; // User canceled, do nothing
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

        // Resize the alert window to fit the content
        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.setMinHeight(Region.USE_PREF_SIZE);  // Adjust height to fit content
        dialogPane.setMinWidth(Region.USE_PREF_SIZE);   // Adjust width to fit content

        alert.showAndWait();
    }


}