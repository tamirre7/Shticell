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

    // Handles POST requests to evaluate the original value sent by the formula builder
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // Set the response content type to plain text with UTF-8 encoding
        resp.setContentType("text/plain;charset=UTF-8");
        try {
            // Obtain the Engine instance from the servlet context
            Engine engine = ServletUtils.getEngine(getServletContext());
            // Read the input stream containing the formula JSON
            InputStream formulaInputStream = req.getInputStream();
            String formulaJson = new String(formulaInputStream.readAllBytes());

            Gson gson = new Gson();
            // Deserialize the JSON data into a Map
            Map<String, String> formulaData = gson.fromJson(formulaJson, new TypeToken<Map<String, String>>(){}.getType());
            // Extract the formula and sheet name from the Map
            String formula = formulaData.get("formula");
            String sheetName = formulaData.get("sheetName");

            // Evaluate the original value of the formula using the engine
            String result = engine.evaluateOriginalValue(formula, sheetName);
            // Write the result to the response
            resp.getWriter().print(result);

        } catch (Exception e) {
            // Handle exceptions by setting the response status to 500 and writing the error message
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write("Error evaluating original value: " + e.getMessage());
        }
    }
}
