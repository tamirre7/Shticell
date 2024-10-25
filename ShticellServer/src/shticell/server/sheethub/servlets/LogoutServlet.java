package shticell.server.sheethub.servlets;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import shticell.server.utils.ServletUtils;
import shticell.server.utils.SessionUtils;
import usermanager.api.UserManager;

import java.io.IOException;

@WebServlet(name = "LogoutServlet", urlPatterns = {"/logout"})
public class LogoutServlet extends HttpServlet {

    // Handles GET requests for logging out a user.
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // Retrieve the username from the session
        String usernameFromSession = SessionUtils.getUsername(request);

        // Get the UserManager instance from the servlet context
        UserManager userManager = ServletUtils.getUserManager(getServletContext());

        // Check if the user is logged in
        if (usernameFromSession != null) {
            // Log the username being cleared from the session
            System.out.println("Clearing session for " + usernameFromSession);

            // Remove the user from the UserManager
            userManager.removeUser(usernameFromSession);

            // Clear the session
            SessionUtils.clearSession(request);
        }
    }
}
