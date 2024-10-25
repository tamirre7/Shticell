package dto;

import java.util.Collections;
import java.util.List;

public class CellDto {
    String cellIdentifier; // Unique identifier for the cell
    private final String originalValue; // The original value of the cell
    private final String effectiveValue; // The effective value after evaluations
    private final int lastModifiedVersion; // The version number of the last modification
    private final List<String> dependencies; // List of cell IDs this cell depends on
    private final List<String> influences; // List of cell IDs that are influenced by this cell
    private final String modifiedBy; // The user who last modified the cell
    private String style = ""; // The style applied to the cell

    // Parameterized constructor
    public CellDto(String cellid, String originalValue, String effectiveValue,
                   int lastModifiedVersion, List<String> dependencies, List<String> influences, String style, String modifiedBy) {
        this.cellIdentifier = cellid;
        this.originalValue = originalValue;
        this.effectiveValue = effectiveValue;
        this.lastModifiedVersion = lastModifiedVersion;
        this.dependencies = Collections.unmodifiableList(dependencies);
        this.influences = Collections.unmodifiableList(influences);
        this.style = style;
        this.modifiedBy = modifiedBy;
    }

    public String getCellId() {
        return cellIdentifier; // Returns the cell identifier
    }

    public String getOriginalValue() {
        return originalValue; // Returns the original value of the cell
    }

    public String getEffectiveValue() {
        return effectiveValue; // Returns the effective value of the cell
    }

    public Integer getLastModifiedVersion() {
        return lastModifiedVersion; // Returns the last modified version number
    }

    public List<String> getDependencies() {
        return dependencies; // Returns the list of dependencies
    }

    public List<String> getInfluences() {
        return influences; // Returns the list of influences
    }

    public String getStyle() {
        return style; // Returns the style of the cell
    }

    public String getModifiedBy() {
        return modifiedBy; // Returns the name of the user who modified the cell
    }
}
