package shticell.client.main;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.stage.Stage;
import shticell.client.sheethub.components.login.api.LoginController;
import shticell.client.sheethub.main.SheetHubMainController;
import shticell.client.sheetpanel.main.SheetViewMainController;
import shticell.client.util.Constants;

import java.awt.*;
import java.io.IOException;

public class ShticellClientMain extends Application {

    @Override
    public void start(Stage primaryStage) {
        try {
            // Load the main page (Sheet Hub) and its controller once
            FXMLLoader loader = new FXMLLoader(getClass().getResource(Constants.SHEET_HUB_MAIN_PAGE_FXML_RESOURCE_LOCATION));
            Parent mainView = loader.load();
            SheetHubMainController sheetHubMainController = loader.getController();

            FXMLLoader sheetViewLoader = new FXMLLoader(getClass().getResource(Constants.SHEET_VIEW_MAIN_PAGE_FXML_RESOURCE_LOCATION));
            ScrollPane sheetViewPane = sheetViewLoader.load();
            SheetViewMainController sheetViewMainController = sheetViewLoader.getController();

            Scene scene = new Scene(mainView);

            sheetHubMainController.setupSheetView(sheetViewMainController,sheetViewPane,scene);

            primaryStage.setScene(scene);
            primaryStage.setTitle("Sheet Hub");
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static void main(String[] args) {
        launch(args);
    }

}
