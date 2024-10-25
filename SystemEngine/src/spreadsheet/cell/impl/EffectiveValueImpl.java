package spreadsheet.cell.impl;

import spreadsheet.cell.api.EffectiveValue;
import spreadsheet.cell.api.CellType;
import java.io.Serializable;
import java.util.Objects;

// Implementation of EffectiveValue interface representing the actual value stored in a spreadsheet cell
// Supports multiple cell types and value type safety
public class EffectiveValueImpl implements EffectiveValue, Serializable {
    // Stores the type of the cell (e.g., NUMBER, STRING, BOOLEAN, etc.)
    private CellType cellType;
    private Object value;


    public EffectiveValueImpl(CellType cellType, Object value) {
        this.cellType = cellType;
        this.value = value;
    }

    // Returns the type of the cell
    @Override
    public CellType getCellType() {
        return cellType;
    }

    // Returns the raw value stored in the cell
    @Override
    public Object getValue() {
        return value;
    }

    // Safely extracts the cell value with type checking
    // Returns null if:
    // - Cell is not initialized
    // - Cell contains invalid value
    // - Requested type doesn't match cell type
    // Parameters:
    // - type: expected class type of the value
    @Override
    public <T> T extractValueWithExpectation(Class<T> type) {
        if (cellType == CellType.NOT_INIT || cellType == CellType.INVALID_VALUE)
            return null;
        if (cellType.isAssignableFrom(type)) {
            return type.cast(value);
        }
        return null;
    }

    // Implements equals method for value comparison
    // Returns true if:
    // - Same object reference
    // - Same class type
    // - Same cell type and value
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EffectiveValueImpl that = (EffectiveValueImpl) o;
        if (cellType != that.cellType) return false;
        return Objects.equals(value, that.value);
    }

    // Generates hash code for the effective value
    // Takes into account both cell type and value
    @Override
    public int hashCode() {
        int result = cellType != null ? cellType.hashCode() : 0;
        result = 31 * result + (value != null ? value.hashCode() : 0);
        return result;
    }

    // Converts the effective value to string representation
    // Special handling for boolean values (converts to uppercase)
    // For all other types, uses standard toString()
    @Override
    public String toString() {
        if (value instanceof Boolean) {
            return ((Boolean) value).toString().toUpperCase();
        }
        return value.toString();
    }
}