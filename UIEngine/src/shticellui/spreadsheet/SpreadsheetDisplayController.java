package shticellui.spreadsheet;

import command.api.Engine;
import dto.CellDto;
import dto.SheetDto;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import shticellui.action.line.ActionLineController;
import shticellui.misc.MiscController;
import shticellui.range.RangeController;

import java.util.*;

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

    public SpreadsheetDisplayController(Engine engine) {
        this.engine = engine;
    }

    @FXML
    public void initialize() {
        scrollPane.setContent(gridPane);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        gridPane.setMinWidth(800);
        gridPane.setMinHeight(600);
    }

    public void setCurrentSheet(SheetDto currentSheet) {
        this.currentSheet = currentSheet;
    }

    public void setMiscController(MiscController miscController) { this.miscController = miscController; }

    public void setActionLineController(ActionLineController actionLineController) {
        this.actionLineController = actionLineController;
    }
    public void setRangeController(RangeController rangeController) {this.rangeController = rangeController;}

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

    public void displayTemporarySheet(SheetDto sheetDto) {
        clearPreviousRangeHighlight();
        clearPreviousHighlights();
        savedSheet = currentSheet;
        currentSheet = sheetDto;
        disableCellClick();
        miscController.disableEditing();
        actionLineController.disableEditing();
        rangeController.disableEditing();
        updateAllCells(sheetDto.getCells());
    }

    private void disableCellClick() {
        for(Label label : cellLabels.values()) {
            label.setOnMouseClicked(null);
        }
    }


    public void displayOriginalSheet() {
        currentSheet = savedSheet;
        actionLineController.enableEditing();
        rangeController.enableEditing();
        miscController.enableEditing();
        clearCells();
        createCells();
        enableCellClick();
        updateAllCells(currentSheet.getCells());
    }

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
        MenuItem styleMenuItem = new MenuItem("Style Cell");
        styleMenuItem.setOnAction(event -> showCellStyleDialog(cellLabel, cellId));
        contextMenu.getItems().add(styleMenuItem);
        cellLabel.setContextMenu(contextMenu);
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
        dialog.setTitle("Style Cell");
        dialog.setHeaderText("Choose cell style:");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);

        ColorPicker backgroundColorPicker = new ColorPicker();
        ColorPicker textColorPicker = new ColorPicker();
        Button resetButton = new Button("Reset Style");

        grid.add(new Label("Background Color:"), 0, 0);
        grid.add(backgroundColorPicker, 1, 0);
        grid.add(new Label("Text Color:"), 0, 1);
        grid.add(textColorPicker, 1, 1);
        grid.add(resetButton, 0, 2, 2, 1);

        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CANCEL);
        if (currentSheet.getCells().get(cellId) == null) {
            SheetDto updatedSheet = engine.addCell(cellId);
            currentSheet = updatedSheet;
        }
        backgroundColorPicker.setOnAction(event -> {
            String newStyle = currentSheet.getCells().get(cellId).getStyle() +
                    "-fx-background-color: " + toRgbString(backgroundColorPicker.getValue()) + ";";
            SheetDto styledSheet = engine.setCellStyle(cellId, newStyle);
            currentSheet = styledSheet;
            applyStyle(cellLabel, cellId);
        });

        textColorPicker.setOnAction(event -> {
            String newStyle = currentSheet.getCells().get(cellId).getStyle() +
                    "-fx-text-fill: " + toRgbString(textColorPicker.getValue()) + ";";
            SheetDto styledSheet = engine.setCellStyle(cellId, newStyle);
            currentSheet = styledSheet;
            applyStyle(cellLabel, cellId);
        });

        resetButton.setOnAction(event -> {
            SheetDto styledSheet = engine.setCellStyle(cellId, "");
            currentSheet = styledSheet;
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

}