package shticell.client.sheethub.components.loadsheet.api;

import javafx.event.ActionEvent;
import shticell.client.sheethub.components.available.sheets.api.AvailableSheetsController;
import shticell.client.sheethub.components.login.api.LoginController;

import java.io.File;

public interface LoadSheetController {
    void loadButtonClicked(ActionEvent event);
    void uploadFile(File file);
    void setLoginController(LoginController loginSheetController);
    void setAvailableSheetsController(AvailableSheetsController availableSheetsController);
    void setGreetingLabel();

}
