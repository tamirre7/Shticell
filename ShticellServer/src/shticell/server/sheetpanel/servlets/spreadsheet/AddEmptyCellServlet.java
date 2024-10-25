package shticell.server.sheetpanel.servlets.spreadsheet;

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
import java.util.List;
import java.util.Map;

@WebServlet(name = "AddEmptyCellServlet", urlPatterns = {"/sheetview/addemptycells"})
public class AddEmptyCellServlet extends HttpServlet {

    // Handles POST requests for adding an empty cell to a specified sheet
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // Set the response content type to JSON
        resp.setContentType("application/json");
        try {
            // Retrieve the Engine instance from the servlet context
            Engine engine = ServletUtils.getEngine(getServletContext());
            if (engine == null) {
                throw new ServletException("No engine found");
            }

            // Read the input stream containing the cell ID JSON
            InputStream cellIdInputStream = req.getInputStream();
            String cellIdJson = new String(cellIdInputStream.readAllBytes());

            // Initialize Gson for JSON processing
            Gson gson = new Gson();
            // Deserialize the JSON data into a Map for easy access
            Map<String, Object> cellParams = gson.fromJson(cellIdJson, new TypeToken<Map<String, Object>>(){}.getType());

            // Extract relevant data from the Map
            List<String> cellIds = (List<String>) cellParams.get("cellIds");
            String sheetName = (String) cellParams.get("sheetName");


            // Call the engine to add an empty cell and retrieve the updated SheetDto
            SheetDto updatedSheet = engine.addEmptyCells(cellIds, sheetName);
            // Serialize the updated SheetDto to JSON and write it to the response
            String jsonResp = gson.toJson(updatedSheet);
            resp.getWriter().write(jsonResp);

        } catch (Exception e) {
            // Handle general exceptions by sending a 500 response
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write("Error adding cell: " + e.getMessage());
        }
    }
}
