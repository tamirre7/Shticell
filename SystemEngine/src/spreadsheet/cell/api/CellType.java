package spreadsheet.cell.api;

// Enum representing different types of values that can be stored in a spreadsheet cell
// Each type is associated with its corresponding Java class for type safety
public enum CellType {
    NUMERIC(Double.class),
    STRING(String.class),
    BOOLEAN(Boolean.class),
    // Represents cells containing invalid or erroneous values
    INVALID_VALUE(Object.class),
    // Represents uninitialized cells or cells with no value
    NOT_INIT(Object.class);

    private Class<?> type;

    CellType(Class<?> type) {
        this.type = type;
    }

    // Checks if a given type is compatible with this cell type
    // Used for type safety when getting/setting cell values
    // Parameters:
    // - aType: The class type to check for compatibility
    // Returns: true if the given type is assignable to this cell type
    public boolean isAssignableFrom(Class<?> aType) {
        return type.isAssignableFrom(aType);
    }
}