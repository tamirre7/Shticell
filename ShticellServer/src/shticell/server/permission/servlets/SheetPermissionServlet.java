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

import java.io.IOException;
import java.util.List;

@WebServlet(name="SheetPermissionsServlet", urlPatterns = {"/permissions/sheetpermissions"})
public class SheetPermissionServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        try {

            Engine engine = ServletUtils.getEngine(getServletContext());
            if (engine == null) {
                throw new ServletException("No engine found");
            }
            String sheetName = req.getParameter("sheetName");
            List<PermissionInfoDto> permissionInfoDtos = engine.getAllSheetPermissions(sheetName);
            Gson gson = new Gson();
            String permissionDtosJson = gson.toJson(permissionInfoDtos);
            resp.getWriter().write(permissionDtosJson);
        }catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write("Error loading file: " + e.getMessage());
        }
    }
}
