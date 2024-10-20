package shticell.client.sheetpanel.action.line.impl;

import com.google.gson.Gson;
import dto.CellDto;
import dto.SheetDto;
import javafx.application.Platform;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;
import shticell.client.sheethub.components.available.sheets.SheetTableRefresher;
import shticell.client.sheetpanel.action.line.VersionSelectorRefresher;
import shticell.client.sheetpanel.action.line.api.ActionLineController;
import shticell.client.sheetpanel.spreadsheet.api.SpreadsheetController;
import shticell.client.util.Constants;
import shticell.client.util.http.HttpClientUtil;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;

import static shticell.client.util.Constants.REFRESH_RATE;
import static shticell.client.util.http.HttpClientUtil.extractSheetFromResponseBody;
import static shticell.client.util.http.HttpClientUtil.showAlert;

public class ActionLineControllerImpl implements ActionLineController {
    @FXML
    private TextField cellidTF;
    @FXML
    private TextField originalvalueTF;
    @FXML
    private TextField lastmodverTF;
    @FXML
    private TextField modifiedBy;
    @FXML
    private Button updatevalbtn;
    @FXML
    private ComboBox<String> versionSelector;
    @FXML
    private Label usernameValueLabel;

    private IntegerProperty latestVersion = new SimpleIntegerProperty(0);
    private ObjectProperty<SheetDto> sheetByVersionProperty = new SimpleObjectProperty<>();
    SpreadsheetController  spreadsheetController;

    private Timer timer;
    private VersionSelectorRefresher versionSelectorRefresher;

