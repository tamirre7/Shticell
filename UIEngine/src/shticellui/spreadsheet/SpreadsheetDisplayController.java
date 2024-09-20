package shticellui.spreadsheet;

import command.api.Engine;
import dto.CellDto;
import dto.SheetDto;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import shticellui.action.line.ActionLineController;
import shticellui.formulabuilder.FormulaBuilder;
import shticellui.misc.MiscController;
import shticellui.range.RangeController;
import shticellui.sortandfilter.SortAndFilterController;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class SpreadsheetDisplayController {

    @FXML private GridPane gridPane;
    @FXML private ScrollPane scrollPane;
    private final Engine engine;
    private ActionLineController actionLineController;
    private int numRows;
    private int numCols;
    private Map<String, Label> cellLabels = new HashMap<>();
    private String lastSelectedCell = null;
    private RangeController rangeController;
    private Set<String> currentlyHighlightedCells = new HashSet<>();
    private SheetDto currentSheet;
    private SheetDto savedSheet;
    private MiscController miscController;
    private SortAndFilterController sortAndFilterController;
    private FormulaBuilder formulaBuilder;

    public SpreadsheetDisplayController(Engine engine) {
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

    public void setFormulaBuilder(FormulaBuilder formulaBuilder) {this.formulaBuilder = formulaBuilder;}

    public void setCurrentSheet(SheetDto currentSheet) {
        this.currentSheet = currentSheet;
    }

    public void setMiscController(MiscController miscController) { this.miscController = miscController; }

    public void setActionLineController(ActionLineController actionLineController) {
        this.actionLineController = actionLineController;
    }
    public void setRangeController(RangeController rangeController) {this.rangeController = rangeController;}

    public void setSortAndFilterController(SortAndFilterController sortAndFilterController) {this.sortAndFilterController = sortAndFilterController;}

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
    }

    private void clearCells(){gridPane.getChildren().clear();}

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

    private void disableCellClick() {
        for(Label label : cellLabels.values()) {
            label.setOnMouseClicked(null);
        }
    }


    public void displayOriginalSheet(boolean versionView) {
        if (savedSheet != null && !versionView) {currentSheet = savedSheet;}
        enableEditing();
        clearCells();
        createCells();
        enableCellClick();
        updateAllCells(currentSheet.getCells());
    }

    public void enableEditing() { actionLineController.enableEditing();
        rangeController.enableEditing();
        miscController.enableEditing();
        sortAndFilterController.enableSortAndFilter();}

    private void enableCellClick() {
        for (Map.Entry<String, Label> entry : cellLabels.entrySet()) {
            String cellId = entry.getKey();
            Label label = entry.getValue();
            label.setOnMouseClicked(event -> handleCellClick(cellId));
        }
    }

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
                        currentSheet = engine.addCell(cellId);
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

    private void handleCellClick(String cellId) {
        clearPreviousHighlights();
        CellDto cellDto = currentSheet.getCells().get(cellId);
        if (cellDto != null) {highlightDependenciesAndInfluences(cellDto); lastSelectedCell = cellId;}
        if (actionLineController != null) {
            actionLineController.setCurrentSheet(currentSheet);
            actionLineController.setCellData(cellDto, cellId);
        }
    }

    private void setupCellContextMenu(Label cellLabel, String cellId) {
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

    private void resetCellStyle(Label cellLabel, String cellId) {
        SheetDto styledSheet = engine.setCellStyle(cellId, "");
        currentSheet = styledSheet;
        applyStyle(cellLabel, cellId);
    }

    private void setupHeaderContextMenu(Label cellLabel, int index, boolean isColumn) {
        ContextMenu contextMenu = new ContextMenu();
        MenuItem resizeMenuItem = new MenuItem("Resize");
        resizeMenuItem.setOnAction(event -> showResizeDialog(index, isColumn));
        MenuItem alignMenuItem = new MenuItem("Set Alignment");
        alignMenuItem.setOnAction(event -> showAlignmentDialog(index, isColumn));
        contextMenu.getItems().addAll(resizeMenuItem, alignMenuItem);
        cellLabel.setContextMenu(contextMenu);
    }

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

    public void updateCell(String cellId, CellDto cellDto) {
        Label cellLabel = cellLabels.get(cellId);
        if (cellLabel != null) {
            cellLabel.setText(cellDto != null ? cellDto.getEffectiveValue() : "");
            applyStyle(cellLabel, cellId);
        }
    }

    private void applyStyle(Label cellLabel, String cellId) {
        String style = currentSheet.getCells().get(cellId).getStyle();
        if (style != null) {
            cellLabel.setStyle(style);
        }
    }

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
            SheetDto updatedSheet = engine.addCell(cellId);
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
                       SheetDto updatedSheet = engine.addCell(cellId);
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
                        SheetDto updatedSheet = engine.addCell(cellId);
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

    private void clearPreviousHighlights() {
        if (lastSelectedCell != null) {
            CellDto lastCellDto = currentSheet.getCells().get(lastSelectedCell);
            clearHighlights(lastCellDto.getDependencies());
            clearHighlights(lastCellDto.getInfluences());
        }
    }

    private void clearHighlights(List<String> cellIds) {
        for (String cellId : cellIds) {
            Label cellLabel = cellLabels.get(cellId);
            if (cellLabel != null) {
                cellLabel.setStyle(currentSheet.getCells().get(cellId).getStyle());
            }

        }
    }

    private void highlightDependenciesAndInfluences(CellDto cellDto) {
        highlightCells(cellDto.getDependencies(), "lightblue"); // Light blue with 50% opacity
        highlightCells(cellDto.getInfluences(), "lightgreen"); // Light green with 50% opacity
    }

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

    public SheetDto getCurrentSheet() {
        return currentSheet;
    }
}