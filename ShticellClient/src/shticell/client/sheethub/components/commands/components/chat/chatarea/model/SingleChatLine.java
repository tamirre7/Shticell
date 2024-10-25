package shticell.client.sheethub.components.commands.components.chat.chatarea.model;

public class SingleChatLine {
    private String chatString;      // Content of the chat message
    private String username;        // Username of the message sender
    private long time;             // Timestamp of the message

    // Gets the chat message content
    public String getChatString() {
        return chatString;
    }

    // Gets the username of message sender
    public String getUsername() {
        return username;
    }

    // Gets the message timestamp
    public long getTime() {
        return time;
    }
}
