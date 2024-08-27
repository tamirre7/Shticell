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
        System.out.println("Cell: identifier: " + cell.getCellId());
        System.out.println("OriginalValue = " + cell.getOriginalValue());
        System.out.println("EffectiveValue = " + cell.getEffectiveValue().getValue().toString());
        System.out.println("LastModifiedVersion = " + cell.getLastModifiedVersion());
        System.out.println("Depends On = " + cell.getDependencies());
        System.out.println("Influence On = " + cell.getInfluences());

    }

    public void printForUpdateFunc()
    {
        System.out.println("Cell identifier: " + cell.getCellId());
        System.out.println("OriginalValue = " + cell.getOriginalValue());
        System.out.println("EffectiveValue = " + cell.getEffectiveValue().getValue().toString());
    }
}
