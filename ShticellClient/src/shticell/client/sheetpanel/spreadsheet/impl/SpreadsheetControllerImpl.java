package shticell.client.sheetpanel.spreadsheet.impl;


import com.google.gson.Gson;
import dto.CellDto;
import dto.DimensionDto;
import dto.SheetDto;
import dto.permission.Permission;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;
import shticell.client.sheetpanel.action.line.api.ActionLineController;
import shticell.client.sheetpanel.command.components.formulabuilder.FormulaBuilder;
import shticell.client.sheetpanel.editingmanager.api.EditingManager;
import shticell.client.sheetpanel.range.api.RangeController;
import shticell.client.sheetpanel.spreadsheet.UISheetModel;
import shticell.client.sheetpanel.spreadsheet.api.SpreadsheetController;
import shticell.client.sheetpanel.misc.api.MiscController;
import shticell.client.util.Constants;
import shticell.client.util.http.HttpClientUtil;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import static shticell.client.util.http.HttpClientUtil.extractSheetFromResponseBody;
import static shticell.client.util.http.HttpClientUtil.showAlert;

// Implementation of the SpreadsheetController interface that manages spreadsheet UI interactions
// and handles cell operations, styling, and display logic
public class SpreadsheetControllerImpl implements SpreadsheetController {
    @FXML
    private GridPane gridPane;
    @FXML private ScrollPane scrollPane;
    private SheetDto currentSheet;
    private SheetDto savedSheet;
    private UISheetModel uiSheetModel;
    private int currentSheetNumRows;
    private int currentSheetNumCols;
    private ActionLineController actionLineController;
    private RangeController rangeController;
    private MiscController miscController;
    private FormulaBuilder formulaBuilder;
    private EditingManager editingManager;
    private Permission permission;

    // Initializes the ScrollPane and GridPane with appropriate settings and listeners
    // for proper display and scrolling behavior
    @FXML
    public void initialize() {
        scrollPane.setContent(gridPane);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);

        // Set a minimum width for the GridPane
        gridPane.setMinWidth(Constants.SHEET_GRID_PANE_WIDTH);
        gridPane.setMinHeight(Constants.SHEET_GRID_PANE_HEIGHT);

