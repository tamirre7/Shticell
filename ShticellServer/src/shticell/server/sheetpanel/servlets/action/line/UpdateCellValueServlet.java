package shticell.server.sheetpanel.servlets.action.line;

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
import shticell.server.utils.SessionUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

@WebServlet(name = "UpdateCellServlet", urlPatterns = {"/sheetview/updatecell"})
public class UpdateCellValueServlet extends HttpServlet {

    // Handles POST requests to update a cell's value in a spreadsheet
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // Set the response content type to application/json
        resp.setContentType("application/json");
        try {
            // Retrieve the username from the session
            String userNameFromSession = SessionUtils.getUsername(req);
            // Obtain the Engine instance from the servlet context
            Engine engine = ServletUtils.getEngine(getServletContext());
            if (engine == null) {
                throw new ServletException("No engine found");
            }
            // Read the input stream containing the cell data
            InputStream cellDataIS = req.getInputStream();
            String cellData = new String(cellDataIS.readAllBytes());

            Gson gson = new Gson();
            // Deserialize the JSON data into a Map
            Map<String, String> cellDetails = gson.fromJson(cellData, new TypeToken<Map<String, String>>(){}.getType());
            // Extract individual cell details from the Map
            String sheetName = cellDetails.get("sheetName");
            String cellId = cellDetails.get("cellid");
            String value = cellDetails.get("newvalue");
            int version = Integer.parseInt(cellDetails.get("version"));

            // Update the cell and get the updated SheetDto
            SheetDto updatedSheet = engine.updateCellWithSheetVersionUpdate(cellId, value, userNameFromSession, sheetName, version);
            // Convert the updated SheetDto to JSON format
            String jsonResp = gson.toJson(updatedSheet);
            // Write the JSON response to the output
            resp.getWriter().write(jsonResp);

        } catch (Exception e) {
            // Handle exceptions by setting the response status to 500 and writing the error message
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write("Error updating cell: " + e.getMessage());
        }
    }
}
