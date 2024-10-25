package chat.chatmanager.impl;

import chat.chatmanager.SingleChatEntry;
import chat.chatmanager.api.ChatManager;
import java.util.ArrayList;
import java.util.List;

public class ChatManagerImpl implements ChatManager {
    private final List<SingleChatEntry> chatDataList;

    public ChatManagerImpl() {
        chatDataList = new ArrayList<>();
    }

    // Adds a new chat entry to the chatDataList with the specified chat string and username.
    @Override
    public synchronized void addChatString(String chatString, String username) {
        chatDataList.add(new SingleChatEntry(chatString, username));
    }

    // Returns a list of chat entries starting from the specified index.
    // If the index is invalid, it defaults to 0.
    @Override
    public synchronized List<SingleChatEntry> getChatEntries(int fromIndex){
        if (fromIndex < 0 || fromIndex > chatDataList.size()) {
            fromIndex = 0;
        }
        return chatDataList.subList(fromIndex, chatDataList.size());
    }

    // Returns the current version of the chat, which is equal to the number of chat entries.
    @Override
    public int getVersion() {
        return chatDataList.size();
    }
}
