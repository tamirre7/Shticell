package shticell.server.permission.servlets;

import com.google.gson.Gson;
import command.api.Engine;
import dto.permission.PermissionResponseDto;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import shticell.server.utils.ServletUtils;

import java.io.IOException;
import java.io.InputStream;

@WebServlet(name = "PermissionResponseServlet", urlPatterns = {"/permissions/response"})
public class PermissionResponseServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            Engine engine = ServletUtils.getEngine(getServletContext());
            if (engine == null) {
                throw new ServletException("No engine found");
            }
            InputStream responseInputStream = req.getInputStream();
            String responseJson = new String(responseInputStream.readAllBytes());

            Gson gson = new Gson();

            PermissionResponseDto permissionResponseDto = gson.fromJson(responseJson, PermissionResponseDto.class);

            if(permissionResponseDto.isApproved())
            {
                engine.permissionApproval(permissionResponseDto);
            }else {
                engine.permissionDenial(permissionResponseDto);
            }
        }catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write("Error updating cell: " + e.getMessage());
        }
    }
}
