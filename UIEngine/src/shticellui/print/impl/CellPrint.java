package shticellui.print.impl;

import dto.CellDto;
import shticellui.print.api.Printable;
import spreadsheet.cell.api.CellType;

public class CellPrint implements Printable {
    private final CellDto cell;

    public CellPrint(CellDto cell) {
        this.cell = cell;
    }

    @Override
    public void print() {
        String effectiveValue = cell.getEffectiveValue().getCellType() == CellType.NOT_INIT ? "EMPTY" : cell.getEffectiveValue().getValue().toString();
        String originalValue = cell.getEffectiveValue().getCellType() == CellType.NOT_INIT ? "EMPTY" : cell.getOriginalValue();

        System.out.println("Cell: identifier: " + cell.getCellId());
        System.out.println("OriginalValue = " + originalValue);
        System.out.println("EffectiveValue = " + effectiveValue);
        System.out.println("LastModifiedVersion = " + cell.getLastModifiedVersion());
        System.out.println("Depends On = " + cell.getDependencies());
        System.out.println("Influence On = " + cell.getInfluences());
    }

    public void printForUpdateFunc()
    {
        String effectiveValue = cell.getEffectiveValue().getCellType() == CellType.NOT_INIT ? "EMPTY" : cell.getEffectiveValue().getValue().toString();
        String originalValue = cell.getEffectiveValue().getCellType() == CellType.NOT_INIT ? "EMPTY" : cell.getOriginalValue();

        System.out.println("Cell identifier: " + cell.getCellId());
        System.out.println("OriginalValue = " + originalValue);
        System.out.println("EffectiveValue = " + effectiveValue);
    }
}
