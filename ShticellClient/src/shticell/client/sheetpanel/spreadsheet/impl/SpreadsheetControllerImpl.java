package shticell.client.sheetpanel.spreadsheet.impl;


import com.google.gson.Gson;
import dto.CellDto;
import dto.DimensionDto;
import dto.SheetDto;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
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
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import static shticell.client.util.http.HttpClientUtil.extractSheetFromResponseBody;
import static shticell.client.util.http.HttpClientUtil.showAlert;

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


    @FXML
    public void initialize() {
        scrollPane.setContent(gridPane);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);

        // Set a minimum width for the GridPane
        gridPane.setMinWidth(800);
        gridPane.setMinHeight(600);

        // Add a listener to adjust the ScrollPane's width
        scrollPane.viewportBoundsProperty().addListener((observable, oldValue, newValue) -> {
            double width = newValue.getWidth();
            gridPane.setPrefWidth(Math.max(width, gridPane.getMinWidth()));
        });
    }

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

    @Override
    public void displayTemporarySheet(SheetDto sheetDto, boolean versionView) {
        uiSheetModel.clearPreviousRangeHighlight();
        uiSheetModel.clearPreviousHighlights();
        savedSheet = currentSheet;
        currentSheet = sheetDto;
        editingManager.disableSheetViewEditing(versionView);
        updateAllCells(sheetDto.getCells());

    }

    @Override
    public void displayOriginalSheet(boolean versionView) {
        if (savedSheet != null && !versionView) {currentSheet = savedSheet;}
        uiSheetModel.clearCells();
        uiSheetModel.createCells(currentSheetNumRows,currentSheetNumCols);
        editingManager.enableSheetViewEditing();

        updateAllCells(currentSheet.getCells());

    }

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

    @Override
    public void setupCellContextMenu(Label cellLabel, String cellId) {
        ContextMenu contextMenu = new ContextMenu();
        MenuItem styleMenuItem = new MenuItem("Setting the cell style");
        MenuItem resetMenuItem = new MenuItem("Reset style");
        MenuItem buildFormulaMenuItem = new MenuItem("Build formula");
        MenuItem dynamicAnalysisMenuItem = new MenuItem("Dynamic analysis");
        dynamicAnalysisMenuItem.setOnAction(event -> dynamicAnalysisSetup(cellLabel,cellId));
        buildFormulaMenuItem.setOnAction(event -> formulaBuilder.buildFormula());
        styleMenuItem.setOnAction(event -> showCellStyleDialog(cellLabel, cellId));
        resetMenuItem.setOnAction(event -> resetCellStyle(cellLabel, cellId));
        contextMenu.getItems().add(dynamicAnalysisMenuItem);
        contextMenu.getItems().add(buildFormulaMenuItem);
        contextMenu.getItems().add(styleMenuItem);
        contextMenu.getItems().add(resetMenuItem);
        cellLabel.setContextMenu(contextMenu);
    }

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

    private void updateCell(String cellId, CellDto cellDto) {
        Label cellLabel = uiSheetModel.getCellLabel(cellId);
        if (cellLabel != null) {
            cellLabel.setText(cellDto != null ? cellDto.getEffectiveValue() : "");
            String style = cellDto.getStyle();
            uiSheetModel.applyStyle(cellLabel, cellId,style);
        }
    }

    @Override
    public void recalculateGridDimensions() {
        double totalWidth = calculateTotalGridWidth();
        double totalHeight = calculateTotalGridHeight();
        gridPane.setMinWidth(Math.max(800, totalWidth));
        gridPane.setPrefWidth(Math.max(800, totalWidth));
        gridPane.setMinHeight(Math.max(600, totalHeight));
        gridPane.setPrefHeight(Math.max(600, totalHeight));
    }

    private double calculateTotalGridWidth() {
        double totalWidth = gridPane.getColumnConstraints().getFirst().getPrefWidth(); // Header column
        for (int i = 0; i <= currentSheetNumCols; i++) {
            totalWidth += gridPane.getColumnConstraints().get(i).getPrefWidth();
        }
        return totalWidth;
    }

    private double calculateTotalGridHeight() {
        double totalHeight = gridPane.getRowConstraints().getFirst().getPrefHeight(); // Header row
        for (int i = 0; i <= currentSheetNumRows; i++) {
            totalHeight += gridPane.getRowConstraints().get(i).getPrefHeight();
        }
        return totalHeight;
    }
    @Override
    public void disableCellClick() {
        for (int col = 1; col <= currentSheetNumCols; col++) {  // Skip column 0 (headers)
            for (int row = 1; row <= currentSheetNumRows; row++) {  // Skip row 0 (headers)
                String cellId = String.valueOf((char) ('A' + col)) + (row); // Generate cellId (e.g., A1, B2, etc.)
                Label cellLabel = uiSheetModel.getCellLabel(cellId);
                if (cellLabel != null) {
                    cellLabel.setOnMouseClicked(null);  // Disable the click event
                }
            }
        }
    }

    @Override
    public void enableCellClick() {
        for (int col = 1; col <= currentSheetNumCols; col++) {  // Skip column 0 (headers)
            for (int row = 1; row <= currentSheetNumRows; row++) {  // Skip row 0 (headers)
                String cellId = String.valueOf((char) ('A' + col - 1)) + row; // Generate cellId (e.g., A1, B2, etc.)
                Label cellLabel = uiSheetModel.getCellLabel(cellId);
                if (cellLabel != null) {
                    cellLabel.setOnMouseClicked(event -> handleCellClick(cellId));  // Enable click event
                }
            }
        }
    }

    @Override
    public SheetDto getCurrentSheet() {
        return currentSheet;
    }

    @Override
    public void setFormulaBuilder(FormulaBuilder formulaBuilder) {this.formulaBuilder = formulaBuilder;}

    @Override
    public void setCurrentSheet(SheetDto sheet) {currentSheet = sheet;}

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

    private void resetCellStyle(Label cellLabel, String cellId) {
        if (currentSheet.getCells().get(cellId) != null) {
            sendCellStyleUpdateRequest (cellId, "");
            cellLabel.setStyle("");
        }
    }

    public void showCellStyleDialog(Label cellLabel, String cellId) {
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
            sendAddEmptyCellRequest(cellId);
        }
        else {
            prevStyle = currentSheet.getCells().get(cellId).getStyle();
        }

        previewButton.addEventFilter(ActionEvent.ACTION, event -> {
            event.consume();
            String newStyle = generateNewStyle(backgroundColorPicker.getValue(), textColorPicker.getValue(), prevStyle);
            uiSheetModel.applyStyle(cellLabel,cellId,newStyle);
            isPrev.set(true);
        });

        okButton.addEventFilter(ActionEvent.ACTION, event -> {
            String newStyle = generateNewStyle(backgroundColorPicker.getValue(), textColorPicker.getValue(), prevStyle);
            sendCellStyleUpdateRequest(cellId, newStyle);
            uiSheetModel.applyStyle(cellLabel,cellId,newStyle);
            isPrev.set(false);
            dialog.close();
        });

        cancelButton.addEventFilter(ActionEvent.ACTION, event -> {
            uiSheetModel.applyStyle(cellLabel,cellId,prevStyle);
            isPrev.set(false);
            dialog.close();
        });

        dialog.setOnHidden(event -> {
            if (dialog.getResult() == null || isPrev.get()) {
                uiSheetModel.applyStyle(cellLabel,cellId,prevStyle);
            }
        });

        dialog.showAndWait();
    }

    private String generateNewStyle(Color backgroundColor, Color textColor, String prevStyle) {
        StringBuilder newStyle = new StringBuilder(prevStyle);

        if (backgroundColor != null) {
            newStyle.append("-fx-background-color: ").append(uiSheetModel.toRgbString(backgroundColor)).append(";");
        }
        if (textColor != null) {
            newStyle.append("-fx-text-fill: ").append(uiSheetModel.toRgbString(textColor)).append(";");
        }

        return newStyle.toString();
    }

    private void sendCellStyleUpdateRequest(String cellId,String cellStyle) {
        Map<String,String> cellStyleParams = new HashMap<>();
        cellStyleParams.put("cellId", cellId);
        cellStyleParams.put("style", cellStyle);
        cellStyleParams.put("sheetName", currentSheet.getSheetName());


        Gson gson = new Gson();
        String cellStyleParamsJson = gson.toJson(cellStyleParams);

        RequestBody requestBody = RequestBody.create(cellStyleParamsJson, MediaType.parse("application/json"));

        Request request = new Request.Builder()
                .url(Constants.UPDATE_CELL_STYLE_PAGE)
                .post(requestBody)
                .build();

        HttpClientUtil.runAsync(request, new Callback() {
            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseBody = response.body().string();
                    Platform.runLater(() -> currentSheet = extractSheetFromResponseBody(responseBody));
                } else {
                    Platform.runLater(() ->
                            showAlert("Error", "Failed to set the cell style: " + response.message())
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

    public void showAlignmentDialog(int index, boolean isColumn) {
        ChoiceDialog<String> dialog = new ChoiceDialog<>("Left", "Left", "Center", "Right");
        dialog.setTitle("Set Alignment");
        dialog.setHeaderText("Choose alignment for " + (isColumn ? "column" : "row") + ":");
        dialog.setContentText("Alignment:");

        dialog.showAndWait().ifPresent(result -> {
            String alignment = switch (result) {
                case "Left" -> "-fx-alignment: center-left;";
                case "Center" -> "-fx-alignment: center;";
                case "Right" -> "-fx-alignment: center-right;";
                default -> "";
            };
            if (isColumn) {
                for (int row = 1; row <= this.currentSheetNumRows; row++) {
                    String cellId = "" + (char)('A' + index - 1) + row;
                    CellDto cellDto = currentSheet.getCells().get(cellId);
                    if (cellDto != null) {
                        String newStyle = currentSheet.getCells().get(cellId).getStyle() + alignment;
                        sendCellStyleUpdateRequest (cellId, newStyle);
                        Label cell = uiSheetModel.getCellLabel(cellId);
                        uiSheetModel.applyStyle(cell, cellId,newStyle);
                    }
                    else {
                        sendAddEmptyCellRequest(cellId);
                        String newStyle = currentSheet.getCells().get(cellId).getStyle() + alignment;
                        sendCellStyleUpdateRequest (cellId, newStyle);
                        Label cell = uiSheetModel.getCellLabel(cellId);
                        uiSheetModel.applyStyle(cell, cellId,newStyle);
                    }
                }
            } else {
                for (int col = 1; col <= this.currentSheetNumCols; col++) {
                    String cellId = "" + (char)('A' + col - 1) + index;
                    CellDto cellDto = currentSheet.getCells().get(cellId);
                    if (cellDto != null) {
                        String newStyle = cellDto.getStyle() + alignment;
                        sendCellStyleUpdateRequest (cellId, newStyle);
                    }
                    else {
                        sendAddEmptyCellRequest(cellId);
                        cellDto = currentSheet.getCells().get(cellId);
                        String newStyle = cellDto.getStyle() + alignment;
                        sendCellStyleUpdateRequest (cellId, newStyle);
                    }
                }
            }
        });
    }

    private void sendAddEmptyCellRequest(String cellId) {
        Map<String,String> cellParams = new HashMap<>();
        cellParams.put("cellId", cellId);
        cellParams.put("sheetName", currentSheet.getSheetName());

        Gson gson = new Gson();
        String cellIdJson = gson.toJson(cellParams);

        RequestBody requestBody = RequestBody.create(cellIdJson,MediaType.parse("application/json"));

        Request request = new Request.Builder()
                .url(Constants.ADD_EMPTY_CELL_PAGE)
                .post(requestBody)
                .build();

        HttpClientUtil.runAsync(request, new Callback() {
            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseBody = response.body().string();
                    Platform.runLater(() -> currentSheet = extractSheetFromResponseBody(responseBody));
                } else {
                    Platform.runLater(() ->
                            showAlert("Error", "Failed to add cell: " + response.message())
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

    public void dynamicAnalysisSetup(Label cellLabel, String cellId) {
        savedSheet = currentSheet;
        openSliderDialog(cellLabel, cellId);
    }

    public void openSliderDialog(Label cellLabel, String cellID) {
        try {
            CellDto cellDto = currentSheet.getCells().get(cellID);
            if (cellDto == null) {
                showAlert("ERROR", "Dynamic analysis is only for numric original values");
                return;
            }
            String originalValue = cellDto.getOriginalValue();
            Double originalDouble = Double.parseDouble(originalValue);
        }
        catch (NumberFormatException e) {
            showAlert("ERROR", "Dynamic analysis is only for numric original values");
            return;
        }

        TextField minValueField = new TextField("0");
        TextField maxValueField = new TextField("100");
        TextField stepSizeField = new TextField("1");

        minValueField.setPromptText("Enter min value");
        maxValueField.setPromptText("Enter max value");
        stepSizeField.setPromptText("Enter step size");

        VBox dialogContent = new VBox();
        dialogContent.getChildren().addAll(
                new Label("Min Value:"), minValueField,
                new Label("Max Value:"), maxValueField,
                new Label("Step Size:"), stepSizeField
        );

        Dialog<ButtonType> setupDialog = new Dialog<>();
        setupDialog.setTitle("Slider Setup");
        setupDialog.getDialogPane().setContent(dialogContent);
        setupDialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        setupDialog.setResultConverter(dialogButton -> {
            if (dialogButton == ButtonType.OK) {
                double min = Double.parseDouble(minValueField.getText());
                double max = Double.parseDouble(maxValueField.getText());
                double step = Double.parseDouble(stepSizeField.getText());
                showSliderDialog(cellLabel, cellID, min, max, step);
            }
            return null;
        });

        setupDialog.showAndWait();
    }

    public void showSliderDialog(Label cellLabel, String cellID, double min, double max, double step) {
        Slider slider = new Slider(min, max, Double.parseDouble(cellLabel.getText()));
        slider.setMajorTickUnit(step);
        slider.setMinorTickCount(0);
        Label sliderValueLabel = new Label(Double.toString(slider.getValue()));
        String realOriginalValue = currentSheet.getCells().get(cellID).getOriginalValue();
        SheetDto tempSheet = new SheetDto(currentSheet.getSheetDimension(),currentSheet.getSheetName(),currentSheet.getVersion(),currentSheet.getCells(),currentSheet.getSheetRanges(),currentSheet.getUploadedBy());

        slider.valueProperty().addListener((observable, oldValue, newValue) -> {
            double roundedValue = Math.round(newValue.doubleValue() / step) * step;
            slider.setValue(roundedValue);
            sliderValueLabel.setText(String.format("%.2f", roundedValue));

            String newvalueStr = String.format("%.2f", roundedValue);
            //slider.setDisable(true);
            sendDynamicAnalysisUpdateRequest(cellID, newvalueStr,slider,tempSheet);
            editingManager.disableSheetViewEditing(false);
        });

        Button doneButton = new Button("Done");

        doneButton.setOnAction(e -> {
            sendDynamicAnalysisUpdateRequest(cellID, realOriginalValue,slider,currentSheet);
            editingManager.enableSheetViewEditing();
            Stage stage = (Stage) doneButton.getScene().getWindow();
            stage.close();
        });

        VBox layout = new VBox(10, new Label("Choose a new value:"), slider, sliderValueLabel, doneButton);
        layout.setAlignment(Pos.CENTER);
        Scene scene = new Scene(layout, 300, 200);

        Stage dialog = new Stage();
        dialog.setTitle("Update Cell Value");
        dialog.setScene(scene);

        dialog.setOnCloseRequest(e -> {
            sendDynamicAnalysisUpdateRequest(cellID, realOriginalValue,slider, tempSheet);
            savedSheet = tempSheet;
            displayOriginalSheet(false);});
        dialog.show();
    }

    private void sendDynamicAnalysisUpdateRequest(String cellId, String cellOriginalValue,Slider slider,SheetDto sheetToUpdate){
        AtomicReference<SheetDto> sheetToUpdateRef = new AtomicReference<>(sheetToUpdate);
        Map<String,String> cellUpdateData = new HashMap<>();
        cellUpdateData.put("cellId", cellId);
        cellUpdateData.put("cellOriginalValue", cellOriginalValue);
        cellUpdateData.put("sheetName", sheetToUpdate.getSheetName());
        cellUpdateData.put("userName",actionLineController.getLoggedUser());
        Gson gson = new Gson();
        String cellUpdateDatajson = gson.toJson(cellUpdateData);

        RequestBody requestBody = RequestBody.create(cellUpdateDatajson, MediaType.parse("application/json"));

        Request request = new Request.Builder()
                .url(Constants.DYNAMIC_ANALYSIS_UPDATE_PAGE)
                .post(requestBody)
                .build();

        HttpClientUtil.runAsync(request, new Callback() {
            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseBody = response.body().string();
                    Platform.runLater(() -> {
                        sheetToUpdateRef.set(extractSheetFromResponseBody(responseBody));
                        updateAllCells(sheetToUpdateRef.get().getCells());
                        // slider.setDisable(false);
                    });
                } else {
                    Platform.runLater(() -> {
                        showAlert("Error", "Failed to update cell: " + response.message());
                        slider.setDisable(false); // Re-enable slider even if there's an error
                    });
                }
            }

            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Platform.runLater(() ->
                        showAlert("Error", "Error: " + e.getMessage())
                );}
        });

    }


}


