package range.impl;

import command.api.Engine;
import dto.RangeDto;
import dto.SheetDto;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import range.api.RangeController;
import spreadsheet.UISheetModel;
import spreadsheet.api.SpreadsheetController;
import spreadsheet.cell.impl.CellIdentifierImpl;
import java.util.Map;
import java.util.Optional;

public class RangeControllerImpl implements RangeController {

    @FXML private ListView<String> rangeListView;
    @FXML private Button addButton;
    @FXML private Button deleteButton;
    private Engine engine;
    private ObservableList<String> rangeItems = FXCollections.observableArrayList();
    private SpreadsheetController spreadsheetController;
    private String currentlyHighlightedRange = null;
    private UISheetModel uiSheetModel;


    public RangeControllerImpl(Engine engine) {
        this.engine = engine;
    }

    @FXML
    public void initialize() {
        rangeListView.setItems(rangeItems);
        rangeListView.setOnMouseClicked(event -> handleMouseClick(event));

    }

    @Override
    public void handleMouseClick(javafx.scene.input.MouseEvent event) {
        if (event.getClickCount() == 1) {  // Check if single click
            String selectedRange = rangeListView.getSelectionModel().getSelectedItem();

            if (selectedRange != null && !selectedRange.trim().isEmpty() && spreadsheetController != null) {
                if (selectedRange.equals(currentlyHighlightedRange)) {
                    // Clear highlight and reset the tracking
                    uiSheetModel.clearPreviousRangeHighlight();
                    currentlyHighlightedRange = null;  // Clear the highlight tracking
                    rangeListView.getSelectionModel().clearSelection(); // Clear selection in ListView
                } else {
                    // Highlight the newly selected range
                    RangeDto rangeDto = spreadsheetController.getCurrentSheet().getSheetRanges().get(selectedRange);
                    if (rangeDto != null) {
                        String topLeft = rangeDto.getTopLeft();
                        String bottomRight = rangeDto.getBottomRight();
                        uiSheetModel.highlightRange(topLeft, bottomRight);
                        currentlyHighlightedRange = selectedRange;  // Update the tracking
                    }
                }
            } else {
                // No selection or no spreadsheet display controller available
                uiSheetModel.clearPreviousRangeHighlight();
                currentlyHighlightedRange = null;
            }
        }
        else {
            uiSheetModel.clearPreviousRangeHighlight();
            currentlyHighlightedRange = null;  // Clear the highlight tracking
            rangeListView.getSelectionModel().clearSelection(); // Clear selection in ListView
        }
    }


    @Override
    public void setSpreadsheetDisplayController(SpreadsheetController spreadsheetController) {
        this.spreadsheetController = spreadsheetController;
    }

    public void setUiSheetModel(UISheetModel uiSheetModel) {
        this.uiSheetModel = uiSheetModel;
    }

    @Override
    public void displayRanges(Map<String, RangeDto> ranges) {
        rangeItems.clear();
        rangeItems.addAll(ranges.keySet());
    }

    @FXML
    @Override
    public void handleAddRange() {
        if (!engine.isFileLoaded())
        {
            showAlert("Error", "A file must be loaded first.");
            return;
        }
        TextInputDialog rangeNameDialog = new TextInputDialog();
        rangeNameDialog.setTitle("Add Range");
        rangeNameDialog.setHeaderText("Enter new range name (example: MyRange):");
        Optional<String> rangeNameResult = rangeNameDialog.showAndWait();

        if (rangeNameResult.isEmpty()) {
            return;
        }
        String rangeName = rangeNameResult.get();

        try {
            if (spreadsheetController.getCurrentSheet().getSheetRanges().get(rangeName) != null)
                throw new IllegalArgumentException("Range name already exists");
        } catch (IllegalArgumentException e) {
            showAlert(Alert.AlertType.WARNING, "Cannot add Range ", e.getMessage());
            return;
        }

        // Prompt for top-left cell identifier
        TextInputDialog topLeftDialog = new TextInputDialog();
        topLeftDialog.setTitle("Top-Left Cell");
        topLeftDialog.setHeaderText("Enter top-left cell (example: A1):");
        Optional<String> topLeftResult = topLeftDialog.showAndWait();

        if (topLeftResult.isEmpty()) {
            return;
        }

        String strTopLeft = topLeftResult.get().toUpperCase();

        // Prompt for bottom-right cell identifier
        TextInputDialog bottomRightDialog = new TextInputDialog();
        bottomRightDialog.setTitle("Bottom-Right Cell");
        bottomRightDialog.setHeaderText("Enter bottom-right cell (example: B2):");
        Optional<String> bottomRightResult = bottomRightDialog.showAndWait();

        if (bottomRightResult.isEmpty()) {
            return;
        }

        String strBottomRight = bottomRightResult.get().toUpperCase();

        try {
            CellIdentifierImpl topLeft = new CellIdentifierImpl(strTopLeft);
            CellIdentifierImpl bottomRight = new CellIdentifierImpl(strBottomRight);
            SheetDto updatedSheet = engine.addRange(rangeName, topLeft, bottomRight);
            spreadsheetController.setCurrentSheet(updatedSheet);
            rangeItems.add(rangeName);
        } catch (IllegalArgumentException e) {
            showAlert(Alert.AlertType.WARNING, "Cannot add Range ", e.getMessage());
        }
    }

    @FXML
    @Override
    public void handleDeleteRange() {
        if (!engine.isFileLoaded())
        {
            showAlert("Error", "A file must be loaded first.");
            return;
        }
        String range = rangeListView.getSelectionModel().getSelectedItem();
        try {
            if (range != null) {
                SheetDto updateSheet = engine.removeRange(range);
                spreadsheetController.setCurrentSheet(updateSheet);
                rangeItems.remove(range);
                this.uiSheetModel.clearPreviousRangeHighlight();

            }
        } catch (IllegalArgumentException e) {
            showAlert(Alert.AlertType.WARNING, "Cannot delete Range ", e.getMessage());
        }
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @Override
    public void disableEditing() {
        rangeListView.setDisable(true);
        addButton.setDisable(true);
        deleteButton.setDisable(true);
    }

    @Override
    public void enableEditing() {
        rangeListView.setDisable(false);
        addButton.setDisable(false);
        deleteButton.setDisable(false);
    }

    private void showAlert(String title, String message) {
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}