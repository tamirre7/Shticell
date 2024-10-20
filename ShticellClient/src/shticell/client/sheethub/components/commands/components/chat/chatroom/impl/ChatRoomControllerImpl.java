package shticell.client.sheethub.components.commands.components.chat.chatroom.impl;

import javafx.fxml.FXML;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import shticell.client.sheethub.components.commands.components.chat.api.HttpStatusUpdate;
import shticell.client.sheethub.components.commands.components.chat.chatarea.api.ChatAreaController;
import shticell.client.sheethub.components.commands.components.chat.chatcommands.api.ChatCommandsController;
import shticell.client.sheethub.components.commands.components.chat.chatroom.api.ChatRoomController;
import shticell.client.sheethub.components.commands.components.chat.users.api.UsersListController;
import shticell.client.sheethub.components.commands.components.controller.api.CommandsMenuController;

import java.io.Closeable;
import java.io.IOException;

public class ChatRoomControllerImpl implements ChatRoomController, HttpStatusUpdate {
    @FXML
    private VBox usersListComponent;
    @FXML private UsersListController usersListComponentController;
    @FXML private VBox actionCommandsComponent;
    @FXML private ChatCommandsController chatCommandsComponentController;
    @FXML private VBox chatAreaComponent;
    @FXML private ChatAreaController chatAreaComponentController;
    private CommandsMenuController commandsMenuComponent;

    @FXML
    public void initialize() {
        usersListComponentController.setHttpStatusUpdate(this);
        chatAreaComponentController.setHttpStatusUpdate(this);

        chatAreaComponentController.autoUpdatesProperty().bind(chatCommandsComponentController.autoUpdatesProperty());
        usersListComponentController.autoUpdatesProperty().bind(chatCommandsComponentController.autoUpdatesProperty());
        chatCommandsComponentController.setChatRoomController(this);
    }
    @Override
    public void setActive() {
        usersListComponentController.startListRefresher();
        chatAreaComponentController.startListRefresher();
    }
    @Override
    public void setInActive() {
        try {
            usersListComponentController.close();
            chatAreaComponentController.close();
        } catch (Exception ignored) {}
    }

    @Override
    public void close() throws IOException {
        commandsMenuComponent.returnToHub();
    }
    @Override
    public void setCommandsMenuComponent(CommandsMenuController commandsMenuComponent)
    {
        this.commandsMenuComponent = commandsMenuComponent;
    }

    @Override
    public void updateHttpLine(String line) {
        System.out.println(line);
    }
}
