package shticell.client.sheethub.components.login.api;

import javafx.event.ActionEvent;
import okhttp3.Call;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public interface LoginController {
    void loginButtonClicked(ActionEvent event);
    String getLoggedUserName();
    void quitButtonClicked(ActionEvent e);
   // void setChatAppMainController(ChatAppMainController chatAppMainController);



}
