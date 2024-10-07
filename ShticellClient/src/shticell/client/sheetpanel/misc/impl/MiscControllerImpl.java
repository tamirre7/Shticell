package shticell.client.sheetpanel.misc.impl;

import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import shticell.client.sheetpanel.misc.api.MiscController;
import shticell.client.sheetpanel.skinmanager.SkinManager;


public class MiscControllerImpl implements MiscController {

    @FXML
    private CheckBox animationsCheckBox;

    @FXML
    private ComboBox<String> skinComboBox;
    private SkinManager skinManager;

    private Scene scene;

    @Override
    public void setSkinManager(SkinManager skinManager) {this.skinManager = skinManager;}
    @Override
    public void setScene(Scene scene) {this.scene = scene;}

    @FXML
    private void initialize() {
        animationsCheckBox.setSelected(true);

        // Add skin options
        skinComboBox.getItems().addAll("Default", "Dark", "Colorful");
        skinComboBox.setValue("Default"); // Set default skin

        skinComboBox.setOnAction(event -> {
            String selectedSkin = skinComboBox.getValue();
            if (selectedSkin != null) {
                applySkin(selectedSkin);
            }
        });

    }
    @Override
    public boolean areAnimationsEnabled() {
        return animationsCheckBox.isSelected();
    }


    @Override
    public void applySkin(String skinFileName) {
        skinManager.applySkin(scene, skinFileName);
    }

}
