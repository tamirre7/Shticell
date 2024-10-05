package spreadsheet.impl;

import action.line.api.ActionLineController;
import command.api.Engine;
import command.components.formulabuilder.FormulaBuilder;
import command.components.sortandfilter.api.SortAndFilterController;
import dto.CellDto;
import dto.SheetDto;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import misc.api.MiscController;
import range.api.RangeController;
import spreadsheet.UISheetModel;
import spreadsheet.api.SpreadsheetController;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class SpreadsheetControllerImpl implements SpreadsheetController {

    @FXML private GridPane gridPane;
    @FXML private ScrollPane scrollPane;
    private final Engine engine;
    private ActionLineController actionLineController;
    private int numRows;
    private int numCols;
    private RangeController rangeController;
    private SheetDto currentSheet;
    private SheetDto savedSheet;
    private MiscController miscController;
    private SortAndFilterController sortAndFilterController;
    private FormulaBuilder formulaBuilder;
    private final UISheetModel uiSheetModel;

    public SpreadsheetControllerImpl(Engine engine, UISheetModel uiSheetModel) {
        this.engine = engine;
        this.uiSheetModel = uiSheetModel;
    }

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

        uiSheetModel.modelSetUp(gridPane,this);
    }

    @Override
    public void setFormulaBuilder(FormulaBuilder formulaBuilder) {this.formulaBuilder = formulaBuilder;}


    @Override
    public void setCurrentSheet(SheetDto currentSheet) {
        this.currentSheet = currentSheet;
    }

    @Override
    public void setMiscController(MiscController miscController) { this.miscController = miscController; }

    @Override
    public void setActionLineController(ActionLineController actionLineController) {
        this.actionLineController = actionLineController;
    }

    @Override
    public void setRangeController(RangeController rangeController) {this.rangeController = rangeController;}

    @Override
    public void setSortAndFilterController(SortAndFilterController sortAndFilterController) {
        this.sortAndFilterController = sortAndFilterController;
    }

    @Override
    public void displaySheet(SheetDto sheetDto) {
        uiSheetModel.clearCells();
        actionLineController.setCurrentSheet(sheetDto);
        actionLineController.clearTextFields();

        this.numRows = sheetDto.getNumRows();
        this.numCols = sheetDto.getNumCols();

        rangeController.displayRanges(sheetDto.getSheetRanges());

        if (gridPane.getChildren().isEmpty()) {
            uiSheetModel.setupGridDimensions(numRows, numCols,currentSheet.getHeightRow(),currentSheet.getWidthCol());
            uiSheetModel.createCells(numRows, numCols);
        }

        updateAllCells(sheetDto.getCells());

        recalculateGridDimensions();

        if (miscController.areAnimationsEnabled()) {
            uiSheetModel.animateSheetAppearance();
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
        for (int i = 0; i <= numCols; i++) {
            totalWidth += gridPane.getColumnConstraints().get(i).getPrefWidth();
        }
        return totalWidth;
    }

    private double calculateTotalGridHeight() {
        double totalHeight = gridPane.getRowConstraints().getFirst().getPrefHeight(); // Header row
        for (int i = 0; i <= numRows; i++) {
            totalHeight += gridPane.getRowConstraints().get(i).getPrefHeight();
        }
        return totalHeight;
    }


    @Override
    public void displayTemporarySheet(SheetDto sheetDto, boolean versionView) {
        uiSheetModel.clearPreviousRangeHighlight();
        uiSheetModel.clearPreviousHighlights();
        savedSheet = currentSheet;
        currentSheet = sheetDto;
       disableEditing(versionView);
        updateAllCells(sheetDto.getCells());
    }

    private void disableEditing(boolean versionView)
    {
        disableCellClick();

        if(!versionView) {
            actionLineController.disableEditing();
        }
        sortAndFilterController.disableSortAndFilter(versionView);
        miscController.disableEditing();
        rangeController.disableEditing();

    }

    private void disableCellClick() {
        for (int col = 1; col <= numCols; col++) {  // Skip column 0 (headers)
            for (int row = 1; row <= numRows; row++) {  // Skip row 0 (headers)
                String cellId = String.valueOf((char) ('A' + col)) + (row); // Generate cellId (e.g., A1, B2, etc.)
                Label cellLabel = uiSheetModel.getCellLabel(cellId);
                if (cellLabel != null) {
                    cellLabel.setOnMouseClicked(null);  // Disable the click event
                }
            }
        }
    }

    @Override
    public void displayOriginalSheet(boolean versionView) {
        if (savedSheet != null && !versionView) {currentSheet = savedSheet;}
        enableEditing();

        updateAllCells(currentSheet.getCells());
    }

    @Override
    public void enableEditing() { actionLineController.enableEditing();
        rangeController.enableEditing();
        miscController.enableEditing();
        sortAndFilterController.enableSortAndFilter();
        uiSheetModel.clearCells();
        uiSheetModel.createCells(numRows,numCols);
        enableCellClick();
    }


    private void enableCellClick() {
        for (int col = 1; col <= numCols; col++) {  // Skip column 0 (headers)
            for (int row = 1; row <= numRows; row++) {  // Skip row 0 (headers)
                String cellId = String.valueOf((char) ('A' + col - 1)) + row; // Generate cellId (e.g., A1, B2, etc.)
                Label cellLabel = uiSheetModel.getCellLabel(cellId);
                if (cellLabel != null) {
                    cellLabel.setOnMouseClicked(event -> handleCellClick(cellId));  // Enable click event
                }
            }
        }
    }


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
            actionLineController.setCurrentSheet(currentSheet);
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

    public void dynamicAnalysisSetup(Label cellLabel, String cellId) {
        savedSheet = currentSheet;
        openSliderDialog(cellLabel, cellId);
    }

    private void resetCellStyle(Label cellLabel, String cellId) {
        if (currentSheet.getCells().get(cellId) != null) {
            currentSheet = engine.setCellStyle(cellId, "");
            uiSheetModel.applyStyle(cellLabel, cellId);
        }
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

        for (int col = 1; col <= numCols; col++) {  // Skip column 0 (headers)
            for (int row = 1; row <= numRows; row++) {  // Skip row 0 (headers)
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
            uiSheetModel.applyStyle(cellLabel, cellId);
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

        if (currentSheet.getCells().get(cellId) == null) {
            currentSheet = engine.addEmptyCell(cellId);
        }
        String prevStyle = currentSheet.getCells().get(cellId).getStyle();

        backgroundColorPicker.setOnAction(event -> {
            String newStyle = currentSheet.getCells().get(cellId).getStyle() +
                    "-fx-background-color: " + uiSheetModel.toRgbString(backgroundColorPicker.getValue()) + ";";
            currentSheet = engine.setCellStyle(cellId, newStyle);
        });

        textColorPicker.setOnAction(event -> {
            String newStyle = currentSheet.getCells().get(cellId).getStyle() +
                    "-fx-text-fill: " + uiSheetModel.toRgbString(textColorPicker.getValue()) + ";";
            currentSheet = engine.setCellStyle(cellId, newStyle);
        });

        previewButton.addEventFilter(ActionEvent.ACTION, event -> {
            event.consume();
            uiSheetModel.applyStyle(cellLabel, cellId);
            isPrev.set(true);

        });

        okButton.addEventFilter(ActionEvent.ACTION, event -> {
            uiSheetModel.applyStyle(cellLabel, cellId);
            isPrev.set(false);
            dialog.close();

        });

        cancelButton.addEventFilter(ActionEvent.ACTION, event -> {
            currentSheet = engine.setCellStyle(cellId, prevStyle);
            uiSheetModel.applyStyle(cellLabel,cellId);
            dialog.close();
        });

        dialog.setOnHidden(event -> {
            if (dialog.getResult() == null || isPrev.get()) {
                currentSheet = engine.setCellStyle(cellId, prevStyle);
                uiSheetModel.applyStyle(cellLabel,cellId);
            }
        });

        dialog.showAndWait();
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
                for (int row = 1; row <= numRows; row++) {
                    String cellId = "" + (char)('A' + index - 1) + row;
                    CellDto cellDto = currentSheet.getCells().get(cellId);
                    if (cellDto != null) {
                        String newStyle = currentSheet.getCells().get(cellId).getStyle() + alignment;
                        currentSheet = engine.setCellStyle(cellId, newStyle);
                        Label cell = uiSheetModel.getCellLabel(cellId);
                        uiSheetModel.applyStyle(cell, cellId);
                    }
                    else {
                        currentSheet = engine.addEmptyCell(cellId);
                        String newStyle = currentSheet.getCells().get(cellId).getStyle() + alignment;
                        currentSheet = engine.setCellStyle(cellId, newStyle);
                        Label cell = uiSheetModel.getCellLabel(cellId);
                        uiSheetModel.applyStyle(cell, cellId);
                    }
                }
            } else {
                for (int col = 1; col <= numCols; col++) {
                    String cellId = "" + (char)('A' + col - 1) + index;
                    CellDto cellDto = currentSheet.getCells().get(cellId);
                    if (cellDto != null) {
                        String newStyle = cellDto.getStyle() + alignment;
                        currentSheet = engine.setCellStyle(cellId, newStyle);
                        Label cell = uiSheetModel.getCellLabel(cellId);
                        uiSheetModel.applyStyle(cell, cellId);
                    }
                    else {
                        currentSheet = engine.addEmptyCell(cellId);
                        cellDto = currentSheet.getCells().get(cellId);
                        String newStyle = cellDto.getStyle() + alignment;
                        currentSheet = engine.setCellStyle(cellId, newStyle);
                        Label cell = uiSheetModel.getCellLabel(cellId);
                        uiSheetModel.applyStyle(cell, cellId);
                    }
                }
            }
        });
    }


    @Override
    public SheetDto getCurrentSheet() {
        return currentSheet;
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

        slider.valueProperty().addListener((observable, oldValue, newValue) -> {
            double roundedValue = Math.round(newValue.doubleValue() / step) * step;
            slider.setValue(roundedValue);
            sliderValueLabel.setText(String.format("%.2f", roundedValue));

            String newvalueStr = String.format("%.2f", roundedValue);
            SheetDto tempSheet = engine.updateCellWithoutSheetVersionUpdate(cellID, newvalueStr);
            disableEditing(false);
            updateAllCells(tempSheet.getCells());
        });

        Button doneButton = new Button("Done");

        doneButton.setOnAction(e -> {
            engine.updateCellWithoutSheetVersionUpdate(cellID, realOriginalValue);
            enableEditing();
            updateAllCells(currentSheet.getCells());// חזרה לגיליון המקורי
            Stage stage = (Stage) doneButton.getScene().getWindow();
            stage.close();
        });

        VBox layout = new VBox(10, new Label("Choose a new value:"), slider, sliderValueLabel, doneButton);
        layout.setAlignment(Pos.CENTER);
        Scene scene = new Scene(layout, 300, 200);

        Stage dialog = new Stage();
        dialog.setTitle("Update Cell Value");
        dialog.setScene(scene);

        dialog.setOnCloseRequest(e -> { savedSheet = engine.updateCellWithoutSheetVersionUpdate(cellID, realOriginalValue);
            displayOriginalSheet(false);});
        dialog.show();
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);

        // Resize the alert window by setting its width and height
        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.setMinHeight(Region.USE_PREF_SIZE); // Adjust height to fit content
        dialogPane.setMinWidth(Region.USE_PREF_SIZE);  // Adjust width to fit content

        alert.showAndWait();
    }
}