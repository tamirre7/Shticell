package shticell.server.permission.servlets;

import com.google.gson.Gson;
import command.api.Engine;
import dto.permission.PermissionInfoDto;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import shticell.server.utils.ServletUtils;
import shticell.server.utils.SessionUtils;

import java.io.IOException;

/**
 * Servlet for retrieving a user's permissions for a specific sheet.
 */
@WebServlet(name = "UserSheetPermissionServlet", urlPatterns = {"/permissions/userpermission"})
public class UserSheetPermissionServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json"); // Set the response content type to JSON
        try {
            // Retrieve the username from the session
            String userNameFromSession = SessionUtils.getUsername(req);

            // Get the Engine instance from the servlet context
            Engine engine = ServletUtils.getEngine(getServletContext());
            if (engine == null) {
                throw new ServletException("No engine found");
            }

            // Get the sheet name from the request parameters
            String sheetName = req.getParameter("sheetName");

            // Retrieve the user's permission information for the specified sheet
            PermissionInfoDto permissionInfoDto = engine.getUserPermissionFromSheet(userNameFromSession, sheetName);

            // Convert the PermissionInfoDto to JSON format
            Gson gson = new Gson();
            String permissionJson = gson.toJson(permissionInfoDto);

            // Write the JSON response to the output
            resp.getWriter().write(permissionJson);
        } catch (Exception e) {
            // Handle exceptions and send an error response
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write("Error loading file: " + e.getMessage());
        }
    }
}
