package shticell.client.sheethub.components.commands.components.permissionresponse;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import dto.permission.PermissionRequestDto;
import dto.SheetDto;
import javafx.application.Platform;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;
import shticell.client.util.http.HttpClientUtil;
import shticell.client.util.Constants;

import java.io.IOException;
import java.util.List;
import java.util.TimerTask;
import java.util.function.Consumer;

import static shticell.client.util.http.HttpClientUtil.showAlert;

public class RequestRefresher extends TimerTask {
    // Consumer to handle the list of permission requests fetched
    private final Consumer<List<PermissionRequestDto>> requestConsumer;
    // Flag to determine if the refresher is active
    private boolean isActive = true;
    // List of owned sheets
    private List<SheetDto> ownedSheets;

    // Constructor initializing the requestConsumer
    public RequestRefresher(Consumer<List<PermissionRequestDto>> requestConsumer) {
        this.requestConsumer = requestConsumer;
    }

    // Fetches the sheets owned by the user
    private void fetchOwnedSheets() {
        HttpClientUtil.runAsync(Constants.OWNED_SHEETS, new Callback() {
            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                // If the response is successful, parse and fetch pending requests
                if (response.isSuccessful()) {
                    String responseBody = response.body().string();
                    ownedSheets = new Gson().fromJson(responseBody, new TypeToken<List<SheetDto>>(){}.getType());
                    fetchPendingRequests(); // Call to fetch pending requests after getting owned sheets
                }
            }
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                // Show alert on failure
                Platform.runLater(() -> showAlert("Error", e.getMessage()));
            }
        });
    }

    // Fetches pending permission requests based on owned sheets
    private void fetchPendingRequests() {
        Gson gson = new Gson();
        String ownedSheetsJson = gson.toJson(ownedSheets);

        // Create request body with the owned sheets JSON
        RequestBody requestBody = RequestBody.create(ownedSheetsJson, MediaType.parse("application/json"));

        Request request = new Request.Builder()
                .url(Constants.SHEET_PENDING_PERMISSIONS_REQUESTS)
                .post(requestBody)
                .build();

        // Execute asynchronous request for pending permissions
        HttpClientUtil.runAsync(request, new Callback() {
            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                // If successful, parse and accept pending requests
                if (response.isSuccessful()) {
                    String responseBody = response.body().string();
                    List<PermissionRequestDto> pendingRequests = List.of(new Gson().fromJson(responseBody, PermissionRequestDto[].class));
                    requestConsumer.accept(pendingRequests);
                }
            }
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                // Show alert on failure
                Platform.runLater(() -> showAlert("Error", e.getMessage()));
            }
        });
    }

    // Runs the timer task to fetch owned sheets
    @Override
    public void run() {
        if (!isActive) {
            return; // Exit if not active
        }
        fetchOwnedSheets();
    }

    // Sets the active state of the refresher
    public void setActive(boolean active) {
        this.isActive = active; // Update active state
    }
}
