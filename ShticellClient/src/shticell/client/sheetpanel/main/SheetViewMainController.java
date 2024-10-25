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
    private ActionLineControllerImpl actionLineComponentController; // Controller for action line
    @FXML
    private SortAndFilterControllerImpl sortAndFilterComponentController; // Controller for sorting and filtering
    @FXML
    private GraphBuilderControllerImpl graphBuilderComponentController; // Controller for graph building
    @FXML
    private RangeControllerImpl rangeComponentController; // Controller for range operations
    @FXML
    private MiscControllerImpl miscComponentController; // Controller for miscellaneous functions
    @FXML
    private SpreadsheetControllerImpl spreadsheetComponentController; // Controller for spreadsheet operations
    @FXML
    private DynamicAnalysisControllerImpl dynamicAnalysisComponentController; // Controller for dynamic analysis

    private Scene scene; // Scene reference
    private SkinManager skinManager; // Manages UI skin
    private SheetHubMainController sheetHubMainController; // Main controller for the sheet hub
    private EditingManager editingManager; // Manages editing operations

    @FXML
    public void initialize() { // Initialization method
        UISheetModel uiSheetModel = new UISheetModel(); // Create UI sheet model
        FormulaBuilder formulaBuilder = new FormulaBuilder(); // Create formula builder
        skinManager = new SkinManager(); // Initialize skin manager

        // Set up controllers with spreadsheet controller
        graphBuilderComponentController.setSpreadsheetController(spreadsheetComponentController);
        formulaBuilder.setActionLineController(actionLineComponentController);
        formulaBuilder.setSpreadsheetController(spreadsheetComponentController);
        actionLineComponentController.setSpreadsheetController(spreadsheetComponentController);
        sortAndFilterComponentController.setSpreadsheetController(spreadsheetComponentController);
        rangeComponentController.setSpreadsheetController(spreadsheetComponentController);

        rangeComponentController.setUiSheetModel(uiSheetModel); // Link range component to UI model
        spreadsheetComponentController.setActionLineController(actionLineComponentController); // Link action line to spreadsheet
        spreadsheetComponentController.setRangeController(rangeComponentController); // Link range controller to spreadsheet
        spreadsheetComponentController.setUiSheetModel(uiSheetModel); // Assign UI model to spreadsheet
        spreadsheetComponentController.setFormulaBuilder(formulaBuilder); // Link formula builder to spreadsheet
        spreadsheetComponentController.setMiscController(miscComponentController); // Link miscellaneous controller to spreadsheet
        miscComponentController.setSkinManager(skinManager); // Set skin manager in misc controller
        dynamicAnalysisComponentController.setSpreadsheetController(spreadsheetComponentController); // Link dynamic analysis to spreadsheet

        // Initialize editing manager with various components
        editingManager = new EditingManagerImpl(spreadsheetComponentController, rangeComponentController,
                sortAndFilterComponentController, actionLineComponentController, dynamicAnalysisComponentController,
                graphBuilderComponentController);
        spreadsheetComponentController.setEditingManager(editingManager); // Set editing manager in spreadsheet controller
    }

    public SpreadsheetController getSpreadsheetController() {
        return spreadsheetComponentController; // Return the spreadsheet controller
    }

    public void setSheetHubMainController(SheetHubMainController sheetHubMainController) {
        this.sheetHubMainController = sheetHubMainController; // Set the sheet hub main controller
    }

    @FXML
    private void returnToHub() {
        sheetHubMainController.switchToHubPage(); // Navigate back to the hub page
    }

    public void initSheet(Scene scene, String loggedUserName) {
        actionLineComponentController.setUsernameLabel(loggedUserName); // Set username in action line
        miscComponentController.setScene(scene); // Set the scene in misc controller
        skinManager.applySkin(scene, "Default"); // Apply default skin to the scene
        actionLineComponentController.startVersionSelectorRefresher(); // Start refresher for version selector
    }

    public void setViewMatchToPermission() {
        String sheetName = spreadsheetComponentController.getCurrentSheet().getSheetName(); // Get current sheet name

        // Build URL for user permissions
        String finalUrl = HttpUrl
                .parse(Constants.USER_PERMISSON_FOR_SHEET)
                .newBuilder()
                .addQueryParameter("sheetName", sheetName)
                .build()
                .toString();

        // Run async HTTP call to get permissions
        HttpClientUtil.runAsync(finalUrl, new Callback() {
            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseBody = response.body().string(); // Get response body
                    Platform.runLater(() -> { // Update UI on JavaFX Application Thread
                        PermissionInfoDto permissionInfoDto = new Gson().fromJson(responseBody, PermissionInfoDto.class); // Deserialize response
                        spreadsheetComponentController.setPermission(permissionInfoDto.getPermissionType()); // Set permission in spreadsheet
                        editingManager.enableSheetViewEditing(permissionInfoDto.getPermissionType()); // Enable editing based on permissions
                    });
                } else {
                    Platform.runLater(() -> // Show alert on error
                            showAlert("Error", "Failed to sort data: " + response.message())
                    );
                }
            }

            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Platform.runLater(() -> // Show alert on failure
                        showAlert("Error", "An error occurred while sorting: " + e.getMessage())
                );
            }
        });
    }
}
