package shticellui.command.impl;

import dto.SheetDto;
import command.api.Engine;
import dto.VerDto;
import shticellui.command.api.Command;
import shticellui.print.api.Printable;
import shticellui.print.impl.SpreadSheetPrint;
import shticellui.print.impl.VersionPrint;

import java.util.Scanner;

public class DisplaySheetVersions implements Command {
    @Override
    public void execute(Engine engine) {
        engine.checkIfFileLoaded();
        VerDto versions = engine.displayVersions();
        Printable versionsPrintable = new VersionPrint(versions);
        versionsPrintable.print();
        System.out.println("\n");
        System.out.println("choose one from the following options:\n");
        System.out.println("1. Display specific version");
        System.out.println("2. return to main menu");
        Scanner scanner = new Scanner(System.in);
        int choice = scanner.nextInt();
        switch (choice) {
            case 1:
                displaySpecificVersion(engine);
            case 2:
                //return to manu

        }
    }

    public void displaySpecificVersion(Engine engine) {
        System.out.println("Enter version to display:\n");
        Scanner scanner = new Scanner(System.in);
        int version = scanner.nextInt();
        SheetDto sheet = engine.displaySheetByVersion(version);
        Printable sheetToPrint = new SpreadSheetPrint(sheet);
        sheetToPrint.print();
        System.out.println("\n");
    }
}
