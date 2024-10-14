package spreadsheet.sheetmanager.versionmanager.impl;

import spreadsheet.api.SpreadSheet;
import spreadsheet.sheetmanager.versionmanager.api.VersionManager;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class VersionManagerImpl implements VersionManager, Serializable {
    private Map<Integer, SpreadSheet> sheetVersionMap = new HashMap<>();
    private int latestVersion = 0;

    @Override
    public SpreadSheet getSheetByVersion(int version) {
        return sheetVersionMap.get(version);
    }

    @Override
    public void updateSheetVersion(SpreadSheet sheet) {
        latestVersion += 1;
        sheetVersionMap.put(latestVersion, sheet);
    }

    @Override
    public int getLatestVersion() {
        return latestVersion;
    }
}