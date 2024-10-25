package shticell.client.sheetpanel.range.impl;

import com.google.gson.Gson;
import dto.RangeDto;
import dto.SheetDto;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;
import shticell.client.sheetpanel.range.api.RangeController;
import shticell.client.sheetpanel.spreadsheet.UISheetModel;
import shticell.client.sheetpanel.spreadsheet.api.SpreadsheetController;
import shticell.client.util.Constants;
import shticell.client.util.http.HttpClientUtil;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

import static shticell.client.util.http.HttpClientUtil.showAlert;

public class RangeControllerImpl implements RangeController {
    @FXML
    private ListView<String> rangeListView;
    @FXML private Button addButton;
    @FXML private Button deleteButton;

    private ObservableList<String> rangeItems = FXCollections.observableArrayList();
    private SpreadsheetController spreadsheetController;
    private String currentlyHighlightedRange = null;
    private UISheetModel uiSheetModel;

    @FXML
    private void initialize() {
        rangeListView.setItems(rangeItems);
        rangeListView.setOnMouseClicked(this::handleMouseClick);
    }

    // handle a range from the list view being clicked
    @Override
    public void handleMouseClick(MouseEvent event) {
        if (event.getClickCount() == 1) {  // Single click to select range
            String selectedRange = rangeListView.getSelectionModel().getSelectedItem();

            if (selectedRange != null && !selectedRange.trim().isEmpty() && spreadsheetController != null) {
                if (selectedRange.equals(currentlyHighlightedRange)) {
                    // Clear highlight if the same range is clicked again
                    uiSheetModel.clearPreviousRangeHighlight();
                    currentlyHighlightedRange = null;
                    rangeListView.getSelectionModel().clearSelection();
                } else {
                    // Highlight the newly selected range
                    RangeDto rangeDto = spreadsheetController.getCurrentSheet().getSheetRanges().get(selectedRange);
                    if (rangeDto != null) {
                        uiSheetModel.highlightRange(rangeDto.getTopLeft(), rangeDto.getBottomRight());
                        currentlyHighlightedRange = selectedRange;  // Update highlight tracking
                    }
                }
            } else {
                // Clear highlight if no valid selection
                uiSheetModel.clearPreviousRangeHighlight();
                currentlyHighlightedRange = null;
            }
        } else {
            // Clear highlight on double-click or other mouse interactions
            uiSheetModel.clearPreviousRangeHighlight();
            currentlyHighlightedRange = null;
            rangeListView.getSelectionModel().clearSelection();
        }
    }

    @Override
    public void setSpreadsheetController(SpreadsheetController spreadsheetController) {
        this.spreadsheetController = spreadsheetController;
    }

    @Override
    public void setUiSheetModel(UISheetModel uiSheetModel) {
        this.uiSheetModel = uiSheetModel;
    }

    @Override
    public void displayRanges(Map<String, RangeDto> ranges) {
        rangeItems.clear();
        rangeItems.addAll(ranges.keySet());
    }

