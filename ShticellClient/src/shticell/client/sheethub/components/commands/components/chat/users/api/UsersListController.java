package shticell.client.sheethub.components.commands.components.chat.users.api;

import javafx.beans.property.BooleanProperty;
import shticell.client.sheethub.components.commands.components.chat.api.HttpStatusUpdate;
import java.io.Closeable;

// Interface for managing the users list view component and its refresh behavior
public interface UsersListController extends Closeable {
    // Sets the HTTP status update handler
    void setHttpStatusUpdate(HttpStatusUpdate httpStatusUpdate);
    // Property to control automatic updates
    BooleanProperty autoUpdatesProperty();
    // Initiates the periodic list refresh mechanism
    void startListRefresher();
}