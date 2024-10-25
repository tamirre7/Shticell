package spreadsheet.sheetmanager.versionmanager.impl;

import spreadsheet.api.SpreadSheet;
import spreadsheet.sheetmanager.versionmanager.api.VersionManager;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

// Manages versions of a spreadsheet, allowing retrieval and updating of different versions.
public class VersionManagerImpl implements VersionManager, Serializable {

    // Map storing spreadsheet versions, where the key is the version number.
    private Map<Integer, SpreadSheet> sheetVersionMap = new HashMap<>();

    // The most recent version number of the spreadsheet.
    private int latestVersion = 0;

    // Retrieves the spreadsheet for a given version number.
    @Override
    public SpreadSheet getSheetByVersion(int version) {
        return sheetVersionMap.get(version);
    }

    // Updates to a new version of the spreadsheet and increments the latest version number.
    @Override
    public void updateSheetVersion(SpreadSheet sheet) {
        latestVersion += 1;
        sheetVersionMap.put(latestVersion, sheet);
    }

    // Returns the number of the latest version of the spreadsheet.
    @Override
    public int getLatestVersion() {
        return latestVersion;
    }
}
