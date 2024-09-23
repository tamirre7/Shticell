package misc.api;

public interface MiscController {
    void handleSaveState();
    void handleLoadState();
    void applySkin(String skinFileName);
    void disableEditing();
    void enableEditing();
    boolean areAnimationsEnabled();

    }
