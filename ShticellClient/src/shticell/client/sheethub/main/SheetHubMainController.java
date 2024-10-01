package shticell.client.sheethub.main;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import shticell.client.sheethub.components.login.api.LoginController;
import shticell.client.sheethub.components.login.impl.LoginControllerImpl;
import shticell.client.sheethub.components.loadsheet.api.LoadSheetController;
import shticell.client.sheethub.components.loadsheet.impl.LoadSheetControllerImpl;
import shticell.client.sheethub.components.avaliable.sheets.api.AvailableSheetsController;
import shticell.client.sheethub.components.avaliable.sheets.impl.AvailableSheetsControllerImpl;
import shticell.client.util.Constants;

import java.io.IOException;

public class SheetHubMainController {

    @FXML
    private BorderPane mainBorderPane;

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
            ((LoginControllerImpl) loginController).setSheetHubMainController(this);
            mainBorderPane.setCenter(loginView);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void switchToMainView() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(Constants.SHEET_HUB_MAIN_PAGE_FXML_RESOURCE_LOCATION));
            Parent mainView = loader.load();

            // Load and initialize the LoadSheetController
            FXMLLoader loadSheetLoader = new FXMLLoader(getClass().getResource(Constants.LOAD_SHEET_PAGE_FXML_RESOURCE_LOCATION));
            Parent loadSheetView = loadSheetLoader.load();
            loadSheetController = loadSheetLoader.getController();
            ((LoadSheetControllerImpl) loadSheetController).setLoginController(loginController);

            // Load and initialize the AvailableSheetsController
            FXMLLoader availableSheetsLoader = new FXMLLoader(getClass().getResource(Constants.AVAILABLE_SHEETS_PAGE_RESOURCE_LOCATION));
            Parent availableSheetsView = availableSheetsLoader.load();
            availableSheetsController = availableSheetsLoader.getController();

            // Set up the connections between controllers
            ((LoadSheetControllerImpl) loadSheetController).setAvailableSheetsController(availableSheetsController);

            // Set up the main view
            BorderPane mainBorderPane = (BorderPane) mainView;
            mainBorderPane.setTop(loadSheetView);
            mainBorderPane.setCenter(availableSheetsView);

            Scene scene = new Scene(mainView);
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void showLoginView() {
        loadLoginView();
    }
}