package shticellui;

import command.impl.EngineImpl;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import command.api.Engine;
import shticellui.action.line.ActionLineController;
import shticellui.loadfilecomp.LoadFileController;
import shticellui.misc.MiscController;
import shticellui.skinmanager.SkinManager;
import shticellui.sortandfilter.SortAndFilterController;
import shticellui.spreadsheet.SpreadsheetDisplayController;
import shticellui.range.RangeController;

import java.io.IOException;

public class MyApp extends Application {

    private Engine engine;
    private SkinManager skinManager;

    @Override
    public void start(Stage primaryStage) throws IOException {
        // Initialize the engine
        engine = new EngineImpl(); // Replace this with actual engine initialization
        skinManager = new SkinManager();
        // Load the FXML file
        FXMLLoader loader = new FXMLLoader(getClass().getResource("shticellApp.fxml"));

        // Create shared controllers
        SpreadsheetDisplayController spreadsheetDisplayController = new SpreadsheetDisplayController(engine);
        RangeController rangeController = new RangeController(engine);
        rangeController.setSpreadsheetDisplayController(spreadsheetDisplayController);

        // Create the ActionLineController once and pass it to both the spreadsheet and load controllers
        ActionLineController actionLineController = new ActionLineController();
        actionLineController.setEngine(engine);
        spreadsheetDisplayController.setActionLineController(actionLineController);
        actionLineController.setSpreadsheetDisplayController(spreadsheetDisplayController);

        // Set the controller factory to inject the engine and controllers
        loader.setControllerFactory(param -> {
            if (param == ActionLineController.class) {
                return actionLineController;
            } else if (param == LoadFileController.class) {
                LoadFileController loadFileController = new LoadFileController(engine, primaryStage, spreadsheetDisplayController);
                loadFileController.setActionLineController(actionLineController); // Directly pass the ActionLineController
                return loadFileController;
            } else if (param == MiscController.class) {
                return new MiscController(engine, primaryStage,skinManager);
            } else if (param == SpreadsheetDisplayController.class) {
                spreadsheetDisplayController.setRangeController(rangeController);
                return spreadsheetDisplayController;
            } else if (param == RangeController.class) {
                return rangeController;
            } else if (param == SortAndFilterController.class) {
                return new SortAndFilterController(engine, spreadsheetDisplayController);
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
