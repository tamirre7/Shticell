package spreadsheet.cell.impl;

import expressions.api.Expression;
import spreadsheet.api.ReadOnlySpreadSheet;
import spreadsheet.cell.api.Cell;
import spreadsheet.cell.api.EffectiveValue;
import spreadsheet.cell.cellstyle.api.CellStyle;
import spreadsheet.cell.cellstyle.impl.CellStyleImpl;


import javax.swing.text.Style;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import static expressions.parser.FunctionParser.parseExpression;

public class CellImpl implements Cell, Serializable
{
    private final CellIdentifierImpl identifier;
    private String originalValue;
    private EffectiveValue effectiveValue;
    private int lastModifiedVersion;
    private List<CellIdentifierImpl> dependencies;
    private List<CellIdentifierImpl> influences;
    private ReadOnlySpreadSheet sheet;
    private CellStyle style;
    private String modifiedBy;


    public CellImpl(CellIdentifierImpl identifier, String originalValue,
                    int lastModifiedVersion,
                     ReadOnlySpreadSheet sheet,String modifiedBy) {
        this.identifier = identifier;
        this.originalValue = originalValue;
        this.effectiveValue = null;
        this.lastModifiedVersion = lastModifiedVersion;
        this.dependencies = new ArrayList<>();
        this.influences = new ArrayList<>();
        this.sheet = sheet;
        this.style = new CellStyleImpl("");
        this.modifiedBy = modifiedBy;
    }
    @Override
    public CellIdentifierImpl getIdentifier() {
        return identifier;
    }
    @Override
    public void setCellOriginalValue(String value){originalValue = value;}
    @Override
    public String getOriginalValue() {
        return originalValue;
    }
    @Override
    public EffectiveValue getEffectiveValue() {
        return effectiveValue;
    }
    @Override
    public boolean calculateEffectiveValue() {
        Expression expression = parseExpression(originalValue, sheet);
        EffectiveValue newEffectiveValue = expression.evaluate(sheet);
        if (newEffectiveValue.equals(effectiveValue)) {
            return false;
        } else {
            effectiveValue = newEffectiveValue;
            return true;
        }
    }
    @Override
    public int getLastModifiedVersion() {
        return lastModifiedVersion;
    }
    @Override
    public List<CellIdentifierImpl> getDependencies() {
        return dependencies;
    }
    @Override
    public List<CellIdentifierImpl> getInfluences() {
        return influences;
    }

    @Override
    public void resetDependencies () {this.dependencies.clear(); }

    @Override
    public void resetInfluences() {this.influences.clear(); }

    @Override
   public void updateVersion(int version) {
        this.lastModifiedVersion = version;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        CellImpl cell = (CellImpl) obj;
        return identifier.equals(cell.identifier);
    }

    @Override
    public void setEffectiveValue (EffectiveValue effectiveValue) {
        this.effectiveValue = effectiveValue;
    }

    @Override
    public int hashCode() {
        return identifier.hashCode();
    }


    @Override
    public CellStyle getCellStyle() {return style;}

    @Override
    public void setModifiedBy(String modifiedBy) {
        this.modifiedBy = modifiedBy;
    }

    @Override
    public String getModifiedBy() {
        return modifiedBy;
    }

    @Override
    public void setCellStyle(CellStyle style) {
        this.style = style;
    }


}

