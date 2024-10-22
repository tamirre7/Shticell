package shticell.server.permission.servlets;

import com.google.gson.Gson;
import command.api.Engine;
import dto.permission.PermissionInfoDto;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import shticell.server.utils.ServletUtils;
import shticell.server.utils.SessionUtils;

import java.io.IOException;

@WebServlet(name = "UserSheetPermissionServlet", urlPatterns = {"/permissions/userpermission"})
public class UserSheetPermissionServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        try {
            String userNameFromSession = SessionUtils.getUsername(req);
            Engine engine = ServletUtils.getEngine(getServletContext());
            if (engine == null) {
                throw new ServletException("No engine found");
            }
            String sheetName = req.getParameter("sheetName");
            PermissionInfoDto permissionInfoDto = engine.getUserPermissionFromSheet(userNameFromSession, sheetName);
            Gson gson = new Gson();
            String permissionJson = gson.toJson(permissionInfoDto);
            resp.getWriter().write(permissionJson);
        }catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write("Error loading file: " + e.getMessage());
        }

    }
}
