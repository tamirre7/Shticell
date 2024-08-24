package dto;


import spreadsheet.cell.api.CellIdentifier;
import spreadsheet.cell.api.EffectiveValue;
import spreadsheet.cell.impl.CellIdentifierImpl;

import java.util.Collections;
import java.util.List;


public class CellDto {
    CellIdentifierImpl cellIdentifier;
    private final String originalValue;
    private final EffectiveValue effectiveValue;
    private final int lastModifiedVersion;
    private final List<CellIdentifierImpl> dependencies;
    private final List<CellIdentifierImpl> influences;

    // Parameterized constructor
    public CellDto(CellIdentifierImpl cellid, String originalValue, EffectiveValue effectiveValue,
                   int lastModifiedVersion, List<CellIdentifierImpl> dependencies, List<CellIdentifierImpl> influences) {
        this.cellIdentifier = cellid;
        this.originalValue = originalValue;
        this.effectiveValue = effectiveValue;
        this.lastModifiedVersion = lastModifiedVersion;
        this.dependencies = Collections.unmodifiableList(dependencies);
        this.influences = Collections.unmodifiableList(influences);
    }

    public CellIdentifierImpl getCellId() {
        return cellIdentifier;
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
