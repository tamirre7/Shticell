package spreadsheet.range.api;
import spreadsheet.cell.impl.CellIdentifierImpl;

public interface Range {

    public CellIdentifierImpl getTopLeft();

    public CellIdentifierImpl getBottomRight();

    void setActive(boolean active);

    String getName();
}
