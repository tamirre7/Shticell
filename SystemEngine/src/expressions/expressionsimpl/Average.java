package expressions.expressionsimpl;

import expressions.api.Expression;
import spreadsheet.api.ReadOnlySpreadSheet;
import spreadsheet.cell.api.CellIdentifier;
import spreadsheet.cell.api.CellType;
import spreadsheet.cell.api.EffectiveValue;
import spreadsheet.cell.impl.EffectiveValueImpl;
import spreadsheet.range.api.Range;

public class Average implements Expression {
    private final Range range;

    public Average (Range range){
        this.range = range;
    }
    @Override
    public EffectiveValue evaluate(ReadOnlySpreadSheet spreadSheet) {
        if (range == null)
        {
            return new EffectiveValueImpl(CellType.INVALID_VALUE,Double.NaN);
        }
        double sum = 0.0;
        int counter = 0;
        // Iterate over each cell in the range
        for (CellIdentifier cellIdentifier : range.getCellsInRange()) {
            EffectiveValue cellValue = spreadSheet.getCellEffectiveValue(cellIdentifier);
            if (cellValue == null){
                sum += 0;
            }
            else {
                // Extract numeric value
                Double numericValue = cellValue.extractValueWithExpectation(Double.class);
                if (numericValue != null) {
                    sum += numericValue; // Add to sum if it's a valid number
                    counter++;
                }
            }
        }
        range.setActive(true);
        double average;
        if (counter == 0)
            average = 0;
        else {
            average = sum / counter;
        }
        // Return the result wrapped in an EffectiveValue
        return new EffectiveValueImpl(CellType.NUMERIC, average);
    }

}
