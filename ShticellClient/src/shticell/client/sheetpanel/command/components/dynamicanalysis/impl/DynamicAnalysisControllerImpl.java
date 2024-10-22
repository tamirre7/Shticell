package shticell.client.sheetpanel.command.components.dynamicanalysis.impl;

import com.google.gson.Gson;
import dto.CellDto;
import dto.SheetDto;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;
import shticell.client.sheetpanel.command.components.dynamicanalysis.api.DynamicAnalysisController;
import shticell.client.sheetpanel.spreadsheet.impl.SpreadsheetControllerImpl;
import shticell.client.util.Constants;
import shticell.client.util.http.HttpClientUtil;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import java.util.stream.Collectors;

import static shticell.client.util.http.HttpClientUtil.extractSheetFromResponseBody;
import static shticell.client.util.http.HttpClientUtil.showAlert;

public class DynamicAnalysisControllerImpl implements DynamicAnalysisController {
    SpreadsheetControllerImpl spreadsheetController;
    @FXML
    private Button dynamicAnalysisButton;

    public void handleAnalysisButtonPress() {

        Map<String, CheckBox> cellCheckboxes = getNumericCellCheckboxes();
        ScrollPane scrollPane = createScrollPane(cellCheckboxes);
        VBox mainContent = createMainContent(scrollPane, cellCheckboxes);
        Dialog<List<String>> dialog = createCellSelectionDialog(cellCheckboxes, mainContent);
        dialog.initModality(Modality.APPLICATION_MODAL); // הוספנו את זה
        Optional<List<String>> result = dialog.showAndWait();
        result.ifPresent(this::openSliderSetupDialog);
    }

    private ScrollPane createScrollPane(Map<String, CheckBox> cellCheckboxes) {
        VBox cellListBox = new VBox(5);
        cellListBox.setPadding(new Insets(10));
        cellCheckboxes.values().forEach(cellListBox.getChildren()::add);
        ScrollPane scrollPane = new ScrollPane(cellListBox);
        scrollPane.setFitToWidth(true);
        scrollPane.setPrefViewportHeight(300);
        return scrollPane;
    }

    private Map<String, CheckBox> getNumericCellCheckboxes() {
        Map<String, CheckBox> cellCheckboxes = new HashMap<>();
        for (Map.Entry<String, CellDto> entry : spreadsheetController.getCurrentSheet().getCells().entrySet()) {
            String cellId = entry.getKey();
            CellDto cellDto = entry.getValue();
            try {
                Double.parseDouble(cellDto.getOriginalValue());
                CheckBox cellCheckBox = new CheckBox(String.format("Cell %s: %s", cellId, cellDto.getOriginalValue()));
                cellCheckBox.setUserData(cellId);
                cellCheckboxes.put(cellId, cellCheckBox);
            } catch (NumberFormatException ignored) {
                // Skip non-numeric cells
            }
        }
        return cellCheckboxes;
    }

    private VBox createMainContent(ScrollPane scrollPane, Map<String, CheckBox> cellCheckboxes) {
        Button selectAllButton = createSelectAllButton(cellCheckboxes);
        Button deselectAllButton = createDeselectAllButton(cellCheckboxes);
        HBox selectionButtonBox = new HBox(10, selectAllButton, deselectAllButton);
        selectionButtonBox.setAlignment(Pos.CENTER);
        VBox mainContent = new VBox(10, scrollPane, selectionButtonBox);
        mainContent.setPadding(new Insets(20, 20, 0, 20));
        return mainContent;
    }

    private Button createSelectAllButton(Map<String, CheckBox> cellCheckboxes) {
        Button selectAllButton = new Button("Select All");
        selectAllButton.setOnAction(e -> cellCheckboxes.values().forEach(cb -> cb.setSelected(true)));
        selectAllButton.setPrefWidth(100);
        return selectAllButton;
    }

