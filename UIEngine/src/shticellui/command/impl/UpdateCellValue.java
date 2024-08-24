package shticellui.command.impl;
import dto.SheetDto;
import dto.CellDto;
import command.api.Engine;
import shticellui.command.api.Command;
import shticellui.print.api.Printable;
import shticellui.print.impl.CellPrint;
import shticellui.print.impl.SpreadSheetPrint;

import java.util.Scanner;

public class UpdateCellValue implements Command {
    @Override
    public void execute(Engine engine) {
        Scanner scanner = new Scanner(System.in);
        String cellID = null;
        CellDto cellDto = null;

        engine.checkIfFileLoaded();

        // Step 1: Loop until a valid cell ID is entered
        while (cellDto == null) {
            System.out.println("Please enter the cell ID:");
            cellID = scanner.nextLine();
            cellID = cellID.toUpperCase();

            try {
                cellDto = engine.displayCellValue(cellID);
                CellPrint printableCell = new CellPrint(cellDto);
                printableCell.printForUpdateFunc(); // Method to print cell details for updating
            } catch (IllegalArgumentException e) {
                System.out.println("Invalid cell ID. Please try again.");
            }
        }

        // Step 2: Loop until a valid new value is entered
        boolean valueUpdated = false;
        while (!valueUpdated) {
            System.out.println("Please enter the new value:");
            String newValue = scanner.nextLine();

            try {
                // Attempt to update the cell value
                engine.updateCell(cellID, newValue);
                valueUpdated = true; // Exit loop if no exception is thrown
            } catch (Exception e) {
                // Handle exceptions related to invalid values or dependencies
                System.out.println("Error updating cell value: " + e.getMessage());
                System.out.println("Please try again.");
            }
        }

        // Step 3: Display the updated spreadsheet state
        SheetDto updatedSheetDto = engine.displayCurrentSpreadsheet();
        Printable printableSheet = new SpreadSheetPrint(updatedSheetDto);
        printableSheet.print(); // Print the entire spreadsheet state
    }
}
