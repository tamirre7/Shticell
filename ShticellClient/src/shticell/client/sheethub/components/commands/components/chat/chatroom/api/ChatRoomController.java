package shticell.client.sheethub.components.commands.components.chat.chatroom.api;

import shticell.client.sheethub.components.commands.components.controller.api.CommandsMenuController;
import java.io.Closeable;

public interface ChatRoomController extends Closeable {
    // Activates the chat room and starts related services
    void setActive();

    // Deactivates the chat room and stops related services
    void setInActive();

    // Sets the commands menu component for navigation control
    void setCommandsMenuComponent(CommandsMenuController commandsMenuComponent);
}