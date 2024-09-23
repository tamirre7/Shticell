package spreadsheet.cell.cellstyle.impl;
import spreadsheet.cell.cellstyle.api.CellStyle;

import java.io.Serializable;


public class CellStyleImpl implements CellStyle, Serializable {
    private String style;

    public CellStyleImpl(String style) {this.style = style;}

    public String getStyle() {
        return style;
    }

    public void setStyle(String style) {
        this.style = style;
    }
}
