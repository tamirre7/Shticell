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
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        try {

            Engine engine = ServletUtils.getEngine(getServletContext());
            if (engine == null) {
                throw new ServletException("No engine found");
            }
            InputStream cellParamsInputStream  = req.getInputStream();

            String cellParamJson = new String(cellParamsInputStream.readAllBytes());

            Gson gson = new Gson();

            Map<String, String> cellParams = gson.fromJson(cellParamJson, new TypeToken<Map<String, String>>(){}.getType());

            String cellId = cellParams.get("cellId");
            String style = cellParams.get("style");
            String sheetName = cellParams.get("sheetName");

            SheetDto updatedSheet = engine.setCellStyle(cellId, style,sheetName);
            String jsonResp = gson.toJson(updatedSheet);
            resp.getWriter().write(jsonResp);

        }catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write("Error updating cell's style: " + e.getMessage());
        }

    }
}
