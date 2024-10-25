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
        //setting up controllers and initialize the login page
        commandsMenuComponentController.setMainController(this);
        availableSheetsComponentController.setPermissionTableController(permissionTableComponentController);
        commandsMenuComponentController.setPermissionTableController(permissionTableComponentController);
        commandsMenuComponentController.setAvailableSheetsControllerTableController(availableSheetsComponentController);
        startUpLoginPage(); // loads the login page
        availableSheetsComponentController.setLoginController(loginController);
    }

    // Loads the login page as the startup view
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

    // Sets the main panel's content
    private void setMainPanelTo(Parent pane) {
        mainPanel.setContent(pane);

    }

    // Switches to hub page
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
        stage.setWidth(Constants.HUB_PAGE_WIDTH);
        stage.setHeight(Constants.HUB_PAGE_HEIGHT);
        stage.centerOnScreen();  // Centers the stage on the screen
    }

    // Switches to login page
    public void switchToLoginPage(){
        setMainPanelTo(loginComponent);
        Stage stage = (Stage) scene.getWindow();
        stage.setWidth(Constants.LOGIN_PAGE_WIDTH);
        stage.setHeight(Constants.LOGIN_PAGE_HEIGHT);
        stage.centerOnScreen();  // Centers the stage on the screen
    }

    // Switches to the sheet view page
    public void switchToSheetViewPage() {
        availableSheetsComponentController.stopTableRefresher();
        commandsMenuComponentController.deactivatePermissionRefresher();
        commandsMenuComponentController.deActivateChatRefreshers();
        sheetViewMainController.initSheet(scene, loginController.getLoggedUserName());
        setMainPanelTo(sheetViewMainPane);
        sheetViewMainController.setViewMatchToPermission();

        // Set window size for the Sheet view page
        Stage stage = (Stage) scene.getWindow();
        stage.setWidth(Constants.SHEET_VIEW_PAGE_WIDTH);
        stage.setHeight(Constants.SHEET_VIEW_PAGE_HEIGHT);
        stage.centerOnScreen();  // Centers the stage on the screen
    }
    //shows a permission request popup
    public void showPermissionRequestPopup(Parent pane) {
        commandsMenuComponentController.activatePermissionRefresher();

        permissionPopupStage = new Stage();
        permissionPopupStage.initModality(Modality.APPLICATION_MODAL);
        permissionPopupStage.initOwner(scene.getWindow());

        Scene popupScene = new Scene(pane);
        permissionPopupStage.setScene(popupScene);
        permissionPopupStage.setTitle("Permission Request");
        permissionPopupStage.setWidth(Constants.PERMISSION_REQUEST_POPUP_WIDTH);
        permissionPopupStage.setHeight(Constants.PERMISSION_REQUEST_POPUP_HEIGHT);
        permissionPopupStage.centerOnScreen();
        permissionPopupStage.showAndWait();
    }
    // Shows a permission response popup
    public void showPermissionResponsePopup(Parent pane) {
        commandsMenuComponentController.activatePermissionRefresher();

        permissionPopupStage = new Stage();
        permissionPopupStage.initModality(Modality.APPLICATION_MODAL);
        permissionPopupStage.initOwner(scene.getWindow());

        Scene popupScene = new Scene(pane);
        permissionPopupStage.setScene(popupScene);
        permissionPopupStage.setTitle("Permission Response");
        permissionPopupStage.setWidth(Constants.PERMISSION_RESPONSE_POPUP_WIDTH);
        permissionPopupStage.setHeight(Constants.PERMISSION_RESPONSE_POPUP_HEIGHT);
        permissionPopupStage.centerOnScreen();
        permissionPopupStage.showAndWait();
    }
    // Show a chat popup
    public void showChatPopup(Parent pane) {
        commandsMenuComponentController.activateChatRefreshers();

        chatPopupStage = new Stage();
        chatPopupStage.initModality(Modality.APPLICATION_MODAL);
        chatPopupStage.initOwner(scene.getWindow());

        Scene popupScene = new Scene(pane);
        chatPopupStage.setScene(popupScene);
        chatPopupStage.setTitle("Chat");
        chatPopupStage.setWidth(Constants.CHAT_POPUP_WIDTH);
        chatPopupStage.setHeight(Constants.CHAT_POPUP_HEIGHT);
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

    public void closeChatPopup(){
        availableSheetsComponentController.startTableRefresher();
        commandsMenuComponentController.activateChatRefreshers();
        chatPopupStage.close();
    }

}
