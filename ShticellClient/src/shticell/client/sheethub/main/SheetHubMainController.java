package shticell.client.sheethub.main;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import shticell.client.sheethub.components.login.api.LoginController;
import shticell.client.sheethub.components.loadsheet.api.LoadSheetController;
import shticell.client.sheethub.components.available.sheets.api.AvailableSheetsController;
import shticell.client.util.Constants;

import java.io.IOException;

public class SheetHubMainController {

    private Stage primaryStage;
    private LoginController loginController;
    private LoadSheetController loadSheetController;
    private AvailableSheetsController availableSheetsController;

    public void initialize(Stage primaryStage) {
        this.primaryStage = primaryStage;
        loadLoginView();
    }

    private void loadLoginView() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(Constants.LOGIN_PAGE_FXML_RESOURCE_LOCATION));
            Parent loginView = loader.load();
            loginController = loader.getController();
            loginController.setSheetHubMainController(this);

            Scene scene = new Scene(loginView);
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void onLoginSuccess() {
        loadMainView();
    }

    private void loadMainView() {
        try {
            // Load the main layout
            FXMLLoader mainLoader = new FXMLLoader(getClass().getResource(Constants.SHEET_HUB_MAIN_PAGE_FXML_RESOURCE_LOCATION));
            Parent mainView = mainLoader.load();  // Assuming a BorderPane as the main layout

            // Load and initialize the LoadSheetController and its view
            FXMLLoader loadSheetLoader = new FXMLLoader(getClass().getResource(Constants.LOAD_SHEET_PAGE_FXML_RESOURCE_LOCATION));
            Parent loadSheetView = loadSheetLoader.load();  // Load the FXML file for LoadSheet
            loadSheetController = loadSheetLoader.getController();  // Now the controller is initialized
            loadSheetController.setLoginController(loginController);

            // Load and initialize the AvailableSheetsController and its view
            FXMLLoader availableSheetsLoader = new FXMLLoader(getClass().getResource(Constants.AVAILABLE_SHEETS_PAGE_RESOURCE_LOCATION));
            Parent availableSheetsView = availableSheetsLoader.load();  // Load the FXML file for AvailableSheets
            availableSheetsController = availableSheetsLoader.getController();  // Now the controller is initialized

            // Set up the connections between controllers
            loadSheetController.setAvailableSheetsController(availableSheetsController);

            // Create the scene and show the stage
            Scene scene = new Scene(mainView);
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}