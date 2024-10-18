package shticell.client.sheethub.components.commands.components.chat.users.api;

import javafx.beans.property.BooleanProperty;
import shticell.client.sheethub.components.commands.components.chat.api.HttpStatusUpdate;

import java.io.Closeable;

public interface UsersListController extends Closeable {
    void setHttpStatusUpdate(HttpStatusUpdate httpStatusUpdate);
    BooleanProperty autoUpdatesProperty();
    void startListRefresher();
}
