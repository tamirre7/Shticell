package shticell.server.sheetpanel.servlets.spreadsheet;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import command.api.Engine;
import dto.SheetDto;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletResponse;
import shticell.server.utils.ServletUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

@WebServlet(name = "CellStyleServlet", urlPatterns = {"/sheetview/updatecellstyle"})
public class CellStyleUpdateServlet extends HttpServlet {

    // Handles POST requests for updating the style of a cell
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

            // Read the input stream containing the cell parameters JSON
            InputStream cellParamsInputStream = req.getInputStream();
            String cellParamJson = new String(cellParamsInputStream.readAllBytes());

            // Initialize Gson for JSON processing
            Gson gson = new Gson();
            // Deserialize the JSON data into a Map for easy access
            Map<String, String> cellParams = gson.fromJson(cellParamJson, new TypeToken<Map<String, String>>(){}.getType());

            // Extract relevant data from the Map
            String cellId = cellParams.get("cellId");
            String style = cellParams.get("style");
            String sheetName = cellParams.get("sheetName");

            // Call the engine to set the cell style and retrieve the updated SheetDto
            SheetDto updatedSheet = engine.setCellStyle(cellId, style, sheetName);
            // Serialize the updated SheetDto to JSON and write it to the response
            String jsonResp = gson.toJson(updatedSheet);
            resp.getWriter().write(jsonResp);

        } catch (Exception e) {
            // Handle general exceptions by sending a 500 response
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write("Error updating cell's style: " + e.getMessage());
        }
    }
}
