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
    private final Consumer<Integer> latestVersionConsumer; // Consumer to handle latest version updates
    private final String sheetName; // Name of the sheet to query
    private boolean isActive = true; // Flag to control the active state of the refresher

    public VersionSelectorRefresher(Consumer<Integer> latestVersionConsumer, String sheetName) {
        this.latestVersionConsumer = latestVersionConsumer; // Initialize consumer
        this.sheetName = sheetName; // Set sheet name
    }

    @Override
    public void run() {
        if (!isActive) { // Check if refresher is active
            return; // Exit if not active
        }

        // Build URL to fetch the latest version of the specified sheet
        String finalUrl = HttpUrl
                .parse(Constants.LATEST_VERSION)
                .newBuilder()
                .addQueryParameter("sheetName", sheetName)
                .build()
                .toString();

        // Execute HTTP request asynchronously
        HttpClientUtil.runAsync(finalUrl, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                // Handle request failure
                Platform.runLater(() ->
                        HttpClientUtil.showAlert("Error", e.getMessage()) // Show error alert
                );
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) {
                try {
                    // Check response status
                    if (response.code() != 200) {
                        String responseBody = response.body() != null ? response.body().string() : "Unknown error";
                        Platform.runLater(() ->
                                HttpClientUtil.showAlert("Error", responseBody) // Show error alert
                        );
                    } else {
                        String responseBody = response.body() != null ? response.body().string() : "0";
                        Platform.runLater(() -> {
                            int latestVersion = Integer.parseInt(responseBody); // Parse latest version
                            latestVersionConsumer.accept(latestVersion); // Update consumer with latest version
                        });
                    }
                } catch (IOException e) {
                    Platform.runLater(() ->
                            HttpClientUtil.showAlert("Error", "An error occurred while processing the response: \n" + e.getMessage())
                    );
                } finally {
                    response.close(); // Ensure response is closed to prevent connection leaks
                }
            }
        });
    }

    public void setActive(boolean active) {
        this.isActive = active; // Set active state for the refresher
    }
}
