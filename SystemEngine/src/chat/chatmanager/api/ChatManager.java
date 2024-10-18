package chat.chatmanager.api;


import chat.chatmanager.SingleChatEntry;

import java.util.List;

public interface ChatManager {
    void addChatString(String chatString, String username);
    List<SingleChatEntry> getChatEntries(int fromIndex);
    int getVersion();
}
