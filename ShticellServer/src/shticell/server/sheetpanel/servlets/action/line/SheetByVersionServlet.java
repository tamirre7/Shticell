package shticell.server.sheetpanel.servlets.action.line;

import com.google.gson.Gson;
import command.api.Engine;
import dto.SheetDto;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import shticell.server.utils.ServletUtils;

import java.io.IOException;

@WebServlet(name = "SheetByVersionServlet", urlPatterns = {"/sheetview/sheetbyversion"})
public class SheetByVersionServlet extends HttpServlet {

    // Handles GET requests to retrieve a sheet by its version number.
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // Set the response content type to application/json
        resp.setContentType("application/json");
        try {
            // Retrieve the version parameter from the request
            String versionParam = req.getParameter("version");
            // Retrieve the sheet name parameter from the request
            String sheetNameParam = req.getParameter("sheetName");

            // Parse the version parameter into an integer
            int version = Integer.parseInt(versionParam);

            // Obtain the Engine instance from the servlet context
            Engine engine = ServletUtils.getEngine(getServletContext());

            // Retrieve the SheetDto by version and sheet name
            SheetDto sheetDto = engine.displaySheetByVersion(version, sheetNameParam);

            // Convert the SheetDto to JSON format
            Gson gson = new Gson();
            String jsonResp = gson.toJson(sheetDto);

            // Write the JSON response to the output
            resp.getWriter().write(jsonResp);
        } catch (Exception e) {
            // Handle exceptions by setting the response status to 500 and writing the error message
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write("Error getting sheet by version: " + e.getMessage());
        }
    }
}
