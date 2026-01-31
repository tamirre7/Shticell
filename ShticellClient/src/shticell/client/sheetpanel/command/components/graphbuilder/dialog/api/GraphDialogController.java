package shticell.client.sheetpanel.command.components.graphbuilder.dialog.api;

public interface GraphDialogController {
    String getXTopCell();
    String getXBottomCell();
    String getYTopCell();
    String getYBottomCell();
    boolean isConfirmed();
    void closeDialog();
}