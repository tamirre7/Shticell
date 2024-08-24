package spreadsheet.cell.impl;

import expressions.api.Expression;
import spreadsheet.api.ReadOnlySpreadSheet;
import spreadsheet.api.SpreadSheet;
import spreadsheet.cell.api.Cell;
import spreadsheet.cell.api.EffectiveValue;

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


    public CellImpl(CellIdentifierImpl identifier, String originalValue,
                    int lastModifiedVersion,
                     ReadOnlySpreadSheet sheet) {
        this.identifier = identifier;
        this.originalValue = originalValue;
        this.effectiveValue = null;
        this.lastModifiedVersion = lastModifiedVersion;
        this.dependencies = new ArrayList<>();
        this.influences = new ArrayList<>();
        this.sheet = sheet;
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
}

