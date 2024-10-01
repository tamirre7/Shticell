package shticell.server.sheethub.servlets;

import com.google.gson.Gson;
import command.api.Engine;
import dto.SaveLoadFileDto;
import dto.SheetDto;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;
import shticell.server.utils.ServletUtils;
import java.io.IOException;
import java.io.InputStream;

@WebServlet(name = "LoadSheetServlet", urlPatterns = "/sheethub/loadsheet")
@MultipartConfig
public class LoadSheetServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");

        try {
            Part filePart = req.getPart("file");
            Engine engine = ServletUtils.getEngine(getServletContext());
            if (engine == null) {
                throw new ServletException("No engine found");
            }

            try (InputStream fileContent = filePart.getInputStream()) {
                SaveLoadFileDto saveLoadFileDto = engine.loadFile(fileContent);
                if(saveLoadFileDto.isSucceeded()) {
                    SheetDto uploadedSheet = engine.displayCurrentSpreadsheet();
                    Gson gson = new Gson();
                    String jsonResp = gson.toJson(uploadedSheet);
                    resp.getWriter().write(jsonResp);}
                else{
                    throw new ServletException("Error loading file" + saveLoadFileDto.getMessage());
                }
            }
        }
        catch (Exception e){
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write("{\"status\":\"error\",\"message\":\"Error loading file: " + e.getMessage() + "\"}");
        }
    }
}
