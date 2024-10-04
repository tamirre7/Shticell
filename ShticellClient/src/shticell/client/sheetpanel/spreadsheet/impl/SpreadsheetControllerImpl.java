package shticell.client.sheetpanel.spreadsheet.impl;

import dto.SheetDto;
import javafx.scene.control.Label;
import shticell.client.sheetpanel.spreadsheet.api.SpreadsheetController;

public class SpreadsheetControllerImpl implements SpreadsheetController {
    @Override
    public SheetDto getCurrentSheet() {
        return null;
    }

    @Override
    public void setCurrentSheet(SheetDto sheet) {

    }

    @Override
    public void handleCellClick(String cellid) {

    }

    @Override
    public void setupCellContextMenu(Label cellLable, String cellid) {

    }

    @Override
    public void recalculateGridDimensions() {

    }

    @Override
    public void setupHeaderContextMenu(Label cellLabel, int index, boolean isColumn) {

    }
}