    // Adding range by sending the server a post request with the relevant details
    @FXML
    @Override
    public void handleAddRange() {
        String rangeName = newRangeNameDialog();
        if (rangeName == null) return;

        String topLeftCell = newRangeTopLeftDialog();
        if (topLeftCell == null) return;

        String bottomRightCell = newRangeBottomRightDialog();
        if (bottomRightCell == null) return;

        // Create new range request
        Map<String, String> newRange = new HashMap<>();
        newRange.put("rangeName", rangeName);
        newRange.put("topLeftCell", topLeftCell);
        newRange.put("bottomRightCell", bottomRightCell);
        newRange.put("sheetName", spreadsheetController.getCurrentSheet().getSheetName());

        // Convert to JSON
        Gson gson = new Gson();
        String newRangeJson = gson.toJson(newRange);

        // Send request to add range
        RequestBody requestBody = RequestBody.create(newRangeJson, MediaType.parse("application/json"));
        Request request = new Request.Builder()
                .url(Constants.ADD_RANGE)
                .post(requestBody)
                .build();

        // Handle asynchronous response
        HttpClientUtil.runAsync(request, new Callback() {
            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseBody = response.body().string();
                    Platform.runLater(() -> {
                        rangeItems.add(rangeName);
                        SheetDto updatedSheet = HttpClientUtil.extractSheetFromResponseBody(responseBody);
                        spreadsheetController.setCurrentSheet(updatedSheet);
                    });
                } else {
                    String errorMessage = response.body() != null ? response.body().string() : "Unknown error";
                    Platform.runLater(() -> showAlert("Error", errorMessage));
                }
            }

            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Platform.runLater(() -> showAlert("Error", "Error: " + e.getMessage()));
            }
        });
    }

    private String newRangeNameDialog() {
        TextInputDialog rangeNameDialog = new TextInputDialog();
        rangeNameDialog.setTitle("Add Range");
        rangeNameDialog.setHeaderText("Enter new range name (example: MyRange):");
        Optional<String> rangeNameResult = rangeNameDialog.showAndWait();
        return rangeNameResult.orElse(null);
    }

    private String newRangeTopLeftDialog() {
        // Prompt for top-left cell identifier
        TextInputDialog topLeftDialog = new TextInputDialog();
        topLeftDialog.setTitle("Top-Left Cell");
        topLeftDialog.setHeaderText("Enter top-left cell (example: A1):");
        Optional<String> topLeftResult = topLeftDialog.showAndWait();
        return topLeftResult.map(String::toUpperCase).orElse(null);
    }

    private String newRangeBottomRightDialog() {
        TextInputDialog bottomRightDialog = new TextInputDialog();
        bottomRightDialog.setTitle("Bottom-Right Cell");
        bottomRightDialog.setHeaderText("Enter bottom-right cell (example: B2):");
        Optional<String> bottomRightResult = bottomRightDialog.showAndWait();
        return bottomRightResult.map(String::toUpperCase).orElse(null);
    }

    // Handling deleting range by sending the server a delete request with the relevant details
    @FXML
    @Override
    public void handleDeleteRange() {
        String rangeToDelete = rangeListView.getSelectionModel().getSelectedItem();
        if (rangeToDelete != null) {
            Gson gson = new Gson();
            Map<String, String> rangeToDeleteMap = new HashMap<>();
            rangeToDeleteMap.put("rangeName", rangeToDelete);
            rangeToDeleteMap.put("sheetName", spreadsheetController.getCurrentSheet().getSheetName());

            String rangeToDeleteJson = gson.toJson(rangeToDeleteMap);
            RequestBody requestBody = RequestBody.create(rangeToDeleteJson, MediaType.parse("application/json"));
            Request request = new Request.Builder()
                    .url(Constants.DELETE_RANGE)
                    .delete(requestBody)
                    .build();

            // Handle asynchronous response
            HttpClientUtil.runAsync(request, new Callback() {
                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                    if (response.isSuccessful()) {
                        String responseBody = response.body().string();
                        Platform.runLater(() -> {
                            SheetDto updatedSheet = HttpClientUtil.extractSheetFromResponseBody(responseBody);
                            spreadsheetController.setCurrentSheet(updatedSheet);
                            rangeItems.remove(rangeToDelete);
                            uiSheetModel.clearPreviousRangeHighlight();
                        });
                    } else {
                        String errorMessage = response.body() != null ? response.body().string() : "Unknown error";
                        Platform.runLater(() -> showAlert("Error", errorMessage));
                    }
                }

                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    Platform.runLater(() -> showAlert("Error", "Error: " + e.getMessage()));
                }
            });
        }
    }

    @Override
    public void disableEditing() {
        rangeListView.setDisable(true);
        addButton.setDisable(true);
        deleteButton.setDisable(true);
    }

    @Override
    public void enableViewOnly() {
        disableEditing();
        rangeListView.setDisable(false);
    }

    @Override
    public void enableEditing() {
        rangeListView.setDisable(false);
        addButton.setDisable(false);
        deleteButton.setDisable(false);
    }
}
