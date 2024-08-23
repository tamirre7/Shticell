package shticellui.command.impl;

import engine.api.Engine;
import dto.CellDto;
import shticellui.command.api.Command;
import shticellui.print.impl.CellPrint;

import java.util.Scanner;

public class DisplayCellValue implements Command {
    @Override
    public void execute(Engine engine) {
        Scanner scanner = new Scanner(System.in);
        String cellID = null;
        CellDto cellDto = null;

        // Step 1: Loop until a valid cell ID is entered
        while (cellDto == null) {
            System.out.println("Please enter the cell ID:");
            cellID = scanner.nextLine();

            try {
                cellDto = engine.displayCellValue(cellID);
                CellPrint printableCell = new CellPrint(cellDto);
                printableCell.print();
            } catch (IllegalArgumentException e) {
                System.out.println("Invalid cell ID. Please try again.");
            }
        }
    }
}
