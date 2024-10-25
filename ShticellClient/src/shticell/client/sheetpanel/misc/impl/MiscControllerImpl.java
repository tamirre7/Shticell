package shticell.client.sheetpanel.misc.impl;

import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.*;
import shticell.client.sheetpanel.misc.api.MiscController;
import shticell.client.sheetpanel.skinmanager.SkinManager;

public class MiscControllerImpl implements MiscController {

    @FXML
    private CheckBox animationsCheckBox; // Checkbox for enabling/disabling animations

    @FXML
    private ComboBox<String> skinComboBox; // ComboBox for selecting skin options
    private SkinManager skinManager; // Manages the skins

    private Scene scene; // Current scene reference

    @Override
    public void setSkinManager(SkinManager skinManager) {
        this.skinManager = skinManager; // Assign the skin manager
    }

    @Override
    public void setScene(Scene scene) {
        this.scene = scene; // Assign the current scene
    }

    @FXML
    private void initialize() {
        animationsCheckBox.setSelected(true); // Set animations to enabled by default

        // Add skin options
        skinComboBox.getItems().addAll("Default", "Dark", "Colorful"); // Populate skin options
        skinComboBox.setValue("Default"); // Set default skin

        skinComboBox.setOnAction(event -> { // Handle skin selection change
            String selectedSkin = skinComboBox.getValue(); // Get selected skin
            if (selectedSkin != null) {
                applySkin(selectedSkin); // Apply the selected skin
            }
        });

    }

    @Override
    public boolean areAnimationsEnabled() {
        return animationsCheckBox.isSelected(); // Return the animation state
    }

    @Override
    public void applySkin(String skinFileName) {
        skinManager.applySkin(scene, skinFileName); // Apply the skin to the scene
    }

}