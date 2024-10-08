package shticell.client.sheethub.components.available.sheets;

import com.google.gson.Gson;
import dto.SheetDto;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;
import shticell.client.util.Constants;
import shticell.client.util.http.HttpClientUtil;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.TimerTask;
import java.util.function.Consumer;

import static shticell.client.util.Constants.GET_AVAILABLE_SHEETS;
import static shticell.client.util.http.HttpClientUtil.showAlert;

public class SheetTableRefresher extends TimerTask {


    private final Consumer<SheetDto[]> tableConsumer;
    private  boolean isActive = true;

    public SheetTableRefresher(Consumer<SheetDto[]> tableConsumer) {

        this.tableConsumer = tableConsumer;
    }

    @Override
    public void run() {
        if (!isActive) {
            return;
        }

        HttpClientUtil.runAsync(Constants.GET_AVAILABLE_SHEETS, new Callback() {

            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Platform.runLater(() -> showAlert("Error", e.getMessage()));

            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseBody = response.body().string();
                    SheetDto[] availableSheets = new Gson().fromJson(responseBody, SheetDto[].class);
                    tableConsumer.accept(availableSheets);
                } else {
                    Platform.runLater(() -> showAlert("Error", "Failed to fetch available sheets: " + response.message()));
                }
            }
        });
    }

    public void setActive(boolean active) {
        this.isActive = active;
    }

}

