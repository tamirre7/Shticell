package shticell.server.sheethub.servlets;

import com.google.gson.Gson;
import command.api.Engine;
import dto.SheetDto;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;
import shticell.server.utils.ServletUtils;

import java.io.IOException;
import java.io.InputStream;

@WebServlet(name = "SheetSelectionServlet",urlPatterns = {"/sheethub/setsheet"})
public class SheetSelectionServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        try {
            Engine engine = ServletUtils.getEngine(getServletContext());
            if (engine == null) {
                throw new ServletException("No engine found");
            }
            InputStream sheetNameInputStream = req.getInputStream();
            String sheetNameJson = new String(sheetNameInputStream.readAllBytes());

            Gson gson = new Gson();

            String sheetName = gson.fromJson(sheetNameJson, String.class);

            SheetDto currentSheet = engine.setCurrentSheet(sheetName);
            String jsonResp = gson.toJson(currentSheet);
            resp.getWriter().write(jsonResp);

        }catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write("{\"status\":\"error\",\"message\":\"Error setting sheet: " + e.getMessage() + "\"}");
        }

    }
}
