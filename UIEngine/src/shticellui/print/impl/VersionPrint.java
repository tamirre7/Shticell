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
        final int versionColumnWidth = 6;
        final int cellsChangedColumnWidth = 10;

        Map<Integer, SheetDto> versionsMap = version.getVersions();

        // Print table header
        System.out.printf("%-" + versionColumnWidth + "s %-" + cellsChangedColumnWidth + "s%n", "Version  ", "Cells Changed");

        // Print table separator line with correct length
        int separatorLength = versionColumnWidth + cellsChangedColumnWidth;
        System.out.println("-".repeat(24));

        // Iterate through the map of versions
        for (Map.Entry<Integer, SheetDto> entry : versionsMap.entrySet()) {
            int versionNumber = entry.getKey();
            int cellsChanged = entry.getValue().getAmountOfCellsChangedInVersion();

            // Print the version number and the number of cells changed with left alignment for version number and right alignment for cells changed
            System.out.printf("%-" + versionColumnWidth + "d %" + cellsChangedColumnWidth + "d%n", versionNumber, cellsChanged);
        }
    }

}

