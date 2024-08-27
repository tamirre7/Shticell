package shticellui.command.impl;

import command.api.Engine;
import dto.SheetDto;
import shticellui.command.api.Command;
import shticellui.print.api.Printable;
import shticellui.print.impl.SpreadSheetPrint;

public class DisplayCurrentSheet implements Command {
    @Override
    public boolean execute(Engine engine) {
        try {
            engine.checkIfFileLoaded();
        } catch (IllegalStateException e) {
            System.out.println(e.getMessage());
            return false; // Return to main menu if no file is loaded
        }

        SheetDto sheet = engine.displayCurrentSpreadsheet();
        Printable sheetToPrint = new SpreadSheetPrint(sheet);
        sheetToPrint.print();
        return true;
    }
}
