package shticell.client.sheethub.components.commands.components.chat.chatcommands.api;

import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.event.ActionEvent;
import shticell.client.sheethub.components.commands.components.chat.chatroom.api.ChatRoomController;

import java.io.IOException;

public interface ChatCommandsController {
    // Returns the property indicating if auto-updates are enabled
    ReadOnlyBooleanProperty autoUpdatesProperty();

    // Handles the return button click event
    void returnClicked(ActionEvent event) throws IOException;

    // Sets the chat room controller reference
    void setChatRoomController(ChatRoomController chatRoomController);
}
