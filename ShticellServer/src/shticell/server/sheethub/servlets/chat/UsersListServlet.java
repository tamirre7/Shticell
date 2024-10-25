package shticell.server.sheethub.servlets.chat;

import com.google.gson.Gson;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import shticell.server.utils.ServletUtils;
import usermanager.api.UserManager;

import java.io.IOException;
import java.util.Set;

/**
 * Servlet for retrieving the list of users in the chat.
 */
@WebServlet(name = "UsersListServlet", urlPatterns = {"/chat/userslist"})
public class UsersListServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // Set the response content type to application/json
        response.setContentType("application/json");

        // Create a Gson instance for converting the user list to JSON format
        Gson gson = new Gson();

        // Retrieve the UserManager instance from the servlet context
        UserManager userManager = ServletUtils.getUserManager(getServletContext());

        // Get the list of users from the UserManager
        Set<String> usersList = userManager.getUsers();

        // Convert the user list to a JSON string
        String json = gson.toJson(usersList);

        // Write the JSON string to the response
        response.getWriter().write(json);
    }
}
