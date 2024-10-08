package spreadsheet.sheetmanager.impl;

import spreadsheet.api.SpreadSheet;
import spreadsheet.sheetmanager.api.SheetManager;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class SheetManagerImpl implements SheetManager, Serializable {
    private String sheetName;
    private Map<Integer, SpreadSheet> sheetVersionMap = new HashMap();
    private int latestVersion = 0;
    private String uploadedBy;

    public SheetManagerImpl(String sheetName,String uploadedBy) {
        this.sheetName = sheetName;
        this.uploadedBy = uploadedBy;
    }
    @Override
    public String getUploadedBy(){return uploadedBy;}
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
