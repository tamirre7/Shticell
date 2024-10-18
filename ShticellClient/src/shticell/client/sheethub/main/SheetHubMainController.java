package shticell.client.sheethub.main;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import shticell.client.sheethub.components.available.sheets.impl.AvailableSheetsControllerImpl;
import shticell.client.sheethub.components.commands.components.controller.impl.CommandsMenuControllerImpl;
import shticell.client.sheethub.components.loadsheet.impl.LoadSheetControllerImpl;
import shticell.client.sheethub.components.login.api.LoginController;
import shticell.client.sheethub.components.permission.table.api.PermissionTableController;
import shticell.client.sheethub.components.permission.table.impl.PermissionTableControllerImpl;
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

    @FXML
    private PermissionTableControllerImpl permissionTableComponentController;

    private SheetViewMainController sheetViewMainController;

    private ScrollPane sheetViewMainPane;

    private Scene scene;

    private LoginController loginController;

    private Stage permissionPopupStage;
    private Stage chatPopupStage;

    @FXML
    public void initialize() {
        commandsMenuComponentController.setMainController(this);
        availableSheetsComponentController.setPermissionTableController(permissionTableComponentController);
        commandsMenuComponentController.setPermissionTableController(permissionTableComponentController);
        commandsMenuComponentController.setAvailableSheetsControllerTableController(availableSheetsComponentController);
        startUpLoginPage();
    }


    private void startUpLoginPage() {
        try {
            // Load the login page
            FXMLLoader loader = new FXMLLoader(getClass().getResource(Constants.LOGIN_PAGE_FXML_RESOURCE_LOCATION));
            loginComponent = loader.load();
            loginController = loader.getController();
            loadSheetComponentController.setLoginController(loginController);
            commandsMenuComponentController.setLoginController(loginController);
            loginController.setSheetHubMainController(this);
            setMainPanelTo(loginComponent);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setMainPanelTo(Parent pane) {
        mainPanel.setContent(pane);

    }

    public void switchToHubPage() {
        loadSheetComponentController.setGreetingLabel();
        commandsMenuComponentController.refreshList();
        availableSheetsComponentController.startTableRefresher();
        commandsMenuComponentController.activatePermissionRefresher();
        scene.getStylesheets().clear();
        scene.getStylesheets().add(getClass().getResource("sheet-hub-styles.css").toExternalForm());
        setMainPanelTo(sheetHubComponent);

        // Set window size for the Hub page
        Stage stage = (Stage) scene.getWindow();
        stage.setWidth(1000);
        stage.setHeight(600);
        stage.centerOnScreen();  // Centers the stage on the screen
    }

    public void switchToLoginPage(){
        setMainPanelTo(loginComponent);
        Stage stage = (Stage) scene.getWindow();
        stage.setWidth(330);
        stage.setHeight(270);
        stage.centerOnScreen();  // Centers the stage on the screen
    }

    public void switchToSheetViewPage() {
        availableSheetsComponentController.stopTableRefresher();
        commandsMenuComponentController.deactivatePermissionRefresher();
        commandsMenuComponentController.deActivateChatRefreshers();
        sheetViewMainController.initSheet(scene, loginController.getLoggedUserName());
        setMainPanelTo(sheetViewMainPane);
        sheetViewMainController.setViewMatchToPermission();

        // Set window size for the Sheet view page
        Stage stage = (Stage) scene.getWindow();
        stage.setWidth(1200);
        stage.setHeight(800);
        stage.centerOnScreen();  // Centers the stage on the screen
    }
    public void showPermissionRequestPopup(Parent pane) {
        commandsMenuComponentController.activatePermissionRefresher();

        permissionPopupStage = new Stage();
        permissionPopupStage.initModality(Modality.APPLICATION_MODAL);
        permissionPopupStage.initOwner(scene.getWindow());

        Scene popupScene = new Scene(pane);
        permissionPopupStage.setScene(popupScene);
        permissionPopupStage.setTitle("Permission Request");
        permissionPopupStage.setWidth(500);
        permissionPopupStage.setHeight(400);
        permissionPopupStage.centerOnScreen();
        permissionPopupStage.showAndWait();
    }

    public void showPermissionResponsePopup(Parent pane) {
        commandsMenuComponentController.activatePermissionRefresher();

        permissionPopupStage = new Stage();
        permissionPopupStage.initModality(Modality.APPLICATION_MODAL);
        permissionPopupStage.initOwner(scene.getWindow());

        Scene popupScene = new Scene(pane);
        permissionPopupStage.setScene(popupScene);
        permissionPopupStage.setTitle("Permission Response");
        permissionPopupStage.setWidth(700);
        permissionPopupStage.setHeight(550);
        permissionPopupStage.centerOnScreen();
        permissionPopupStage.showAndWait();
    }
    public void showChatPopup(Parent pane) {
        commandsMenuComponentController.activateChatRefreshers();

        chatPopupStage = new Stage();
        chatPopupStage.initModality(Modality.APPLICATION_MODAL);
        chatPopupStage.initOwner(scene.getWindow());

        Scene popupScene = new Scene(pane);
        chatPopupStage.setScene(popupScene);
        chatPopupStage.setTitle("Chat");
        chatPopupStage.setWidth(700);
        chatPopupStage.setHeight(550);
        chatPopupStage.centerOnScreen();
        chatPopupStage.showAndWait();
    }

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

    public void closePermissionPopup(){
        availableSheetsComponentController.startTableRefresher();
        commandsMenuComponentController.activatePermissionRefresher();
        permissionPopupStage.close();
    }

}
