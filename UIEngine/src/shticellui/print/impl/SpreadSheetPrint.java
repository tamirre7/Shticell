package shticellui.print.impl;

import dto.CellDto;
import dto.SheetDto;
import shticellui.print.api.Printable;
import spreadsheet.cell.api.CellIdentifier;
import spreadsheet.cell.api.EffectiveValue;
import spreadsheet.cell.impl.CellIdentifierImpl;


public class SpreadSheetPrint implements Printable {
    private final SheetDto sheet;

    public SpreadSheetPrint(SheetDto sheet) {
        this.sheet = sheet;
    }

    @Override
    public void print() {
        // Print version and sheet name
        System.out.println("Version: " + sheet.getVersion());
        System.out.println("Sheet Name: " + sheet.getName());

        int numRows = sheet.getSheetDimensions().getNumRows();
        int numCols = sheet.getSheetDimensions().getNumCols();
        int widthCol = sheet.getSheetDimensions().getWidthCol();
        int heightRow = sheet.getSheetDimensions().getHeightRow();

        // Print column headers with centered letters
        System.out.print("  "); // Initial space for row numbers
        System.out.print("|");
        for (int col = 0; col < numCols; col++) {
            String header = String.valueOf((char) ('A' + col));
            int padding = (widthCol - header.length()) / 2; // Calculate padding for centering
            System.out.print(" ".repeat(Math.max(0, padding))); // Add left padding
            System.out.print(header); // Print the column letter
            System.out.print(" ".repeat(Math.max(0, widthCol - padding - header.length()))); // Add right padding
            if (col < numCols - 1) {
                System.out.print("|");
            }
        }
        System.out.println("|"); // Add separator after last column

        // Print each row
        for (int row = 1; row <= numRows; row++) {
            System.out.printf("%02d", row); // Row number
            System.out.print("|");

            for (int col = 0; col < numCols; col++) {
                CellIdentifier cellID = new CellIdentifierImpl(row, (char) ('A' + col));
                CellDto currentCell = sheet.getCells().get(cellID);
                if (currentCell == null) {
                    System.out.printf("%-" + widthCol + "s", ' ');
                } else {
                    EffectiveValue cellContent = currentCell.getEffectiveValue(); // Retrieve cell content
                    System.out.printf("%-" + widthCol + "s", cellContent.getValue());
                }

                if (col < numCols - 1) {
                    System.out.print("|");
                }
            }
            System.out.println("|"); // Add separator after last column

            // Print additional empty lines for row height
            for (int h = 1; h < heightRow; h++) {
                System.out.print("  "); // Initial space for row numbers
                System.out.print("|");
                for (int col = 0; col < numCols; col++) {
                    System.out.print(" ".repeat(widthCol));
                    if (col < numCols - 1) {
                        System.out.print("|");
                    }
                }
                System.out.println("|"); // Add separator after last column
            }
        }
    }
}
