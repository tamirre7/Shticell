package spreadsheet.cell.impl;

import expressions.api.Expression;
import spreadsheet.api.ReadOnlySpreadSheet;
import spreadsheet.cell.api.Cell;
import spreadsheet.cell.api.CellIdentifier;
import spreadsheet.cell.api.EffectiveValue;
import spreadsheet.cell.cellstyle.api.CellStyle;
import spreadsheet.cell.cellstyle.impl.CellStyleImpl;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import static expressions.parser.FunctionParser.parseExpression;

// Implementation of Cell interface representing a single cell in a spreadsheet
// Maintains both original and calculated values, dependencies, and styling
public class CellImpl implements Cell, Serializable {
    private final CellIdentifier identifier;
    private String originalValue;
    private EffectiveValue effectiveValue;
    private Integer lastModifiedVersion;
    private List<CellIdentifier> dependencies;
    private List<CellIdentifier> influences;
    private ReadOnlySpreadSheet sheet;
    private CellStyle style;
    private String modifiedBy;

    public CellImpl(CellIdentifier identifier, String originalValue,
                    Integer lastModifiedVersion,
                    ReadOnlySpreadSheet sheet, String modifiedBy) {
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

    // Returns cell's unique identifier
    @Override
    public CellIdentifier getIdentifier() {
        return identifier;
    }

    // Returns the raw input value
    @Override
    public String getOriginalValue() {
        return originalValue;
    }

    // Returns the calculated value
    @Override
    public EffectiveValue getEffectiveValue() {
        return effectiveValue;
    }

    // Evaluates cell's formula/expression and updates effective value
    // Returns true if value changed, false if unchanged
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

    // Returns the last modification version number
    @Override
    public int getLastModifiedVersion() {
        return lastModifiedVersion;
    }

    // Returns list of cells this cell depends on
    @Override
    public List<CellIdentifier> getDependencies() {
        return dependencies;
    }

    // Returns list of cells that depend on this cell
    @Override
    public List<CellIdentifier> getInfluences() {
        return influences;
    }

    // Clears all dependencies
    @Override
    public void resetDependencies() {
        this.dependencies.clear();
    }

    // Clears all influences
    @Override
    public void resetInfluences() {
        this.influences.clear();
    }

    // Updates the modification version number
    @Override
    public void updateVersion(int version) {
        this.lastModifiedVersion = version;
    }

    // Compares cells based on their identifiers
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        CellImpl cell = (CellImpl) obj;
        return identifier.equals(cell.identifier);
    }

    // Sets the calculated value directly
    @Override
    public void setEffectiveValue(EffectiveValue effectiveValue) {
        this.effectiveValue = effectiveValue;
    }

    // Generates hash code based on cell identifier
    @Override
    public int hashCode() {
        return identifier.hashCode();
    }

    // Returns cell's style properties
    @Override
    public CellStyle getCellStyle() {
        return style;
    }


    // Returns username of last modifier
    @Override
    public String getModifiedBy() {
        return modifiedBy;
    }

    // Updates cell's style properties
    @Override
    public void setCellStyle(CellStyle style) {
        this.style = style;
    }
}