package shticell.client.sheetpanel.command.components.formulabuilder.impl;

import javafx.animation.PauseTransition;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.stage.Stage;
import javafx.util.Duration;
import shticell.client.sheetpanel.action.line.api.ActionLineController;
import shticell.client.sheetpanel.command.components.formulabuilder.api.FormulaBuilderController;

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
          //  updateSubFormulaPreviews();
          //  updateResultPreview();
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

    private void sendTempEvaluateRequest(String formula) {

    }

    @FXML
    public void applyFormula() {
        String formula = formulaEditor.getText();
       // actionLineController.updateCellValue(formula);
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
}
