package shticell.client.sheetpanel.action.line;

import javafx.application.Platform;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;
import shticell.client.util.Constants;
import shticell.client.util.http.HttpClientUtil;

import java.io.IOException;
import java.util.TimerTask;
import java.util.function.Consumer;

public class VersionSelectorRefresher extends TimerTask {
    private final Consumer<Integer> latestVersionConsumer;
    private final String sheetName;
    private boolean isActive = true;

    public VersionSelectorRefresher(Consumer<Integer> latestVersionConsumer, String sheetName) {
        this.latestVersionConsumer = latestVersionConsumer;
        this.sheetName = sheetName;
    }

    @Override
    public void run() {
        if (!isActive) {
            return;
        }

        String finalUrl = HttpUrl
                .parse(Constants.LATEST_VERSION)
                .newBuilder()
                .addQueryParameter("sheetName", sheetName)
                .build()
                .toString();

        HttpClientUtil.runAsync(finalUrl, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Platform.runLater(() ->
                        HttpClientUtil.showAlert("Error", e.getMessage())
                );
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.code() != 200) {
                    String responseBody = response.body().string();
                    Platform.runLater(() ->
                            HttpClientUtil.showAlert("Error", responseBody)
                    );
                } else {
                    String responseBody = response.body().string();
                    Platform.runLater(() -> {
                        int latestVersion = Integer.parseInt(responseBody);
                        latestVersionConsumer.accept(latestVersion);
                    });
                }
            }
        });
    }

    public void setActive(boolean active) {
        this.isActive = active;
    }
}