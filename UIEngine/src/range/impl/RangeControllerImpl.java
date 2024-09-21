package range.impl;

import command.api.Engine;
import dto.RangeDto;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import range.api.RangeController;
import spreadsheet.impl.SpreadsheetControllerImpl;
import spreadsheet.cell.impl.CellIdentifierImpl;
import java.util.Map;
import java.util.Optional;

public class RangeControllerImpl implements RangeController {

    @FXML private ListView<String> rangeListView;
    @FXML private Button addButton;
    @FXML private Button deleteButton;
    private final Engine engine;
    private ObservableList<String> rangeItems = FXCollections.observableArrayList();
    private SpreadsheetControllerImpl spreadsheetControllerImpl;
    private String currentlyHighlightedRange = null;


    public RangeController(Engine engine) {
        this.engine = engine;
    }

    @FXML
    public void initialize() {
        rangeListView.setItems(rangeItems);
        rangeListView.setOnMouseClicked(event -> handleMouseClick(event));

    }

    @Override
    private void handleMouseClick(javafx.scene.input.MouseEvent event) {
        if (event.getClickCount() == 1) {  // Check if single click
            String selectedRange = rangeListView.getSelectionModel().getSelectedItem();

            if (selectedRange != null && !selectedRange.trim().isEmpty() && spreadsheetControllerImpl != null) {
                if (selectedRange.equals(currentlyHighlightedRange)) {
                    // Clear highlight and reset the tracking
                    spreadsheetControllerImpl.clearPreviousRangeHighlight();
                    currentlyHighlightedRange = null;  // Clear the highlight tracking
                    rangeListView.getSelectionModel().clearSelection(); // Clear selection in ListView
                } else {
                    // Highlight the newly selected range
                    RangeDto rangeDto = spreadsheetControllerImpl.getCurrentSheet().getSheetRanges().get(selectedRange);
                    if (rangeDto != null) {
                        String topLeft = rangeDto.getTopLeft();
                        String bottomRight = rangeDto.getBottomRight();
                        spreadsheetControllerImpl.highlightRange(topLeft, bottomRight);
                        currentlyHighlightedRange = selectedRange;  // Update the tracking
                    }
                }
            } else {
                // No selection or no spreadsheet display controller available
                spreadsheetControllerImpl.clearPreviousRangeHighlight();
                currentlyHighlightedRange = null;
            }
        }
        else {
            spreadsheetControllerImpl.clearPreviousRangeHighlight();
            currentlyHighlightedRange = null;  // Clear the highlight tracking
            rangeListView.getSelectionModel().clearSelection(); // Clear selection in ListView
        }
    }


    @Override
    public void setSpreadsheetDisplayController(SpreadsheetControllerImpl spreadsheetControllerImpl) {
        this.spreadsheetControllerImpl = spreadsheetControllerImpl;
    }

    @Override
    public void displayRanges(Map<String, RangeDto> ranges) {
        rangeItems.clear();
        rangeItems.addAll(ranges.keySet());
    }

    @FXML
    @Override
    public void handleAddRange() {
        TextInputDialog rangeNameDialog = new TextInputDialog();
        rangeNameDialog.setTitle("Add Range");
        rangeNameDialog.setHeaderText("Enter new range name (example: MyRange):");
        Optional<String> rangeNameResult = rangeNameDialog.showAndWait();

        if (rangeNameResult.isEmpty()) {
            return;
        }
        String rangeName = rangeNameResult.get();

        try {
            if (spreadsheetControllerImpl.getCurrentSheet().getSheetRanges().get(rangeNameResult) != null)
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
            engine.addRange(rangeName, topLeft, bottomRight);
            rangeItems.add(rangeName);
        } catch (IllegalArgumentException e) {
            showAlert(Alert.AlertType.WARNING, "Cannot add Range ", e.getMessage());
        }
    }

    @FXML
    @Override
    public void handleDeleteRange() {
        String range = rangeListView.getSelectionModel().getSelectedItem();
        try {
            if (range != null) {
                engine.removeRange(range);
                rangeItems.remove(range);
                this.spreadsheetControllerImpl.clearPreviousRangeHighlight();

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
}
