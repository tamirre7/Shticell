package shticell.client.sheethub.components.commands.components.chat.users.impl;

import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import shticell.client.sheethub.components.commands.components.chat.api.HttpStatusUpdate;
import shticell.client.sheethub.components.commands.components.chat.users.UserListRefresher;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import static shticell.client.util.Constants.REFRESH_RATE;

public class UsersListControllerImpl implements shticell.client.sheethub.components.commands.components.chat.users.api.UsersListController {

    private Timer timer;
    private TimerTask listRefresher;
    private final BooleanProperty autoUpdate;
    private final IntegerProperty totalUsers;
    private HttpStatusUpdate httpStatusUpdate;

    @FXML
    private ListView<String> usersListView;
    @FXML private Label chatUsersLabel;

    public UsersListControllerImpl() {
        autoUpdate = new SimpleBooleanProperty();
        totalUsers = new SimpleIntegerProperty();
    }

    @FXML
    public void initialize() {
        chatUsersLabel.textProperty().bind(Bindings.concat("Chat Users: (", totalUsers.asString(), ")"));
    }
    @Override
    public void setHttpStatusUpdate(HttpStatusUpdate httpStatusUpdate) {
        this.httpStatusUpdate = httpStatusUpdate;

    }
    @Override
    public BooleanProperty autoUpdatesProperty() {
        return autoUpdate;
    }

    private void updateUsersList(List<String> usersNames) {
        Platform.runLater(() -> {
            ObservableList<String> items = usersListView.getItems();
            items.clear();
            items.addAll(usersNames);
            totalUsers.set(usersNames.size());
        });
    }
    @Override
    public void startListRefresher() {
        listRefresher = new UserListRefresher(
                autoUpdate,
                httpStatusUpdate::updateHttpLine,
                this::updateUsersList);
        timer = new Timer();
        timer.schedule(listRefresher, REFRESH_RATE, REFRESH_RATE);
    }

    @Override
    public void close() {
        usersListView.getItems().clear();
        totalUsers.set(0);
        if (listRefresher != null && timer != null) {
            listRefresher.cancel();
            timer.cancel();
        }
    }
}
