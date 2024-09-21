package spreadsheet.impl;

import action.line.impl.ActionLineControllerImpl;
import command.api.Engine;
import command.components.formulabuilder.FormulaBuilder;
import command.components.sortandfilter.impl.SortAndFilterController;
import command.components.sortandfilter.impl.SortAndFilterControllerImpl;
import dto.CellDto;
import dto.SheetDto;
import javafx.animation.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;
import action.line.impl.ActionLineController;
import misc.MiscController;
import misc.impl.MiscControllerImpl;
import range.RangeController;
import range.impl.RangeControllerImpl;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class SpreadsheetControllerImpl {

    @FXML private GridPane gridPane;
    @FXML private ScrollPane scrollPane;
    private final Engine engine;
    private ActionLineControllerImpl actionLineController;
    private int numRows;
    private int numCols;
    private Map<String, Label> cellLabels = new HashMap<>();
    private String lastSelectedCell = null;
    private RangeControllerImpl rangeController;
    private Set<String> currentlyHighlightedCells = new HashSet<>();
    private SheetDto currentSheet;
    private SheetDto savedSheet;
    private MiscControllerImpl miscController;
    private SortAndFilterControllerImpl sortAndFilterController;
    private FormulaBuilder formulaBuilder;

    public SpreadsheetControllerImpl(Engine engine) {
        this.engine = engine;
    }

    @FXML
    public void initialize() {
        scrollPane.setContent(gridPane);
        scrollPane.setFitToWidth(false);  // Change this to false
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
    public void setFormulaBuilder(FormulaBuilder formulaBuilder) {this.formulaBuilder = formulaBuilder;}

    @Override
    public void setCurrentSheet(SheetDto currentSheet) {
        this.currentSheet = currentSheet;
    }

    @Override
    public void setMiscController(MiscControllerImpl miscController) { this.miscController = miscController; }

    @Override
    public void setActionLineController(ActionLineControllerImpl actionLineController) {
        this.actionLineController = actionLineController;
    }

    @Override
    public void setRangeController(RangeControllerImpl rangeController) {this.rangeController = rangeController;}

    @Override
    public void setSortAndFilterController(SortAndFilterControllerImpl sortAndFilterController) {this.sortAndFilterController = sortAndFilterController;}

    @Override
    public void displaySheet(SheetDto sheetDto) {
        clearCells();
        actionLineController.setCurrentSheet(sheetDto);
        actionLineController.clearTextFields();

        this.numRows = sheetDto.getNumRows();
        this.numCols = sheetDto.getNumCols();

        rangeController.displayRanges(sheetDto.getSheetRanges());

        if (gridPane.getChildren().isEmpty()) {
            setupGridDimensions();
            createCells();
        }

        updateAllCells(sheetDto.getCells());

        if (miscController.areAnimationsEnabled()) {
            animateSheetAppearance();
        }
    }

    @Override
    private void animateSheetAppearance() {
        FadeTransition fadeTransition = new FadeTransition(Duration.seconds(1), gridPane);
        fadeTransition.setFromValue(0.0);
        fadeTransition.setToValue(1.0);

        ScaleTransition scaleTransition = new ScaleTransition(Duration.seconds(1), gridPane);
        scaleTransition.setFromX(0.8);
        scaleTransition.setFromY(0.8);
        scaleTransition.setToX(1.0);
        scaleTransition.setToY(1.0);

        ParallelTransition parallelTransition = new ParallelTransition(fadeTransition, scaleTransition);
        parallelTransition.play();
    }

    @Override
    private void clearCells(){gridPane.getChildren().clear();}

    @Override
    public void setupGridDimensions() {
        gridPane.getColumnConstraints().clear();
        gridPane.getRowConstraints().clear();

        ColumnConstraints headerColumn = new ColumnConstraints(30);
        gridPane.getColumnConstraints().add(headerColumn);

        for (int col = 0; col < numCols; col++) {
            ColumnConstraints column = new ColumnConstraints(100); // Default width
            column.setHgrow(Priority.SOMETIMES);
            gridPane.getColumnConstraints().add(column);
        }

        RowConstraints headerRow = new RowConstraints(30);
        gridPane.getRowConstraints().add(headerRow);

        for (int row = 0; row < numRows; row++) {
            RowConstraints rowConstraint = new RowConstraints(25); // Default height
            rowConstraint.setVgrow(Priority.SOMETIMES);
            gridPane.getRowConstraints().add(rowConstraint);
        }
    }

    @Override
    private void createCells() {
        for (int col = 0; col <= numCols; col++) {
            for (int row = 0; row <= numRows; row++) {
                Label cellLabel;
                if (col == 0 && row == 0) {
                    cellLabel = new Label("");
                } else if (col == 0) {
                    cellLabel = new Label(Integer.toString(row));
                } else if (row == 0) {
                    cellLabel = new Label(Character.toString((char) ('A' + col - 1)));
                } else {
                    cellLabel = new Label("");
                    String cellId = "" + (char)('A' + col - 1) + row;
                    cellLabels.put(cellId, cellLabel);
                }
                setupCell(cellLabel, col, row);
            }
        }
    }

    @Override
    public void displayTemporarySheet(SheetDto sheetDto, boolean versionView) {
        clearPreviousRangeHighlight();
        clearPreviousHighlights();
        savedSheet = currentSheet;
        currentSheet = sheetDto;
        disableCellClick();

        if(!versionView) {
            actionLineController.disableEditing();
        }
        sortAndFilterController.disableSortAndFilter(versionView);
        miscController.disableEditing();
        rangeController.disableEditing();
        updateAllCells(sheetDto.getCells());
    }

    @Override
    private void disableCellClick() {
        for(Label label : cellLabels.values()) {
            label.setOnMouseClicked(null);
        }
    }

    @Override
    public void displayOriginalSheet(boolean versionView) {
        if (savedSheet != null && !versionView) {currentSheet = savedSheet;}
        enableEditing();
        clearCells();
        createCells();
        enableCellClick();
        updateAllCells(currentSheet.getCells());
    }

    @Override
    public void enableEditing() { actionLineController.enableEditing();
        rangeController.enableEditing();
        miscController.enableEditing();
        sortAndFilterController.enableSortAndFilter();}

    @Override
    private void enableCellClick() {
        for (Map.Entry<String, Label> entry : cellLabels.entrySet()) {
            String cellId = entry.getKey();
            Label label = entry.getValue();
            label.setOnMouseClicked(event -> handleCellClick(cellId));
        }
    }

    @Override
    private void setupCell(Label cellLabel, int col, int row) {
        cellLabel.getStyleClass().add("cell");
        if (col == 0) {
            cellLabel.getStyleClass().add("header-cell");
            cellLabel.getStyleClass().add("row-header");
        }
        if (row == 0) {
            cellLabel.getStyleClass().add("header-cell");
            cellLabel.getStyleClass().add("column-header");
        }
        cellLabel.setMaxWidth(Double.MAX_VALUE);
        cellLabel.setMaxHeight(Double.MAX_VALUE);
        GridPane.setHgrow(cellLabel, Priority.SOMETIMES);
        GridPane.setVgrow(cellLabel, Priority.SOMETIMES);
        gridPane.add(cellLabel, col, row);

        if (col > 0 && row > 0) {
            String cellId = "" + (char)('A' + col - 1) + row;
            cellLabel.setOnMouseClicked(event -> handleCellClick(cellId));
            setupCellContextMenu(cellLabel, cellId);
        } else if (col > 0 || row > 0) {
            setupHeaderContextMenu(cellLabel, col > 0 ? col : row, col > 0);
        }
    }

    @Override
    public void highlightRange(String topLeft, String bottomRight) {
        clearPreviousRangeHighlight();

        int startCol = topLeft.charAt(0) - 'A' + 1;
        int startRow = Integer.parseInt(topLeft.substring(1));
        int endCol = bottomRight.charAt(0) - 'A' + 1;
        int endRow = Integer.parseInt(bottomRight.substring(1));

        for (int col = startCol; col <= endCol; col++) {
            for (int row = startRow; row <= endRow; row++) {
                String cellId = "" + (char)('A' + col - 1) + row;
                Label cellLabel = cellLabels.get(cellId);
                if (cellLabel != null) {
                    if (currentSheet.getCells().containsKey(cellId) == false)
                        currentSheet = engine.addEmptyCell(cellId);
                    // Check if the cell is already highlighted
                    if (!currentSheet.getCells().get(cellId).getStyle().contains("-fx-border-color: blue; -fx-border-width: 1px; ")) {
                        String currentStyle = currentSheet.getCells().get(cellId).getStyle();
                        String newStyle = currentStyle + "-fx-border-color: blue; -fx-border-width: 1px; ";
                        cellLabel.setStyle(newStyle);
                        currentlyHighlightedCells.add(cellId);
                    }
                }
            }
        }
    }

    @Override
    public void clearPreviousRangeHighlight() {
        for (String cellId : currentlyHighlightedCells) {
            Label cellLabel = cellLabels.get(cellId);
            if (cellLabel != null) {
                String style = currentSheet.getCells().get(cellId).getStyle();
                style = style.replaceAll("-fx-border-color: blue; -fx-border-width: 1px; ", "");
                cellLabel.setStyle(style);
            }
        }
        currentlyHighlightedCells.clear();
    }

    @Override
    private void handleCellClick(String cellId) {
        clearPreviousHighlights();

        CellDto cellDto = currentSheet.getCells().get(cellId);
        if (cellDto != null) {
            highlightDependenciesAndInfluences(cellDto);
            lastSelectedCell = cellId;

            // Check if animations are enabled before adding the pulsing effect
            if (miscController.areAnimationsEnabled()) {
                Label selectedCellLabel = cellLabels.get(cellId); // Get the Label from the map
                if (selectedCellLabel != null) {
                    addPulsingEffect(selectedCellLabel); // Add the pulsing effect
                }
            }
        }

        if (actionLineController != null) {
            actionLineController.setCurrentSheet(currentSheet);
            actionLineController.setCellData(cellDto, cellId);
        }
    }

    @Override
    private void addPulsingEffect(Node cell) {
        ScaleTransition pulse = new ScaleTransition(Duration.seconds(0.5), cell); // Shorter duration for a quicker pulse
        pulse.setFromX(1.0);
        pulse.setFromY(1.0);
        pulse.setToX(1.1);  // Slightly larger increase in size
        pulse.setToY(1.1);  // Ensure both axes scale together
        pulse.setCycleCount(4);
        pulse.setAutoReverse(true);  // Creates the pulsing effect

        pulse.play();
    }

    @Override
    private void setupCellContextMenu(Label cellLabel, String cellId) {
        ContextMenu contextMenu = new ContextMenu();
        MenuItem styleMenuItem = new MenuItem("Setting the cell style");
        MenuItem resetMenuItem = new MenuItem("Reset style");
        MenuItem buildFormulaMenuItem = new MenuItem("Build formula");
        MenuItem dynamicAnalysisMenuItem = new MenuItem("Dynamic analysis");
        dynamicAnalysisMenuItem.setOnAction(event -> openSliderDialog(cellLabel,cellId));
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
    private void resetCellStyle(Label cellLabel, String cellId) {
        SheetDto styledSheet = engine.setCellStyle(cellId, "");
        currentSheet = styledSheet;
        applyStyle(cellLabel, cellId);
    }

    @Override
    private void setupHeaderContextMenu(Label cellLabel, int index, boolean isColumn) {
        ContextMenu contextMenu = new ContextMenu();
        MenuItem resizeMenuItem = new MenuItem("Resize");
        resizeMenuItem.setOnAction(event -> showResizeDialog(index, isColumn));
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

        for (Map.Entry<String, Label> entry : cellLabels.entrySet()) {
            String cellId = entry.getKey();
            Label cellLabel = entry.getValue();
            if (!cells.containsKey(cellId)) {
                cellLabel.setText("");  // Set to empty string if not in the map
            }
        }
    }

    @Override
    public void updateCell(String cellId, CellDto cellDto) {
        Label cellLabel = cellLabels.get(cellId);
        if (cellLabel != null) {
            cellLabel.setText(cellDto != null ? cellDto.getEffectiveValue() : "");
            applyStyle(cellLabel, cellId);
        }
    }

    @Override
    private void applyStyle(Label cellLabel, String cellId) {
        String style = currentSheet.getCells().get(cellId).getStyle();
        if (style != null) {
            cellLabel.setStyle(style);
        }
    }

    @Override
    private void showCellStyleDialog(Label cellLabel, String cellId) {
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
            SheetDto updatedSheet = engine.addEmptyCell(cellId);
            currentSheet = updatedSheet;
        }
        String prevStyle = currentSheet.getCells().get(cellId).getStyle();

        backgroundColorPicker.setOnAction(event -> {
            String newStyle = currentSheet.getCells().get(cellId).getStyle() +
                    "-fx-background-color: " + toRgbString(backgroundColorPicker.getValue()) + ";";
            SheetDto styledSheet = engine.setCellStyle(cellId, newStyle);
            currentSheet = styledSheet;
        });

        textColorPicker.setOnAction(event -> {
            String newStyle = currentSheet.getCells().get(cellId).getStyle() +
                    "-fx-text-fill: " + toRgbString(textColorPicker.getValue()) + ";";
            SheetDto styledSheet = engine.setCellStyle(cellId, newStyle);
            currentSheet = styledSheet;
        });

        previewButton.addEventFilter(ActionEvent.ACTION, event -> {
            event.consume();
            applyStyle(cellLabel, cellId);
            isPrev.set(true);

        });

        okButton.addEventFilter(ActionEvent.ACTION, event -> {
            applyStyle(cellLabel, cellId);
            isPrev.set(false);
            dialog.close();

        });

        cancelButton.addEventFilter(ActionEvent.ACTION, event -> {
            SheetDto styledSheet = engine.setCellStyle(cellId, prevStyle);
            currentSheet = styledSheet;
            applyStyle(cellLabel,cellId);
            dialog.close();
        });

        dialog.setOnHidden(event -> {
            if (dialog.getResult() == null || isPrev.get()) {
                SheetDto styledSheet = engine.setCellStyle(cellId, prevStyle);
                currentSheet = styledSheet;
                applyStyle(cellLabel,cellId);
            }
        });

        dialog.showAndWait();
    }

    @Override
    private void showResizeDialog(int index, boolean isColumn) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Resize " + (isColumn ? "Column" : "Row"));
        dialog.setHeaderText("Enter new " + (isColumn ? "width" : "height") + " in pixels:");
        dialog.setContentText("Size:");

        dialog.showAndWait().ifPresent(result -> {
            try {
                double size = Double.parseDouble(result);
                if (isColumn) {
                    gridPane.getColumnConstraints().get(index).setPrefWidth(size);
                } else {
                    gridPane.getRowConstraints().get(index).setPrefHeight(size);
                }
            } catch (NumberFormatException e) {
                showAlert("Invalid input", "Please enter a valid number.");
            }
        });
    }

    @Override
    private void showAlignmentDialog(int index, boolean isColumn) {
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
                        SheetDto styledSheet = engine.setCellStyle(cellId, newStyle);
                        currentSheet = styledSheet;
                        Label cell = cellLabels.get(cellId);
                        applyStyle(cell, cellId);
                    }
                    else {
                       SheetDto updatedSheet = engine.addEmptyCell(cellId);
                       currentSheet = updatedSheet;
                        String newStyle = currentSheet.getCells().get(cellId).getStyle() + alignment;
                        SheetDto styledSheet = engine.setCellStyle(cellId, newStyle);
                        currentSheet = styledSheet;
                        Label cell = cellLabels.get(cellId);
                        applyStyle(cell, cellId);
                    }
                }
            } else {
                for (int col = 1; col <= numCols; col++) {
                    String cellId = "" + (char)('A' + col - 1) + index;
                    CellDto cellDto = currentSheet.getCells().get(cellId);
                    if (cellDto != null) {
                        String newStyle = cellDto.getStyle() + alignment;
                        SheetDto styledSheet = engine.setCellStyle(cellId, newStyle);
                        currentSheet = styledSheet;
                        Label cell = cellLabels.get(cellId);
                        applyStyle(cell, cellId);
                    }
                    else {
                        SheetDto updatedSheet = engine.addEmptyCell(cellId);
                        currentSheet = updatedSheet;
                        cellDto = currentSheet.getCells().get(cellId);
                        String newStyle = cellDto.getStyle() + alignment;
                        SheetDto styledSheet = engine.setCellStyle(cellId, newStyle);
                        currentSheet = styledSheet;
                        Label cell = cellLabels.get(cellId);
                        applyStyle(cell, cellId);
                    }
                }
            }
        });
    }

    @Override
    private String toRgbString(Color color) {
        return String.format("#%02X%02X%02X",
                (int) (color.getRed() * 255),
                (int) (color.getGreen() * 255),
                (int) (color.getBlue() * 255));
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    @Override
    private void clearPreviousHighlights() {
        if (lastSelectedCell != null) {
            CellDto lastCellDto = currentSheet.getCells().get(lastSelectedCell);
            clearHighlights(lastCellDto.getDependencies());
            clearHighlights(lastCellDto.getInfluences());
        }
    }

    @Override
    private void clearHighlights(List<String> cellIds) {
        for (String cellId : cellIds) {
            Label cellLabel = cellLabels.get(cellId);
            if (cellLabel != null) {
                cellLabel.setStyle(currentSheet.getCells().get(cellId).getStyle());
            }

        }
    }

    @Override
    private void highlightDependenciesAndInfluences(CellDto cellDto) {
        highlightCells(cellDto.getDependencies(), "lightblue");
        highlightCells(cellDto.getInfluences(), "lightgreen");

    }

    @Override
    private void highlightCells(List<String> cellIds, String color) {
        for (String cellId : cellIds) {
            Label cellLabel = cellLabels.get(cellId);
            if (cellLabel != null) {
                String currentStyle = currentSheet.getCells().get(cellId).getStyle();
                String newStyle = currentStyle + "-fx-background-color: " + color + ";";
                cellLabel.setStyle(newStyle);
            }
        }
    }

    @Override
    public SheetDto getCurrentSheet() {
        return currentSheet;
    }

    @Override
    private void openSliderDialog(Label cellLabel, String cellID) {
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

    @Override
    private void showSliderDialog(Label cellLabel, String cellID, double min, double max, double step) {
        savedSheet = currentSheet;
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
            SheetDto tempSheet = engine.updateCell(cellID, newvalueStr);
            displayTemporarySheet(tempSheet, true);

        });

        Button doneButton = new Button("Done");

        doneButton.setOnAction(e -> {
            savedSheet = engine.updateCell(cellID, realOriginalValue);
            displayOriginalSheet(false); // חזרה לגיליון המקורי
            Stage stage = (Stage) doneButton.getScene().getWindow();
            stage.close();
        });

        VBox layout = new VBox(10, new Label("Choose a new value:"), slider, sliderValueLabel, doneButton);
        layout.setAlignment(Pos.CENTER);
        Scene scene = new Scene(layout, 300, 200);

        Stage dialog = new Stage();
        dialog.setTitle("Update Cell Value");
        dialog.setScene(scene);

        dialog.setOnCloseRequest(e -> { savedSheet = engine.updateCell(cellID, realOriginalValue);
            displayOriginalSheet(false);});
        dialog.show();
    }

}