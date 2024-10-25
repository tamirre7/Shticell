package shticell.client.sheethub.components.login.api;

import javafx.event.ActionEvent;
import shticell.client.sheethub.main.SheetHubMainController;

public interface LoginController {
    // Handles the action when the login button is clicked.
    void loginButtonClicked(ActionEvent event);

    // Returns the username of the logged-in user.
    String getLoggedUserName();

    // Handles the action when the quit button is clicked.
    void quitButtonClicked(ActionEvent e);

    // Sets the main controller for the SheetHub application.
    void setSheetHubMainController(SheetHubMainController sheetHubMainController);
}
