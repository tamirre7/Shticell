package shticell.server.sheetpanel.servlets.action.line;

import command.api.Engine;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import shticell.server.utils.ServletUtils;

import java.io.IOException;

@WebServlet(name = "LatestVersionServlet" , urlPatterns = {"/sheetview/latestversion"})
public class LatestVersionServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/plain;charset=UTF-8");
        try{
            String sheetName = req.getParameter("sheetName");
            Engine engine = ServletUtils.getEngine(getServletContext());
            engine.setCurrentSheet(sheetName);
            int latestVersion = engine.getLatestVersion();
            resp.getWriter().print(latestVersion);

        }catch (Exception e){
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write("{\"status\":\"error\",\"message\":\"Error getting version: " + e.getMessage() + "\"}");

        }
    }
}
