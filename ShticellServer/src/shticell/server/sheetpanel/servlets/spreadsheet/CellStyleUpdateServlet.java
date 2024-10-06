package shticell.server.sheetpanel.servlets.spreadsheet;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import command.api.Engine;
import dto.SheetDto;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;
import shticell.server.utils.ServletUtils;
import spreadsheet.cell.impl.CellIdentifierImpl;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

public class CellStyleUpdateServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        try {
            Part cellParamPart = req.getPart("cellParam");
            Engine engine = ServletUtils.getEngine(getServletContext());
            if (engine == null) {
                throw new ServletException("No engine found");
            }
            InputStream cellParamsInputStream  = cellParamPart.getInputStream();

            String cellParamJson = new String(cellParamsInputStream.readAllBytes());

            Gson gson = new Gson();

            Map<String, String> cellParams = gson.fromJson(cellParamJson, new TypeToken<Map<String, String>>(){}.getType());

            String cellId = cellParams.get("cellId");
            String style = cellParams.get("style");

            SheetDto updatedSheet = engine.setCellStyle(cellId, style);
            String jsonResp = gson.toJson(updatedSheet);
            resp.getWriter().write(jsonResp);

        }catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write("{\"status\":\"error\",\"message\":\"Error updating cell's style: " + e.getMessage() + "\"}");
        }

    }
}