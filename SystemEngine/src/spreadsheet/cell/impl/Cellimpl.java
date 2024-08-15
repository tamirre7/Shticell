package spreadsheet.cell.impl;

import spreadsheet.cell.api.Cell;

import java.util.List;

public class Cellimpl implements Cell
{
    private final CellIdentifierimpl identifier;
    private String originalValue;
    private String effectiveValue;
    private int lastModifiedVersion;
    private List<CellIdentifierimpl> dependencies;
    private List<CellIdentifierimpl> influences;


    public Cellimpl(CellIdentifierimpl identifier, String originalValue, String effectiveValue,
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
    public String getOriginalValue() {
        return originalValue;
    }
    @Override
    public String getEffectiveValue() {
        return effectiveValue;
    }
    @Override
    public void setEffectiveValue(String effectiveValue) {
        this.effectiveValue = effectiveValue;
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
    public void updateCell(String newOriginalValue, String newEffectiveValue, int newVersion,
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

