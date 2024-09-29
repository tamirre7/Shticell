import com.google.gson.Gson;
import command.api.Engine;
import command.impl.EngineImpl;
import dto.SheetDto;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
@WebServlet(name = "hey",urlPatterns = "/hello")
public class HelloWorld extends HttpServlet {
    public void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException {
        res.getWriter().println("heyyy");
        Engine engine = new EngineImpl();
        SheetDto sheetDto;
        Gson gson = new Gson();
    }
}
