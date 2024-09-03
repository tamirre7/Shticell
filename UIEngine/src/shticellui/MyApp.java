package shticellui;

import command.api.Engine;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import shticellui.loadfilecomp.LoadFileController;

public class MyApp extends Application {

    private Engine engine; // Engine instance

    @Override
    public void start(Stage primaryStage) throws Exception {
        // Load the main application layout
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/shticellui/shticellApp.fxml"));
        loader.setControllerFactory(param -> new LoadFileController(engine, primaryStage));

        Parent root = loader.load();

        // Set the scene and show the stage
        primaryStage.setTitle("Shticell");
        primaryStage.setScene(new Scene(root, 600, 400));
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
