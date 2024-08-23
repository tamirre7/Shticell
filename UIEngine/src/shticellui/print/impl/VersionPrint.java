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
        final int cellsChangedColumnWidth = 20;

        Map<Integer, SheetDto> versionsMap = version.getVersions();

        // Print table header
        System.out.printf("%-" + versionColumnWidth + "s %-" + cellsChangedColumnWidth + "s%n", "Version", "Cells Changed");
        System.out.println("-".repeat(versionColumnWidth + cellsChangedColumnWidth));

        // Iterate through the map of versions
        for (Map.Entry<Integer, SheetDto> entry : versionsMap.entrySet()) {
            int versionNumber = entry.getKey();
            int cellsChanged = entry.getValue().getAmountOfCellsChangedInVersion();

            // Print the version number and the number of cells changed with center alignment
            System.out.printf("%" + (versionColumnWidth + (versionColumnWidth - String.valueOf(versionNumber).length()) / 2) + "d %" + (cellsChangedColumnWidth + (cellsChangedColumnWidth - String.valueOf(cellsChanged).length()) / 2) + "d%n", versionNumber, cellsChanged);
        }
    }
}
