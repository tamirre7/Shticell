package spreadsheet.cell.cellstyle.impl;
import spreadsheet.cell.cellstyle.api.CellStyle;


public class CellStyleImpl implements CellStyle {
    private String style;

    public CellStyleImpl(String style) {this.style = style;}

    public String getStyle() {
        return style;
    }

    public void setStyle(String style) {
        this.style = style;
    }
}
