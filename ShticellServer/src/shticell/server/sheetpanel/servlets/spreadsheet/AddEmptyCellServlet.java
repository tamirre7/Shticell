package shticell.server.sheetpanel.servlets.spreadsheet;

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

@WebServlet(name = "AddEmptyCellServlet", urlPatterns = {"/sheetview/addemptycell"})
public class AddEmptyCellServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        try {
            Part cellIdPart = req.getPart("cellId");
            Engine engine = ServletUtils.getEngine(getServletContext());
            if (engine == null) {
                throw new ServletException("No engine found");
            }
            InputStream cellIdInputStream = cellIdPart.getInputStream();
            String cellIdJson = new String(cellIdInputStream.readAllBytes());

            Gson gson = new Gson();
            String cellId = gson.fromJson(cellIdJson, String.class);

            SheetDto updatedSheet = engine.addEmptyCell(cellId);
            String jsonResp = gson.toJson(updatedSheet);
            resp.getWriter().write(jsonResp);

        }catch (Exception e)
        {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write("{\"status\":\"error\",\"message\":\"Error adding cell: " + e.getMessage() + "\"}");
        }
}
}
