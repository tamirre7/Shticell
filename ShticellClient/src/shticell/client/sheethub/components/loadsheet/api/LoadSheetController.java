package shticell.client.sheethub.components.loadsheet.api;

import javafx.event.ActionEvent;
import shticell.client.sheethub.components.login.api.LoginController;

import java.io.File;

public interface LoadSheetController {
    // Handles the action when the load button is clicked.
    void loadButtonClicked(ActionEvent event);

    // Uploads a file for loading a sheet.
    void uploadFile(File file);

    // Sets the login controller for managing user sessions.
    void setLoginController(LoginController loginSheetController);

    // Sets the greeting label to display a welcome message or user information.
    void setGreetingLabel();
}
