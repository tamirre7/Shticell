package shticellui.print.impl;

import dto.SheetDto;
import dto.VerDto;
import shticellui.print.api.Printable;

import java.util.Map;

public class VersionPrint implements Printable {
    private final VerDto version;

    public VersionPrint(VerDto version) {
        this.version = version;
    }

    @Override
    public void print() {
        // Define column widths
        final int versionColumnWidth = 10;
        final int cellsChangedColumnWidth = 15;

        Map<Integer, SheetDto> versionsMap = version.getVersions();

        // Print table header
        System.out.printf("%-" + versionColumnWidth + "s %-" + cellsChangedColumnWidth + "s%n", "Version", "Cells Changed");

        // Print table separator line with correct length
        int separatorLength = versionColumnWidth + cellsChangedColumnWidth;
        System.out.println("-".repeat(separatorLength));

        // Iterate through the map of versions
        for (Map.Entry<Integer, SheetDto> entry : versionsMap.entrySet()) {
            int versionNumber = entry.getKey();
            int cellsChanged = entry.getValue().getAmountOfCellsChangedInVersion();

            // Calculate padding for center alignment
            String versionStr = String.valueOf(versionNumber);
            String cellsChangedStr = String.valueOf(cellsChanged);
            int versionPaddingLeft = (versionColumnWidth - versionStr.length()) / 2;
            int cellsChangedPaddingLeft = (cellsChangedColumnWidth - cellsChangedStr.length()) / 2;

            // Print the version number and the number of cells changed with center alignment
            System.out.printf("%" + (versionPaddingLeft + versionStr.length()) + "s%" + (cellsChangedPaddingLeft + versionPaddingLeft + cellsChangedStr.length()) + "s%n", versionStr, cellsChangedStr);
        }
    }

}

