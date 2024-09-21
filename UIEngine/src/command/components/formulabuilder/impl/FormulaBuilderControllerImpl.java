package command.components.formulabuilder.impl;

import action.line.api.ActionLineController;
import command.api.Engine;
import command.components.formulabuilder.api.FormulaBuilderController;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;


import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

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

    private ActionLineController actionLineController;
    private Engine engine;

    @FXML
    private void initialize() {
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
            }
        });
    }

    private void setupFormulaEditorListener() {
        formulaEditor.textProperty().addListener((observable, oldValue, newValue) -> {
            updateFormulaPreview();
            updateSubFormulaPreviews();
            updateResultPreview();
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

    private void updateFormulaPreview() {
        formulaPreview.setText(formulaEditor.getText());
    }

    private void updateSubFormulaPreviews() {
        StringBuilder previews = new StringBuilder();
        String formula = formulaEditor.getText();

        List<String> subFormulas = extractNestedFormulas(formula);

        for (String subFormula : subFormulas) {
            try {
                String result = engine.evaluateOriginalValue(subFormula);
                previews.append(subFormula).append(" = ").append(result).append("\n");
            } catch (Exception e) {
                previews.append(subFormula).append(" = Error: ").append(e.getMessage()).append("\n");
            }
        }

        subFormulaPreviews.setText(previews.toString());
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

    private void updateResultPreview() {
        try {
            String result = engine.evaluateOriginalValue(formulaEditor.getText());
            resultPreview.setText(result);
        } catch (Exception e) {
            resultPreview.setText("Error: " + e.getMessage());
        }
    }

    public void setup(ActionLineController actionLineController, Engine engine) {
        this.actionLineController = actionLineController;
        this.engine = engine;
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
}