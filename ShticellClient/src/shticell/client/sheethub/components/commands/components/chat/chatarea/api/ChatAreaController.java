package shticell.client.sheethub.components.commands.components.chat.chatarea.api;

import javafx.beans.property.BooleanProperty;
import shticell.client.sheethub.components.commands.components.chat.api.HttpStatusUpdate;

import java.io.Closeable;

public interface ChatAreaController extends Closeable {
    BooleanProperty autoUpdatesProperty();
    void sendButtonClicked(javafx.event.ActionEvent event);
    void setHttpStatusUpdate(HttpStatusUpdate httpStatusUpdate);
    void startListRefresher();
}
