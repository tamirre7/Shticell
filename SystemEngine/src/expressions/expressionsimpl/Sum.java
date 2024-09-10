package expressions.expressionsimpl;

import expressions.api.Expression;
import spreadsheet.api.ReadOnlySpreadSheet;
import spreadsheet.cell.api.Cell;
import spreadsheet.cell.api.CellType;
import spreadsheet.cell.api.EffectiveValue;
import spreadsheet.cell.impl.CellIdentifierImpl;
import spreadsheet.cell.impl.EffectiveValueImpl;
import spreadsheet.range.impl.RangeImpl;

public class Sum implements Expression {
    private final RangeImpl range;

    public Sum (RangeImpl range){
        this.range = range;
    }
    @Override
    public EffectiveValue evaluate(ReadOnlySpreadSheet spreadSheet) {
       if (range == null){
           return new EffectiveValueImpl(CellType.INVALID_VALUE,Double.NaN);
       }
        double sum = 0.0;
        // Iterate over each cell in the range
        for (CellIdentifierImpl cellIdentifier : range.getCellsInRange()) {
            Cell cell = spreadSheet.getCell(cellIdentifier);
            if (cellIdentifier != null) {
                EffectiveValue cellValue = cell.getEffectiveValue();

                // Extract numeric value
                Double numericValue = cellValue.extractValueWithExpectation(Double.class);

                if (numericValue != null) {
                    sum += numericValue; // Add to sum if it's a valid number
                }
                else {
                        if (cellValue.getCellType() == CellType.NOT_INIT)
                            sum += 0;
                        else
                            return new EffectiveValueImpl(CellType.INVALID_VALUE,Double.NaN);
                }
            }
        }

        // Return the result wrapped in an EffectiveValue
        return new EffectiveValueImpl(CellType.NUMERIC, sum);
    }

}
