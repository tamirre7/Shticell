package spreadsheet.cell.api;

public enum CellType {
    NUMERIC_DOUBLE(Double.class) ,
    NUMERIC_INT(Integer.class) ,
    STRING(String.class) ,
    BOOLEAN(Boolean.class) ;

    private Class<?> type;

    CellType(Class<?> type) {
        this.type = type;
    }

    public boolean isAssignableFrom(Class<?> aType) {
        return type.isAssignableFrom(aType);
    }
}
