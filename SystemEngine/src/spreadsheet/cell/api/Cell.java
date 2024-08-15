package spreadsheet.cell.api;

import spreadsheet.cell.impl.CellIdentifierimpl;

import java.util.List;

public interface Cell
{
    CellIdentifierimpl getIdentifier();

    String getOriginalValue();

    String getEffectiveValue();

    void setEffectiveValue(String effectiveValue);

    int getLastModifiedVersion();

    List<CellIdentifierimpl> getDependencies();

    List<CellIdentifierimpl> getInfluences();

     void updateCell(String newOriginalValue, String newEffectiveValue, int newVersion,
                           List<CellIdentifierimpl> newDependencies, List<CellIdentifierimpl> newInfluences);
}
