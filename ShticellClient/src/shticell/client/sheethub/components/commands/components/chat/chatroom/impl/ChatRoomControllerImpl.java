package shticell.client.sheethub.components.commands.components.chat.chatroom.impl;

import javafx.fxml.FXML;
import javafx.scene.layout.VBox;
import shticell.client.sheethub.components.commands.components.chat.api.HttpStatusUpdate;
import shticell.client.sheethub.components.commands.components.chat.chatarea.api.ChatAreaController;
import shticell.client.sheethub.components.commands.components.chat.chatcommands.api.ChatCommandsController;
import shticell.client.sheethub.components.commands.components.chat.chatroom.api.ChatRoomController;
import shticell.client.sheethub.components.commands.components.chat.users.api.UsersListController;
import shticell.client.sheethub.components.commands.components.controller.api.CommandsMenuController;

import java.io.IOException;

public class ChatRoomControllerImpl implements ChatRoomController, HttpStatusUpdate {
    @FXML private VBox usersListComponent;            // UI component for displaying users list
    @FXML private UsersListController usersListComponentController;  // Controller for users list
    @FXML private VBox actionCommandsComponent;       // UI component for chat commands
    @FXML private ChatCommandsController chatCommandsComponentController;  // Controller for chat commands
    @FXML private VBox chatAreaComponent;            // UI component for chat messages
    @FXML private ChatAreaController chatAreaComponentController;    // Controller for chat area
    private CommandsMenuController commandsMenuComponent;            // Navigation controller

    // Initializes the chat room by setting up component relationships and bindings
    @FXML
    public void initialize() {
        usersListComponentController.setHttpStatusUpdate(this);
        chatAreaComponentController.setHttpStatusUpdate(this);

        chatAreaComponentController.autoUpdatesProperty().bind(chatCommandsComponentController.autoUpdatesProperty());
        usersListComponentController.autoUpdatesProperty().bind(chatCommandsComponentController.autoUpdatesProperty());
        chatCommandsComponentController.setChatRoomController(this);
    }

    // Activates the chat room by starting user list and chat area refreshers
    @Override
    public void setActive() {
        usersListComponentController.startListRefresher();
        chatAreaComponentController.startListRefresher();
    }

    // Deactivates the chat room by closing user list and chat area components
    @Override
    public void setInActive() {
        try {
            usersListComponentController.close();
            chatAreaComponentController.close();
        } catch (Exception ignored) {}
    }

    // Closes the chat room and returns to the hub
    @Override
    public void close() throws IOException {
        commandsMenuComponent.chatReturnToHub();
    }

    // Sets the commands menu component for navigation between views
    @Override
    public void setCommandsMenuComponent(CommandsMenuController commandsMenuComponent) {
        this.commandsMenuComponent = commandsMenuComponent;
    }

    // Updates the HTTP status line in the UI
    @Override
    public void updateHttpLine(String line) {
        System.out.println(line);
    }
}
