package spreadsheet.cell.style.impl;

import spreadsheet.cell.style.api.CellStyle;

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
