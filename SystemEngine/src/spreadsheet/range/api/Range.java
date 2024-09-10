package spreadsheet.range.api;
import spreadsheet.cell.api.Cell;

public interface Range {

    public String getName();

    public Cell getTopLeft();

    public Cell getBottomRight();

    public boolean isCellContains(Cell cell);
}
