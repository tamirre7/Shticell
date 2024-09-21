package loadfilecomp.api;

import action.line.api.ActionLineController;

public interface LoadFileController {
    void setActionLineController(ActionLineController actionLineController);
    void handleLoadFile();
    void updateProgressBar();

    }
