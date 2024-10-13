package shticell.client.sheethub.components.commands.components.permissionresponse;

import com.google.gson.Gson;
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

public class RequestWatcher extends TimerTask {
    private final Consumer<List<PermissionRequestDto>> requestConsumer;
    private boolean isActive = true;
    private List<SheetDto> ownedSheets;

    public RequestWatcher(Consumer<List<PermissionRequestDto>> requestConsumer) {
        this.requestConsumer = requestConsumer;
    }

    private void fetchOwnedSheets() {
        // Implementation to fetch owned sheets
        HttpClientUtil.runAsync(Constants.OWNED_SHEETS, new Callback() {
            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseBody = response.body().string();
                    ownedSheets = List.of(new Gson().fromJson(responseBody, SheetDto[].class));
                    fetchPendingRequests(); // Call to fetch pending requests after getting owned sheets
                }
            }
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Platform.runLater(() -> showAlert("Error", e.getMessage()));
            }
        });
    }

    private void fetchPendingRequests() {
        Gson gson = new Gson();
        String ownedSheetsJson = gson.toJson(ownedSheets);

        RequestBody requestBody = RequestBody.create(ownedSheetsJson, MediaType.parse("application/json"));

        Request request = new Request.Builder()
                .url(Constants.SHEET_PENDING_PERMISSIONS_REQUESTS)
                .post(requestBody)
                .build();

        HttpClientUtil.runAsync(request, new Callback() {
            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseBody = response.body().string();
                    List<PermissionRequestDto> pendingRequests = List.of(new Gson().fromJson(responseBody, PermissionRequestDto[].class));
                    requestConsumer.accept(pendingRequests);
                }
            }
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Platform.runLater(() -> showAlert("Error", e.getMessage()));
            }
        });
    }

    @Override
    public void run() {
        if (!isActive) {
            return;
        }
        fetchOwnedSheets();
    }
    public void setActive(boolean active) {
        this.isActive = active;
    }
}
