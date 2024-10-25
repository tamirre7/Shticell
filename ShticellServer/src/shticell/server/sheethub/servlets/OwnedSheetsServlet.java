package shticell.server.sheethub.servlets;

import com.google.gson.Gson;
import command.api.Engine;
import dto.SheetDto;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import shticell.server.utils.ServletUtils;
import shticell.server.utils.SessionUtils;

import java.io.IOException;
import java.util.List;

@WebServlet(name = "OwnedSheetsServlet", urlPatterns = {"/sheethub/ownedsheets"})
public class OwnedSheetsServlet extends HttpServlet {

    // Handles GET requests to retrieve owned sheets for the logged-in user.
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            // Retrieve the username from the session
            String userNameFromSession = SessionUtils.getUsername(req);

            // Get the Engine instance from the servlet context
            Engine engine = ServletUtils.getEngine(getServletContext());
            if (engine == null) {
                // Throw an exception if the engine is not found
                throw new ServletException("No engine found");
            }

            // Retrieve the list of sheets owned by the user
            List<SheetDto> ownedSheets = engine.getOwnedSheets(userNameFromSession);

            // Convert the list of owned sheets to JSON format
            Gson gson = new Gson();
            String ownedSheetsJson = gson.toJson(ownedSheets);

            // Set the response content type to application/json
            resp.setContentType("application/json");
            // Write the JSON data to the response
            resp.getWriter().write(ownedSheetsJson);
        } catch (Exception e) {
            // Handle exceptions by setting the response status to 500 and writing the error message
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write("Error: " + e.getMessage());
        }
    }
}
