package shticell.client.sheethub.components.commands.components.chat.chatarea.model;

import java.util.ArrayList;
import java.util.List;

public class ChatLines {
    private List<SingleChatLine> entries;

    public ChatLines() {
        this.entries = new ArrayList<>();
    }

    public List<SingleChatLine> getEntries() {
        return entries;
    }
}
