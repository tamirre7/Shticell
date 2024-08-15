package spreadsheet.cell.api;

import spreadsheet.cell.impl.CellIdentifierimpl;

import java.util.List;

public interface Cell
{
    CellIdentifierimpl getIdentifier();

    void setCellOriginalValue(String value);

    String getOriginalValue();

    void calculateEffectiveValue();

    EffectiveValue getEffectiveValue();

    int getLastModifiedVersion();

    List<CellIdentifierimpl> getDependencies();

    List<CellIdentifierimpl> getInfluences();

     void updateCell(String newOriginalValue, EffectiveValue newEffectiveValue, int newVersion,
                           List<CellIdentifierimpl> newDependencies, List<CellIdentifierimpl> newInfluences);
}
