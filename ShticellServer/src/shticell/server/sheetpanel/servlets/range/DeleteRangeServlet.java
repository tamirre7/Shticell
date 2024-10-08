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
    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        try {

            Engine engine = ServletUtils.getEngine(getServletContext());
            if (engine == null) {
                throw new ServletException("No engine found");
            }
            InputStream rangeDataInputStream = req.getInputStream();
            String rangeDataJson = new String(rangeDataInputStream.readAllBytes());

            Gson gson = new Gson();

            Map<String, String> rangeData = gson.fromJson(rangeDataJson, new TypeToken<Map<String, String>>(){}.getType());

            String rangeName = rangeData.get("rangeName");
            String sheetName = rangeData.get("sheetName");

            engine.setCurrentSheet(sheetName);
            SheetDto updatedSheet = engine.removeRange(rangeName);
            String jsonResp = gson.toJson(updatedSheet);
            resp.getWriter().write(jsonResp);

        }catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write("{\"status\":\"error\",\"message\":\"Error deleting range: " + e.getMessage() + "\"}");
        }

    }
}
