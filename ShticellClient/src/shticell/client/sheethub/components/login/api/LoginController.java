package shticell.client.sheethub.components.login.api;

import javafx.event.ActionEvent;
import shticell.client.sheethub.main.SheetHubMainController;

public interface LoginController {
    void loginButtonClicked(ActionEvent event);
    String getLoggedUserName();
    void quitButtonClicked(ActionEvent e);
   // void setChatAppMainController(ChatAppMainController chatAppMainController);
   void setSheetHubMainController(SheetHubMainController sheetHubMainController);


}
