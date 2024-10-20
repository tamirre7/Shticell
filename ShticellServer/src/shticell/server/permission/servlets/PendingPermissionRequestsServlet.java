package shticell.server.permission.servlets;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import command.api.Engine;
import dto.permission.PermissionRequestDto;
import dto.SheetDto;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import shticell.server.utils.ServletUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@WebServlet(name = "PendingPermissionRequestsServlet",urlPatterns = {"/permissions/pendingrequests"})
public class PendingPermissionRequestsServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            Engine engine = ServletUtils.getEngine(getServletContext());
            if (engine == null) {
                throw new ServletException("No engine found");
            }
            InputStream sheetsInputStream = req.getInputStream();
            String sheetsJson = new String(sheetsInputStream.readAllBytes());

            Gson gson = new Gson();
            List<SheetDto> sheetDtoList = new Gson().fromJson(sheetsJson, new TypeToken<List<SheetDto>>(){}.getType());
            List<PermissionRequestDto> permissionRequestDtos = new ArrayList<>();
            for (SheetDto sheetDto : sheetDtoList) {
                List<PermissionRequestDto> sheetPendingPermissionRequest = engine.getPendingRequests(sheetDto.getSheetName());
                permissionRequestDtos.addAll(sheetPendingPermissionRequest);
            }
            String permissionRequestJson = gson.toJson(permissionRequestDtos);
            resp.setContentType("application/json");
            resp.getWriter().write(permissionRequestJson);

        }catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write("{\"status\":\"error\",\"message\":\"Error setting sheet: " + e.getMessage() + "\"}");
        }
    }
}