package shticell.server.sheetpanel.servlets.action.line;

import com.google.gson.Gson;
import command.api.Engine;
import dto.SheetDto;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import shticell.server.utils.ServletUtils;

import java.io.IOException;

@WebServlet(name = "SheetByVersionServlet", urlPatterns = {"/sheetview/sheetbyversion"})
public class SheetByVersionServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        try {
            String versionParam = req.getParameter("version");
            int version = Integer.parseInt(versionParam);
            Engine engine = ServletUtils.getEngine(getServletContext());
            SheetDto sheetDto = engine.displaySheetByVersion(version);
            Gson gson = new Gson();
            String jsonResp = gson.toJson(sheetDto);
            resp.getWriter().write(jsonResp);
        }
        catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write("{\"status\":\"error\",\"message\":\"Error getting sheet by version: " + e.getMessage() + "\"}");
        }
    }
}
