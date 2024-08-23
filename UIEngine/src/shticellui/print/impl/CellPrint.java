package shticellui.print.impl;

import dto.CellDto;
import shticellui.print.api.Printable;

public class CellPrint implements Printable {
    private final CellDto cell;

    public CellPrint(CellDto cell) {
        this.cell = cell;
    }

    @Override
    public void print() {
        System.out.println("Cell: " +
                "identifier =" + cell.getCellId() +
                ", originalValue ='" + cell.getOriginalValue() + '\'' +
                ", effectiveValue ='" + cell.getEffectiveValue() + '\'' +
                ", lastModifiedVersion =" + cell.getLastModifiedVersion() +
                ", dependencies =" + cell.getDependencies() +
                ", influences =" + cell.getInfluences());
    }

    public void printForUpdateFunc()
    {
        System.out.println("Cell: " +
                "identifier =" + cell.getCellId() +
                ", originalValue ='" + cell.getOriginalValue() + '\'' +
                ", effectiveValue ='" + cell.getEffectiveValue());
    }
}
