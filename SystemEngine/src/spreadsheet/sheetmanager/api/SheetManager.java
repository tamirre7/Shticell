package spreadsheet.sheetmanager.api;

import spreadsheet.api.SpreadSheet;

public interface SheetManager {
    String getSheetName();
    SpreadSheet getSheetByVersion(int version);
    void updateSheetVersion(SpreadSheet sheet);
    int getLatestVersion();
    String getUploadedBy();
}
