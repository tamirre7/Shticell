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
import shticell.client.sheetpanel.action.line.api.ActionLineController;
import shticell.client.sheetpanel.spreadsheet.api.SpreadsheetController;
import shticell.client.util.Constants;
import shticell.client.util.http.HttpClientUtil;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

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

    @FXML
    public void initialize() {


        updatevalbtn.setOnAction(event -> {
            updateCellValue(null);
        });

        latestVersion.addListener((obs, oldVersion, newVersion) -> {
            if (newVersion != null && newVersion.intValue() > 0) {
                populateVersionSelector();  // Populate the ComboBox when the latest version is retrieved
            }
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
                        populateVersionSelector();
                    });
                }
            }
        });
    }

    @Override
    public void populateVersionSelector() {
        getLatestVersion();
        versionSelector.getItems().clear();
        for (int i = 1; i <=  latestVersion.get(); i++) {
            versionSelector.getItems().add(String.valueOf(i));
        }

    }

    private void getLatestVersion() {
        //noinspection ConstantConditions

        String finalUrl = HttpUrl
                .parse(Constants.LATEST_VERSION)
                .newBuilder()
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
                        int latest = Integer.parseInt(responseBody);
                        latestVersion.set(latest);
                    });
                }
            }
        });
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
    public void enableEditing(){
        updatevalbtn.setDisable(false);
        originalvalueTF.setDisable(false);
        versionSelector.setDisable(false);
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
