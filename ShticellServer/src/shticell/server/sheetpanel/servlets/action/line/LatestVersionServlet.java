package shticell.server.sheetpanel.servlets.action.line;

import command.api.Engine;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import shticell.server.utils.ServletUtils;

import java.io.IOException;

@WebServlet(name = "LatestVersionServlet", urlPatterns = {"/sheetview/latestversion"})
public class LatestVersionServlet extends HttpServlet {

    // Handles GET requests to retrieve the latest version of a specified sheet.
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // Set the response content type to plain text with UTF-8 encoding
        resp.setContentType("text/plain;charset=UTF-8");
        try {
            // Retrieve the sheet name from the request parameters
            String sheetName = req.getParameter("sheetName");

            // Get the Engine instance from the servlet context
            Engine engine = ServletUtils.getEngine(getServletContext());

            // Retrieve the latest version of the specified sheet
            int latestVersion = engine.getLatestVersion(sheetName);

            // Write the latest version to the response
            resp.getWriter().print(latestVersion);

        } catch (Exception e) {
            // Handle exceptions by setting the response status to 500 and writing the error message
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write("Error getting version: " + e.getMessage());
        }
    }
}
