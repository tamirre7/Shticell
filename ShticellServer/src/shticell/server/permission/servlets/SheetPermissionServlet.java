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

/**
 * Servlet for retrieving permissions associated with a specific sheet.
 */
@WebServlet(name = "SheetPermissionsServlet", urlPatterns = {"/permissions/sheetpermissions"})
public class SheetPermissionServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json"); // Set the response content type to JSON
        try {
            // Retrieve the Engine instance from the servlet context
            Engine engine = ServletUtils.getEngine(getServletContext());
            if (engine == null) {
                throw new ServletException("No engine found");
            }

            // Get the sheet name from the request parameters
            String sheetName = req.getParameter("sheetName");

            // Retrieve all permissions for the specified sheet
            List<PermissionInfoDto> permissionInfoDtos = engine.getAllSheetPermissions(sheetName);

            // Convert the list of PermissionInfoDto to JSON format
            Gson gson = new Gson();
            String permissionDtosJson = gson.toJson(permissionInfoDtos);

            // Write the JSON response to the output
            resp.getWriter().write(permissionDtosJson);
        } catch (Exception e) {
            // Handle exceptions and send an error response
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write("Error loading file: " + e.getMessage());
        }
    }
}
