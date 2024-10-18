package shticell.client.sheethub.components.commands.components.chat.chatarea.impl;

import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
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

import java.io.Closeable;
import java.io.IOException;
import java.util.Timer;
import java.util.stream.Collectors;

import static shticell.client.util.Constants.CHAT_LINE_FORMATTING;
import static shticell.client.util.Constants.REFRESH_RATE;

public class ChatAreaControllerImpl implements ChatAreaController {
    private final IntegerProperty chatVersion;
    private final BooleanProperty autoScroll;
    private final BooleanProperty autoUpdate;
    private HttpStatusUpdate httpStatusUpdate;
    private ChatAreaRefresher chatAreaRefresher;
    private Timer timer;

    @FXML
    private ToggleButton autoScrollButton;
    @FXML private TextArea chatLineTextArea;
    @FXML private TextArea mainChatLinesTextArea;
    @FXML private Label chatVersionLabel;

    public ChatAreaControllerImpl() {
        chatVersion = new SimpleIntegerProperty();
        autoScroll = new SimpleBooleanProperty();
        autoUpdate = new SimpleBooleanProperty();
    }

    @FXML
    public void initialize() {
        autoScroll.bind(autoScrollButton.selectedProperty());
        chatVersionLabel.textProperty().bind(Bindings.concat("Chat Version: ", chatVersion.asString()));
    }
    @Override
    public BooleanProperty autoUpdatesProperty() {
        return autoUpdate;
    }

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
    @Override
    public void setHttpStatusUpdate(HttpStatusUpdate chatRoomMainController) {
        this.httpStatusUpdate = chatRoomMainController;
    }

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

    @Override
    public void close() throws IOException {
        chatVersion.set(0);
        chatLineTextArea.clear();
        if (chatAreaRefresher != null && timer != null) {
            chatAreaRefresher.cancel();
            timer.cancel();
        }
    }
}
