package shticell.client.sheetpanel.action.line.impl;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;
import shticell.client.util.Constants;
import shticell.client.util.http.HttpClientUtil;

import java.io.IOException;

public class ActionLineControllerImpl {
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

    @FXML
    public void updateCellValue(ActionEvent event) {
        String cellId = cellidTF.getText().toUpperCase();
        String newValue = originalvalueTF.getText();

        //noinspection ConstantConditions
        String finalUrl = HttpUrl
                .parse(Constants.UPDATE_CELL_PAGE)
                .newBuilder()
                .addQueryParameter("cellid", cellId)
                .addQueryParameter("originalvalue", newValue)
                .build()
                .toString();


        HttpClientUtil.runAsync(finalUrl, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Platform.runLater(() ->
                        showErrorAlert("Error", e.getMessage())
                );
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.code() != 200) {
                    String responseBody = response.body().string();
                    Platform.runLater(() ->
                            showErrorAlert("Error", responseBody)
                    );
                } else {
                    Platform.runLater(() -> {
                        populateVersionSelector();

                    });
                }
            }
        });
    }

    private void populateVersionSelector() {
        int numOfVersions = getLatestVersion();
        versionSelector.getItems().clear();
        for (int i = 1; i <= numOfVersions; i++) {
            versionSelector.getItems().add(String.valueOf(i));
        }
    }

    private int getLatestVersion() {
        Integer latestVersion = 0;
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
                        showErrorAlert("Error", e.getMessage())
                );
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.code() != 200) {
                    String responseBody = response.body().string();
                    Platform.runLater(() ->
                            showErrorAlert("Error", responseBody)
                    );
                } else {
                    String responseBody = response.body().string();
                    Platform.runLater(() -> {
                        //???????????
                    });
                }
            }
        });
        return latestVersion;
    }

    private void showErrorAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
