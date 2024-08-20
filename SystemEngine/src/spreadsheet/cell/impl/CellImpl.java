package spreadsheet.cell.impl;

import expressions.api.Expression;
import spreadsheet.api.ReadOnlySpreadSheet;
import spreadsheet.api.SpreadSheet;
import spreadsheet.cell.api.Cell;
import spreadsheet.cell.api.EffectiveValue;

import java.util.List;

import static expressions.parser.FunctionParser.parseExpression;

public class CellImpl implements Cell
{
    private final CellIdentifierImpl identifier;
    private String originalValue;
    private EffectiveValue effectiveValue;
    private int lastModifiedVersion;
    private List<CellIdentifierImpl> dependencies;
    private List<CellIdentifierImpl> influences;
    private ReadOnlySpreadSheet sheet;


    public CellImpl(CellIdentifierImpl identifier, String originalValue, EffectiveValue effectiveValue,
                    int lastModifiedVersion, List<CellIdentifierImpl> dependencies,
                    List<CellIdentifierImpl> influences, ReadOnlySpreadSheet sheet) {
        this.identifier = identifier;
        this.originalValue = originalValue;
        this.effectiveValue = effectiveValue;
        this.lastModifiedVersion = lastModifiedVersion;
        this.dependencies = dependencies;
        this.influences = influences;
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
    public void calculateEffectiveValue() {
        Expression expression = parseExpression(originalValue, sheet);
        this.effectiveValue = expression.evaluate(sheet);
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
    public void updateCell(String newOriginalValue, EffectiveValue newEffectiveValue, int newVersion,
                           List<CellIdentifierImpl> newDependencies, List<CellIdentifierImpl> newInfluences) {
        this.originalValue = newOriginalValue;
        this.effectiveValue = newEffectiveValue;
        this.lastModifiedVersion = newVersion;
        this.dependencies = newDependencies;
        this.influences = newInfluences;
    }

    @Override
    public String toString() {
        return "Cell:" +
                "identifier=" + identifier +
                ", originalValue='" + originalValue + '\'' +
                ", effectiveValue='" + effectiveValue + '\'' +
                ", lastModifiedVersion=" + lastModifiedVersion +
                ", dependencies=" + dependencies +
                ", influences=" + influences;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        CellImpl cell = (CellImpl) obj;
        return identifier.equals(cell.identifier);
    }

    @Override
    public int hashCode() {
        return identifier.hashCode();
    }
}

