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
    public boolean execute(Engine engine) {
        try {
            engine.checkIfFileLoaded();
        } catch (IllegalStateException e) {
            System.out.println(e.getMessage());
            return false; // Return to main menu if no file is loaded
        }

        VerDto versions = engine.displayVersions();
        Printable versionsPrintable = new VersionPrint(versions);
        versionsPrintable.print();
        System.out.println("\n");

        while (true) {
            System.out.println("Enter a version number you would like to display (or 'back' to return to main menu):");
            Scanner scanner = new Scanner(System.in);
            String input = scanner.nextLine().trim().toLowerCase();

            if (input.equals("back")) {
                return false; // Return to main menu
            }

            try {
                int version = Integer.parseInt(input);
                displaySpecificVersion(engine, version);
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a valid version number or 'back'.");
            } catch (IllegalArgumentException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    private void displaySpecificVersion(Engine engine, int version) {
        SheetDto sheet = engine.displaySheetByVersion(version);
        Printable sheetToPrint = new SpreadSheetPrint(sheet);
        sheetToPrint.print();
        System.out.println("\n");
    }
}
