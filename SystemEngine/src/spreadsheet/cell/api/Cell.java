package spreadsheet.cell.api;

import spreadsheet.cell.cellstyle.api.CellStyle;
import spreadsheet.cell.impl.CellIdentifierImpl;


import javax.swing.text.Style;
import java.util.List;

public interface Cell
{
    CellIdentifier getIdentifier();

    void setCellOriginalValue(String value);

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
    void setModifiedBy(String modifiedBy);
    String getModifiedBy();

}
