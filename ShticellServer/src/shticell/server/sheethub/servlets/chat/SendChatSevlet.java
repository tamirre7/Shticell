package shticell.server.sheethub.servlets.chat;

import chat.chatmanager.api.ChatManager;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import shticell.server.utils.ServletUtils;
import shticell.server.utils.SessionUtils;
import shticell.server.utils.constants.Constants;

@WebServlet(name = "SendChatServlet",urlPatterns = {"/chat/sendChat"})
public class SendChatSevlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) {
        ChatManager chatManager = ServletUtils.getChatManager(getServletContext());
        String username = SessionUtils.getUsername(request);
        if (username == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        }

        String userChatString = request.getParameter(Constants.CHAT_PARAMETER);
        if (userChatString != null && !userChatString.isEmpty()) {
            logServerMessage("Adding chat string from " + username + ": " + userChatString);
            synchronized (getServletContext()) {
                chatManager.addChatString(userChatString, username);
            }
        }

    }

    private void logServerMessage(String message) {
        System.out.println(message);
    }
}