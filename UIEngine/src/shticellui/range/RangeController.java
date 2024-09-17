package shticellui.range;
import command.api.Engine;
import dto.RangeDto;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import shticellui.spreadsheet.SpreadsheetDisplayController;
import spreadsheet.cell.impl.CellIdentifierImpl;

import java.util.Map;
import java.util.Optional;
public class RangeController {

    @FXML private ListView<String> rangeListView;
    @FXML private Button addButton;
    @FXML private Button deleteButton;
    private final Engine engine;
    private ObservableList<String> rangeItems = FXCollections.observableArrayList();
    private SpreadsheetDisplayController spreadsheetDisplayController;
    private String currentlyHighlightedRange = null;



    public RangeController(Engine engine) {
        this.engine = engine;
    }

    @FXML
    public void initialize() {
        rangeListView.setItems(rangeItems);
    }

    public void setSpreadsheetDisplayController(SpreadsheetDisplayController spreadsheetDisplayController) {
        this.spreadsheetDisplayController = spreadsheetDisplayController;
    }

    public void displayRanges(Map<String, RangeDto> ranges) {
        rangeItems.clear();
        for(String rangeKey : ranges.keySet()) {
            rangeItems.add(rangeKey);
        }
    }

    @FXML
    public void handleAddRange() {
        TextInputDialog rangeNameDialog = new TextInputDialog();
        rangeNameDialog.setTitle("Add Range");
        rangeNameDialog.setHeaderText("Enter new range name (example: MyRange):");
        Optional<String> rangeNameResult = rangeNameDialog.showAndWait();

        // If the user cancels the range name dialog, stop the process
        if (!rangeNameResult.isPresent()) {
            return;
        }
        String rangeName = rangeNameResult.get();

        try {
            if (engine.getRange(rangeName) != null)
                 throw new IllegalArgumentException("Range name already exists");
         }
         catch (IllegalArgumentException e) {
                showAlert(Alert.AlertType.WARNING, "Cannot add Range ", e.getMessage());
                return;

         }
        // Prompt for top-left cell identifier
        TextInputDialog topLeftDialog = new TextInputDialog();
        topLeftDialog.setTitle("Top-Left Cell");
        topLeftDialog.setHeaderText("Enter top-left cell (example: A1):");
        Optional<String> topLeftResult = topLeftDialog.showAndWait();

        // If the user cancels the top-left cell dialog, stop the process
        if (!topLeftResult.isPresent()) {
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
            engine.addRange(rangeName, topLeft, bottomRight);
            rangeItems.add(rangeName);
        } catch (IllegalArgumentException e) {
            showAlert(Alert.AlertType.WARNING, "Cannot add Range ", e.getMessage());
        }
    }



    @FXML
    public void handleDeleteRange(){
        String range = rangeListView.getSelectionModel().getSelectedItem();
        try {
            if (range != null) {
                engine.removeRange(range);
                rangeItems.remove(range);
            }
        }
        catch(IllegalArgumentException e) {
            showAlert(Alert.AlertType.WARNING, "Cannot delete Range ", e.getMessage());
        }

    }
    @FXML
    public void handleRangeSelection() {
        String selectedRange = rangeListView.getSelectionModel().getSelectedItem();
        if (selectedRange != null && spreadsheetDisplayController != null) {
            if (selectedRange.equals(currentlyHighlightedRange)) {
                // If the selected range is already highlighted, clear the highlight
                spreadsheetDisplayController.clearPreviousRangeHighlight();
                currentlyHighlightedRange = null;
            } else {
                // Highlight the newly selected range
                RangeDto rangeDto = engine.getRange(selectedRange);
                if (rangeDto != null) {
                    String topLeft = rangeDto.getTopLeft();
                    String bottomRight = rangeDto.getBottomRight();
                    spreadsheetDisplayController.highlightRange(topLeft, bottomRight);
                    currentlyHighlightedRange = selectedRange;
                }
            }
        }
    }
    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void disableEditing() {
        rangeListView.setDisable(true);
        addButton.setDisable(true);
        deleteButton.setDisable(true);
    }

    public void enableEditing() {
        rangeListView.setDisable(false);
        addButton.setDisable(false);
        deleteButton.setDisable(false);
    }
}
