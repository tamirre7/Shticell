package shticell.client.sheethub.components.available.sheets;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import dto.permission.SheetPermissionDto;
import javafx.application.Platform;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;
import shticell.client.util.Constants;
import shticell.client.util.http.HttpClientUtil;

import java.io.IOException;
import java.util.List;
import java.util.TimerTask;
import java.util.function.Consumer;

import static shticell.client.util.http.HttpClientUtil.showAlert;

public class SheetTableRefresher extends TimerTask {

    private final Consumer<List<SheetPermissionDto>> tableConsumer; // Consumer to handle the fetched sheet data
    private boolean isActive = true; // Flag to control the execution of the refresher

    // Constructor that initializes the table consumer
    public SheetTableRefresher(Consumer<List<SheetPermissionDto>> tableConsumer) {
        this.tableConsumer = tableConsumer;
    }

    // Executes the task to fetch available sheets from the server
    @Override
    public void run() {
        if (!isActive) {
            return; // Stop execution if not active
        }

        // Asynchronously fetch available sheets
        HttpClientUtil.runAsync(Constants.AVAILABLE_SHEETS, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                // Show an alert in case of an error
                Platform.runLater(() -> showAlert("Error", e.getMessage()));
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseBody = response.body().string(); // Get the response body
                    // Parse the response body to a list of SheetPermissionDto
                    List<SheetPermissionDto> availableSheets = new Gson().fromJson(responseBody, new TypeToken<List<SheetPermissionDto>>(){}.getType());
                    tableConsumer.accept(availableSheets);// Pass the data to the consumer
                } else {
                    // Show an alert if the response is not successful
                    Platform.runLater(() -> showAlert("Error", "Failed to fetch available sheets: " + response.message()));
                }
            }
        });
    }

    // Sets the active status of the refresher
    public void setActive(boolean active) {
        this.isActive = active; // Update the active flag
    }
}
