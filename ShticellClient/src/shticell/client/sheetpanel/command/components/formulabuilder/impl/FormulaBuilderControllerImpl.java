package shticell.client.sheetpanel.command.components.formulabuilder.impl;

import com.google.gson.Gson;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.stage.Stage;
import javafx.util.Duration;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;
import shticell.client.sheetpanel.action.line.api.ActionLineController;
import shticell.client.sheetpanel.command.components.formulabuilder.api.FormulaBuilderController;
import shticell.client.sheetpanel.spreadsheet.api.SpreadsheetController;
import shticell.client.util.Constants;
import shticell.client.util.http.HttpClientUtil;

import java.io.IOException;
import java.util.*;

import static shticell.client.util.http.HttpClientUtil.showAlert;

public class FormulaBuilderControllerImpl implements FormulaBuilderController {
    @FXML
    private TreeView<String> functionTreeView;
    @FXML
    private TextField formulaEditor;
    @FXML
    private TextField formulaPreview;
    @FXML
    private TextArea subFormulaPreviews;
    @FXML
    private TextField resultPreview;

    private StringProperty finalResultProperty = new SimpleStringProperty();

    private ActionLineController actionLineController;
    private SpreadsheetController spreadsheetController;

    @FXML
    private void initialize() {
        resultPreview.textProperty().bind(finalResultProperty);
        setupFunctionTree();
        setupTreeViewListener();
        setupFormulaEditorListener();
    }

    private void setupFunctionTree() {
        TreeItem<String> root = new TreeItem<>("Functions");
        root.setExpanded(true);

        addFunctionCategory(root, "Arithmetic", "ABS", "DIVIDE", "MINUS", "MOD", "PERCENT", "PLUS", "POW", "TIMES");
        addFunctionCategory(root, "Ranges", "SUM", "AVERAGE");
        addFunctionCategory(root, "References", "REF");
        addFunctionCategory(root, "String", "CONCAT", "SUB");
        addFunctionCategory(root, "Boolean", "IF", "EQUAL", "BIGGER", "LESS", "NOT", "OR", "AND");

        functionTreeView.setRoot(root);
        functionTreeView.setShowRoot(false);
    }

    private void addFunctionCategory(TreeItem<String> parent, String category, String... functions) {
        TreeItem<String> categoryItem = new TreeItem<>(category);
        for (String function : functions) {
            categoryItem.getChildren().add(new TreeItem<>(function));
        }
        parent.getChildren().add(categoryItem);
    }

    private void setupTreeViewListener() {
        functionTreeView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && newValue.isLeaf()) {
                insertFunctionIntoEditor(newValue.getValue());
                PauseTransition pause = new PauseTransition(Duration.millis(200));
                pause.setOnFinished(event -> functionTreeView.getSelectionModel().clearSelection());
                pause.play();
            }
        });
    }
    private void insertFunctionIntoEditor(String functionName) {
        String template = "{" + functionName + ",}";
        int caretPosition = formulaEditor.getCaretPosition();
        String currentText = formulaEditor.getText();
        String newText = currentText.substring(0, caretPosition) + template + currentText.substring(caretPosition);
        formulaEditor.setText(newText);
        formulaEditor.positionCaret(caretPosition + template.length() - 1);
    }

    private void setupFormulaEditorListener() {
        formulaEditor.textProperty().addListener((observable, oldValue, newValue) -> {
            updateFormulaPreview();
            updateSubFormulaPreviews();
            updateResultPreview();
        });
    }

    private List<String> extractNestedFormulas(String formula) {
        List<String> formulas = new ArrayList<>();
        Stack<Integer> stack = new Stack<>();

        for (int i = 0; i < formula.length(); i++) {
            if (formula.charAt(i) == '{') {
                stack.push(i);
            } else if (formula.charAt(i) == '}') {
                if (!stack.isEmpty()) {
                    int start = stack.pop();
                    String subFormula = formula.substring(start, i + 1);
                    formulas.add(subFormula);
                }
            }
        }

        return formulas;
    }
    private void updateSubFormulaPreviews() {
        StringBuilder previews = new StringBuilder();
        String formula = formulaEditor.getText();
        List<String> subFormulas = extractNestedFormulas(formula);

        // Loop through each subFormula and send evaluation request
        for (String subFormula : subFormulas) {
            sendEvaluationRequestForSubFormulas(previews, subFormula);
        }
    }

    private void sendEvaluationRequestForSubFormulas(StringBuilder previews, String subFormula) {
        Map<String,String> formulaToEval = new HashMap<>();
        formulaToEval.put("formula", subFormula);
        formulaToEval.put("sheetName", spreadsheetController.getCurrentSheet().getSheetName());

        Gson gson = new Gson();
        String formulaToEvalJson = gson.toJson(formulaToEval);

        RequestBody requestBody = RequestBody.create(formulaToEvalJson, MediaType.parse("application/json"));
        Request request = new Request.Builder()
                .url(Constants.EVALUATE_ORIGINAL_VALUE)
                .post(requestBody)
                .build();

        HttpClientUtil.runAsync(request, new Callback() {
            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseBody = response.body().string();
                    Platform.runLater(() -> {
                        // Append each subFormula result when it's received
                        previews.append(subFormula).append(" = ").append(responseBody).append("\n");
                        // Update the TextArea with the new preview
                        subFormulaPreviews.setText(previews.toString());
                    });
                } else {
                    Platform.runLater(() -> {
                        previews.append(subFormula).append(" = ").append(response.message()).append("\n");
                        subFormulaPreviews.setText(previews.toString());
                    });
                }
            }

            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Platform.runLater(() -> {
                    previews.append(subFormula).append(" = Error: ").append(e.getMessage()).append("\n");
                    subFormulaPreviews.setText(previews.toString());
                });
            }
        });
    }

    private void updateResultPreview() {
        sendEvaluationRequestForFinalResult(formulaPreview.getText());

    }

    private void sendEvaluationRequestForFinalResult(String formula) {
        Map<String,String> formulaToEval = new HashMap<>();
        formulaToEval.put("formula", formula);
        formulaToEval.put("sheetName", spreadsheetController.getCurrentSheet().getSheetName());

        Gson gson = new Gson();
        String formulaToEvalJson = gson.toJson(formulaToEval);

        RequestBody requestBody = RequestBody.create(formulaToEvalJson,MediaType.parse("application/json"));

        Request request = new Request.Builder()
                .url(Constants.EVALUATE_ORIGINAL_VALUE)
                .post(requestBody)
                .build();

        HttpClientUtil.runAsync(request, new Callback() {
            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseBody = response.body().string();
                    Platform.runLater(() -> {
                      finalResultProperty.set(responseBody);
                    });
                } else {
                    Platform.runLater(() ->
                     finalResultProperty.set(response.message())
                    );
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

    @FXML
    public void applyFormula() {
        String formula = formulaEditor.getText();
        actionLineController.updateCellValue(formula);
        closeWindow();
    }

    @FXML
    public void cancelFormula() {
        closeWindow();
    }

    private void closeWindow() {
        Stage stage = (Stage) functionTreeView.getScene().getWindow();
        stage.close();
    }

    private void updateFormulaPreview() {
        formulaPreview.setText(formulaEditor.getText());
    }

    @Override
    public void setActionLineController(ActionLineController actionLineController) {this.actionLineController = actionLineController;}

    @Override
    public void setSpreadsheetController(SpreadsheetController spreadsheetController){this.spreadsheetController = spreadsheetController;}
}
