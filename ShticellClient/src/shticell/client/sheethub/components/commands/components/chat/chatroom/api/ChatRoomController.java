package shticell.client.sheethub.components.commands.components.chat.chatroom.api;

import shticell.client.sheethub.components.commands.components.controller.api.CommandsMenuController;

import java.io.Closeable;

public interface ChatRoomController extends Closeable {
    void setActive();
    void setInActive();
    void setCommandsMenuComponent(CommandsMenuController commandsMenuComponent);
}
