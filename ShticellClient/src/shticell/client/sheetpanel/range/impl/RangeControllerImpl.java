package shticell.client.sheetpanel.range.impl;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import shticell.client.sheetpanel.range.api.RangeController;

public class RangeControllerImpl implements RangeController {
    @FXML
    private ListView<String> rangeListView;
    @FXML private Button addButton;
    @FXML private Button deleteButton;


}
