package spreadsheet.cell.impl;

import spreadsheet.cell.api.Cell;
import spreadsheet.cell.api.EffectiveValue;

import java.util.List;

public class Cellimpl implements Cell
{
    private final CellIdentifierimpl identifier;
    private String originalValue;
    private EffectiveValue effectiveValue;
    private int lastModifiedVersion;
    private List<CellIdentifierimpl> dependencies;
    private List<CellIdentifierimpl> influences;


    public Cellimpl(CellIdentifierimpl identifier, String originalValue, EffectiveValue effectiveValue,
                    int lastModifiedVersion, List<CellIdentifierimpl> dependencies,
                    List<CellIdentifierimpl> influences) {
        this.identifier = identifier;
        this.originalValue = originalValue;
        this.effectiveValue = effectiveValue;
        this.lastModifiedVersion = lastModifiedVersion;
        this.dependencies = dependencies;
        this.influences = influences;
    }
    @Override
    public CellIdentifierimpl getIdentifier() {
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
        //Expression expression = new BinaryExpression()

       // effectiveValue = expression.evaluate();
    }
    @Override
    public int getLastModifiedVersion() {
        return lastModifiedVersion;
    }
    @Override
    public List<CellIdentifierimpl> getDependencies() {
        return dependencies;
    }
    @Override
    public List<CellIdentifierimpl> getInfluences() {
        return influences;
    }
    @Override
    public void updateCell(String newOriginalValue, EffectiveValue newEffectiveValue, int newVersion,
                           List<CellIdentifierimpl> newDependencies, List<CellIdentifierimpl> newInfluences) {
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
        Cellimpl cell = (Cellimpl) obj;
        return identifier.equals(cell.identifier);
    }

    @Override
    public int hashCode() {
        return identifier.hashCode();
    }
}

