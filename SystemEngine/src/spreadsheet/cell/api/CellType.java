package spreadsheet.cell.api;

public enum CellType {
    NUMERIC(Double.class) ,
    STRING(String.class) ,
    BOOLEAN(Boolean.class),
    INVALID_VALUE(Object.class),
    NOT_INIT(Object.class);

    private Class<?> type;

    CellType(Class<?> type) {
        this.type = type;
    }

    public boolean isAssignableFrom(Class<?> aType) {
        return type.isAssignableFrom(aType);
    }
}
