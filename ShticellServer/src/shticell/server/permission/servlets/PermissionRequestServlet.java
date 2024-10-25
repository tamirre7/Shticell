package shticell.server.permission.servlets;

import com.google.gson.Gson;
import command.api.Engine;
import dto.permission.PermissionRequestDto;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import shticell.server.utils.ServletUtils;
import shticell.server.utils.SessionUtils;

import java.io.IOException;
import java.io.InputStream;

/**
 * Servlet for handling permission requests from users.
 */
@WebServlet(name = "PermissionRequestServlet", urlPatterns = {"/permissions/requestpermission"})
public class PermissionRequestServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            // Retrieve the username from the session
            String userName = SessionUtils.getUsername(request);
            Engine engine = ServletUtils.getEngine(getServletContext());
            if (engine == null) {
                throw new ServletException("No engine found");
            }

            // Read the JSON request body
            InputStream requestInputStream = request.getInputStream();
            String requestJson = new String(requestInputStream.readAllBytes());

            // Deserialize JSON to PermissionRequestDto
            Gson gson = new Gson();
            PermissionRequestDto permissionRequestDto = gson.fromJson(requestJson, PermissionRequestDto.class);

            // Process the permission request
            engine.permissionRequest(permissionRequestDto.getId(),
                    permissionRequestDto.getSheetName(),
                    permissionRequestDto.getPermissionType(),
                    permissionRequestDto.getMessage(),
                    userName);
        } catch (Exception e) {
            // Handle exceptions and send error response
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("Error setting sheet: " + e.getMessage());
        }
    }
}
