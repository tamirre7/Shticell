package shticell.client.sheethub.components.commands.components.chat.chatarea.model;

import java.util.List;

public class ChatLinesWithVersion {
    private int version;                    // Version number of chat lines batch
    private List<SingleChatLine> entries;   // List of chat line entries

    // Gets the version number of the chat lines batch
    public int getVersion() {
        return version;
    }

    // Sets the version number of the chat lines batch
    public void setVersion(int version) {
        this.version = version;
    }

    // Gets the list of chat line entries
    public List<SingleChatLine> getEntries() {
        return entries;
    }

    // Sets the list of chat line entries
    public void setEntries(List<SingleChatLine> entries) {
        this.entries = entries;
    }
}