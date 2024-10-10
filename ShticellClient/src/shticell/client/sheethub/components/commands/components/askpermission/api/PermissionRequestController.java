package shticell.client.sheethub.components.commands.components.askpermission.api;

import javafx.event.ActionEvent;
import shticell.client.sheethub.components.commands.components.controller.api.CommandsMenuController;

public interface PermissionRequestController {
    void handleSubmit(ActionEvent event);

    void handleCancel(ActionEvent event);

    void setCommandsMenuController(CommandsMenuController commandsMenuController);
}
