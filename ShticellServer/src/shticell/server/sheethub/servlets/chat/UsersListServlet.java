package shticell.server.sheethub.servlets.chat;

import com.google.gson.Gson;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import shticell.server.utils.ServletUtils;
import usermanager.api.UserManager;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@WebServlet(name = "UsersListServlet", urlPatterns = {"/chat/userslist"})
public class UsersListServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        Gson gson = new Gson();
        UserManager userManager = ServletUtils.getUserManager(getServletContext());
        Set<String> usersList = userManager.getUsers();
        String json = gson.toJson(usersList);
        response.getWriter().write(json);
    }
}
