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
import shticellui.spreadsheet.SpreadsheetDisplayController;
import shticellui.range.RangeController;

import java.io.IOException;

public class MyApp extends Application {

    private Engine engine;

    @Override
    public void start(Stage primaryStage) throws IOException {
        // Initialize the engine
        engine = new EngineImpl(); // Replace this with actual engine initialization

        // Load the FXML file
        FXMLLoader loader = new FXMLLoader(getClass().getResource("shticellApp.fxml"));

        // Create shared controllers
        SpreadsheetDisplayController spreadsheetDisplayController = new SpreadsheetDisplayController(engine);
        RangeController rangeController = new RangeController(engine);
        rangeController.setSpreadsheetDisplayController(spreadsheetDisplayController);

        // Set the controller factory to inject the engine and controllers
        loader.setControllerFactory(param -> {
            if (param == ActionLineController.class) {
                ActionLineController actionLineController = new ActionLineController();
                actionLineController.setEngine(engine);
                spreadsheetDisplayController.setActionLineController(actionLineController);
                actionLineController.setSpreadsheetDisplayController(spreadsheetDisplayController);
                return actionLineController;
            } else if (param == LoadFileController.class) {
                return new LoadFileController(engine, primaryStage, spreadsheetDisplayController);
            } else if (param == MiscController.class) {
                return new MiscController(engine, primaryStage);
            } else if (param == SpreadsheetDisplayController.class) {
                spreadsheetDisplayController.setRangeController(rangeController);
                return spreadsheetDisplayController;
            } else if (param == RangeController.class) {
                return rangeController;
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

        // Set the title and scene, then show the stage
        primaryStage.setTitle("Shticell Application");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}