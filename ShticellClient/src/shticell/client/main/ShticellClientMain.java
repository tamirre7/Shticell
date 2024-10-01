package shticell.client.main;

import javafx.application.Application;
import javafx.stage.Stage;
import shticell.client.sheethub.main.SheetHubMainController;

public class ShticellClientMain extends Application {

    @Override
    public void start(Stage primaryStage) {
        SheetHubMainController mainController = new SheetHubMainController();
        mainController.initialize(primaryStage);
    }

    public static void main(String[] args) {
        launch(args);
    }
}