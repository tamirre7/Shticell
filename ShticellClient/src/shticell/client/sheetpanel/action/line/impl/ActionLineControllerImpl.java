package shticell.client.sheetpanel.action.line.impl;

import com.google.gson.Gson;
import dto.CellDto;
import dto.SheetDto;
import javafx.application.Platform;
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
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

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
    private Button updatevalbtn;
    @FXML
    private ComboBox<String> versionSelector;

    SpreadsheetController  spreadsheetController;

    @FXML
    public void initialize() {
        updatevalbtn.setOnAction(event -> {
            updateCellValue(null);
        });

        // Listen for version selection changes
        versionSelector.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                loadSpreadsheetVersion(Integer.valueOf(newValue));
            }
        });
    }

    private void loadSpreadsheetVersion(Integer version) {
        SheetDto sheetByVersion = getSheetByVersion(version);
        if(version != getLatestVersion())
        {
            spreadsheetController.displayTemporarySheet(sheetByVersion,true);
        }
        else
        {
            spreadsheetController.setCurrentSheet(sheetByVersion);
            spreadsheetController.displayOriginalSheet(true);
        }
    }

    private SheetDto getSheetByVersion(Integer version) {
        AtomicReference<SheetDto> sheetByVersion = new AtomicReference<>();

        String finalUrl = HttpUrl
                .parse(Constants.SHEET_BY_VERSION_PAGE)
                .newBuilder()
                .addQueryParameter("version", String.valueOf(version))
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
                        sheetByVersion.set(extractSheetFromResponseBody(responseBody));
                    });
                }
            }
        });
        return sheetByVersion.get();

    }

    @FXML
    @Override
    public void updateCellValue(String preBuildOriginalValue) {
        String cellId = cellidTF.getText().toUpperCase();
        String newValue = originalvalueTF.getText();

        Map<String,String> cellData = new HashMap<>();
        cellData.put("cellid", cellId);
        cellData.put("newvalue", newValue);

        Gson gson = new Gson();
        String cellDataJson = gson.toJson(cellData);

       RequestBody requestBody = RequestBody.create(cellDataJson, MediaType.parse("application/json"));

        Request request = new Request.Builder()
                .url(Constants.UPDATE_CELL_PAGE)
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
                        populateVersionSelector();
                        SheetDto updatedSheet = extractSheetFromResponseBody(responseBody);
                        spreadsheetController.setCurrentSheet(updatedSheet);
                    });
                }
            }
        });
    }

    @Override
    public void populateVersionSelector() {
        int numOfVersions = getLatestVersion();
        versionSelector.getItems().clear();
        for (int i = 1; i <= numOfVersions; i++) {
            versionSelector.getItems().add(String.valueOf(i));
        }

    }

    private int getLatestVersion() {
        AtomicInteger latestVersion = new AtomicInteger(0);
        //noinspection ConstantConditions
        String finalUrl = HttpUrl
                .parse(Constants.LATEST_VERSION_PAGE)
                .newBuilder()
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
                        latestVersion.set(Integer.parseInt(responseBody));
                    });
                }
            }
        });
        return latestVersion.get();
    }



    @Override
    public void setCellData(CellDto cellDto, String cellId) {
        cellidTF.setText(cellId);
        if (cellDto != null && isActiveCell(cellDto.getCellId())) {
            originalvalueTF.setText(cellDto.getOriginalValue());
            lastmodverTF.setText(cellDto.getLastModifiedVersion().toString());
        }
        else {
            originalvalueTF.setText("");
            lastmodverTF.setText("");
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
