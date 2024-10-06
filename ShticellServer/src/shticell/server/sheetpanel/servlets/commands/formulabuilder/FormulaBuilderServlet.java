package shticell.server.sheetpanel.servlets.commands.formulabuilder;

import command.api.Engine;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import shticell.server.utils.ServletUtils;

import java.io.IOException;

@WebServlet(name = "FormulaBuilderServlet", urlPatterns = {"/sheetview/evaluateoriginalvalue"})
public class FormulaBuilderServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/plain;charset=UTF-8");
        try {
            Engine engine = ServletUtils.getEngine(getServletContext());
            String formula = req.getParameter("formula");
            String result = engine.evaluateOriginalValue(formula);
            resp.getWriter().print(result);

        }catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write("{\"status\":\"error\",\"message\":\"Error evaluating original value: " + e.getMessage() + "\"}");

        }
    }
}
