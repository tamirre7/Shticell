package shticell.client.sheethub.components.permission.table;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import dto.permission.PermissionInfoDto;
import javafx.application.Platform;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;
import shticell.client.util.Constants;
import shticell.client.util.http.HttpClientUtil;
import java.io.IOException;
import java.util.List;
import java.util.TimerTask;
import java.util.function.Consumer;

import static shticell.client.util.http.HttpClientUtil.showAlert;

public class PermissionRefresher extends TimerTask {
    private final Consumer<List<PermissionInfoDto>> permissionConsumer; // Consumer to process the fetched permissions.
    private boolean isActive = true; // Flag to control the activity of the refresher.
    private final String sheetName; // Name of the sheet for which permissions are fetched.

    public PermissionRefresher(Consumer<List<PermissionInfoDto>> permissionConsumer, String sheetName) {
        this.permissionConsumer = permissionConsumer; // Initializes the permission consumer.
        this.sheetName = sheetName; // Sets the sheet name.
    }

    @Override
    public void run() {
        if (!isActive) { // Checks if the refresher is active.
            return; // Exit if not active.
        }

        // Build the URL for fetching permissions based on the sheet name.
        String finalUrl = HttpUrl
                .parse(Constants.SHEET_PERMISSIONS)
                .newBuilder()
                .addQueryParameter("sheetName", sheetName)
                .build()
                .toString();

        // Asynchronously execute the HTTP request.
        HttpClientUtil.runAsync(finalUrl, new Callback() {
            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.isSuccessful()) { // Check if the response is successful.
                    String responseBody = response.body().string(); // Read the response body.
                    Platform.runLater(() -> {
                        // Parse the JSON response to a list of PermissionInfoDto.
                        List<PermissionInfoDto> permissions = new Gson().fromJson(responseBody, new TypeToken<List<PermissionInfoDto>>(){}.getType());
                        permissionConsumer.accept(permissions); // Pass the permissions to the consumer.
                    });
                } else {
                    // Show an alert in case of an unsuccessful response.
                    Platform.runLater(() ->
                            showAlert("Error", "Failed to load permissions: " + response.message())
                    );
                }
            }

            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                // Handle failure to fetch permissions.
                Platform.runLater(() ->
                        showAlert("Error", "An error occurred while loading permissions: " + e.getMessage())
                );
            }
        });
    }

    public void setActive(boolean active) {
        this.isActive = active; // Set the active state of the refresher.
    }
}
