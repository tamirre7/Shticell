package shticell.client.sheetpanel.main;

import javafx.fxml.FXML;
import javafx.scene.Scene;
import shticell.client.sheethub.main.SheetHubMainController;
import shticell.client.sheetpanel.action.line.impl.ActionLineControllerImpl;
import shticell.client.sheetpanel.command.components.formulabuilder.FormulaBuilder;
import shticell.client.sheetpanel.command.components.graphbuilder.builder.impl.GraphBuilderControllerImpl;
import shticell.client.sheetpanel.command.components.sortandfilter.impl.SortAndFilterControllerImpl;
import shticell.client.sheetpanel.editingmanager.api.EditingManager;
import shticell.client.sheetpanel.editingmanager.impl.EditingManagerImpl;
import shticell.client.sheetpanel.misc.impl.MiscControllerImpl;
import shticell.client.sheetpanel.range.impl.RangeControllerImpl;
import shticell.client.sheetpanel.skinmanager.SkinManager;
import shticell.client.sheetpanel.spreadsheet.UISheetModel;
import shticell.client.sheetpanel.spreadsheet.api.SpreadsheetController;
import shticell.client.sheetpanel.spreadsheet.impl.SpreadsheetControllerImpl;

public class SheetViewMainController {
    @FXML
    private ActionLineControllerImpl actionLineComponentController;
    @FXML
    private SortAndFilterControllerImpl sortAndFilterComponentController;
    @FXML
    private GraphBuilderControllerImpl graphBuilderComponentController;
    @FXML
    private RangeControllerImpl rangeComponentController;
    @FXML
    private MiscControllerImpl miscComponentController;
    @FXML
    private SpreadsheetControllerImpl spreadsheetComponentController;

    private Scene scene;

    private SkinManager skinManager;

    private SheetHubMainController sheetHubMainController;

    @FXML
    public void initialize() {
        UISheetModel uiSheetModel = new UISheetModel();
        EditingManager editingManager = new EditingManagerImpl(spreadsheetComponentController, rangeComponentController,sortAndFilterComponentController,actionLineComponentController);
        FormulaBuilder formulaBuilder = new FormulaBuilder();
        skinManager = new SkinManager();
        graphBuilderComponentController.setSpreadsheetController(spreadsheetComponentController);
        formulaBuilder.setActionLineController(actionLineComponentController);
        formulaBuilder.setSpreadsheetController(spreadsheetComponentController);
        actionLineComponentController.setSpreadsheetController(spreadsheetComponentController);
        sortAndFilterComponentController.setSpreadsheetController(spreadsheetComponentController);
        rangeComponentController.setSpreadsheetController(spreadsheetComponentController);
        rangeComponentController.setUiSheetModel(uiSheetModel);
        spreadsheetComponentController.setActionLineController(actionLineComponentController);
        spreadsheetComponentController.setRangeController(rangeComponentController);
        spreadsheetComponentController.setUiSheetModel(uiSheetModel);
        spreadsheetComponentController.setEditingManager(editingManager);
        spreadsheetComponentController.setFormulaBuilder(formulaBuilder);
        spreadsheetComponentController.setMiscController(miscComponentController);
        miscComponentController.setSkinManager(skinManager);
    }
    public SpreadsheetController getSpreadsheetController() {return spreadsheetComponentController;}

    public void setSheetHubMainController(SheetHubMainController sheetHubMainController) {
        this.sheetHubMainController = sheetHubMainController;
    }

    @FXML
    private void returnToHub(){sheetHubMainController.switchToHubPage();}

    public void initSheet(Scene scene, String loggedUserName) {
        actionLineComponentController.setUsernameLabel(loggedUserName);
        miscComponentController.setScene(scene);
        skinManager.applySkin(scene,"Default");
        actionLineComponentController.populateVersionSelector();
   }



}
