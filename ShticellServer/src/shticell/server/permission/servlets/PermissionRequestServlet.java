package shticell.server.permission.servlets;

import com.google.gson.Gson;
import command.api.Engine;
import dto.PermissionRequestDto;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import shticell.server.utils.ServletUtils;
import shticell.server.utils.SessionUtils;

import java.io.IOException;
import java.io.InputStream;

@WebServlet(name = "", urlPatterns = {"/permissions/requestpermission"})
public class PermissionRequestServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            String userName = SessionUtils.getUsername(request);
            Engine engine = ServletUtils.getEngine(getServletContext());
            if (engine == null) {
                throw new ServletException("No engine found");
            }
            InputStream requestInputStream = request.getInputStream();

            String requestJson = new String(requestInputStream.readAllBytes());
            Gson gson = new Gson();
            PermissionRequestDto permissionRequestDto = gson.fromJson(requestJson, PermissionRequestDto.class);
            engine.permissionRequest(permissionRequestDto.getSheetName(),permissionRequestDto.getPermissionType(),permissionRequestDto.getMessage(),userName);
        }catch (Exception e)
        {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"status\":\"error\",\"message\":\"Error setting sheet: " + e.getMessage() + "\"}");
        }

    }
}
