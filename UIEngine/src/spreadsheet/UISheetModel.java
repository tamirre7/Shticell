package spreadsheet;

import dto.CellDto;
import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.ScaleTransition;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;
import javafx.scene.paint.Color;
import javafx.util.Duration;

import javafx.scene.layout.*;
import spreadsheet.api.SpreadsheetController;

import java.util.*;


public class UISheetModel {
    private GridPane gridPane;
    private SpreadsheetController spreadSheetController;
    private Map<String, Label> cellLabels = new HashMap<>();
    private String lastSelectedCell = null;
    private Set<String> currentlyHighlightedCells = new HashSet<>();

    public void modelSetUp(GridPane gridPane, SpreadsheetController spreadSheetController) {this.gridPane = gridPane; this.spreadSheetController = spreadSheetController;}

    public Label getCellLabel(String label) {return cellLabels.get(label);}

    public void createCells(int numRows, int numCols) {
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
            cellLabel.setOnMouseClicked(event -> spreadSheetController.handleCellClick(cellId));
            spreadSheetController.setupCellContextMenu(cellLabel, cellId);
        } else if (col > 0 || row > 0) {
            spreadSheetController.setupHeaderContextMenu(cellLabel, col > 0 ? col : row, col > 0);
        }
    }

    public void applyStyle(Label cellLabel, String cellId) {
        String style = spreadSheetController.getCurrentSheet().getCells().get(cellId).getStyle();
        if (style != null) {
            cellLabel.setStyle(style);
        }
    }

    public void clearCells(){gridPane.getChildren().clear();}

    public String toRgbString(Color color) {
        return String.format("#%02X%02X%02X",
                (int) (color.getRed() * 255),
                (int) (color.getGreen() * 255),
                (int) (color.getBlue() * 255));
    }

    public void addPulsingEffect(Node cell) {
        ScaleTransition pulse = new ScaleTransition(Duration.seconds(0.5), cell); // Shorter duration for a quicker pulse
        pulse.setFromX(1.0);
        pulse.setFromY(1.0);
        pulse.setToX(1.1);  // Slightly larger increase in size
        pulse.setToY(1.1);  // Ensure both axes scale together
        pulse.setCycleCount(4);
        pulse.setAutoReverse(true);  // Creates the pulsing effect

        pulse.play();
    }


    public void animateSheetAppearance() {
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

    public void setupGridDimensions(int numRows, int numCols,int rowHeight, int colWidth) {
        gridPane.getColumnConstraints().clear();
        gridPane.getRowConstraints().clear();

        ColumnConstraints headerColumn = new ColumnConstraints(30);
        headerColumn.setHgrow(Priority.NEVER);
        gridPane.getColumnConstraints().add(headerColumn);

        for (int col = 0; col <= numCols; col++) {
            ColumnConstraints column = new ColumnConstraints(colWidth);
            column.setHgrow(Priority.SOMETIMES);
            gridPane.getColumnConstraints().add(column);
        }

        RowConstraints headerRow = new RowConstraints(30);
        headerRow.setVgrow(Priority.NEVER);
        gridPane.getRowConstraints().add(headerRow);

        for (int row = 0; row <= numRows; row++) {
            RowConstraints rowConstraint = new RowConstraints(rowHeight); // Default height
            rowConstraint.setVgrow(Priority.SOMETIMES);
            gridPane.getRowConstraints().add(rowConstraint);
        }
    }

    public void clearPreviousHighlights() {
        if (lastSelectedCell != null) {
            CellDto lastCellDto = spreadSheetController.getCurrentSheet().getCells().get(lastSelectedCell);
            if(lastCellDto != null) {
                clearHighlights(lastCellDto.getDependencies());
                clearHighlights(lastCellDto.getInfluences());
            }
        }
    }


    private void clearHighlights(List<String> cellIds) {
        for (String cellId : cellIds) {
            Label cellLabel = cellLabels.get(cellId);
            if (cellLabel != null) {
                cellLabel.setStyle(spreadSheetController.getCurrentSheet().getCells().get(cellId).getStyle());
            }
        }
    }


    public void highlightDependenciesAndInfluences(CellDto cellDto) {
        lastSelectedCell = cellDto.getCellId();
        highlightCells(cellDto.getDependencies(), "lightblue");
        highlightCells(cellDto.getInfluences(), "lightgreen");
    }

    public void showResizeDialog(int index, boolean isColumn) {
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
                spreadSheetController.recalculateGridDimensions();
            } catch (NumberFormatException e) {
                showAlert("Invalid input", "Please enter a valid number.");
            }
        });
    }

    private void highlightCells(List<String> cellIds, String color) {
        for (String cellId : cellIds) {
            Label cellLabel = cellLabels.get(cellId);
            if (cellLabel != null) {
                String currentStyle = spreadSheetController.getCurrentSheet().getCells().get(cellId).getStyle();
                String newStyle = currentStyle + "-fx-background-color: " + color + ";";
                cellLabel.setStyle(newStyle); }
        }
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
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
                    // Check if the cell is already highlighted
                    if (!spreadSheetController.getCurrentSheet().getCells().get(cellId).getStyle().contains("-fx-border-color: blue; -fx-border-width: 1px; ")) {
                        String currentStyle = spreadSheetController.getCurrentSheet().getCells().get(cellId).getStyle();
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
                String style = spreadSheetController.getCurrentSheet().getCells().get(cellId).getStyle();
                style = style.replaceAll("-fx-border-color: blue; -fx-border-width: 1px; ", "");
                cellLabel.setStyle(style);
            }
        }
        currentlyHighlightedCells.clear();
    }
}
