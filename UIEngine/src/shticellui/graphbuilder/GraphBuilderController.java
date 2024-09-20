package shticellui.graphbuilder;

import dto.SheetDto;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.*;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import shticellui.spreadsheet.SpreadsheetDisplayController;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;


public class GraphBuilderController {

    private SpreadsheetDisplayController spreadsheetDisplayController;

    public GraphBuilderController(SpreadsheetDisplayController spreadsheetDisplayController) {
        this.spreadsheetDisplayController = spreadsheetDisplayController;
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
            dialogStage.initModality(Modality.APPLICATION_MODAL); // חלון מודאלי
            dialogStage.setScene(new Scene(root));
            dialogStage.showAndWait();

            GraphDialogController dialogController = loader.getController();

            String xTopCell = dialogController.getXTopCell().toUpperCase();
            String xBottomCell = dialogController.getXBottomCell().toUpperCase();
            String yTopCell = dialogController.getYTopCell().toUpperCase();
            String yBottomCell = dialogController.getYBottomCell().toUpperCase();
            String graphType = dialogController.getGraphType();

            if (!dialogController.isConfirmed()) {
                return;
            }

            if (xTopCell != null && yTopCell != null && graphType != null
                    && xBottomCell != null && yBottomCell != null) {
                displayGraph(xTopCell, xBottomCell, yTopCell, yBottomCell, graphType, spreadsheetDisplayController);
            } else {
                showError("You must fill all fields.");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void displayGraph(String xTop, String xBottom, String yTop, String yBottom,
                              String graphType, SpreadsheetDisplayController spreadsheetDisplayController) {
        Stage stage = new Stage();
        stage.setTitle("Graph Display");

        int topRow = extractRowFromCell(xTop);
        int bottomRow = extractRowFromCell(xBottom);
        int numOfRows = bottomRow - topRow + 1;
        SheetDto sheetDto = spreadsheetDisplayController.getCurrentSheet();
        char xCol = xTop.charAt(0);
        char yCol = yTop.charAt(0);

        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel("X Axis: " + xTop + " - " + xBottom);
        yAxis.setLabel("Y Axis: " + yTop + " - " + yBottom);
        XYChart.Series<String, Number> series = new XYChart.Series<>();

        Set<Double> xValuesSet = new HashSet<>();
        List<Double> xValuesList = new ArrayList<>();

        for (int i = 0; i < numOfRows; i++) {
            String xCellId = xCol + (String.valueOf(topRow + i));
            String yCellId = yCol + (String.valueOf(topRow + i));
            String xValueStr = sheetDto.getCells().get(xCellId).getEffectiveValue();
            String yValue = sheetDto.getCells().get(yCellId).getEffectiveValue();
            Double yValueNumber = Double.valueOf(yValue);
            Double xValue = Double.valueOf(xValueStr);

            if (xValuesSet.contains(xValue)) {
                showError("Error: Duplicate X value found: " + xValueStr + ". Cannot create graph with duplicate X values.");
                return;
            }

            xValuesSet.add(xValue);
            xValuesList.add(xValue);

            series.getData().add(new XYChart.Data<>(xValueStr, yValueNumber));
        }

        xValuesList.sort(Collections.reverseOrder());

        List<String> sortedXValuesStrList = xValuesList.stream()
                .map(String::valueOf)
                .collect(Collectors.toList());
        xAxis.setCategories(FXCollections.observableArrayList(sortedXValuesStrList));

        xAxis.setAutoRanging(false);

        if (graphType.equals("Line Chart")) {
            LineChart<String, Number> lineChart = new LineChart<>(xAxis, yAxis);
            lineChart.setTitle("Line Graph");
            lineChart.getData().add(series);
            VBox vbox = new VBox(lineChart);
            Scene scene = new Scene(vbox, 800, 400);
            stage.setScene(scene);
            stage.show();
        } else if (graphType.equals("Bar Chart")) {
            BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis);
            barChart.setTitle("Bar Graph");
            barChart.getData().add(series);
            VBox vbox = new VBox(barChart);
            Scene scene = new Scene(vbox, 800, 400);
            stage.setScene(scene);
            stage.show();
        }
    }


    public static int extractRowFromCell(String cell) {
        Pattern pattern = Pattern.compile("[A-Z](\\d+)"); // תואם אות אחת ואחריה מספרים
        Matcher matcher = pattern.matcher(cell);

        if (matcher.find()) {
            return Integer.parseInt(matcher.group(1)); // מחזיר את המספר בלבד
        } else {
            throw new IllegalArgumentException("No valid cell reference found");
        }
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR, message, ButtonType.OK);
        alert.showAndWait();
    }
}
