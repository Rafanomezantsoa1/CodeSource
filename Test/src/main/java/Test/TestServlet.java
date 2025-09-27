package Test;

import framework.Affichage;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import java.io.IOException;

public class TestServlet extends HttpServlet {
    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String result = Affichage.test();  // appelle le framework
        resp.getWriter().write(result
                + "\n"
                + "URL :"
                + req.getRequestURI() + "\n");
    }
}