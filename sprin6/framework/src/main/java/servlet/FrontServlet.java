package servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.util.Arrays;
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

        System.out.println("\n=== SERVICE CALLED ===");

        // -------------------------------
        // 1️⃣ Analyse correcte de l'URL
        // -------------------------------
        String requestURI = request.getRequestURI(); // /projetFrameworkLalaina/test/arg
        String contextPath = request.getContextPath(); // /projetFrameworkLalaina
        String servletPath = request.getServletPath(); // /test

        // Enlève le contextPath → /test/arg
        String path = requestURI.substring(contextPath.length());

        // Enlève /test → /arg
        String resourcePath = path.substring(servletPath.length());
        if (resourcePath.isEmpty())
            resourcePath = "/";

        // Route complète telle qu'enregistrée dans urlMethodMap
        String fullRoute = servletPath + resourcePath; // /test/arg

        System.out.println("RequestURI = " + requestURI);
        System.out.println("contextPath = " + contextPath);
        System.out.println("servletPath = " + servletPath);
        System.out.println("path (after contextPath removed) = " + path);
        System.out.println("resourcePath (final) = " + resourcePath);
        System.out.println("fullRoute (lookup) = " + fullRoute);

        // Recherche exacte
        Method method = urlMethodMap.get(fullRoute);
        System.out.println("Method found by exact match = " + method);

        // ------------------------------------------
        // 2️⃣ Gestion des routes dynamiques {id} etc.
        // ------------------------------------------
        if (method == null) {
            for (String urlPattern : urlMethodMap.keySet()) {
                String regex = urlPattern.replaceAll("\\{[^/]+}", "[^/]+");
                if (fullRoute.matches(regex)) {
                    method = urlMethodMap.get(urlPattern);
                    break;
                }
            }
        }

        // Si aucune route trouvée → page framework
        if (method == null) {
            showFrameworkPage(response, fullRoute);
            return;
        }

        // ------------------------------------------
        // 3️⃣ Récupération des paramètres de méthode
        // ------------------------------------------
        java.lang.reflect.Parameter[] params = method.getParameters();
        Object[] args = new Object[params.length];

        System.out.println("Paramètres présents dans la requête :");
        request.getParameterMap()
                .forEach((key, values) -> System.out.println("  " + key + " = " + String.join(", ", values)));

        for (int i = 0; i < params.length; i++) {
            String paramName = params[i].getName(); // nom du paramètre
            String rawValue = request.getParameter(paramName);

            if (rawValue == null) {
                showFrameworkPage(response, fullRoute);
                return;
            }
            args[i] = rawValue;
        }

        try {
            // ------------------------------------
            // 4️⃣ Instanciation du controller
            // ------------------------------------
            Object instance = method.getDeclaringClass()
                    .getDeclaredConstructor()
                    .newInstance();

            // Invocation
            Object result = method.invoke(instance, args);

            response.setContentType("text/html;charset=UTF-8");
            PrintWriter out = response.getWriter();

            // Retour d'une String
            if (result instanceof String str) {
                if (str.endsWith(".jsp")) {
                    RequestDispatcher dispatcher = request.getRequestDispatcher("/" + str);
                    dispatcher.forward(request, response);
                    return;
                }
                out.println(str);
                return;
            }

            // Retour d’un ModelVue
            if (result instanceof model.ModelVue) {
                model.ModelVue mv = (model.ModelVue) result;
                mv.getData().forEach(request::setAttribute);

                RequestDispatcher dispatcher = request.getRequestDispatcher("/" + mv.getVue());
                dispatcher.forward(request, response);
                return;
            }

            // Debug si autre type
            out.println("<h2>💡 Méthode trouvée :</h2>");
            out.println("<p>Classe : " + method.getDeclaringClass().getSimpleName() + "</p>");
            out.println("<p>Méthode : " + method.getName() + "</p>");
            out.println("<p>Paramètres reçus : " + Arrays.toString(args) + "</p>");

        } catch (Exception e) {
            throw new ServletException("Erreur lors de l'exécution de la méthode pour l'URL: " + fullRoute, e);
        }
    }

    // ... reste du code inchangé

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

    // @Override
    // protected void doGet(HttpServletRequest req, HttpServletResponse resp)
    // throws ServletException, IOException {
    // System.out.println("=== DOGET HIT ===");
    // System.out.println("RequestURI: " + req.getRequestURI());
    // System.out.println("ContextPath: " + req.getContextPath());

    // resp.setContentType("text/html");
    // PrintWriter out = resp.getWriter();
    // out.println("<h1>FrontServlet est accessible!</h1>");
    // out.println("<p>URI: " + req.getRequestURI() + "</p>");
    // out.println("<p>Chemin: " + req.getContextPath() + "</p>");
    // }
}