    private Button createDeselectAllButton(Map<String, CheckBox> cellCheckboxes) {
        Button deselectAllButton = new Button("Deselect All");
        deselectAllButton.setOnAction(e -> cellCheckboxes.values().forEach(cb -> cb.setSelected(false)));
        deselectAllButton.setPrefWidth(100);
        return deselectAllButton;
    }

    private Dialog<List<String>> createCellSelectionDialog(Map<String, CheckBox> cellCheckboxes, VBox mainContent) {
        Dialog<List<String>> dialog = new Dialog<>();
        dialog.setTitle("Select Cells for Dynamic Analysis");
        dialog.setHeaderText("Choose cells with numeric values:");
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        dialog.getDialogPane().setContent(mainContent);
        dialog.initModality(Modality.APPLICATION_MODAL); // Disable background interaction
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == ButtonType.OK) {
                return cellCheckboxes.values().stream()
                        .filter(CheckBox::isSelected)
                        .map(cb -> (String) cb.getUserData())
                        .collect(Collectors.toList());
            }
            return null;
        });
        return dialog;
    }

    public void openSliderSetupDialog(List<String> cellIds) {
        if (cellIds.isEmpty()) {
            showAlert("ERROR", "No cells selected for dynamic analysis");
            return;
        }
        VBox dialogContent = createSliderSetupContent();
        Dialog<ButtonType> setupDialog = createSliderSetupDialog(dialogContent);
        setupDialog.initModality(Modality.APPLICATION_MODAL); // הוספנו את זה
        setupDialog.setResultConverter(dialogButton -> {
            if (dialogButton == ButtonType.OK) {
                processSliderSetupDialog(cellIds, dialogContent);
            }
            return null;
        });
        setupDialog.showAndWait();
    }

    private VBox createSliderSetupContent() {
        TextField minValueField = new TextField("0");
        TextField maxValueField = new TextField("100");
        TextField stepSizeField = new TextField("1");
        minValueField.setPromptText("Enter min value");
        maxValueField.setPromptText("Enter max value");
        stepSizeField.setPromptText("Enter step size");
        VBox dialogContent = new VBox(10);
        dialogContent.getChildren().addAll(
                new Label("Min Value:"), minValueField,
                new Label("Max Value:"), maxValueField,
                new Label("Step Size:"), stepSizeField
        );
        return dialogContent;
    }

    private Dialog<ButtonType> createSliderSetupDialog(VBox dialogContent) {
        Dialog<ButtonType> setupDialog = new Dialog<>();
        setupDialog.setTitle("Slider Setup");
        setupDialog.getDialogPane().setContent(dialogContent);
        setupDialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        setupDialog.initModality(Modality.APPLICATION_MODAL); // Disable background interaction
        return setupDialog;
    }

    private void processSliderSetupDialog(List<String> cellIds, VBox dialogContent) {
        TextField minValueField = (TextField) dialogContent.getChildren().get(1);
        TextField maxValueField = (TextField) dialogContent.getChildren().get(3);
        TextField stepSizeField = (TextField) dialogContent.getChildren().get(5);
        try {
            double min = Double.parseDouble(minValueField.getText());
            double max = Double.parseDouble(maxValueField.getText());
            double step = Double.parseDouble(stepSizeField.getText());
            showMultiCellSliderDialog(cellIds, min, max, step);
        } catch (NumberFormatException e) {
            showAlert("ERROR", "Please enter valid numeric values for min, max, and step size");
        }
    }

    public void showMultiCellSliderDialog(List<String> cellIds, double min, double max, double step) {
        GridPane layout = createGridPane();
        Map<String, Slider> sliders = new HashMap<>();
        Map<String, String> originalValues = createSliders(cellIds, sliders, min, max, step);

        populateGridWithSliders(cellIds, sliders, originalValues, layout, min, max, step);

        Button okButton = createOkButton(cellIds, sliders, originalValues);
        HBox buttonBox = createButtonBox(okButton);
        layout.add(buttonBox, 0, cellIds.size(), 3, 1);

        VBox contentBox = createContentBox(layout);
        ScrollPane scrollPane = createScrollPane(contentBox, cellIds.size());

        Stage dialog = createDialog(scrollPane);
        dialog.initModality(Modality.APPLICATION_MODAL); // הוספנו את זה
        setDialogProperties(dialog, okButton, cellIds, sliders, originalValues);

        dialog.showAndWait(); // שינינו מ-show ל-showAndWait
        centerDialog(dialog);
    }

    private GridPane createGridPane() {
        GridPane layout = new GridPane();
        layout.setHgap(10);
        layout.setVgap(10);
        layout.setPadding(new Insets(20));
        return layout;
    }

    private void populateGridWithSliders(List<String> cellIds, Map<String, Slider> sliders, Map<String, String> originalValues, GridPane layout, double min, double max, double step) {
        for (int i = 0; i < cellIds.size(); i++) {
            String cellId = cellIds.get(i);
            CellDto cellDto = spreadsheetController.getCurrentSheet().getCells().get(cellId);
            String originalValue = cellDto.getOriginalValue();
            originalValues.put(cellId, originalValue);

            double initialValue = Double.parseDouble(originalValue);
            Slider slider = new Slider(min, max, initialValue);
            slider.setMajorTickUnit(step);
            slider.setMinorTickCount(0);
            slider.setPrefWidth(200);

            Label cellLabel = new Label("Cell " + cellId + ":");
            Label valueLabel = createValueLabel(initialValue);

            attachSliderListener(slider, step, valueLabel, cellId);

            sliders.put(cellId, slider);

            layout.add(cellLabel, 0, i);
            layout.add(slider, 1, i);
            layout.add(valueLabel, 2, i);
        }
    }

    private Label createValueLabel(double initialValue) {
        Label valueLabel = new Label(String.format("%.2f", initialValue));
        valueLabel.setMinWidth(50);
        valueLabel.setAlignment(Pos.CENTER_RIGHT);
        return valueLabel;
    }

    private void attachSliderListener(Slider slider, double step, Label valueLabel, String cellId) {
        slider.valueProperty().addListener((observable, oldValue, newValue) -> {
            double roundedValue = Math.round(newValue.doubleValue() / step) * step;
            slider.setValue(roundedValue);
            valueLabel.setText(String.format("%.2f", roundedValue));

            String newValueStr = String.format("%.2f", roundedValue);
            sendDynamicAnalysisUpdateRequest(cellId, newValueStr, slider, spreadsheetController.getCurrentSheet());
        });
    }

    private Button createOkButton(List<String> cellIds, Map<String, Slider> sliders, Map<String, String> originalValues) {
        Button okButton = new Button("OK");
        okButton.setDefaultButton(true);
        okButton.setPrefWidth(100);
        okButton.setOnAction(e -> {
            for (String cellId : cellIds) {
                sendDynamicAnalysisUpdateRequest(cellId, originalValues.get(cellId), sliders.get(cellId), spreadsheetController.getCurrentSheet());
            }
            Stage stage = (Stage) okButton.getScene().getWindow();
            stage.close();
        });
        return okButton;
    }

    private HBox createButtonBox(Button okButton) {
        HBox buttonBox = new HBox(okButton);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setPadding(new Insets(20, 0, 0, 0));
        return buttonBox;
    }

    private VBox createContentBox(GridPane layout) {
        VBox contentBox = new VBox(layout);
        contentBox.setPadding(new Insets(0, 10, 0, 10));
        return contentBox;
    }

    private ScrollPane createScrollPane(VBox contentBox, int cellCount) {
        ScrollPane scrollPane = new ScrollPane(contentBox);
        scrollPane.setFitToWidth(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(cellCount > 10 ? ScrollPane.ScrollBarPolicy.AS_NEEDED : ScrollPane.ScrollBarPolicy.NEVER);
        return scrollPane;
    }

    private Stage createDialog(ScrollPane scrollPane) {
        StackPane root = new StackPane(scrollPane);
        root.setPrefWidth(400);

        Scene scene = new Scene(root);
        Stage dialog = new Stage();
        dialog.setTitle("Update Cell Values");
        dialog.setScene(scene);
        dialog.setResizable(false);
        dialog.sizeToScene();

        return dialog;
    }

    private void setDialogProperties(Stage dialog, Button okButton, List<String> cellIds, Map<String, Slider> sliders, Map<String, String> originalValues) {
        dialog.setOnCloseRequest(e -> {
            for (String cellId : cellIds) {
                sendDynamicAnalysisUpdateRequest(cellId, originalValues.get(cellId), sliders.get(cellId), spreadsheetController.getCurrentSheet());
            }
            spreadsheetController.displayOriginalSheet(false);
        });

        double maxHeight = Math.min(Screen.getPrimary().getVisualBounds().getHeight() * 0.8, 600);
        if (dialog.getHeight() > maxHeight) {
            dialog.setHeight(maxHeight);
        }
    }

    private void centerDialog(Stage dialog) {
        dialog.setX((Screen.getPrimary().getVisualBounds().getWidth() - dialog.getWidth()) / 2);
        dialog.setY((Screen.getPrimary().getVisualBounds().getHeight() - dialog.getHeight()) / 2);
    }

    private Map<String, String> createSliders(List<String> cellIds, Map<String, Slider> sliders, double min, double max, double step) {
        Map<String, String> originalValues = new HashMap<>();
        for (String cellId : cellIds) {
            CellDto cellDto = spreadsheetController.getCurrentSheet().getCells().get(cellId);
            String originalValue = cellDto.getOriginalValue();
            originalValues.put(cellId, originalValue);

            Slider slider = createSlider(min, max, step, originalValue);
            sliders.put(cellId, slider);
        }
        return originalValues;
    }

    private Slider createSlider(double min, double max, double step, String originalValue) {
        double initialValue = Double.parseDouble(originalValue);
        Slider slider = new Slider(min, max, initialValue);
        slider.setMajorTickUnit(step);
        slider.setMinorTickCount(0);
        slider.setPrefWidth(200);
        return slider;
    }

    public void sendDynamicAnalysisUpdateRequest(String cellId, String cellOriginalValue, Slider slider, SheetDto sheetToUpdate) {
        Map<String, String> cellUpdateData = new HashMap<>();
        cellUpdateData.put("cellId", cellId);
        cellUpdateData.put("cellOriginalValue", cellOriginalValue);
        cellUpdateData.put("sheetName", sheetToUpdate.getSheetName());
        cellUpdateData.put("version",spreadsheetController.getCurrentSheet().getVersion().toString());

        Gson gson = new Gson();
        String cellUpdateDataJson = gson.toJson(cellUpdateData);

        RequestBody requestBody = RequestBody.create(cellUpdateDataJson, MediaType.parse("application/json"));

        Request request = new Request.Builder()
                .url(Constants.DYNAMIC_ANALYSIS_UPDATE)
                .post(requestBody)
                .build();

        HttpClientUtil.runAsync(request, new Callback() {
            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseBody = response.body().string();
                    Platform.runLater(() -> {
                        SheetDto updatedSheet = extractSheetFromResponseBody(responseBody);
                        spreadsheetController.updateAllCells(updatedSheet.getCells());
                    });
                } else {
                    Platform.runLater(() -> {
                        showAlert("Error", "Failed to update cell: " + response.message());
                        slider.setDisable(false);
                    });
                }
            }

            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Platform.runLater(() ->
                        showAlert("Error", "Error: " + e.getMessage())
                );
            }
        });
    }

    public void setSpreadsheetController(SpreadsheetControllerImpl spreadsheetController) {
        this.spreadsheetController = spreadsheetController;
    }

    @Override
    public void enableDynamicAnalysis() {
        dynamicAnalysisButton.setDisable(false);
    }

    @Override
    public void disableDynamicAnalysis() {
        dynamicAnalysisButton.setDisable(true);
    }
}
