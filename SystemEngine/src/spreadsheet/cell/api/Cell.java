package spreadsheet.cell.api;

import spreadsheet.cell.impl.CellIdentifierImpl;

import java.util.List;

public interface Cell
{
    CellIdentifierImpl getIdentifier();

    void setCellOriginalValue(String value);

    String getOriginalValue();

    void calculateEffectiveValue();

    EffectiveValue getEffectiveValue();

    int getLastModifiedVersion();

    List<CellIdentifierImpl> getDependencies();

    List<CellIdentifierImpl> getInfluences();

     void updateCell(String newOriginalValue, EffectiveValue newEffectiveValue, int newVersion,
                     List<CellIdentifierImpl> newDependencies, List<CellIdentifierImpl> newInfluences);
}
