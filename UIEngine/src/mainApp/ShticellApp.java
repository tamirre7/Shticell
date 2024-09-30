package mainapp;

import action.line.api.ActionLineController;
import action.line.impl.ActionLineControllerImpl;
import command.components.formulabuilder.FormulaBuilder;
import command.components.graphbuilder.builder.api.GraphBuilderController;
import command.components.graphbuilder.builder.impl.GraphBuilderControllerImpl;
import command.components.sortandfilter.api.SortAndFilterController;
import command.components.sortandfilter.impl.SortAndFilterControllerImpl;
import command.impl.EngineImpl;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import command.api.Engine;
import loadfilecomp.impl.LoadFileControllerImpl;
import misc.api.MiscController;
import misc.impl.MiscControllerImpl;
import range.api.RangeController;
import range.impl.RangeControllerImpl;
import skinmanager.SkinManager;
import spreadsheet.UISheetModel;
import spreadsheet.api.SpreadsheetController;
import spreadsheet.impl.SpreadsheetControllerImpl;

import java.io.IOException;

public class ShticellApp extends Application {

    private Engine engine;
    private SkinManager skinManager;
    FormulaBuilder formulaBuilder;

    @Override
    public void start(Stage primaryStage) throws IOException {
        // Initialize the engine and other componentss
        engine = new EngineImpl();
        skinManager = new SkinManager();
        formulaBuilder = new FormulaBuilder();
        formulaBuilder.setEngine(engine);
        UISheetModel uiSheetModel = new UISheetModel();

        SpreadsheetController spreadsheetController = new SpreadsheetControllerImpl(engine, uiSheetModel);
        RangeController rangeController = new RangeControllerImpl(engine);
        rangeController.setSpreadsheetDisplayController(spreadsheetController);
        rangeController.setUiSheetModel(uiSheetModel);

        ActionLineController actionLineController = new ActionLineControllerImpl();
        actionLineController.setEngine(engine);
        spreadsheetController.setActionLineController(actionLineController);
        spreadsheetController.setFormulaBuilder(formulaBuilder);
        actionLineController.setSpreadsheetDisplayController(spreadsheetController);
        formulaBuilder.setActionLineController(actionLineController);

        MiscController miscController = new MiscControllerImpl(engine, primaryStage, skinManager, spreadsheetController, actionLineController);

        SortAndFilterController sortAndFilterController = new SortAndFilterControllerImpl(engine, spreadsheetController);

        GraphBuilderController graphBuilderController = new GraphBuilderControllerImpl(spreadsheetController);

        // Load the main FXML file
        FXMLLoader mainLoader = new FXMLLoader(getClass().getResource("shticellApp.fxml"));

        mainLoader.setControllerFactory(param -> {
            if (param == ActionLineController.class || param == ActionLineControllerImpl.class) {
                return actionLineController;
            } else if (param == LoadFileControllerImpl.class) {
                LoadFileControllerImpl loadFileController = new LoadFileControllerImpl(engine, primaryStage, spreadsheetController);
                loadFileController.setActionLineController(actionLineController);
                return loadFileController;
            } else if (param == MiscController.class || param == MiscControllerImpl.class) {
                return miscController;
            } else if (param == SpreadsheetController.class || param == SpreadsheetControllerImpl.class) {
                spreadsheetController.setRangeController(rangeController);
                spreadsheetController.setMiscController(miscController);
                spreadsheetController.setSortAndFilterController(sortAndFilterController);
                return spreadsheetController;
            } else if (param == RangeController.class || param == RangeControllerImpl.class) {
                return rangeController;
            } else if (param == SortAndFilterController.class || param == SortAndFilterControllerImpl.class) {
                return sortAndFilterController;
            } else if (param == GraphBuilderController.class || param == GraphBuilderControllerImpl.class) {
                return graphBuilderController;
            } else {
                // For any other controller, let JavaFX create it
                try {
                    return param.getDeclaredConstructor().newInstance();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        });

        // Load the root element from the main FXML file
        Parent root = mainLoader.load();

        // Create the scene
        Scene scene = new Scene(root);
        skinManager.applySkin(scene, "Default");

        // Set the title and scene, then show the stage
        primaryStage.setTitle("Shticell Application");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
