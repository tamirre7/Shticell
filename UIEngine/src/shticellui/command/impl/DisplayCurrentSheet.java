package shticellui.command.impl;

import engine.api.Engine;
import dto.SheetDto;
import shticellui.command.api.Command;
import shticellui.print.api.Printable;
import shticellui.print.impl.SpreadSheetPrint;

public class DisplayCurrentSheet implements Command {
    @Override
    public void execute(Engine engine) {
        SheetDto sheet = engine.displayCurrentSpreadsheet();
        Printable sheetToPrint = new SpreadSheetPrint(sheet);
        sheetToPrint.print();
    }
}
