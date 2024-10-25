package shticell.client.sheethub.components.commands.components.chat.chatarea;

import com.google.gson.Gson;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;
import shticell.client.sheethub.components.commands.components.chat.chatarea.model.ChatLinesWithVersion;
import shticell.client.util.Constants;
import shticell.client.util.http.HttpClientUtil;

import java.io.IOException;
import java.util.TimerTask;
import java.util.function.Consumer;

public class ChatAreaRefresher extends TimerTask {
    private final Consumer<String> httpRequestLoggerConsumer;         // Logger for HTTP requests
    private final Consumer<ChatLinesWithVersion> chatlinesConsumer;   // Consumer for processing chat lines
    private final IntegerProperty chatVersion;                        // Current chat version
    private final BooleanProperty shouldUpdate;                       // Flag for enabling updates
    private int requestNumber;                                        // Counter for HTTP requests

    // Initializes the refresher with required dependencies
    public ChatAreaRefresher(IntegerProperty chatVersion, BooleanProperty shouldUpdate,
                             Consumer<String> httpRequestLoggerConsumer,
                             Consumer<ChatLinesWithVersion> chatlinesConsumer) {
        this.httpRequestLoggerConsumer = httpRequestLoggerConsumer;
        this.chatlinesConsumer = chatlinesConsumer;
        this.chatVersion = chatVersion;
        this.shouldUpdate = shouldUpdate;
        requestNumber = 0;
    }

    // Executes the refresh task to fetch new chat messages
    @Override
    public void run() {
        if (!shouldUpdate.get()) {
            return;
        }

        final int finalRequestNumber = ++requestNumber;

        //noinspection ConstantConditions
        String finalUrl = HttpUrl
                .parse(Constants.CHAT_LINES_LIST)
                .newBuilder()
                .addQueryParameter("chatversion", String.valueOf(chatVersion.get()))
                .build()
                .toString();

        httpRequestLoggerConsumer.accept("About to invoke: " + finalUrl + " | Chat Request # " + finalRequestNumber);

        HttpClientUtil.runAsync(finalUrl, new Callback() {
            // Handles HTTP request failure
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                httpRequestLoggerConsumer.accept("Something went wrong with Chat Request # " + finalRequestNumber);
            }

            // Handles successful HTTP response
            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    String rawBody = response.body().string();
                    httpRequestLoggerConsumer.accept("Response of Chat Request # " + finalRequestNumber + ": " + rawBody);
                    Gson gson = new Gson();
                    ChatLinesWithVersion chatLinesWithVersion = gson.fromJson(rawBody, ChatLinesWithVersion.class);
                    chatlinesConsumer.accept(chatLinesWithVersion);
                } else {
                    httpRequestLoggerConsumer.accept("Something went wrong with Request # " + finalRequestNumber + ". Code is " + response.code());
                }
            }
        });
    }
}