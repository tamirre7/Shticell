package shticell.server.sheethub.servlets;

import com.google.gson.Gson;
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
import java.util.List;

@WebServlet(name = "OwnedSheetsServlet",urlPatterns = {"/sheethub/ownedsheets"})
public class OwnedSheetsServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
       try {
           String userNameFromSession = SessionUtils.getUsername(req);
           Engine engine = ServletUtils.getEngine(getServletContext());
           if (engine == null) {
               throw new ServletException("No engine found");
           }
           List<SheetDto> ownedSheets = engine.getOwnedSheets(userNameFromSession);
           Gson gson = new Gson();
           String ownedSheetsJson = gson.toJson(ownedSheets);
           resp.setContentType("application/json");
           resp.getWriter().write(ownedSheetsJson);
       }catch (Exception e) {
           resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
           resp.getWriter().write("Error: " + e.getMessage());
       }
    }
}
