package shticell.server.sheethub.servlets.chat;

import chat.chatmanager.api.ChatManager;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import shticell.server.utils.ServletUtils;
import shticell.server.utils.SessionUtils;
import shticell.server.utils.constants.Constants;

/**
 * Servlet for handling sending chat messages.
 */
@WebServlet(name = "SendChatServlet", urlPatterns = {"/chat/sendChat"})
public class SendChatSevlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) {
        // Retrieve the ChatManager instance
        ChatManager chatManager = ServletUtils.getChatManager(getServletContext());

        // Get the username from the session
        String username = SessionUtils.getUsername(request);
        if (username == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // Set status to UNAUTHORIZED if user is not logged in
            return; // Exit if the user is not authorized
        }

        // Retrieve the chat message from the request parameters
        String userChatString = request.getParameter(Constants.CHAT_PARAMETER);
        if (userChatString != null && !userChatString.isEmpty()) {
            logServerMessage("Adding chat string from " + username + ": " + userChatString); // Log the chat message
            // Synchronize access to the chat manager to ensure thread safety
            synchronized (getServletContext()) {
                chatManager.addChatString(userChatString, username); // Add the chat message to the chat manager
            }
        }
    }

    // Helper method to log server messages
    private void logServerMessage(String message) {
        System.out.println(message);
    }
}
