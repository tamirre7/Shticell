package spreadsheet.cell.impl;

import spreadsheet.cell.api.EffectiveValue;
import spreadsheet.cell.api.CellType;

public class EffectiveValueimpl implements EffectiveValue {
    private CellType cellType;
    private Object value;

    public void EffectiveValueImpl(CellType cellType, Object value) {
        this.cellType = cellType;
        this.value = value;
    }

    @Override
    public CellType getCellType() {
        return cellType;
    }

    @Override
    public Object getValue() {
        return value;
    }

    @Override
    public <T> T extractValueWithExpectation(Class<T> type) {
        if (cellType.isAssignableFrom(type)) {
            return type.cast(value);
        }
        // error handling... exception ? return null ?
        return null;
    }
}
