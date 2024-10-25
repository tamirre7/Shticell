package shticell.client.sheethub.components.commands.components.chat.chatarea.impl;

import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.ToggleButton;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;
import shticell.client.sheethub.components.commands.components.chat.api.HttpStatusUpdate;
import shticell.client.sheethub.components.commands.components.chat.chatarea.ChatAreaRefresher;
import shticell.client.sheethub.components.commands.components.chat.chatarea.api.ChatAreaController;
import shticell.client.sheethub.components.commands.components.chat.chatarea.model.ChatLinesWithVersion;
import shticell.client.util.Constants;
import shticell.client.util.http.HttpClientUtil;

import java.io.IOException;
import java.util.Timer;
import java.util.stream.Collectors;

import static shticell.client.util.Constants.CHAT_LINE_FORMATTING;
import static shticell.client.util.Constants.REFRESH_RATE;

public class ChatAreaControllerImpl implements ChatAreaController {
    private final IntegerProperty chatVersion;    // Current version of chat messages
    private final BooleanProperty autoScroll;     // Flag for auto-scrolling chat
    private final BooleanProperty autoUpdate;     // Flag for auto-updating chat
    private HttpStatusUpdate httpStatusUpdate;    // Callback for HTTP status updates
    private ChatAreaRefresher chatAreaRefresher; // Timer task for refreshing chat
    private Timer timer;                         // Timer for scheduling updates

    @FXML private ToggleButton autoScrollButton;  // Button to toggle auto-scroll
    @FXML private TextArea chatLineTextArea;      // Input area for new messages
    @FXML private TextArea mainChatLinesTextArea; // Display area for chat messages

    // Initializes controller with default properties
    public ChatAreaControllerImpl() {
        chatVersion = new SimpleIntegerProperty();
        autoScroll = new SimpleBooleanProperty();
        autoUpdate = new SimpleBooleanProperty();
    }

    // Binds auto-scroll property to toggle button
    @FXML
    public void initialize() {
        autoScroll.bind(autoScrollButton.selectedProperty());
    }

    // Returns the auto-update property
    @Override
    public BooleanProperty autoUpdatesProperty() {
        return autoUpdate;
    }

    // Handles sending a new chat message
    @FXML
    @Override
    public void sendButtonClicked(ActionEvent event) {
        String chatLine = chatLineTextArea.getText();
        String finalUrl = HttpUrl
                .parse(Constants.SEND_CHAT_LINE)
                .newBuilder()
                .addQueryParameter("userstring", chatLine)
                .build()
                .toString();

        httpStatusUpdate.updateHttpLine(finalUrl);
        HttpClientUtil.runAsync(finalUrl, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                httpStatusUpdate.updateHttpLine("Attempt to send chat line [" + chatLine + "] request ended with failure...:(");
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (!response.isSuccessful()) {
                    httpStatusUpdate.updateHttpLine("Attempt to send chat line [" + chatLine + "] request ended with failure. Error code: " + response.code());
                }
            }
        });

        chatLineTextArea.clear();
    }

    // Sets the HTTP status update callback
    @Override
    public void setHttpStatusUpdate(HttpStatusUpdate chatRoomMainController) {
        this.httpStatusUpdate = chatRoomMainController;
    }

    // Updates the chat display with new messages
    private void updateChatLines(ChatLinesWithVersion chatLinesWithVersion) {
        if (chatLinesWithVersion.getVersion() != chatVersion.get()) {
            String deltaChatLines = chatLinesWithVersion
                    .getEntries()
                    .stream()
                    .map(singleChatLine -> {
                        long time = singleChatLine.getTime();
                        return String.format(CHAT_LINE_FORMATTING, time, time, time, singleChatLine.getUsername(), singleChatLine.getChatString());
                    }).collect(Collectors.joining());

            Platform.runLater(() -> {
                chatVersion.set(chatLinesWithVersion.getVersion());

                if (autoScroll.get()) {
                    mainChatLinesTextArea.appendText(deltaChatLines);
                    mainChatLinesTextArea.selectPositionCaret(mainChatLinesTextArea.getLength());
                    mainChatLinesTextArea.deselect();
                } else {
                    int originalCaretPosition = mainChatLinesTextArea.getCaretPosition();
                    mainChatLinesTextArea.appendText(deltaChatLines);
                    mainChatLinesTextArea.positionCaret(originalCaretPosition);
                }
            });
        }
    }

    // Starts the periodic chat refresh timer
    @Override
    public void startListRefresher() {
        chatAreaRefresher = new ChatAreaRefresher(
                chatVersion,
                autoUpdate,
                httpStatusUpdate::updateHttpLine,
                this::updateChatLines);
        timer = new Timer();
        timer.schedule(chatAreaRefresher, REFRESH_RATE, REFRESH_RATE);
    }

    // Cleans up resources and stops updates
    @Override
    public void close() throws IOException {
        chatLineTextArea.clear();
        if (chatAreaRefresher != null && timer != null) {
            chatAreaRefresher.cancel();
            timer.cancel();
        }
    }
}