package shticell.server.sheetpanel.servlets.dynamic.analysis;

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

@WebServlet(name = "DynamicAnalysisServlet", urlPatterns = {"/sheetview/dynamicanalysisupdate"})
public class DynamicAnalysisServlet extends HttpServlet {

    // Handles POST requests for dynamic analysis updates
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // Set the response content type to JSON
        resp.setContentType("application/json");
        try {
            // Retrieve the username from the session
            String userNameFromSession = SessionUtils.getUsername(req);
            // Obtain the Engine instance from the servlet context
            Engine engine = ServletUtils.getEngine(getServletContext());
            if (engine == null) {
                throw new ServletException("No engine found");
            }

            // Read the input stream containing the cell data JSON
            InputStream cellDataInputStream = req.getInputStream();
            String cellDataJson = new String(cellDataInputStream.readAllBytes());

            // Initialize Gson for JSON processing
            Gson gson = new Gson();

            // Deserialize the JSON data into a Map for easy access
            Map<String, String> cellData = gson.fromJson(cellDataJson, new TypeToken<Map<String, String>>(){}.getType());

            // Extract relevant data from the Map
            String sheetName = cellData.get("sheetName");
            String cellId = cellData.get("cellId");
            String cellOriginalValue = cellData.get("cellOriginalValue");
            int version = Integer.parseInt(cellData.get("version"));

            // Update the cell without changing the sheet version
            SheetDto updatedSheet = engine.updateCellWithoutSheetVersionUpdate(cellId, cellOriginalValue, userNameFromSession, sheetName, version);
            // Serialize the updated SheetDto to JSON and write it to the response
            String jsonResp = gson.toJson(updatedSheet);
            resp.getWriter().write(jsonResp);

        } catch (Exception e) {
            // Handle exceptions by setting the response status to 500 and writing the error message
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write("Error dynamic analysis: " + e.getMessage());
        }
    }
}
