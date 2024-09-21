package command.components.graphbuilder.builder.impl;

import command.components.graphbuilder.dialog.GraphDialogController;
import dto.SheetDto;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.*;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import spreadsheet.impl.SpreadsheetControllerImpl;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;


public class GraphBuilderControllerImpl {

    private SpreadsheetControllerImpl spreadsheetControllerImpl;

    public GraphBuilderController(SpreadsheetControllerImpl spreadsheetControllerImpl) {
        this.spreadsheetControllerImpl = spreadsheetControllerImpl;
    }
    public GraphBuilderController() {
    }

    @FXML
    public void buildGraph() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/shticellui/graphbuilder/graphDialog.fxml"));
            Parent root = loader.load();

            Stage dialogStage = new Stage();
            dialogStage.setTitle("Build Graph");
            dialogStage.initModality(Modality.APPLICATION_MODAL);
            dialogStage.setScene(new Scene(root));
            dialogStage.showAndWait();

            GraphDialogController dialogController = loader.getController();

            String xTopCell = dialogController.getXTopCell().toUpperCase();
            String xBottomCell = dialogController.getXBottomCell().toUpperCase();
            String yTopCell = dialogController.getYTopCell().toUpperCase();
            String yBottomCell = dialogController.getYBottomCell().toUpperCase();

            if (!dialogController.isConfirmed()) {
                return;
            }

            if (xTopCell != null && yTopCell != null && xBottomCell != null
                    && yBottomCell != null) {
                displayGraph(xTopCell, xBottomCell, yTopCell, yBottomCell, spreadsheetControllerImpl);
            } else {
                showError("You must fill all fields.");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void displayGraph(String xTop, String xBottom, String yTop, String yBottom,
                              SpreadsheetControllerImpl spreadsheetControllerImpl) {
        Stage stage = new Stage();
        stage.setTitle("Graph Display");

        int topRow = extractRowFromCell(xTop);
        int bottomRow = extractRowFromCell(xBottom);
        int numOfRows = bottomRow - topRow + 1;
        SheetDto sheetDto = spreadsheetControllerImpl.getCurrentSheet();
        char xCol = xTop.charAt(0);
        char yCol = yTop.charAt(0);

        List<Double> xValuesList = new ArrayList<>();
        List<XYChart.Data<Number, Number>> numericSeriesData = new ArrayList<>();
        List<XYChart.Data<String, Number>> stringSeriesData = new ArrayList<>();
        Set<Double> xValuesSet = new HashSet<>();

        for (int i = 0; i < numOfRows; i++) {
            String xCellId = xCol + (String.valueOf(topRow + i));
            String yCellId = yCol + (String.valueOf(topRow + i));
            String xValueStr = sheetDto.getCells().get(xCellId).getEffectiveValue();
            String yValue = sheetDto.getCells().get(yCellId).getEffectiveValue();
            Double yValueNumber = Double.valueOf(yValue);
            Double xValue = Double.valueOf(xValueStr);

            if (xValuesSet.contains(xValue)) {
                showError("Error: Cannot create graph with duplicate X values.");
                return;
            }
            xValuesSet.add(xValue);

            xValuesList.add(xValue);

            numericSeriesData.add(new XYChart.Data<>(xValue, yValueNumber));
            stringSeriesData.add(new XYChart.Data<>(xValueStr, yValueNumber));
        }

        Collections.sort(xValuesList);

        ComboBox<String> graphTypeComboBox = new ComboBox<>();
        graphTypeComboBox.getItems().addAll("Line Chart", "Bar Chart");
        graphTypeComboBox.setValue("Line Chart");

        VBox vbox = new VBox(10);
        vbox.setAlignment(Pos.CENTER);
        Scene scene = new Scene(vbox, 800, 400);
        stage.setScene(scene);

        LineChart<Number, Number> lineChart = createLineChart(numericSeriesData, xTop, xBottom, yTop, yBottom);
        vbox.getChildren().addAll(graphTypeComboBox, lineChart);

        graphTypeComboBox.setOnAction(event -> {
            String selectedGraphType = graphTypeComboBox.getValue();
            vbox.getChildren().remove(1);

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

    private LineChart<Number, Number> createLineChart(List<XYChart.Data<Number, Number>> seriesData,
                                                      String xTop, String xBottom, String yTop, String yBottom) {
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

    private BarChart<String, Number> createBarChart(List<XYChart.Data<String, Number>> seriesData,
                                                    List<Double> xValuesList, String xTop, String xBottom, String yTop, String yBottom) {
        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel("X Axis: " + xTop + " - " + xBottom);
        yAxis.setLabel("Y Axis: " + yTop + " - " + yBottom);

        // המרה למחרוזות
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



    public static int extractRowFromCell(String cell) {
        Pattern pattern = Pattern.compile("[A-Z](\\d+)");
        Matcher matcher = pattern.matcher(cell);

        if (matcher.find()) {
            return Integer.parseInt(matcher.group(1));
        } else {
            throw new IllegalArgumentException("No valid cell reference found");
        }
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR, message, ButtonType.OK);
        alert.showAndWait();
    }
}