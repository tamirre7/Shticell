package dto;

import spreadsheet.cell.api.EffectiveValue;
import spreadsheet.cell.impl.CellIdentifierImpl;

import java.util.Collections;
import java.util.List;

public class CellDto {
    private final CellIdentifierImpl identifier;
    private final String originalValue;
    private final EffectiveValue effectiveValue;
    private final int lastModifiedVersion;
    private final List<CellIdentifierImpl> dependencies;
    private final List<CellIdentifierImpl> influences;

    // Parameterized constructor
    public CellDto(CellIdentifierImpl identifier, String originalValue, EffectiveValue effectiveValue,
                   int lastModifiedVersion, List<CellIdentifierImpl> dependencies, List<CellIdentifierImpl> influences) {
        this.identifier = identifier;
        this.originalValue = originalValue;
        this.effectiveValue = effectiveValue;
        this.lastModifiedVersion = lastModifiedVersion;
        this.dependencies = Collections.unmodifiableList(dependencies);
        this.influences = Collections.unmodifiableList(influences);
    }

    // Getters
    public CellIdentifierImpl getCellId() {
        return identifier;
    }

    public String getOriginalValue() {
        return originalValue;
    }

    public EffectiveValue getEffectiveValue() {
        return effectiveValue;
    }

    public int getLastModifiedVersion() {
        return lastModifiedVersion;
    }

    public List<CellIdentifierImpl> getDependencies() {
        return dependencies;
    }

    public List<CellIdentifierImpl> getInfluences() {
        return influences;
    }
}