    @FXML
    public void initialize() {


        updatevalbtn.setOnAction(event -> {
            updateCellValue(null);
        });

        versionSelector.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                getSheetByVersion(Integer.valueOf(newValue));  // Retrieve the sheet for the selected version
            }
        });

        // Add a single listener to the sheetByVersionProperty to update the display
        sheetByVersionProperty.addListener((obs, oldSheetValue, newSheetValue) -> {
            if (newSheetValue != null) {
                int selectedVersion = Integer.parseInt(versionSelector.getSelectionModel().getSelectedItem());
                displaySheetByVersion(newSheetValue, selectedVersion);
            }
        });

        latestVersion.addListener((obs, oldVersion, newVersion) -> {
            if (newVersion != null && newVersion.intValue() > 0) {
                Platform.runLater(this::updateVersionSelector);
            }
        });

        versionSelector.setOnMouseClicked(event -> versionSelector.setStyle(""));
    }
    @Override
    public String getLoggedUser(){return usernameValueLabel.getText();}

    @Override
    public void setUsernameLabel(String usernameLabel) {this.usernameValueLabel.setText(usernameLabel);}

    private void displaySheetByVersion(SheetDto sheetByVersion,Integer version) {
        if(version != latestVersion.get())
        {
            spreadsheetController.displayTemporarySheet(sheetByVersion,true);
        }
        else
        {
            spreadsheetController.setCurrentSheet(sheetByVersion);
            spreadsheetController.displayOriginalSheet(true);
        }
    }

    private void getSheetByVersion(Integer version) {

        String finalUrl = HttpUrl
                .parse(Constants.SHEET_BY_VERSION)
                .newBuilder()
                .addQueryParameter("version", String.valueOf(version))
                .addQueryParameter("sheetName",spreadsheetController.getCurrentSheet().getSheetName())
                .build()
                .toString();


        HttpClientUtil.runAsync(finalUrl, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {

                Platform.runLater(() ->
                        showAlert("Error", e.getMessage())
                );
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.code() != 200) {
                    String responseBody = response.body().string();
                    Platform.runLater(() ->
                            showAlert("Error", responseBody)
                    );
                } else {
                    String responseBody = response.body().string();
                    Platform.runLater(() -> {
                        sheetByVersionProperty.set(extractSheetFromResponseBody(responseBody));
                    });
                }
            }
        });
    }

    @FXML
    @Override
    public void updateCellValue(String preBuildOriginalValue) {
        String cellId = cellidTF.getText().toUpperCase();
        String newValue = originalvalueTF.getText();

        Map<String,String> cellData = new HashMap<>();
        cellData.put("cellid", cellId);
        cellData.put("sheetName", spreadsheetController.getCurrentSheet().getSheetName());
        cellData.put("version", spreadsheetController.getCurrentSheet().getVersion().toString());
        if (preBuildOriginalValue == null) { cellData.put("newvalue", newValue);}
        else{cellData.put("newvalue", preBuildOriginalValue);}

        Gson gson = new Gson();
        String cellDataJson = gson.toJson(cellData);

       RequestBody requestBody = RequestBody.create(cellDataJson, MediaType.parse("application/json"));

        Request request = new Request.Builder()
                .url(Constants.UPDATE_CELL)
                .post(requestBody)
                .build();

        HttpClientUtil.runAsync(request, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Platform.runLater(() ->
                        showAlert("Error", e.getMessage())
                );
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.code() != 200) {
                    String responseBody = response.body().string();
                    Platform.runLater(() ->
                            showAlert("Error", responseBody)
                    );
                } else {
                    String responseBody = response.body().string();
                    Platform.runLater(() -> {
                        SheetDto updatedSheet = extractSheetFromResponseBody(responseBody);
                        spreadsheetController.setCurrentSheet(updatedSheet);
                        spreadsheetController.updateAllCells(updatedSheet.getCells());
                    });
                }
            }
        });
    }

    @Override
    public void startVersionSelectorRefresher() {
        stopVersionSelectorRefresher();

        versionSelectorRefresher = new VersionSelectorRefresher(this::handleLatestVersion,spreadsheetController.getCurrentSheet().getSheetName());
        timer = new Timer();
        timer.schedule(versionSelectorRefresher, REFRESH_RATE, REFRESH_RATE);
    }

    private void updateVersionSelector() {
        int currentLatestVersion = latestVersion.get();
        versionSelector.getItems().clear();
        for (int i = 1; i <= currentLatestVersion; i++) {
            versionSelector.getItems().add(String.valueOf(i));
        }

        // Indicate new version if the selector was previously populated
        if (versionSelector.getItems().size() > 1) {
            versionSelector.setStyle("-fx-background-color: red;");
        }
    }
    private void handleLatestVersion(int newLatestVersion) {
        if (newLatestVersion > latestVersion.get()) {
            latestVersion.set(newLatestVersion);
        }
    }

    @Override
    public void stopVersionSelectorRefresher() {
        if (versionSelectorRefresher != null) {
            versionSelectorRefresher.setActive(false);
        }
        if (timer != null) {
            timer.cancel();
            timer.purge();
        }
    }

    @Override
    public void setCellData(CellDto cellDto, String cellId) {
        cellidTF.setText(cellId);
        if (cellDto != null && isActiveCell(cellDto.getCellId())) {
            originalvalueTF.setText(cellDto.getOriginalValue());
            lastmodverTF.setText(cellDto.getLastModifiedVersion().toString());
            modifiedBy.setText(cellDto.getModifiedBy());
        }
        else {
            originalvalueTF.setText("");
            lastmodverTF.setText("");
            modifiedBy.setText("");
        }

    }

    private boolean isActiveCell(String cellId) {
        Map<String,CellDto>activeCells = spreadsheetController.getCurrentSheet().getCells();
        return activeCells.containsKey(cellId);
    }

    @Override
    public void disableEditing(boolean versionView){
        updatevalbtn.setDisable(true);
        originalvalueTF.setDisable(true);
        if(!versionView) {versionSelector.setDisable(true);}
    }

    @Override
    public void enableEditing(boolean versionView){
        updatevalbtn.setDisable(false);
        originalvalueTF.setDisable(false);
        if(versionView) {versionSelector.setDisable(false);}
    }
    @Override
    public void clearTextFields() {
        cellidTF.clear();
        originalvalueTF.clear();
        lastmodverTF.clear();
    }

    @Override
    public void setSpreadsheetController(SpreadsheetController spreadsheetController){this.spreadsheetController = spreadsheetController;}
}
