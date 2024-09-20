package shticellui.formulabuilder;

import command.api.Engine;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import shticellui.action.line.ActionLineController;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FormulaBuilderController {
    @FXML
    private TreeView<String> functionTreeView;
    @FXML
    private TextArea formulaEditor;
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

        addFunctionCategory(root, "Arithmetic", "ABS", "DIVIDE", "MINUS", "MOD", "PERCENT", "PLUS", "POW", "TIMES", "SUM");
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
        Pattern pattern = Pattern.compile("\\{[^{}]+\\}");
        Matcher matcher = pattern.matcher(formula);

        while (matcher.find()) {
            String subFormula = matcher.group();
            try {
                String result = engine.evaluateOriginalValue(subFormula);
                previews.append(subFormula).append(" = ").append(result).append("\n");
            } catch (Exception e) {
                previews.append(subFormula).append(" = Error: ").append(e.getMessage()).append("\n");
            }
        }

        subFormulaPreviews.setText(previews.toString());
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