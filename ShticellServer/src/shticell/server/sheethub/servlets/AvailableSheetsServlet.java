package shticell.server.sheethub.servlets;

import com.google.gson.Gson;
import command.api.Engine;
import dto.permission.SheetPermissionDto;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import shticell.server.utils.ServletUtils;
import shticell.server.utils.SessionUtils;

import java.io.IOException;
import java.util.List;

/**
 * Servlet for retrieving the list of available sheets for the logged-in user.
 */
@WebServlet(name ="AvailableSheetsServlet", urlPatterns = {"/sheethub/availablesheets"})
public class AvailableSheetsServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // Set the response content type to application/json
        resp.setContentType("application/json");

        try {
            // Retrieve the username from the session
            String userNameFromSession = SessionUtils.getUsername(req);

            // Get the Engine instance from the servlet context
            Engine engine = ServletUtils.getEngine(getServletContext());
            if (engine == null) {
                throw new ServletException("No engine found");
            }

            // Create a Gson instance for converting the available sheets to JSON format
            Gson gson = new Gson();

            // Retrieve the list of available sheets for the user
            List<SheetPermissionDto> availableSheets = engine.getAllSheets(userNameFromSession);

            // Convert the available sheets to a JSON string
            String availableSheetsJson = gson.toJson(availableSheets);

            // Write the JSON string to the response
            resp.getWriter().write(availableSheetsJson);

        } catch (Exception e) {
            // Handle exceptions by setting the response status to 500 and writing the error message
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write("Error setting sheet: " + e.getMessage());
        }
    }
}
