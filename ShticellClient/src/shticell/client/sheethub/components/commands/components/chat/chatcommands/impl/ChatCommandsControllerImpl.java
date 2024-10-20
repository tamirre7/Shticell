package shticell.client.sheethub.components.commands.components.chat.chatcommands.impl;

import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ToggleButton;
import shticell.client.sheethub.components.commands.components.chat.chatcommands.api.ChatCommandsController;
import shticell.client.sheethub.components.commands.components.chat.chatroom.api.ChatRoomController;

import java.io.IOException;

public class ChatCommandsControllerImpl implements ChatCommandsController {
    private final BooleanProperty autoUpdates;
    @FXML
    private ToggleButton autoUpdatesButton;
    ChatRoomController chatRoomController;

    public ChatCommandsControllerImpl() {
        autoUpdates = new SimpleBooleanProperty();
    }

    @FXML
    public void initialize() {
        autoUpdates.bind(autoUpdatesButton.selectedProperty());
    }
    @Override
    public ReadOnlyBooleanProperty autoUpdatesProperty() {
        return autoUpdates;
    }
    @Override
    @FXML
    public void returnClicked(ActionEvent event) throws IOException {
        chatRoomController.close();
    }
    @Override
    public void setChatRoomController(ChatRoomController chatRoomController) {
        this.chatRoomController = chatRoomController;
    }

}