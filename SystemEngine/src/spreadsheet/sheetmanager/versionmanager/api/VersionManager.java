package spreadsheet.sheetmanager.versionmanager.api;

import spreadsheet.api.SpreadSheet;

public interface VersionManager {
    SpreadSheet getSheetByVersion(int version);
    void updateSheetVersion(SpreadSheet sheet);
    int getLatestVersion();
}

