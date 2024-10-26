package shticell.server.sheetpanel.servlets.range;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import command.api.Engine;
import dto.SheetDto;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import shticell.server.utils.ServletUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

@WebServlet(name = "DeleteRangeServlet", urlPatterns = {"/sheetview/deleterange"})
public class DeleteRangeServlet extends HttpServlet {

    // Handles DELETE requests for removing a range from a sheet
    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        // Set the response content type to JSON
        resp.setContentType("application/json");
        try {
            // Retrieve the Engine instance from the servlet context
            Engine engine = ServletUtils.getEngine(getServletContext());
            if (engine == null) {
                throw new ServletException("No engine found");
            }

            // Read the input stream containing the range data JSON
            InputStream rangeDataInputStream = req.getInputStream();
            String rangeDataJson = new String(rangeDataInputStream.readAllBytes());

            // Initialize Gson for JSON processing
            Gson gson = new Gson();

            // Deserialize the JSON data into a Map for easy access
            Map<String, String> rangeData = gson.fromJson(rangeDataJson, new TypeToken<Map<String, String>>(){}.getType());

            // Extract relevant data from the Map
            String rangeName = rangeData.get("rangeName");
            String sheetName = rangeData.get("sheetName");

            // Call the engine to remove the specified range and retrieve the updated SheetDto
            SheetDto updatedSheet = engine.removeRange(rangeName, sheetName);
            // Serialize the updated SheetDto to JSON and write it to the response
            String jsonResp = gson.toJson(updatedSheet);
            resp.getWriter().write(jsonResp);

        } catch (IllegalArgumentException e) {
            // Handle specific illegal argument exceptions by sending a 400 response
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("Error deleting range: " + e.getMessage());
        } catch (Exception e) {
            // Handle general exceptions by sending a 500 response
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write("Error deleting range: " + e.getMessage());
        }
    }
}
