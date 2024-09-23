package skinmanager;

import javafx.scene.Scene;

public class SkinManager {

    public SkinManager() {}

    public void applySkin(Scene scene, String skinFileName) {
        String css = getClass().getResource("/skinmanager/styles/" + skinFileName + ".css").toExternalForm();
        scene.getStylesheets().clear();
        scene.getStylesheets().add(css);
    }
}
