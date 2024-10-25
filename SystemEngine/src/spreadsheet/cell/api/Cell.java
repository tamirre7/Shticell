package spreadsheet.cell.api;

import spreadsheet.cell.cellstyle.api.CellStyle;
import java.util.List;

public interface Cell
{
    CellIdentifier getIdentifier();
    String getOriginalValue();
    boolean calculateEffectiveValue();
    EffectiveValue getEffectiveValue();
    int getLastModifiedVersion();
    List<CellIdentifier> getDependencies();
    List<CellIdentifier> getInfluences();
    void updateVersion(int newVersion);
    void setEffectiveValue (EffectiveValue effectiveValue);
    void resetDependencies ();
    void resetInfluences();
    void setCellStyle(CellStyle style);
    CellStyle getCellStyle();
    String getModifiedBy();

}
