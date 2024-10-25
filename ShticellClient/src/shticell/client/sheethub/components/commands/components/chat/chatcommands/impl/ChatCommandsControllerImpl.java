package shticell.client.sheethub.components.commands.components.chat.chatcommands.impl;

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
    private final BooleanProperty autoUpdates;        // Property for auto-update toggle state
    @FXML private ToggleButton autoUpdatesButton;     // Button to toggle auto-updates
    ChatRoomController chatRoomController;            // Reference to chat room controller

    // Initializes the controller with default values
    public ChatCommandsControllerImpl() {
        autoUpdates = new SimpleBooleanProperty();
    }

    // Binds the auto-updates property to the toggle button state
    @FXML
    public void initialize() {
        autoUpdates.bind(autoUpdatesButton.selectedProperty());
    }

    // Returns the auto-updates property for binding
    @Override
    public ReadOnlyBooleanProperty autoUpdatesProperty() {
        return autoUpdates;
    }

    // Handles return button click by closing the chat room
    @Override
    @FXML
    public void returnClicked(ActionEvent event) throws IOException {
        chatRoomController.close();
    }

    // Sets the chat room controller reference for navigation
    @Override
    public void setChatRoomController(ChatRoomController chatRoomController) {
        this.chatRoomController = chatRoomController;
    }
}