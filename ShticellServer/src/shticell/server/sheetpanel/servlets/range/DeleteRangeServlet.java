package shticell.server.sheetpanel.servlets.range;

import com.google.gson.Gson;
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

@WebServlet(name = "DeleteRangeServlet", urlPatterns = {"/sheetview/deleterange"})
public class DeleteRangeServlet extends HttpServlet {
    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        try {
            Part rangeDataPart = req.getPart("rangeName");
            Engine engine = ServletUtils.getEngine(getServletContext());
            if (engine == null) {
                throw new ServletException("No engine found");
            }
            InputStream rangeDataInputStream = rangeDataPart.getInputStream();
            String rangeDataJson = new String(rangeDataInputStream.readAllBytes());

            Gson gson = new Gson();

            String rangeName = gson.fromJson(rangeDataJson, String.class);

            SheetDto updatedSheet = engine.removeRange(rangeName);
            String jsonResp = gson.toJson(updatedSheet);
            resp.getWriter().write(jsonResp);

        }catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write("{\"status\":\"error\",\"message\":\"Error deleting range: " + e.getMessage() + "\"}");
        }

    }
}
