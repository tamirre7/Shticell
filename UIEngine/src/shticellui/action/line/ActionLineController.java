package shticellui.action.line;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import command.api.Engine;
import dto.SheetDto;
import dto.CellDto;
import shticellui.spreadsheet.SpreadsheetDisplayController;

public class ActionLineController {

    @FXML
    private TextField cellidTF;
    @FXML
    private TextField originalvalueTF;
    @FXML
    private TextField lastmodverTF;
    @FXML
    private Button updatevalbtn;
    @FXML
    private ChoiceBox<String> versionSelector;

    private Engine engine;
    private SheetDto currentSheet;
    private SpreadsheetDisplayController spreadsheetDisplayController;

    public ActionLineController() {

    }
    public void setEngine(Engine engine) {this.engine=engine;}

    @FXML
    public void initialize() {
        // Initialize components, populate version selector if necessary
        updatevalbtn.setOnAction(event -> updateCellValue());

        versionSelector.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                loadSpreadsheetVersion(Integer.valueOf(newValue));
            }
        });
    }
    public void setSpreadsheetDisplayController(SpreadsheetDisplayController spreadsheetDisplayController) {this.spreadsheetDisplayController=spreadsheetDisplayController;}


    public void setCellData(CellDto cellDto, String cellId) {
        cellidTF.setText(cellId);
        originalvalueTF.setText(cellDto != null ? cellDto.getOriginalValue() : "");
        lastmodverTF.setText(cellDto.getLastModifiedVersion().toString());
    }

    public void setCurrentSheet(SheetDto sheet) {
        this.currentSheet = sheet;
    }

    private void updateCellValue() {
        String cellId = cellidTF.getText();
        String newValue = originalvalueTF.getText();

        if (cellId != null && !cellId.isEmpty() && newValue != null) {
            // Update cell in the engine
           currentSheet = engine.updateCell(cellId, newValue);

            spreadsheetDisplayController.displaySheet(currentSheet);
        }
    }


    private void loadSpreadsheetVersion(Integer version) {
        // Load the specified version of the spreadsheet
        currentSheet = engine.displaySheetByVersion(version);
        lastmodverTF.setText(version.toString());
    }

    public void populateVersionSelector(String[] availableVersions) {
        versionSelector.getItems().clear();
        versionSelector.getItems().addAll(availableVersions);
        versionSelector.getSelectionModel().selectFirst();
    }
}
