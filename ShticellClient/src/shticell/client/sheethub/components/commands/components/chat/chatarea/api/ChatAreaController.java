package shticell.client.sheethub.components.commands.components.chat.chatarea.api;

import javafx.beans.property.BooleanProperty;
import shticell.client.sheethub.components.commands.components.chat.api.HttpStatusUpdate;
import java.io.Closeable;

public interface ChatAreaController extends Closeable {
    // Property indicating if auto-updates are enabled
    BooleanProperty autoUpdatesProperty();
    // Handles the send button click event
    void sendButtonClicked(javafx.event.ActionEvent event);
    // Sets the HTTP status update callback
    void setHttpStatusUpdate(HttpStatusUpdate httpStatusUpdate);
    // Starts the chat refresh timer
    void startListRefresher();
}