        // Add a listener to adjust the ScrollPane's width
        scrollPane.viewportBoundsProperty().addListener((observable, oldValue, newValue) -> {
            double width = newValue.getWidth();
            gridPane.setPrefWidth(Math.max(width, gridPane.getMinWidth()));
        });
    }

    // Displays a sheet by setting up dimensions, creating cells, and updating their content
    // Also handles range display and animations if enabled
    @Override
    public void displaySheet(SheetDto sheetDto) {
        uiSheetModel.clearCells();
        actionLineController.clearTextFields();

        DimensionDto sheetDimensions = sheetDto.getSheetDimension();

        this.currentSheetNumRows = sheetDimensions.getNumRows();
        this.currentSheetNumCols = sheetDimensions.getNumCols();

        rangeController.displayRanges(sheetDto.getSheetRanges());

        if (gridPane.getChildren().isEmpty()) {
            uiSheetModel.setupGridDimensions(currentSheetNumRows, currentSheetNumCols,sheetDimensions.getHeightRow(),sheetDimensions.getWidthCol());
            uiSheetModel.createCells(currentSheetNumRows, currentSheetNumCols);
        }

        updateAllCells(sheetDto.getCells());

        recalculateGridDimensions();

        if (miscController.areAnimationsEnabled()) {
            uiSheetModel.animateSheetAppearance();
        }
    }

    // Displays a temporary sheet view, used for version history or state viewing
    // Saves the current sheet state and updates display settings accordingly
    @Override
    public void displayTemporarySheet(SheetDto sheetDto, boolean versionView) {
        uiSheetModel.clearPreviousRangeHighlight();
        uiSheetModel.clearPreviousHighlights();
        savedSheet = currentSheet;
        currentSheet = sheetDto;
        if(versionView)
        {
            editingManager.enableVersionViewRead();
        }
        else
        {
            editingManager.enableSheetStateView();
        }
        updateAllCells(sheetDto.getCells());
    }

    // Restores the display to the original sheet after viewing a temporary sheet
    // Resets cell display and editing permissions
    @Override
    public void displayOriginalSheet(boolean versionView) {
        if (savedSheet != null && !versionView) {currentSheet = savedSheet;}
        uiSheetModel.clearCells();
        uiSheetModel.createCells(currentSheetNumRows,currentSheetNumCols);
        editingManager.enableSheetViewEditing(permission);

        updateAllCells(currentSheet.getCells());
    }

    // Handles cell click events by highlighting dependencies and influences
    // Updates action line with cell data and adds visual effects if animations are enabled
    @Override
    public void handleCellClick(String cellId) {
        uiSheetModel.clearPreviousHighlights();

        CellDto cellDto = currentSheet.getCells().get(cellId);
        if (cellDto != null) {
            uiSheetModel.highlightDependenciesAndInfluences(cellDto);

            // Check if animations are enabled before adding the pulsing effect
            if (miscController.areAnimationsEnabled()) {
                Label selectedCellLabel = uiSheetModel.getCellLabel(cellId); // Get the Label from the map
                if (selectedCellLabel != null) {
                    uiSheetModel.addPulsingEffect(selectedCellLabel); // Add the pulsing effect
                }
            }
        }

        if (actionLineController != null) {
            actionLineController.setCellData(cellDto, cellId);
        }
    }

    // Sets up the context menu for individual cells with style, reset, and formula building options
    @Override
    public void setupCellContextMenu(Label cellLabel, String cellId) {
        ContextMenu contextMenu = new ContextMenu();
        MenuItem styleMenuItem = new MenuItem("Setting the cell style");
        MenuItem resetMenuItem = new MenuItem("Reset style");
        MenuItem buildFormulaMenuItem = new MenuItem("Build formula");
        buildFormulaMenuItem.setOnAction(event -> formulaBuilder.buildFormula());
        styleMenuItem.setOnAction(event -> showCellStyleDialog(cellLabel, cellId));
        resetMenuItem.setOnAction(event -> resetCellStyle(cellLabel, cellId));
        contextMenu.getItems().add(buildFormulaMenuItem);
        contextMenu.getItems().add(styleMenuItem);
        contextMenu.getItems().add(resetMenuItem);
        cellLabel.setContextMenu(contextMenu);
    }

    // Sets up the context menu for header cells (rows/columns) with resize and alignment options
    @Override
    public void setupHeaderContextMenu(Label cellLabel, int index, boolean isColumn) {
        ContextMenu contextMenu = new ContextMenu();
        MenuItem resizeMenuItem = new MenuItem("Resize");
        resizeMenuItem.setOnAction(event -> uiSheetModel.showResizeDialog(index, isColumn));
        MenuItem alignMenuItem = new MenuItem("Set Alignment");
        alignMenuItem.setOnAction(event -> showAlignmentDialog(index, isColumn));
        contextMenu.getItems().addAll(resizeMenuItem, alignMenuItem);
        cellLabel.setContextMenu(contextMenu);
    }

    // Updates all cells in the sheet with their current values and clears empty cells
    @Override
    public void updateAllCells(Map<String, CellDto> cells) {
        for (Map.Entry<String, CellDto> entry : cells.entrySet()) {
            updateCell(entry.getKey(), entry.getValue());
        }

        for (int col = 1; col <= this.currentSheetNumCols; col++) {  // Skip column 0 (headers)
            for (int row = 1; row <= this.currentSheetNumRows; row++) {  // Skip row 0 (headers)
                String cellId = String.valueOf((char) ('A' + col - 1)) + row;  // Generate cellId (e.g., A1, B2, etc.)
                Label cellLabel = uiSheetModel.getCellLabel(cellId);

                // If the cell is not in the map, set its text to an empty string
                if (cellLabel != null && !cells.containsKey(cellId)) {
                    cellLabel.setText("");
                }
            }
        }
    }

    // Updates a single cell's display with its value and style
    private void updateCell(String cellId, CellDto cellDto) {
        Label cellLabel = uiSheetModel.getCellLabel(cellId);
        if (cellLabel != null) {
            cellLabel.setText(cellDto != null ? cellDto.getEffectiveValue() : "");
            String style = cellDto.getStyle();
            uiSheetModel.applyStyle(cellLabel,style);
        }
    }

    // Recalculates and updates the grid dimensions based on current content
    @Override
    public void recalculateGridDimensions() {
        double totalWidth = calculateTotalGridWidth();
        double totalHeight = calculateTotalGridHeight();
        gridPane.setMinWidth(Math.max(Constants.SHEET_GRID_PANE_WIDTH, totalWidth));
        gridPane.setPrefWidth(Math.max(Constants.SHEET_GRID_PANE_WIDTH, totalWidth));
        gridPane.setMinHeight(Math.max(Constants.SHEET_GRID_PANE_HEIGHT, totalHeight));
        gridPane.setPrefHeight(Math.max(Constants.SHEET_GRID_PANE_HEIGHT, totalHeight));
    }

    // Calculates the total width of the grid including all columns
    private double calculateTotalGridWidth() {
        double totalWidth = gridPane.getColumnConstraints().getFirst().getPrefWidth(); // Header column
        for (int i = 0; i <= currentSheetNumCols; i++) {
            totalWidth += gridPane.getColumnConstraints().get(i).getPrefWidth();
        }
        return totalWidth;
    }

    // Calculates the total height of the grid including all rows
    private double calculateTotalGridHeight() {
        double totalHeight = gridPane.getRowConstraints().getFirst().getPrefHeight(); // Header row
        for (int i = 0; i <= currentSheetNumRows; i++) {
            totalHeight += gridPane.getRowConstraints().get(i).getPrefHeight();
        }
        return totalHeight;
    }

    // Disables cell click events and context menus for all cells
    @Override
    public void disableCellClick() {
        for (int col = 1; col <= currentSheetNumCols; col++) {  // Skip column 0 (headers)
            for (int row = 1; row <= currentSheetNumRows; row++) {  // Skip row 0 (headers)
                String cellId = String.valueOf((char) ('A' + col)) + (row); // Generate cellId (e.g., A1, B2, etc.)
                Label cellLabel = uiSheetModel.getCellLabel(cellId);
                if (cellLabel != null) {
                    cellLabel.setOnMouseClicked(null);  // Disable the click event
                    cellLabel.setContextMenu(null);     // Disable the context menu
                }
            }
        }
    }

    // Enables cell click events and context menus for all cells based on user permissions
    @Override
    public void enableCellClick() {
        for (int col = 1; col <= currentSheetNumCols; col++) {  // Skip column 0 (headers)
            for (int row = 1; row <= currentSheetNumRows; row++) {  // Skip row 0 (headers)
                String cellId = String.valueOf((char) ('A' + col - 1)) + row; // Generate cellId (e.g., A1, B2, etc.)
                Label cellLabel = uiSheetModel.getCellLabel(cellId);
                if (cellLabel != null) {
                    cellLabel.setOnMouseClicked(event -> handleCellClick(cellId));  // Enable click event
                    if(permission == Permission.OWNER || permission == Permission.WRITER)
                    {
                        setupCellContextMenu(cellLabel, cellId);
                    }
                }
            }
        }
    }

    // Returns the current sheet
    @Override
    public SheetDto getCurrentSheet() {
        return currentSheet;
    }

    // Setter methods for various controllers and components
    @Override
    public void setFormulaBuilder(FormulaBuilder formulaBuilder) {this.formulaBuilder = formulaBuilder;}

    @Override
    public void setCurrentSheet(SheetDto sheet) {currentSheet = sheet;
        rangeController.displayRanges(sheet.getSheetRanges());}

    @Override
    public void setUiSheetModel(UISheetModel uiSheetModel){
        this.uiSheetModel = uiSheetModel;
        uiSheetModel.modelSetUp(gridPane,this);
    }

    @Override
    public void setActionLineController(ActionLineController actionLineController){this.actionLineController = actionLineController;}

    @Override
    public void setRangeController(RangeController rangeController){this.rangeController = rangeController;}

    @Override
    public void setMiscController(MiscController miscController){this.miscController = miscController;}

    @Override
    public void setEditingManager(EditingManager editingManager){this.editingManager = editingManager;}

    // Resets the style of a cell to default
    private void resetCellStyle(Label cellLabel, String cellId) {
        List<String> cells = new ArrayList<>();
        cells.add(cellId);
        if (currentSheet.getCells().get(cellId) != null) {
            sendCellStyleUpdateRequest (cells, "");
            cellLabel.setStyle("");
        }
    }

    // Shows a dialog for setting cell styles including background and text colors
    public void showCellStyleDialog(Label cellLabel, String cellId) {
        List<String> cells = new ArrayList<>();
        cells.add(cellId);
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Set Cell Style");
        dialog.setHeaderText("Choose style:");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);

        ColorPicker backgroundColorPicker = new ColorPicker();
        ColorPicker textColorPicker = new ColorPicker();

        grid.add(new Label("Background Color:"), 0, 0);
        grid.add(backgroundColorPicker, 1, 0);
        grid.add(new Label("Text Color:"), 0, 1);
        grid.add(textColorPicker, 1, 1);

        dialog.getDialogPane().setContent(grid);
        ButtonType okButtonType = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelButtonType = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
        ButtonType previewButtonType = new ButtonType("Preview", ButtonBar.ButtonData.OTHER);
        dialog.getDialogPane().getButtonTypes().addAll(okButtonType, cancelButtonType, previewButtonType);

        Node okButton = dialog.getDialogPane().lookupButton(okButtonType);
        Node cancelButton = dialog.getDialogPane().lookupButton(cancelButtonType);
        Node previewButton = dialog.getDialogPane().lookupButton(previewButtonType);

        AtomicBoolean isPrev = new AtomicBoolean(false);
        String prevStyle;
        if (currentSheet.getCells().get(cellId) == null) {
            prevStyle = "";
            List<String> cellToAdd = new ArrayList<>();
            cellToAdd.add(cellId);
            sendAddEmptyCellsRequest(cellToAdd);
        }
        else {
            prevStyle = currentSheet.getCells().get(cellId).getStyle();
        }

        previewButton.addEventFilter(ActionEvent.ACTION, event -> {
            event.consume();
            String newStyle = generateNewStyle(backgroundColorPicker.getValue(), textColorPicker.getValue(), prevStyle);
            uiSheetModel.applyStyle(cellLabel,newStyle);
            isPrev.set(true);
        });

        okButton.addEventFilter(ActionEvent.ACTION, event -> {
            String newStyle = generateNewStyle(backgroundColorPicker.getValue(), textColorPicker.getValue(), prevStyle);
            sendCellStyleUpdateRequest(cells, newStyle);
            uiSheetModel.applyStyle(cellLabel,newStyle);
            isPrev.set(false);
            dialog.close();
        });

        cancelButton.addEventFilter(ActionEvent.ACTION, event -> {
            uiSheetModel.applyStyle(cellLabel,prevStyle);
            isPrev.set(false);
            dialog.close();
        });

        dialog.setOnHidden(event -> {
            if (dialog.getResult() == null || isPrev.get()) {
                uiSheetModel.applyStyle(cellLabel,prevStyle);
            }
        });

        dialog.showAndWait();
    }

    // Generates a new cell style string by adding specified background and text colors
    private String generateNewStyle(Color backgroundColor, Color textColor, String prevStyle) {
        StringBuilder newStyle = new StringBuilder(prevStyle);

        if (backgroundColor != null) {
            newStyle.append(Constants.BACKGROUND_COLOR).append(uiSheetModel.toRgbString(backgroundColor)).append(";");

        }
        if (textColor != null) {
            newStyle.append(Constants.TEXT_COLOR).append(uiSheetModel.toRgbString(textColor)).append(";");
        }

        return newStyle.toString();
    }

    // Sends an asynchronous request to update the style of a specified cell
    private void sendCellStyleUpdateRequest(List<String> cellIds,String cellStyle) {
        Map<String,Object> cellStyleParams = new HashMap<>();
        cellStyleParams.put("cellIds", cellIds);
        cellStyleParams.put("style", cellStyle);
        cellStyleParams.put("sheetName", currentSheet.getSheetName());


        Gson gson = new Gson();
        String cellStyleParamsJson = gson.toJson(cellStyleParams);

        RequestBody requestBody = RequestBody.create(cellStyleParamsJson, MediaType.parse("application/json"));

        Request request = new Request.Builder()
                .url(Constants.UPDATE_CELLS_STYLE)
                .post(requestBody)
                .build();

        HttpClientUtil.runAsync(request, new Callback() {
            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseBody = response.body().string();
                    Platform.runLater(() -> currentSheet = extractSheetFromResponseBody(responseBody));
                } else {
                    String errorMessage = response.body() != null ? response.body().string() : response.message();
                    Platform.runLater(() -> showAlert("Error", "Failed to set cell style: \n" + errorMessage)
                    );
                }
            }

            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Platform.runLater(() ->
                        showAlert("Error", "Error: " + e.getMessage())
                );
            }
        });

    }

    // Shows a dialog for setting text alignment for cells in a specified row or column
    public void showAlignmentDialog(int index, boolean isColumn) {

        List<String>cellsToAdd = new ArrayList<>();

        ChoiceDialog<String> dialog = new ChoiceDialog<>("Left", "Left", "Center", "Right");
        dialog.setTitle("Set Alignment");
        dialog.setHeaderText("Choose alignment for " + (isColumn ? "column" : "row") + ":");
        dialog.setContentText("Alignment:");

        dialog.showAndWait().ifPresent(result -> {
            String alignment = switch (result) {
                case "Left" -> Constants.ALIGNMENT_LEFT;
                case "Center" -> Constants.ALIGNMENT_CENTER;
                case "Right" -> Constants.ALIGNMENT_RIGHT;
                default -> "";
            };
            if (isColumn) {
                for (int row = 1; row <= this.currentSheetNumRows; row++) {
                    String cellId = "" + (char)('A' + index - 1) + row;
                    CellDto cellDto = currentSheet.getCells().get(cellId);
                    if (cellDto != null) {
                        String newStyle = currentSheet.getCells().get(cellId).getStyle() + alignment;
                        List<String>cellToSet = new ArrayList<>();
                        cellToSet.add(cellId);
                        sendCellStyleUpdateRequest (cellToSet, newStyle);
                        Label cell = uiSheetModel.getCellLabel(cellId);
                        uiSheetModel.applyStyle(cell,newStyle);
                    }
                    else {
                      cellsToAdd.add(cellId);
                      Label cell = uiSheetModel.getCellLabel(cellId);
                      uiSheetModel.applyStyle(cell,alignment);
                    }
                }
            } else {
                for (int col = 1; col <= this.currentSheetNumCols; col++) {
                    String cellId = "" + (char)('A' + col - 1) + index;
                    CellDto cellDto = currentSheet.getCells().get(cellId);
                    if (cellDto != null) {
                        String newStyle = currentSheet.getCells().get(cellId).getStyle() + alignment;
                        List<String>cellToSet = new ArrayList<>();
                        cellToSet.add(cellId);
                        sendCellStyleUpdateRequest (cellToSet, newStyle);
                        Label cell = uiSheetModel.getCellLabel(cellId);
                        uiSheetModel.applyStyle(cell,newStyle);
                    }
                    else {
                        cellsToAdd.add(cellId);
                        Label cell = uiSheetModel.getCellLabel(cellId);
                        uiSheetModel.applyStyle(cell,alignment);
                    }
                }
            }
            if(!cellsToAdd.isEmpty()) {
                sendUpdateAlignmentRequestForEmptyCell(cellsToAdd,alignment);
            }
        });
    }

    // Sends an asynchronous request to update the alignments of new empty cells
    private void sendUpdateAlignmentRequestForEmptyCell(List <String>cellIds,String cellStyle) {
        Map<String, Object> requestParams = new HashMap<>();
        requestParams.put("cellIds", cellIds);
        requestParams.put("sheetName", currentSheet.getSheetName());

        Gson gson = new Gson();
        String cellIdJson = gson.toJson(requestParams);

        RequestBody requestBody = RequestBody.create(cellIdJson,MediaType.parse("application/json"));

        Request request = new Request.Builder()
                .url(Constants.ADD_EMPTY_CELLS)
                .post(requestBody)
                .build();

        HttpClientUtil.runAsync(request, new Callback() {
            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseBody = response.body().string();
                    Platform.runLater(() -> {
                        currentSheet = extractSheetFromResponseBody(responseBody);
                        sendCellStyleUpdateRequest(cellIds,cellStyle);

                    });
                } else {
                    String errorMessage = response.body() != null ? response.body().string() : response.message();
                    Platform.runLater(() -> showAlert("Error", "Failed to add cell: \n" + errorMessage)
                    );
                }
            }

            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Platform.runLater(() ->
                        showAlert("Error", "Error: " + e.getMessage())
                );
            }
        });}

    // Sending the server a request to add an empty cell to the sheet
    private void sendAddEmptyCellsRequest(List<String> cellIds) {
        Map<String, Object> requestParams = new HashMap<>();
        requestParams.put("cellIds", cellIds);
        requestParams.put("sheetName", currentSheet.getSheetName());

        Gson gson = new Gson();
        String cellIdJson = gson.toJson(requestParams);

        RequestBody requestBody = RequestBody.create(cellIdJson,MediaType.parse("application/json"));

        Request request = new Request.Builder()
                .url(Constants.ADD_EMPTY_CELLS)
                .post(requestBody)
                .build();

        HttpClientUtil.runAsync(request, new Callback() {
            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseBody = response.body().string();
                    Platform.runLater(() -> currentSheet = extractSheetFromResponseBody(responseBody));
                } else {
                    String errorMessage = response.body() != null ? response.body().string() : response.message();
                    Platform.runLater(() -> showAlert("Error", "Failed to add cell: \n" + errorMessage)
                    );
                }
            }

            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Platform.runLater(() ->
                        showAlert("Error", "Error: " + e.getMessage())
                );
            }
        });
    }

    // Setting the permission of the user for the held sheet
    @Override
    public void setPermission(Permission permission)
    {
        this.permission = permission;
    }
}


