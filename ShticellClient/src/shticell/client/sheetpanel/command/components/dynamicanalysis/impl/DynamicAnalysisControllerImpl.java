package shticell.client.sheetpanel.command.components.dynamicanalysis.impl;

import com.google.gson.Gson;
import dto.CellDto;
import dto.SheetDto;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Screen;
import javafx.stage.Stage;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;
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

public class DynamicAnalysisControllerImpl {
    SpreadsheetControllerImpl spreadsheetController;

    public void handleAnalysisButtonPress() {
        VBox cellListBox = new VBox(5);
        cellListBox.setPadding(new Insets(10));

        Map<String, CheckBox> cellCheckboxes = new HashMap<>();

        for (Map.Entry<String, CellDto> entry : spreadsheetController.getCurrentSheet().getCells().entrySet()) {
            String cellId = entry.getKey();
            CellDto cellDto = entry.getValue();
            try {
                Double.parseDouble(cellDto.getOriginalValue());
                CheckBox cellCheckBox = new CheckBox(String.format("Cell %s: %s", cellId, cellDto.getOriginalValue()));
                cellCheckBox.setUserData(cellId);
                cellCheckboxes.put(cellId, cellCheckBox);
                cellListBox.getChildren().add(cellCheckBox);
            } catch (NumberFormatException e) {
                // Skip non-numeric cells
            }
        }

        ScrollPane scrollPane = new ScrollPane(cellListBox);
        scrollPane.setFitToWidth(true);
        scrollPane.setPrefViewportHeight(300);

        Dialog<List<String>> dialog = new Dialog<>();
        dialog.setTitle("Select Cells for Dynamic Analysis");
        dialog.setHeaderText("Choose cells with numeric values:");

        // Create "Select All" and "Deselect All" buttons
        Button selectAllButton = new Button("Select All");
        selectAllButton.setOnAction(e -> cellCheckboxes.values().forEach(cb -> cb.setSelected(true)));
        selectAllButton.setPrefWidth(100);

        Button deselectAllButton = new Button("Deselect All");
        deselectAllButton.setOnAction(e -> cellCheckboxes.values().forEach(cb -> cb.setSelected(false)));
        deselectAllButton.setPrefWidth(100);

        HBox selectionButtonBox = new HBox(10, selectAllButton, deselectAllButton);
        selectionButtonBox.setAlignment(Pos.CENTER);

        // Create main content
        VBox mainContent = new VBox(10, scrollPane, selectionButtonBox);
        mainContent.setPadding(new Insets(20, 20, 0, 20));

        // Set up OK and Cancel buttons
        ButtonType okButtonType = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelButtonType = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().addAll(okButtonType, cancelButtonType);

        // Create a custom HBox for centering the OK and Cancel buttons
        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER); // מרכז את התוכן
        buttonBox.getChildren().addAll(
                dialog.getDialogPane().lookupButton(okButtonType),
                dialog.getDialogPane().lookupButton(cancelButtonType)
        );

