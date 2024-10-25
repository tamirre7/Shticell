package shticell.client.sheetpanel.skinmanager;

import javafx.scene.Scene;

import static shticell.client.util.Constants.STYLES_FOLDER_LOCATION;

public class SkinManager {

    // Constructor for SkinManager
    public SkinManager() {}

    // Applies a skin to the given scene using the specified skin file name
    public void applySkin(Scene scene, String skinFileName) {
        // Get the CSS file as a URL
        String css = getClass().getResource(STYLES_FOLDER_LOCATION + skinFileName + ".css").toExternalForm();
        // Clear existing stylesheets from the scene
        scene.getStylesheets().clear();
        // Add the new stylesheet to the scene
        scene.getStylesheets().add(css);
    }
}
