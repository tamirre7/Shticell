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
import jakarta.servlet.http.Part;
import shticell.server.utils.ServletUtils;
import spreadsheet.cell.impl.CellIdentifierImpl;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

@WebServlet(name = "AddRangeServlet", urlPatterns = {"/sheetview/addrange"})
public class AddRangeServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
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
            String topLeftCellStr = rangeData.get("topLeftCell");
            String bottomRightCellStr = rangeData.get("bottomRightCell");
            String sheetName = rangeData.get("sheetName");

            CellIdentifierImpl topLeft = new CellIdentifierImpl(topLeftCellStr);
            CellIdentifierImpl bottomRight = new CellIdentifierImpl(bottomRightCellStr);

            engine.setCurrentSheet(sheetName);
            SheetDto updatedSheet = engine.addRange(rangeName,topLeft,bottomRight);
            String jsonResp = gson.toJson(updatedSheet);
            resp.getWriter().write(jsonResp);

        }catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write("{\"status\":\"error\",\"message\":\"Error adding range: " + e.getMessage() + "\"}");
        }
    }
}
