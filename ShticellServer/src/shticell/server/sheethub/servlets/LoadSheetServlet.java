package shticell.server.sheethub.servlets;

import com.google.gson.Gson;
import command.api.Engine;
import dto.SaveLoadFileDto;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;
import shticell.server.utils.ServletUtils;
import shticell.server.utils.SessionUtils;

import java.io.IOException;
import java.io.InputStream;

@WebServlet(name = "LoadSheetServlet", urlPatterns = {"/sheethub/loadsheet"})
@MultipartConfig
public class LoadSheetServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");

        try {
            String userNameFromSession = SessionUtils.getUsername(req);
            Part filePart = req.getPart("file");
            Engine engine = ServletUtils.getEngine(getServletContext());
            if (engine == null) {
                throw new ServletException("No engine found");
            }

            try (InputStream fileContent = filePart.getInputStream()) {
                SaveLoadFileDto saveLoadFileDto = engine.loadFile(fileContent, userNameFromSession);
                if (!saveLoadFileDto.isSucceeded()) {
                    resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                }
                String json = new Gson().toJson(saveLoadFileDto);
                resp.getWriter().write(json);
            }
        }
        catch (Exception e){
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write(e.getMessage());
        }
    }
}
