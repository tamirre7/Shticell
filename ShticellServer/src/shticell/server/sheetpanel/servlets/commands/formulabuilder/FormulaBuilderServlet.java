package shticell.server.sheetpanel.servlets.commands.formulabuilder;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import command.api.Engine;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import shticell.server.utils.ServletUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

@WebServlet(name = "FormulaBuilderServlet", urlPatterns = {"/sheetview/evaluateoriginalvalue"})
public class FormulaBuilderServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/plain;charset=UTF-8");
        try {
            Engine engine = ServletUtils.getEngine(getServletContext());
            InputStream formulaInputStream = req.getInputStream();
            String formulaJson = new String(formulaInputStream.readAllBytes());

            Gson gson = new Gson();

            Map<String, String> formulaData = gson.fromJson(formulaJson, new TypeToken<Map<String, String>>(){}.getType());
            String formula = formulaData.get("formula");
            String sheetName = formulaData.get("sheetName");

            String result = engine.evaluateOriginalValue(formula,sheetName);
            resp.getWriter().print(result);

        }catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write("Error evaluating original value: " + e.getMessage());

        }
    }
}
