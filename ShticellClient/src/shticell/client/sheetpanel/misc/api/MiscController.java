package shticell.client.sheetpanel.misc.api;

import javafx.scene.Scene;
import shticell.client.sheetpanel.skinmanager.SkinManager;

public interface MiscController {
    void applySkin(String skinFileName);
    boolean areAnimationsEnabled();
    void setSkinManager(SkinManager skinManager);
    void setScene(Scene scene);
    }
