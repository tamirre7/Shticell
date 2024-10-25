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

/**
 * Servlet for loading a spreadsheet file into the application.
 */
@WebServlet(name = "LoadSheetServlet", urlPatterns = "/sheethub/loadsheet")
@MultipartConfig
public class LoadSheetServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // Set the response content type to application/json
        resp.setContentType("application/json");

        try {
            // Retrieve the username from the session
            String userNameFromSession = SessionUtils.getUsername(req);

            // Get the file part from the request
            Part filePart = req.getPart("file");

            // Obtain the Engine instance from the servlet context
            Engine engine = ServletUtils.getEngine(getServletContext());
            if (engine == null) {
                throw new ServletException("No engine found");
            }

            // Process the uploaded file
            try (InputStream fileContent = filePart.getInputStream()) {
                // Load the file using the engine and get the result
                SaveLoadFileDto saveLoadFileDto = engine.loadFile(fileContent, userNameFromSession);

                // Check if the loading was successful
                if (!saveLoadFileDto.isSucceeded()) {
                    throw new ServletException(saveLoadFileDto.getMessage());
                }

                // Convert the result to JSON and write it to the response
                String json = new Gson().toJson(saveLoadFileDto);
                resp.getWriter().write(json);
            }
        } catch (Exception e) {
            // Handle exceptions by setting the response status to 500 and writing the error message
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write(e.getMessage());
        }
    }
}
