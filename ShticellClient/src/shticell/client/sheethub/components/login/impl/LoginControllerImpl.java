package shticell.client.sheethub.components.login.impl;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;
import shticell.client.sheethub.components.login.api.LoginController;
import shticell.client.sheethub.main.SheetHubMainController;
import shticell.client.util.http.HttpClientUtil;
import shticell.client.util.Constants;
import java.io.IOException;

public class LoginControllerImpl implements LoginController {

    @FXML
    public TextField userNameTextField;

    @FXML
    public Label errorLabel;

    //  private ChatAppMainController chatAppMainController;
    private SheetHubMainController sheetHubMainController;
    private final StringProperty errorMessageProperty = new SimpleStringProperty();
    private String loggedUserName;

    @FXML
    public void initialize() {
        errorLabel.textProperty().bind(errorMessageProperty);
    }

    @FXML
    @Override
    public void loginButtonClicked(ActionEvent event) {

        String userName = userNameTextField.getText();
        if (userName.isEmpty()) {
            errorMessageProperty.set("You can't login with an empty user name.");
            return;
        }

        //noinspection ConstantConditions
        String finalUrl = HttpUrl
                .parse(Constants.LOGIN_PAGE)
                .newBuilder()
                .addQueryParameter("username", userName)
                .build()
                .toString();

        HttpClientUtil.runAsync(finalUrl, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Platform.runLater(() ->
                        errorMessageProperty.set("Something went wrong: " + e.getMessage())
                );
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.code() != 200) {
                    String responseBody = response.body().string();
                    Platform.runLater(() ->
                            errorMessageProperty.set(responseBody)
                    );
                } else {
                    Platform.runLater(() -> {
                        loggedUserName = userName;
                        //chatAppMainController.updateUserName(userName);
                       // chatAppMainController.switchToChatRoom();
                        sheetHubMainController.switchToHubPage();
                    });
                }
            }
        });
    }

    @Override
    public String getLoggedUserName() {return loggedUserName;}

    @FXML
    @Override
    public void quitButtonClicked(ActionEvent e) {
        Platform.exit();
    }

//    @Override
//    public void setChatAppMainController(ChatAppMainController chatAppMainController) {
//        this.chatAppMainController = chatAppMainController;
//    }
    @Override
public void setSheetHubMainController(SheetHubMainController sheetHubMainController) {
    this.sheetHubMainController = sheetHubMainController;
}
}

