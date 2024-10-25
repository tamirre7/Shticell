package shticell.server.sheethub.servlets.chat;

import chat.chatmanager.SingleChatEntry;
import chat.chatmanager.api.ChatManager;
import com.google.gson.Gson;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import shticell.server.utils.ServletUtils;
import shticell.server.utils.SessionUtils;
import shticell.server.utils.constants.Constants;

/**
 * Servlet for handling getting the chat lines requests.
 */
@WebServlet(name ="ChatServlet", urlPatterns = {"/chat/chatlines"})
public class ChatServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // Set response content type to JSON
        response.setContentType("application/json");

        // Retrieve the ChatManager instance
        ChatManager chatManager = ServletUtils.getChatManager(getServletContext());

        // Get the username from the session
        String username = SessionUtils.getUsername(request);
        if (username == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // Set status to UNAUTHORIZED if user is not logged in
            return;
        }

        // Retrieve the chat version parameter from the request
        int chatVersion = ServletUtils.getIntParameter(request, Constants.CHAT_VERSION_PARAMETER);
        if (chatVersion == Constants.INT_PARAMETER_ERROR) {
            return; // Return if there's an error with the parameter
        }

        int chatManagerVersion;
        List<SingleChatEntry> chatEntries;

        // Synchronize on the servlet context to ensure thread safety when accessing chat entries
        synchronized (getServletContext()) {
            chatManagerVersion = chatManager.getVersion(); // Get the current version of the chat
            chatEntries = chatManager.getChatEntries(chatVersion); // Get chat entries based on the provided version
        }

        // Create the response JSON string containing chat entries and version
        ChatAndVersion cav = new ChatAndVersion(chatEntries, chatManagerVersion);
        Gson gson = new Gson();
        String jsonResponse = gson.toJson(cav); // Convert to JSON format

        // Log server messages for debugging
        logServerMessage("Server Chat version: " + chatManagerVersion + ", User '" + username + "' Chat version: " + chatVersion);
        logServerMessage(jsonResponse);

        // Write the JSON response back to the client
        try (PrintWriter out = response.getWriter()) {
            out.print(jsonResponse);
            out.flush();
        }
    }

    // Helper method to log server messages
    private void logServerMessage(String message){
        System.out.println(message);
    }

    // Inner class to represent chat entries and version
    private static class ChatAndVersion {
        final private List<SingleChatEntry> entries; // List of chat entries
        final private int version; // Version of the chat

        public ChatAndVersion(List<SingleChatEntry> entries, int version) {
            this.entries = entries;
            this.version = version;
        }
    }
}
