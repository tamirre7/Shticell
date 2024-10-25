package shticell.client.sheetpanel.command.components.sortandfilter.impl;

import com.google.gson.Gson;
import dto.*;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;
import shticell.client.sheetpanel.command.components.sortandfilter.api.SortAndFilterController;
import shticell.client.sheetpanel.spreadsheet.api.SpreadsheetController;
import shticell.client.util.Constants;
import shticell.client.util.http.HttpClientUtil;
import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static shticell.client.util.http.HttpClientUtil.extractSheetFromResponseBody;
import static shticell.client.util.http.HttpClientUtil.showAlert;

// Implements sorting and filtering functionality for spreadsheet data
public class SortAndFilterControllerImpl implements SortAndFilterController {
    @FXML
    private Button sortButton;
    @FXML
    private Button filterButton;
    @FXML
    private Button resetButton;
    List<String> columnsToSortOrFilter;
    RangeDto sortOrFilterRange;
    SpreadsheetController spreadsheetController;

    @Override
    public void handleFilter() {
        // Show dialog to get range and columns for filtering
        sortAndFilterDialog();
        if (!isSortOrFilterInputValid()) {
            return;
        }

        Map<String, List<String>> selectedValuesForColumns = collectSelectedFilterValues();

        if (!selectedValuesForColumns.isEmpty()) {
            sendFilterRequest(selectedValuesForColumns);
        }
    }

    // Validate that required range and column data is present
    private boolean isSortOrFilterInputValid() {
        return sortOrFilterRange != null && columnsToSortOrFilter != null && !columnsToSortOrFilter.isEmpty();
    }

    // Collect filter values for each selected column
    private Map<String, List<String>> collectSelectedFilterValues() {
        Map<String, List<String>> selectedValuesForColumns = new HashMap<>();
        for (String column : columnsToSortOrFilter) {
            List<String> uniqueValues = getUniqueValuesForColumn(sortOrFilterRange, column);
            if (uniqueValues.isEmpty()) {
                showAlert("Error", "No values found in the selected column: " + column);
                continue;}

            List<String> selectedValues = showFilterDialogForColumn(column, uniqueValues);
            if (selectedValues != null) {
                selectedValuesForColumns.put(column, selectedValues);
            }
        }
        return selectedValuesForColumns;
    }

    // Create dialog with checkboxes for selecting filter values
    private List<String> showFilterDialogForColumn(String column, List<String> uniqueValues) {
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
        return selectedValuesResult.orElse(null);
    }

