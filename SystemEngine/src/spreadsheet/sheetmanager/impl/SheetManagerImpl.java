package spreadsheet.sheetmanager.impl;

import spreadsheet.api.SpreadSheet;
import spreadsheet.sheetmanager.api.SheetManager;

import java.util.HashMap;
import java.util.Map;

public class SheetManagerImpl implements SheetManager {
    private String sheetName;
    private Map<Integer, SpreadSheet> sheetVersionMap = new HashMap();
    int latestVersion = 0;

    public SheetManagerImpl(String sheetName) {
        this.sheetName = sheetName;
    }
    @Override
    public String getSheetName() {return this.sheetName;}

    @Override
    public SpreadSheet getSheetByVersion(int version) {return sheetVersionMap.get(version);}

    @Override
    public void updateSheetVersion(SpreadSheet sheet) {
        latestVersion +=1;
        sheetVersionMap.put(latestVersion, sheet);
    }

    @Override
    public int getLatestVersion() {return latestVersion;}




}
