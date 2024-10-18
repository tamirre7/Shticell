package shticell.client.sheetpanel.main;

import com.google.gson.Gson;
import dto.permission.PermissionInfoDto;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;
import shticell.client.sheethub.main.SheetHubMainController;
import shticell.client.sheetpanel.action.line.impl.ActionLineControllerImpl;
import shticell.client.sheetpanel.command.components.dynamicanalysis.impl.DynamicAnalysisControllerImpl;
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
import shticell.client.util.Constants;
import shticell.client.util.http.HttpClientUtil;

import java.io.IOException;

import static shticell.client.util.http.HttpClientUtil.showAlert;

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
    @FXML
    private DynamicAnalysisControllerImpl dynamicAnalysisComponentController;

    private Scene scene;

    private SkinManager skinManager;

    private SheetHubMainController sheetHubMainController;

    private EditingManager editingManager;

    @FXML
    public void initialize() {
        UISheetModel uiSheetModel = new UISheetModel();
        editingManager = new EditingManagerImpl(spreadsheetComponentController, rangeComponentController,sortAndFilterComponentController,actionLineComponentController);
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
        dynamicAnalysisComponentController.setSpreadsheetController(spreadsheetComponentController);
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
        editingManager.disableSheetViewEditing(false);
   }


    public void setViewMatchToPermission(){
        String sheetName = spreadsheetComponentController.getCurrentSheet().getSheetName();

        String finalUrl = HttpUrl
                .parse(Constants.USER_PERMISSON_FOR_SHEET)
                .newBuilder()
                .addQueryParameter("sheetName", sheetName)
                .build()
                .toString();

        HttpClientUtil.runAsync(finalUrl, new Callback() {
            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseBody = response.body().string();
                    Platform.runLater(() -> {
                        PermissionInfoDto permissionInfoDto = new Gson().fromJson(responseBody, PermissionInfoDto.class);
                        spreadsheetComponentController.setPermission(permissionInfoDto.getPermissionType());
                        editingManager.enableSheetViewEditing(permissionInfoDto.getPermissionType());
                    });
                } else {
                    Platform.runLater(() ->
                            showAlert("Error", "Failed to sort data: " + response.message())

                    );
                }
            }

            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Platform.runLater(() ->
                        showAlert("Error", "An error occurred while sorting: " + e.getMessage())
                );
            }
        });
    }

}
