package shticellui.spreadsheet;

import command.api.Engine;
import dto.CellDto;
import dto.SheetDto;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import shticellui.action.line.ActionLineController;

public class SpreadsheetDisplayController {

    @FXML private GridPane gridPane;
    @FXML private ScrollPane scrollPane;
    private final Engine engine;
    private ActionLineController actionLineController;
    private int numRows;
    private int numCols;

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

    public void setActionLineController(ActionLineController actionLineController) {
        this.actionLineController = actionLineController;
    }

    private void setupGridDimensions() {
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
        GridPane.setHgrow(cellLabel, Priority.ALWAYS);
        GridPane.setVgrow(cellLabel, Priority.ALWAYS);
        gridPane.add(cellLabel, col, row);

        if (col > 0 && row > 0) {
            cellLabel.setOnMouseClicked(event -> {
                String cellId = "" + (char)('A' + col - 1) + row;
                CellDto cellDto = engine.displayCellValue(cellId);
                if (actionLineController != null) {
                    actionLineController.setCellData(cellDto, cellId);
                }
            });

            // Add context menu for cell styling
            ContextMenu contextMenu = new ContextMenu();
            MenuItem styleMenuItem = new MenuItem("Style Cell");
            styleMenuItem.setOnAction(event -> showCellStyleDialog(cellLabel));
            contextMenu.getItems().add(styleMenuItem);
            cellLabel.setContextMenu(contextMenu);
        } else if (col > 0 || row > 0) {
            // Add context menu for column/row operations
            ContextMenu contextMenu = new ContextMenu();
            MenuItem resizeMenuItem = new MenuItem("Resize");
            resizeMenuItem.setOnAction(event -> showResizeDialog(col > 0 ? col : row, col > 0));
            MenuItem alignMenuItem = new MenuItem("Set Alignment");
            alignMenuItem.setOnAction(event -> showAlignmentDialog(col > 0 ? col : row, col > 0));
            contextMenu.getItems().addAll(resizeMenuItem, alignMenuItem);
            cellLabel.setContextMenu(contextMenu);
        }
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
                    Label cell = getNodeByRowColumnIndex(row, index, gridPane);
                    if (cell != null) {
                        cell.setStyle(cell.getStyle() + alignment);
                    }
                }
            } else {
                for (int col = 1; col <= numCols; col++) {
                    Label cell = getNodeByRowColumnIndex(index, col, gridPane);
                    if (cell != null) {
                        cell.setStyle(cell.getStyle() + alignment);
                    }
                }
            }
        });
    }

    private void showCellStyleDialog(Label cellLabel) {
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

        backgroundColorPicker.setOnAction(event ->
                cellLabel.setStyle(cellLabel.getStyle() + "-fx-background-color: " + toRgbString(backgroundColorPicker.getValue()) + ";"));

        textColorPicker.setOnAction(event ->
                cellLabel.setStyle(cellLabel.getStyle() + "-fx-text-fill: " + toRgbString(textColorPicker.getValue()) + ";"));

        resetButton.setOnAction(event -> cellLabel.setStyle(""));

        dialog.showAndWait();
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

    public void displaySheet(SheetDto sheetDto) {
        this.numRows = sheetDto.getNumRows();
        this.numCols = sheetDto.getNumCols();

        gridPane.getChildren().clear();
        gridPane.getColumnConstraints().clear();
        gridPane.getRowConstraints().clear();

        setupGridDimensions();

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
                    String cellId = "" + (char)('A' + col - 1) + row;
                    CellDto cellDto = sheetDto.getCells().get(cellId);
                    cellLabel = new Label(cellDto != null ? cellDto.getEffectiveValue() : "");
                }
                setupCell(cellLabel, col, row);
            }
        }
    }

    private Label getNodeByRowColumnIndex(final int row, final int col, GridPane gridPane) {
        for (javafx.scene.Node node : gridPane.getChildren()) {
            if (GridPane.getRowIndex(node) == row && GridPane.getColumnIndex(node) == col) {
                return (Label) node;
            }
        }
        return null;
    }
}