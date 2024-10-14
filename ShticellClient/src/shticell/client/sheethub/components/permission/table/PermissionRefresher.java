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
import java.util.stream.Collectors;

import static shticell.client.util.http.HttpClientUtil.showAlert;

public class PermissionRefresher extends TimerTask {
    private final Consumer<List<PermissionInfoDto>> permissionConsumer;
    private boolean isActive = true;
    private String sheetName;

    public PermissionRefresher(Consumer<List<PermissionInfoDto>> permissionConsumer, String sheetName) {
        this.permissionConsumer = permissionConsumer;
        this.sheetName = sheetName;
    }

    @Override
    public void run() {
        if(!isActive){
            return;
        }

        String finalUrl = HttpUrl
                .parse(Constants.SHEET_PERMISSIONS)
                .newBuilder()
                .addQueryParameter("sheetName", sheetName)
                .build()
                .toString();

        HttpClientUtil.runAsync(finalUrl, new Callback() {
            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseBody = response.body().string();
                    Platform.runLater(() -> {
                        List<PermissionInfoDto> permissions = new Gson().fromJson(responseBody, new TypeToken<List<PermissionInfoDto>>(){}.getType());
                       permissionConsumer.accept(permissions);
                    });
                } else {
                    Platform.runLater(() ->
                            showAlert("Error", "Failed to load permissions: " + response.message())
                    );
                }
            }

            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Platform.runLater(() ->
                        showAlert("Error", "An error occurred while loading permissions: " + e.getMessage())
                );
            }
        });
    }

    public void setActive(boolean active) {
        this.isActive = active;
    }
}
