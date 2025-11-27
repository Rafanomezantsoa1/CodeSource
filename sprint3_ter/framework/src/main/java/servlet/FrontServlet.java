package servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.util.Map;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import methods.ScannerPackage;

@WebServlet(name = "FrontServlet", urlPatterns = { "/test/*" }, loadOnStartup = 1)
public class FrontServlet extends HttpServlet {

    private Map<String, Method> urlMethodMap;

    @Override
    public void init() throws ServletException {
        try {
            ClassLoader webAppClassLoader = getServletContext().getClassLoader();
            String basePackage = "controller";
            urlMethodMap = ScannerPackage.getUrlMethodMap(basePackage, webAppClassLoader);
            System.out.println("Routes détectées au démarrage");
            urlMethodMap.forEach((url, method) -> {
                System.out.println(url + " -> " +
                        method.getDeclaringClass().getSimpleName() + "." + method.getName());
            });

        } catch (Exception e) {
            throw new ServletException("Erreur lors du scan des controllers", e);
        }
    }

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
    
        String requestURI = request.getRequestURI();
        String contextPath = request.getContextPath();
        String resourcePath = requestURI.substring(contextPath.length());
    
        Method method = urlMethodMap.get(resourcePath);
    
        // Chercher une route qui matche les URLs avec {param}
        if (method == null) {
            for (String urlPattern : urlMethodMap.keySet()) {
                // Transformer /dept/{id} -> /dept/[^/]+
                String regex = urlPattern.replaceAll("\\{[^/]+}", "[^/]+");
                if (resourcePath.matches(regex)) {
                    method = urlMethodMap.get(urlPattern);
                    break;
                }
            }
        }
    
        if (method != null) {
            try {
                Object instance = method.getDeclaringClass().getDeclaredConstructor().newInstance();
                Object result = method.invoke(instance);
    
                response.setContentType("text/html;charset=UTF-8");
                PrintWriter out = response.getWriter();
    
                if (result instanceof String) {
                    out.println(result);
                    return;
                } else if (result instanceof model.ModelVue) {
                    model.ModelVue mv = (model.ModelVue) result;
    
                    for (Map.Entry<String, Object> entry : mv.getData().entrySet()) {
                        request.setAttribute(entry.getKey(), entry.getValue());
                    }
    
                    RequestDispatcher dispatcher = request.getRequestDispatcher("/" + mv.getVue());
                    dispatcher.forward(request, response);
                    return;
                } else {
                    out.println("<h2>💡 Méthode trouvée :</h2>");
                    out.println("<p>Classe : " + method.getDeclaringClass().getSimpleName() + "</p>");
                    out.println("<p>Méthode : " + method.getName() + "</p>");
                    return;
                }
    
            } catch (Exception e) {
                throw new ServletException("Erreur lors de l'exécution de la méthode pour l'URL: " + resourcePath, e);
            }
        }
    
        try {
            java.net.URL resource = getServletContext().getResource(resourcePath);
            if (resource != null) {
                RequestDispatcher defaultServlet = getServletContext().getNamedDispatcher("default");
                if (defaultServlet != null) {
                    defaultServlet.forward(request, response);
                    return;
                }
            }
        } catch (Exception e) {
            throw new ServletException("Erreur lors de la vérification de la ressource: " + resourcePath, e);
        }
    
        showFrameworkPage(response, resourcePath);
    }
    

    private void showFrameworkPage(HttpServletResponse response, String requestedPath)
            throws IOException {
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();

        out.println("<!DOCTYPE html>");
        out.println("<html lang='fr'>");
        out.println("<head>");
        out.println("<meta charset='UTF-8'>");
        out.println("<meta name='viewport' content='width=device-width, initial-scale=1.0'>");
        out.println("<title>Bienvenue dans le Framework !</title>");
        out.println("</head>");
        out.println("<body>");
        out.println("<div class='container'>");
        out.println("<h1>Framework Java</h1>");
        out.println("<div class='message'>");
        out.println("<p>Oups! Route pas encore gérée!</p>");
        out.println("<p>URL demandée :</p>");
        out.println("<div class='path'><strong>" + requestedPath + "</strong></div>");
        out.println("</div>");
        out.println("</div>");
        out.println("</body>");
        out.println("</html>");
    }
}
