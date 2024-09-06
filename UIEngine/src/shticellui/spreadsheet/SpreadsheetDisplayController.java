package shticellui.spreadsheet;

import command.api.Engine;
import dto.CellDto;
import dto.SheetDto;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.Priority;
import javafx.scene.control.TextField;
import shticellui.action.line.ActionLineController;

public class SpreadsheetDisplayController {

    @FXML
    private GridPane gridPane;
    @FXML
    private ScrollPane scrollPane;
    private final Engine engine;

    private ActionLineController actionLineController;  // Reference to ActionLineController

    private int numRows;
    private int numCols;

    public SpreadsheetDisplayController(Engine engine) {
        this.engine = engine;
    }

    @FXML
    public void initialize() {
        // Initialization code if needed
        scrollPane.setContent(gridPane);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        gridPane.setMinWidth(800); // Set minimum width
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
            ColumnConstraints column = new ColumnConstraints();
            column.setHgrow(Priority.ALWAYS); // Allow columns to grow
            column.setFillWidth(true);
            gridPane.getColumnConstraints().add(column);
        }

        RowConstraints headerRow = new RowConstraints(30);
        gridPane.getRowConstraints().add(headerRow);

        for (int row = 0; row < numRows; row++) {
            RowConstraints rowConstraint = new RowConstraints();
            rowConstraint.setVgrow(Priority.ALWAYS); // Allow rows to grow
            rowConstraint.setFillHeight(true);
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

        // Click to update action line
        if (col > 0 && row > 0) { // Don't allow headers to be editable
            cellLabel.setOnMouseClicked(event -> {
                String cellId = "" + (char)('A' + col - 1) + row;
                CellDto cellDto = engine.displayCellValue(cellId);  // Retrieve cell data from engine

                if (actionLineController != null) {
                    actionLineController.setCellData(cellDto, cellId);  // Update action line
                }
            });
        }
    }

    public void displaySheet(SheetDto sheetDto) {
        this.numRows = sheetDto.getNumRows();
        this.numCols = sheetDto.getNumCols();

        // Clear the existing content
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
    public void refreshCell(String cellId, CellDto updatedCell) {
        // Parse the cellId (e.g., "A1") to determine its grid coordinates
        char columnLetter = cellId.charAt(0);
        int row = Integer.parseInt(cellId.substring(1));
        int col = columnLetter - 'A' + 1;  // Convert letter to column index

        // Find the label at that position
        Label cellLabel = (Label) getNodeByRowColumnIndex(row, col, gridPane);

        if (cellLabel != null) {
            // Update the label with the new value
            cellLabel.setText(updatedCell.getEffectiveValue());
        }
    }

    // Helper method to get a node at a specific grid position
    private Label getNodeByRowColumnIndex(final int row, final int col, GridPane gridPane) {
        for (javafx.scene.Node node : gridPane.getChildren()) {
            if (GridPane.getRowIndex(node) == row && GridPane.getColumnIndex(node) == col) {
                return (Label) node;
            }
        }
        return null;
    }

}
