package spreadsheet.range.api;
import spreadsheet.cell.api.Cell;
import spreadsheet.cell.impl.CellIdentifierImpl;

public interface Range {

    public String getName();

    public CellIdentifierImpl getTopLeft();

    public CellIdentifierImpl getBottomRight();

    public boolean isCellContains(Cell cell);

    void setActive(boolean active);
}
