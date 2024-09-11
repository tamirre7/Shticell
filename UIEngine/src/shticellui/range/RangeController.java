package shticellui.range;
import command.api.Engine;
import dto.RangeDto;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextInputDialog;
import shticellui.spreadsheet.SpreadsheetDisplayController;
import spreadsheet.cell.impl.CellIdentifierImpl;

import java.util.Map;
import java.util.Optional;
public class RangeController {

    @FXML private ListView<String> rangeListView;
    private final Engine engine;
    private ObservableList<String> rangeItems = FXCollections.observableArrayList();
    private SpreadsheetDisplayController spreadsheetDisplayController;

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

        rangeNameResult.ifPresent(rangeName -> {
            // Prompt for top-left cell identifier
            TextInputDialog topLeftDialog = new TextInputDialog();
            topLeftDialog.setTitle("Top-Left Cell");
            topLeftDialog.setHeaderText("Enter top-left cell (example: A1):");
            Optional<String> topLeftResult = topLeftDialog.showAndWait();

            // Prompt for bottom-right cell identifier
            TextInputDialog bottomRightDialog = new TextInputDialog();
            bottomRightDialog.setTitle("Bottom-Right Cell");
            bottomRightDialog.setHeaderText("Enter bottom-right cell (example: B2):");
            Optional<String> bottomRightResult = bottomRightDialog.showAndWait();

            if (topLeftResult.isPresent() && bottomRightResult.isPresent()) {
                CellIdentifierImpl topLeft = new CellIdentifierImpl(topLeftResult.get());
                CellIdentifierImpl bottomRight = new CellIdentifierImpl(bottomRightResult.get());
                engine.addRange(rangeName, topLeft, bottomRight);
                rangeItems.add(rangeName);
            }
        });
    }


    @FXML
    public void handleDeleteRange(){
        String range = rangeListView.getSelectionModel().getSelectedItem();
        if(range != null) {
            engine.removeRange(range);
            rangeItems.remove(range);
        }

    }
    @FXML
    public void handleRangeSelection() {
        String selectedRange = rangeListView.getSelectionModel().getSelectedItem();
        if (selectedRange != null && spreadsheetDisplayController != null) {
            RangeDto rangeDto = engine.getRange(selectedRange);
            if (rangeDto != null) {
                String topLeft = rangeDto.getTopLeft();
                String bottomRight = rangeDto.getBottomRight();
                spreadsheetDisplayController.highlightRange(topLeft, bottomRight);
            }
        }
    }
}
