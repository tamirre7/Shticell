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
import jakarta.servlet.http.Part;
import shticell.server.utils.ServletUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

@WebServlet(name = "UpdateCellServlet", urlPatterns = {"/sheetview/updatecell"})
public class UpdateCellValueServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
       resp.setContentType("application/json");

        try {
            Engine engine = ServletUtils.getEngine(getServletContext());
            if (engine == null) {
                throw new ServletException("No engine found");
            }
            InputStream cellDataIS = req.getInputStream();
            String cellData = new String(cellDataIS.readAllBytes());

            Gson gson = new Gson();

            Map<String, String> cellDetails = gson.fromJson(cellData, new TypeToken<Map<String, String>>(){}.getType());

            String cellId = cellDetails.get("cellid");
            String value = cellDetails.get("newvalue");

            SheetDto updatedSheet = engine.updateCellWithSheetVersionUpdate(cellId, value);
            String jsonResp = gson.toJson(updatedSheet);
            resp.getWriter().write(jsonResp);

        }catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write("{\"status\":\"error\",\"message\":\"Error updating cell: " + e.getMessage() + "\"}");
        }
    }

}