        // Add everything to the dialog
        VBox dialogContent = new VBox(20, mainContent, buttonBox);
        dialog.getDialogPane().setContent(dialogContent);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == okButtonType) {
                return cellCheckboxes.values().stream()
                        .filter(CheckBox::isSelected)
                        .map(cb -> (String) cb.getUserData())
                        .collect(Collectors.toList());
            }
            return null;
        });

        Optional<List<String>> result = dialog.showAndWait();
        result.ifPresent(this::openSliderSetupDialog);
    }

    public void openSliderSetupDialog(List<String> cellIds) {
        if (cellIds.isEmpty()) {
            showAlert("ERROR", "No cells selected for dynamic analysis");
            return;
        }

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

        Dialog<ButtonType> setupDialog = new Dialog<>();
        setupDialog.setTitle("Slider Setup");
        setupDialog.getDialogPane().setContent(dialogContent);
        setupDialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        setupDialog.setResultConverter(dialogButton -> {
            if (dialogButton == ButtonType.OK) {
                try {
                    double min = Double.parseDouble(minValueField.getText());
                    double max = Double.parseDouble(maxValueField.getText());
                    double step = Double.parseDouble(stepSizeField.getText());
                    showMultiCellSliderDialog(cellIds, min, max, step);
                } catch (NumberFormatException e) {
                    showAlert("ERROR", "Please enter valid numeric values for min, max, and step size");
                    return null;
                }
            }
            return null;
        });

        setupDialog.showAndWait();
    }

    public void showMultiCellSliderDialog(List<String> cellIds, double min, double max, double step) {
        GridPane layout = new GridPane();
        layout.setHgap(10);
        layout.setVgap(10);
        layout.setPadding(new Insets(20));

        Map<String, String> originalValues = new HashMap<>();
        Map<String, Slider> sliders = new HashMap<>();

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
            Label valueLabel = new Label(String.format("%.2f", initialValue));
            valueLabel.setMinWidth(50);
            valueLabel.setAlignment(Pos.CENTER_RIGHT);

            slider.valueProperty().addListener((observable, oldValue, newValue) -> {
                double roundedValue = Math.round(newValue.doubleValue() / step) * step;
                slider.setValue(roundedValue);
                valueLabel.setText(String.format("%.2f", roundedValue));

                String newValueStr = String.format("%.2f", roundedValue);
                sendDynamicAnalysisUpdateRequest(cellId, newValueStr, slider, spreadsheetController.getCurrentSheet());
            });

            sliders.put(cellId, slider);

            layout.add(cellLabel, 0, i);
            layout.add(slider, 1, i);
            layout.add(valueLabel, 2, i);
        }

        Button okButton = new Button("OK");
        okButton.setDefaultButton(true);
        okButton.setPrefWidth(100);
        okButton.setOnAction(e -> {
            for (String cellId : cellIds) {
                sendDynamicAnalysisUpdateRequest(cellId, originalValues.get(cellId), sliders.get(cellId), spreadsheetController.getCurrentSheet());
            }
           //spreadsheetController.editingManager.enableSheetViewEditing(permission);
            Stage stage = (Stage) okButton.getScene().getWindow();
            stage.close();
        });

        HBox buttonBox = new HBox(okButton);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setPadding(new Insets(20, 0, 0, 0));

        layout.add(buttonBox, 0, cellIds.size(), 3, 1);

        VBox contentBox = new VBox(layout);
        contentBox.setPadding(new Insets(0, 10, 0, 10));

        ScrollPane scrollPane = new ScrollPane(contentBox);
        scrollPane.setFitToWidth(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(cellIds.size() > 10 ? ScrollPane.ScrollBarPolicy.AS_NEEDED : ScrollPane.ScrollBarPolicy.NEVER); // הגדרת מדיניות הגלילה לפי מספר התאים

        StackPane root = new StackPane(scrollPane);
        root.setPrefWidth(400);

        Scene scene = new Scene(root);

        Stage dialog = new Stage();
        dialog.setTitle("Update Cell Values");
        dialog.setScene(scene);
        dialog.setResizable(false);

        dialog.setOnCloseRequest(e -> {
            for (String cellId : cellIds) {
                sendDynamicAnalysisUpdateRequest(cellId, originalValues.get(cellId), sliders.get(cellId), spreadsheetController.getCurrentSheet());
            }
            spreadsheetController.displayOriginalSheet(false);
        });

        // Set the dialog size to fit its content
        dialog.sizeToScene();

        // Limit the maximum height of the dialog
        double maxHeight = Math.min(Screen.getPrimary().getVisualBounds().getHeight() * 0.8, 600);
        if (dialog.getHeight() > maxHeight) {
            dialog.setHeight(maxHeight);
        }

        dialog.show();

        // Center the dialog on the screen
        dialog.setX((Screen.getPrimary().getVisualBounds().getWidth() - dialog.getWidth()) / 2);
        dialog.setY((Screen.getPrimary().getVisualBounds().getHeight() - dialog.getHeight()) / 2);
    }

    public void sendDynamicAnalysisUpdateRequest(String cellId, String cellOriginalValue, Slider slider, SheetDto sheetToUpdate) {
        Map<String, String> cellUpdateData = new HashMap<>();
        cellUpdateData.put("cellId", cellId);
        cellUpdateData.put("cellOriginalValue", cellOriginalValue);
        cellUpdateData.put("sheetName", sheetToUpdate.getSheetName());
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
}
