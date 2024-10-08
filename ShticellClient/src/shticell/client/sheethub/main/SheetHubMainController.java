package shticell.client.sheethub.main;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import shticell.client.sheethub.components.available.sheets.impl.AvailableSheetsControllerImpl;
import shticell.client.sheethub.components.commands.components.controller.impl.CommandsMenuControllerImpl;
import shticell.client.sheethub.components.loadsheet.impl.LoadSheetControllerImpl;
import shticell.client.sheethub.components.login.api.LoginController;
import shticell.client.sheetpanel.main.SheetViewMainController;
import shticell.client.util.Constants;

import java.io.IOException;

public class SheetHubMainController {
    @FXML
    private BorderPane sheetHubComponent;
    
    @FXML
    private ScrollPane mainPanel;
    
    private ScrollPane loginComponent;
    @FXML
    private LoadSheetControllerImpl loadSheetComponentController;
    @FXML
    private AvailableSheetsControllerImpl availableSheetsComponentController;
    @FXML
    private CommandsMenuControllerImpl commandsMenuComponentController;

    private SheetViewMainController sheetViewMainController;

    private ScrollPane sheetViewMainPane;

    private Scene scene;

    private LoginController loginController;


    @FXML
    public void initialize() {
        loadSheetComponentController.setAvailableSheetsController(availableSheetsComponentController);
        commandsMenuComponentController.setMainController(this);
        startUpLoginPage();
    }


    private void startUpLoginPage() {
        try {
            // Load the login page
            FXMLLoader loader = new FXMLLoader(getClass().getResource(Constants.LOGIN_PAGE_FXML_RESOURCE_LOCATION));
            loginComponent = loader.load();
            loginController = loader.getController();
            loadSheetComponentController.setLoginController(loginController);
            loginController.setSheetHubMainController(this);
            setMainPanelTo(loginComponent);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setMainPanelTo(Parent pane) {
        mainPanel.setContent(pane);

    }

    public void switchToHubPage(){
        loadSheetComponentController.setGreetingLabel();
        commandsMenuComponentController.refreshList();
        // availableSheetsComponentController.startTableRefresher();
        scene.getStylesheets().clear();
        scene.getStylesheets().add(getClass().getResource("sheet-hub-styles.css").toExternalForm());
        setMainPanelTo(sheetHubComponent);

    }
    public void switchToLoginPage(){
        setMainPanelTo(loginComponent);
    }
    public void switchToSheetViewPage(){
        availableSheetsComponentController.stopTableRefresher();
        sheetViewMainController.initSheet(scene,loginController.getLoggedUserName());
        setMainPanelTo(sheetViewMainPane);}

    public void setupSheetView(SheetViewMainController sheetViewMainController,ScrollPane sheetViewMainPane,Scene scene){
        this.sheetViewMainController = sheetViewMainController;
        sheetViewMainController.setSheetHubMainController(this);
        availableSheetsComponentController.setSpreadsheetController(sheetViewMainController.getSpreadsheetController());
        this.sheetViewMainPane = sheetViewMainPane;
        this.scene = scene;
    }

    public void logout()
    {
        availableSheetsComponentController.stopTableRefresher();
        commandsMenuComponentController.logoutButtonClicked();
    }

}
