package shticell.client.sheetpanel.command.components.graphbuilder.builder.impl;

import javafx.scene.control.Button;
import shticell.client.sheetpanel.command.components.graphbuilder.builder.api.GraphBuilderController;
import shticell.client.sheetpanel.command.components.graphbuilder.dialog.api.GraphDialogController;
import dto.SheetDto;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.*;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import shticell.client.sheetpanel.spreadsheet.api.SpreadsheetController;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static shticell.client.util.http.HttpClientUtil.showAlert;

public class GraphBuilderControllerImpl implements GraphBuilderController {

    private SpreadsheetController spreadsheetController;
    @FXML
    private Button graphButton;

    @Override
    // Sets the spreadsheet controller for this graph builder
    public void setSpreadsheetController(SpreadsheetController spreadsheetController) {
        this.spreadsheetController = spreadsheetController;
    }

    @Override
    // Enables the graph building button
    public void enableGraphBuild() {
        graphButton.setDisable(false);
    }

    @Override
    // Disables the graph building button
    public void disableGraphBuild() {
        graphButton.setDisable(true);
    }

    @FXML
    @Override
    // Builds a graph based on user input from the graph dialog
    public void buildGraph() {
        // Check if there is a loaded sheet
        if (spreadsheetController.getCurrentSheet() == null) {
            showAlert("Error", "A file must be loaded first.");
            return;
        }
        try {
            // Load the graph dialog FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/shticell/client/sheetpanel/command/components/graphbuilder/dialog/graphdialog.fxml"));
            Parent root = loader.load();

            // Create and display the dialog stage
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Build Graph");
            dialogStage.initModality(Modality.APPLICATION_MODAL);
            dialogStage.setScene(new Scene(root));
            dialogStage.showAndWait();

            // Retrieve the dialog controller
            GraphDialogController dialogController = loader.getController();

            // Get the cell references for the graph axes
            String xTopCell = dialogController.getXTopCell().toUpperCase();
            String xBottomCell = dialogController.getXBottomCell().toUpperCase();
            String yTopCell = dialogController.getYTopCell().toUpperCase();
            String yBottomCell = dialogController.getYBottomCell().toUpperCase();

            // Check if the dialog was confirmed
            if (!dialogController.isConfirmed()) {
                return;
            }

            // Check that all cell references are provided
            if (xTopCell != null && yTopCell != null && xBottomCell != null && yBottomCell != null) {
                // Display the graph with the provided cell references
                displayGraph(xTopCell, xBottomCell, yTopCell, yBottomCell, spreadsheetController);
            } else {
                showAlert("Error", "You must fill all fields.");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Displays the graph in a new stage
    private void displayGraph(String xTop, String xBottom, String yTop, String yBottom, SpreadsheetController spreadsheetController) {
        Stage stage = new Stage();
        stage.setTitle("Graph Display");

        // Extract the row numbers from the provided cell references
        int topRow = extractRowFromCell(xTop);
        int bottomRow = extractRowFromCell(xBottom);
        int numOfRows = bottomRow - topRow + 1;
        SheetDto sheetDto = spreadsheetController.getCurrentSheet();
        char xCol = xTop.charAt(0);
        char yCol = yTop.charAt(0);

        List<Double> xValuesList = new ArrayList<>();
        List<XYChart.Data<Number, Number>> numericSeriesData = new ArrayList<>();
        List<XYChart.Data<String, Number>> stringSeriesData = new ArrayList<>();
        Set<Double> xValuesSet = new HashSet<>();

        // Iterate over the specified rows to gather data
        for (int i = 0; i < numOfRows; i++) {
            String xCellId = xCol + (String.valueOf(topRow + i));
            String yCellId = yCol + (String.valueOf(topRow + i));
            String xValueStr = sheetDto.getCells().get(xCellId).getEffectiveValue();
            String yValue = sheetDto.getCells().get(yCellId).getEffectiveValue();
            Double yValueNumber = Double.valueOf(yValue);
            Double xValue = Double.valueOf(xValueStr);

            // Check for duplicate X values
            if (xValuesSet.contains(xValue)) {
                showAlert("ERROR", "Cannot create graph with duplicate X values.");
                return;
            }
            xValuesSet.add(xValue);
            xValuesList.add(xValue);

            // Add data to the series lists
            numericSeriesData.add(new XYChart.Data<>(xValue, yValueNumber));
            stringSeriesData.add(new XYChart.Data<>(xValueStr, yValueNumber));
        }

        // Sort the X values for proper display
        Collections.sort(xValuesList);

        // Create a ComboBox for selecting graph type
        ComboBox<String> graphTypeComboBox = new ComboBox<>();
        graphTypeComboBox.getItems().addAll("Line Chart", "Bar Chart");
        graphTypeComboBox.setValue("Line Chart");

        VBox vbox = new VBox(10);
        vbox.setAlignment(Pos.CENTER);
        Scene scene = new Scene(vbox, 800, 400);
        stage.setScene(scene);

        // Create the initial line chart
        LineChart<Number, Number> lineChart = createLineChart(numericSeriesData, xTop, xBottom, yTop, yBottom);
        vbox.getChildren().addAll(graphTypeComboBox, lineChart);

        // Set action for graph type selection
        graphTypeComboBox.setOnAction(event -> {
            String selectedGraphType = graphTypeComboBox.getValue();
            vbox.getChildren().remove(1);

            // Create and add the selected graph type
            if (selectedGraphType.equals("Line Chart")) {
                LineChart<Number, Number> newLineChart = createLineChart(numericSeriesData, xTop, xBottom, yTop, yBottom);
                vbox.getChildren().add(newLineChart);
            } else if (selectedGraphType.equals("Bar Chart")) {
                BarChart<String, Number> newBarChart = createBarChart(stringSeriesData, xValuesList, xTop, xBottom, yTop, yBottom);
                vbox.getChildren().add(newBarChart);
            }
        });

        stage.show();
    }

    // Creates a line chart based on the provided data
    private LineChart<Number, Number> createLineChart(List<XYChart.Data<Number, Number>> seriesData, String xTop, String xBottom, String yTop, String yBottom) {
        NumberAxis xAxis = new NumberAxis();
        NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel("X Axis: " + xTop + " - " + xBottom);
        yAxis.setLabel("Y Axis: " + yTop + " - " + yBottom);

        XYChart.Series<Number, Number> series = new XYChart.Series<>();
        series.getData().addAll(seriesData);

        LineChart<Number, Number> lineChart = new LineChart<>(xAxis, yAxis);
        lineChart.getData().add(series);

        lineChart.setLegendVisible(false);

        return lineChart;
    }

    // Creates a bar chart based on the provided data
    private BarChart<String, Number> createBarChart(List<XYChart.Data<String, Number>> seriesData, List<Double> xValuesList, String xTop, String xBottom, String yTop, String yBottom) {
        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel("X Axis: " + xTop + " - " + xBottom);
        yAxis.setLabel("Y Axis: " + yTop + " - " + yBottom);

        List<String> sortedXValuesStrList = xValuesList.stream()
                .map(String::valueOf)
                .collect(Collectors.toList());
        xAxis.setCategories(FXCollections.observableArrayList(sortedXValuesStrList));

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.getData().addAll(seriesData);

        BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis);
        barChart.getData().add(series);

        barChart.setLegendVisible(false);

        return barChart;
    }

    // Extracts the row number from a given cell reference
    private static int extractRowFromCell(String cell) {
        Pattern pattern = Pattern.compile("[A-Z](\\d+)");
        Matcher matcher = pattern.matcher(cell);

        if (matcher.find()) {
            return Integer.parseInt(matcher.group(1));
        } else {
            throw new IllegalArgumentException("No valid cell reference found");
        }
    }

}
