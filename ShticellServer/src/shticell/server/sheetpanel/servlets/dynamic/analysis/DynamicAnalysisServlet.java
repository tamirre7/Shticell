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
import jakarta.servlet.http.Part;
import shticell.server.utils.ServletUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

@WebServlet(name = "DynamicAnalysisServlet" , urlPatterns = {"/sheetview/dynamicanalysisupdate"})
public class DynamicAnalysisServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        try {
            Part cellDataPart = req.getPart("cellData");
            Engine engine = ServletUtils.getEngine(getServletContext());
            if (engine == null) {
                throw new ServletException("No engine found");
            }
            InputStream cellDataInputStream = cellDataPart.getInputStream();
            String cellDataJson = new String(cellDataInputStream.readAllBytes());

            Gson gson = new Gson();

            Map<String, String> cellData = gson.fromJson(cellDataJson, new TypeToken<Map<String, String>>(){}.getType());

            String cellId = cellData.get("cellId");
            String cellOriginalValue = cellData.get("cellOriginalValue");

            SheetDto updatedSheet = engine.updateCellWithoutSheetVersionUpdate(cellId, cellOriginalValue);
            String jsonResp = gson.toJson(updatedSheet);
            resp.getWriter().write(jsonResp);

        }catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write("{\"status\":\"error\",\"message\":\"Error dynamic analysis: " + e.getMessage() + "\"}");
        }
    }

}
