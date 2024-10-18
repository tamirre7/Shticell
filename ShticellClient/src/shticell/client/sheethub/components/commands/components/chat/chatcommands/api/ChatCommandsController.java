package shticell.client.sheethub.components.commands.components.chat.chatcommands.api;

import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.event.ActionEvent;
import shticell.client.sheethub.components.commands.components.chat.chatroom.api.ChatRoomController;

import java.io.IOException;

public interface ChatCommandsController {
    ReadOnlyBooleanProperty autoUpdatesProperty();
    void quitClicked(ActionEvent event) throws IOException;
    void setChatRoomController(ChatRoomController chatRoomController);
}
