package shticell.client.sheetpanel.misc.api;

import javafx.scene.Scene;
import shticell.client.sheetpanel.skinmanager.SkinManager;

public interface MiscController {
    void applySkin(String skinFileName); // Applies the specified skin to the UI
    boolean areAnimationsEnabled(); // Checks if animations are enabled
    void setSkinManager(SkinManager skinManager); // Sets the skin manager
    void setScene(Scene scene); // Sets the current scene
}
