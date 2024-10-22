package shticell.server.sheethub.servlets;

import com.google.gson.Gson;
import command.api.Engine;
import dto.SheetDto;
import dto.permission.SheetPermissionDto;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import shticell.server.utils.ServletUtils;
import shticell.server.utils.SessionUtils;

import java.io.IOException;
import java.util.List;

@WebServlet(name ="AvailableSheetsServlet", urlPatterns = {"/sheethub/availablesheets"})
public class AvailableSheetsServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        try {
            String userNameFromSession = SessionUtils.getUsername(req);
            Engine engine = ServletUtils.getEngine(getServletContext());
            if (engine == null) {
                throw new ServletException("No engine found");
            }
            Gson gson = new Gson();
            List<SheetPermissionDto> availableSheets = engine.getAllSheets(userNameFromSession);

            String availableSheetsJson = gson.toJson(availableSheets);
            resp.getWriter().write(availableSheetsJson);

        }catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write("Error setting sheet: " + e.getMessage());
        }
    }
}
