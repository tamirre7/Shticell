package cell;
import java.util.List;


public class Cell
{
    private final CellIdentifier identifier;
    private String originalValue;
    private String effectiveValue;
    private int lastModifiedVersion;
    private List<CellIdentifier> dependencies;
    private List<CellIdentifier> influences;


    public Cell(CellIdentifier identifier, String originalValue, String effectiveValue,
                int lastModifiedVersion, List<CellIdentifier> dependencies,
                List<CellIdentifier> influences) {
        this.identifier = identifier;
        this.originalValue = originalValue;
        this.effectiveValue = effectiveValue;
        this.lastModifiedVersion = lastModifiedVersion;
        this.dependencies = dependencies;
        this.influences = influences;
    }

    public CellIdentifier getIdentifier() {
        return identifier;
    }

    public String getOriginalValue() {
        return originalValue;
    }

    public String getEffectiveValue() {
        return effectiveValue;
    }

    public void setEffectiveValue(String effectiveValue) {
        this.effectiveValue = effectiveValue;
    }

    public int getLastModifiedVersion() {
        return lastModifiedVersion;
    }

    public List<CellIdentifier> getDependencies() {
        return dependencies;
    }

    public List<CellIdentifier> getInfluences() {
        return influences;
    }

    public void updateCell(String newOriginalValue, String newEffectiveValue, int newVersion,
                           List<CellIdentifier> newDependencies, List<CellIdentifier> newInfluences) {
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
        Cell cell = (Cell) obj;
        return identifier.equals(cell.identifier);
    }

    @Override
    public int hashCode() {
        return identifier.hashCode();
    }
}

