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

    @Override
    public synchronized void addChatString(String chatString, String username) {
        chatDataList.add(new SingleChatEntry(chatString, username));
    }
    @Override
    public synchronized List<SingleChatEntry> getChatEntries(int fromIndex){
        if (fromIndex < 0 || fromIndex > chatDataList.size()) {
            fromIndex = 0;
        }
        return chatDataList.subList(fromIndex, chatDataList.size());
    }
    @Override
    public int getVersion() {
        return chatDataList.size();
    }

}
