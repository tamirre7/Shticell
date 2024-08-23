package shticellui.print.impl;

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

        int numRows = sheet.getSheetDimentions().getNumRows();
        int numCols = sheet.getSheetDimentions().getNumCols();
        int widthCol = sheet.getSheetDimentions().getWidthCol();
        int heightRow = sheet.getSheetDimentions().getHeightRow();

        // Print column headers
        System.out.print("  "); // Initial space for row numbers
        for (int col = 0; col < numCols; col++) {
            System.out.print(String.format("%-" + widthCol + "s", (char) ('A' + col)));
            if (col < numCols - 1) {
                System.out.print("|");
            }
        }
        System.out.println();

        // Print each row
        for (int row = 0; row < numRows; row++) {
            System.out.print(String.format("%02d", row + 1)); // Row number

            for (int col = 0; col < numCols; col++) {
                CellIdentifier cellID = new CellIdentifierImpl(row,(char) ('A' + col));
                EffectiveValue cellContent = sheet.getCells().get(cellID).getEffectiveValue(); // Retrieve cell content
                if (cellContent != null)
                    System.out.print(String.format("%-" + widthCol + "s", cellContent.getValue()));
                else
                    System.out.print(String.format("%-" + widthCol + "s", ' '));

                if (col < numCols - 1) {
                    System.out.print("|");
                }
            }
            System.out.println();
        }
    }
}
