package dto;




import java.util.Collections;
import java.util.List;


public class CellDto {
    String cellIdentifier;
    private final String originalValue;
    private final String effectiveValue;
    private final int lastModifiedVersion;
    private final List<String> dependencies;
    private final List<String> influences;
    private String style = "";
    private String alignment = "-fx-alignment: center;";

    // Parameterized constructor
    public CellDto(String cellid, String originalValue, String effectiveValue,
                   int lastModifiedVersion, List<String> dependencies, List<String> influences,String style, String alignment) {
        this.cellIdentifier = cellid;
        this.originalValue = originalValue;
        this.effectiveValue = effectiveValue;
        this.lastModifiedVersion = lastModifiedVersion;
        this.dependencies = Collections.unmodifiableList(dependencies);
        this.influences = Collections.unmodifiableList(influences);
        this.style = style;
        this.alignment = alignment;
    }

    public String getCellId() {
        return cellIdentifier;
    }

    public String getOriginalValue() {
        return originalValue;
    }

    public String getEffectiveValue() {
        return effectiveValue;
    }

    public Integer getLastModifiedVersion() {
        return lastModifiedVersion;
    }

    public List<String> getDependencies() {
        return dependencies;
    }

    public List<String> getInfluences() {
        return influences;
    }

    public String getStyle() {return style;}

    public String getAlignment() {return alignment;}
}
