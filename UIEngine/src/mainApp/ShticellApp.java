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
import spreadsheet.api.SpreadSheetController;
import spreadsheet.impl.SpreadsheetControllerImpl;

import java.io.IOException;

public class ShticellApp extends Application {

    private Engine engine;
    private SkinManager skinManager;
    FormulaBuilder formulaBuilder;

    @Override
    public void start(Stage primaryStage) throws IOException {
        // Initialize the engine
        engine = new EngineImpl(); // Replace this with actual engine initialization
        skinManager = new SkinManager();
        formulaBuilder = new FormulaBuilder();
        formulaBuilder.setEngine(engine);


        // Load the FXML file
        FXMLLoader loader = new FXMLLoader(getClass().getResource("shticellApp.fxml"));

        // Create shared controllers
        SpreadSheetController spreadsheetController = new SpreadsheetControllerImpl(engine);
        RangeController rangeController = new RangeControllerImpl(engine);
        rangeController.setSpreadsheetDisplayController(spreadsheetController);

        // Create the ActionLineController once and pass it to both the spreadsheet and load controllers
        ActionLineController actionLineController = new ActionLineControllerImpl();
        actionLineController.setEngine(engine);
        spreadsheetController.setActionLineController(actionLineController);
        spreadsheetController.setFormulaBuilder(formulaBuilder);
        actionLineController.setSpreadsheetDisplayController(spreadsheetController);
        formulaBuilder.setActionLineController(actionLineController);

        // Create MiscController and pass dependencies
        MiscController miscController = new MiscControllerImpl(engine, primaryStage, skinManager, spreadsheetController, actionLineController);

        // Create SortAndFilterController and pass dependencies
        SortAndFilterController sortAndFilterController = new SortAndFilterControllerImpl(engine, spreadsheetController);

        // Set the controller factory to inject the engine and controllers
        loader.setControllerFactory(param -> {
            if (param == ActionLineController.class) {
                return actionLineController;
            } else if (param == LoadFileControllerImpl.class) {
                LoadFileControllerImpl loadFileController = new LoadFileControllerImpl(engine, primaryStage, spreadsheetController);
                loadFileController.setActionLineController(actionLineController);
                return loadFileController;
            } else if (param == MiscController.class) {
                return miscController;
            } else if (param == SpreadsheetControllerImpl.class) {
                spreadsheetController.setRangeController(rangeController);
                spreadsheetController.setMiscController(miscController);
                spreadsheetController.setSortAndFilterController(sortAndFilterController); // Set SortAndFilterController
                return spreadsheetController;
            } else if (param == RangeController.class) {
                return rangeController;
            } else if (param == SortAndFilterController.class) {
                return sortAndFilterController;
            }else if (param == GraphBuilderController.class) {
                return new GraphBuilderControllerImpl(spreadsheetController);
            } else {
                try {
                    return param.getDeclaredConstructor().newInstance();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        });

        // Load the root element from the FXML file
        Parent root = loader.load();

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