    // Prepare and send filter request to server
    private void sendFilterRequest(Map<String, List<String>> selectedValuesForColumns) {
        DimensionDto sheetDimensions = spreadsheetController.getCurrentSheet().getSheetDimension();
        DataToFilterDto dataToFilterDto = new DataToFilterDto(sortOrFilterRange, selectedValuesForColumns, sheetDimensions, spreadsheetController.getCurrentSheet().getSheetName());

        Gson gson = new Gson();
        String dataToFilterJson = gson.toJson(dataToFilterDto);

        RequestBody requestBody = RequestBody.create(dataToFilterJson, MediaType.parse("application/json"));

        Request request = new Request.Builder()
                .url(Constants.FILTER)
                .post(requestBody)
                .build();

        HttpClientUtil.runAsync(request, new Callback() {
            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseBody = response.body().string();
                    Platform.runLater(() -> {
                        SheetDto filterdSheetDto = extractSheetFromResponseBody(responseBody);
                        spreadsheetController.displayTemporarySheet(filterdSheetDto,false);
                    });
                } else {
                    String errorMessage = response.body() != null ? response.body().string() : response.message();
                    Platform.runLater(() -> showAlert("Error", "Failed to filter data: \n" + errorMessage)
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

    // Reset sheet to original state without sorting or filtering
    @Override
    public void handleResetSortFilter() {
        spreadsheetController.displayOriginalSheet(false);
    }

    // Prepare and send sort request
    @Override
    public void handleSort() {
        // Show dialog to get range and columns for sorting
        sortAndFilterDialog();

        if (!isSortOrFilterInputValid()) {
            return;
        }

        DimensionDto sheetDimensions = spreadsheetController.getCurrentSheet().getSheetDimension();
        DataToSortDto dataToSort = new DataToSortDto(columnsToSortOrFilter,sortOrFilterRange,sheetDimensions,spreadsheetController.getCurrentSheet().getSheetName());

        Gson gson = new Gson();
        String dataToSortJson = gson.toJson(dataToSort);

        RequestBody requestBody = RequestBody.create(dataToSortJson, MediaType.parse("application/json"));

        Request request = new Request.Builder()
                .url(Constants.SORT)
                .post(requestBody)
                .build();

        HttpClientUtil.runAsync(request, new Callback() {
            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseBody = response.body().string();
                    Platform.runLater(() -> {
                        SheetDto sortedSheetDto = extractSheetFromResponseBody(responseBody);
                        spreadsheetController.displayTemporarySheet(sortedSheetDto,false);
                    });
                } else {
                    String errorMessage = response.body() != null ? response.body().string() : response.message();
                    Platform.runLater(() -> showAlert("Error", "Failed to sort data: \n" + errorMessage)
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

    // Get unique values from a specific column within the range
    private List<String> getUniqueValuesForColumn(RangeDto range, String column) {
        Set<String> uniqueValues = new HashSet<>();
        SheetDto sheet = spreadsheetController.getCurrentSheet();

        String topLeft = range.getTopLeft();
        String bottomRight = range.getBottomRight();
        int colIndex = column.charAt(0) - 'A';

        for (int row = extractRowFromCell(topLeft); row <= extractRowFromCell(bottomRight); row++) {
            CellDto cell = sheet.getCells().get(createCellId(row,colIndex));
            if (cell != null)
                uniqueValues.add(cell.getEffectiveValue());
        }

        return new ArrayList<>(uniqueValues);
    }

    // Create cell identifier in format 'A1', 'B2', etc.
    private String createCellId(int row, int col) {
        return String.valueOf((char) ('A' + col)) + (row);
    }

    // Extract row number from cell reference (e.g., 'A1' -> 1)
    private static int extractRowFromCell(String cell) {
        Pattern pattern = Pattern.compile("[A-Z](\\d+)");
        Matcher matcher = pattern.matcher(cell);

        if (matcher.find()) {
            return Integer.parseInt(matcher.group(1));
        } else {
            throw new IllegalArgumentException("No valid cell reference found");
        }
    }

    // Get range selection from user through dialog
    private Optional<RangeDto> getRangeFromUser(String rangeName) {
        Optional<String> topLeftResult = promptForCell("Top-Left Cell", "Enter top-left cell (example: A1):");
        if (topLeftResult.isEmpty()) {
            return Optional.empty();
        }
        String StrTopLeft = topLeftResult.get().toUpperCase();

        Optional<String> bottomRightResult = promptForCell("Bottom-Right Cell", "Enter bottom-right cell (example: B2):");
        if (bottomRightResult.isEmpty()) {
            return Optional.empty();
        }
        String StrBottomRight = bottomRightResult.get().toUpperCase();

        RangeDto range = new RangeDto(rangeName, StrTopLeft, StrBottomRight,false);
        return Optional.of(range);
    }

    // Show dialog for cell selection
    private Optional<String> promptForCell(String title, String headerText) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle(title);
        dialog.setHeaderText(headerText);
        Optional<String> result = dialog.showAndWait();

        if (result.isEmpty() || result.get().trim().isEmpty()) {
            showAlert("Error", "Cell ID cannot be empty.");
            return Optional.empty();
        }

        return result;
    }

    // Create UI for column selection
    private List<String> getColumnsFromUser(char leftCol, char rightCol) {
        int numOfColumns = rightCol - leftCol + 1;
        ComboBox<String> columnComboBox = new ComboBox<>();
        for (int i = 0; i < numOfColumns; i++) {
            String columnName = String.valueOf((char) (leftCol + i));
            columnComboBox.getItems().add(columnName);
        }
        columnComboBox.setPromptText("Select a column");
        ListView<String> selectedColumnsListView = new ListView<>();
        ObservableList<String> selectedColumns = FXCollections.observableArrayList();
        selectedColumnsListView.setItems(selectedColumns);

        Button addButton = createAddButton(columnComboBox, selectedColumns);
        Button removeButton = createRemoveButton(selectedColumnsListView, selectedColumns);

        return showColumnDialog(columnComboBox, addButton, selectedColumnsListView, removeButton);
    }

    // Create button for adding selected columns
    private Button createAddButton(ComboBox<String> columnComboBox, ObservableList<String> selectedColumns) {
        Button addButton = new Button("Add");
        addButton.setDisable(true);

        columnComboBox.valueProperty().addListener((obs, oldVal, newVal) -> {
            addButton.setDisable(newVal == null || selectedColumns.contains(newVal));
        });

        addButton.setOnAction(e -> {
            String selectedColumn = columnComboBox.getValue();
            if (selectedColumn != null && !selectedColumns.contains(selectedColumn)) {
                selectedColumns.add(selectedColumn);
            }
            columnComboBox.setValue(null);
        });

        return addButton;
    }

    // Create button for removing selected columns
    private Button createRemoveButton(ListView<String> selectedColumnsListView, ObservableList<String> selectedColumns) {
        Button removeButton = new Button("Remove Selected");
        removeButton.setDisable(true);

        selectedColumnsListView.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            removeButton.setDisable(newVal == null);
        });

        removeButton.setOnAction(e -> {
            String selectedColumn = selectedColumnsListView.getSelectionModel().getSelectedItem();
            if (selectedColumn != null) {
                selectedColumns.remove(selectedColumn);
            }
        });

        return removeButton;
    }

    // Show dialog for column selection
    private List<String> showColumnDialog(ComboBox<String> columnComboBox, Button addButton, ListView<String> selectedColumnsListView, Button removeButton) {
        Dialog<List<String>> columnDialog = new Dialog<>();
        columnDialog.getDialogPane().setPrefSize(300, 500);
        columnDialog.setTitle("Select Columns");
        columnDialog.setHeaderText("Select columns (in order):");

        ButtonType okButtonType = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
        columnDialog.getDialogPane().getButtonTypes().addAll(okButtonType, ButtonType.CANCEL);

        VBox layout = new VBox(10);
        layout.getChildren().addAll(new Label("Choose a column and click 'Add':"), columnComboBox, addButton, new Label("Selected columns:"), selectedColumnsListView, removeButton);
        layout.setPadding(new Insets(10));

        columnDialog.getDialogPane().setContent(layout);

        columnDialog.setResultConverter(dialogButton -> {
            if (dialogButton == okButtonType) {
                return new ArrayList<>(selectedColumnsListView.getItems());
            }
            return null;
        });

        Optional<List<String>> result = columnDialog.showAndWait();
        return result.orElse(Collections.emptyList());
    }

    // Show dialog for selecting range and columns
    private void sortAndFilterDialog() {
        Optional<RangeDto> rangeResult = getRangeFromUser("sortAndFilterRange");
        if (rangeResult.isEmpty()) {
            return;
        }

        RangeDto range = rangeResult.get();
        sortOrFilterRange = range;

        char leftCol = range.getTopLeft().charAt(0);
        char rightCol = range.getBottomRight().charAt(0);

        List<String> columns = getColumnsFromUser(leftCol, rightCol);
        if (columns.isEmpty()) {
            return;
        }

        this.columnsToSortOrFilter = columns;
    }

    // Enables the sort and filter functionalities by enabling the sort, filter, and reset buttons.
    @Override
    public void enableSortAndFilter() {
        sortButton.setDisable(false);
        filterButton.setDisable(false);
        resetButton.setDisable(false);
    }

    // Enables only the reset functionality by disabling sort and filter, but keeping the reset button enabled.
    @Override
    public void enableResetOnly() {
        disableSortAndFilter();
        resetButton.setDisable(false);
    }

    // Disables all sort, filter, and reset functionalities by disabling the corresponding buttons.
    @Override
    public void disableSortAndFilter() {
        sortButton.setDisable(true);
        filterButton.setDisable(true);
        resetButton.setDisable(true);
    }

    // Sets the SpreadsheetController instance.
    @Override
    public void setSpreadsheetController(SpreadsheetController spreadsheetController) {
        this.spreadsheetController = spreadsheetController;
    }


}
