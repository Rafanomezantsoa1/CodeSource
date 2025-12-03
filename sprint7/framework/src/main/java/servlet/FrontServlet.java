package servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import methods.ScannerPackage;

@WebServlet(name = "FrontServlet", urlPatterns = { "/test/*" }, loadOnStartup = 1)
public class FrontServlet extends HttpServlet {

    private Map<String, Map<String, Method>> urlMethodMap;

    @Override
    public void init() throws ServletException {
        try {
            ClassLoader loader = getServletContext().getClassLoader();
            urlMethodMap = ScannerPackage.getUrlMethodMap("controller", loader);

            System.out.println("Routes détectées au démarrage :");
            urlMethodMap.forEach((url, methodMap) -> {
                methodMap.forEach((httpMethod, method) -> {
                    System.out.println(httpMethod + " " + url + " -> " +
                            method.getDeclaringClass().getSimpleName() + "." + method.getName());
                });
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
        String servletPath = request.getServletPath();

        String resourcePath = requestURI.substring(contextPath.length() + servletPath.length());
        if (resourcePath.isEmpty()) resourcePath = "/";

        String fullRoute = servletPath + resourcePath;
        String httpMethod = request.getMethod();

        Method method = null;
        String matchedPattern = null;

        Map<String, Method> methodMap = urlMethodMap.get(fullRoute);
        if (methodMap != null) method = methodMap.get(httpMethod);

        // Route dynamique
        if (method == null) {
            for (String urlPattern : urlMethodMap.keySet()) {
                String regex = urlPattern.replaceAll("\\{[^/]+}", "([^/]+)");
                regex = "^" + regex + "$";
                Matcher m = Pattern.compile(regex).matcher(fullRoute);
                if (m.matches()) {
                    methodMap = urlMethodMap.get(urlPattern);
                    method = methodMap.get(httpMethod);
                    matchedPattern = urlPattern;
                    break;
                }
            }
        }

        if (method == null) {
            showFrameworkPage(response, fullRoute);
            return;
        }

        java.lang.reflect.Parameter[] params = method.getParameters();
        Object[] args = new Object[params.length];

        try {
            if (matchedPattern != null) {
                String regex = matchedPattern.replaceAll("\\{[^/]+}", "([^/]+)");
                Matcher m = Pattern.compile(regex).matcher(fullRoute);
                m.matches();

                for (int i = 0; i < params.length; i++) {
                    String value = m.group(i + 1);
                    Class<?> type = params[i].getType();
                    if (type == int.class || type == Integer.class) args[i] = Integer.parseInt(value);
                    else args[i] = value;
                }
            } else if ("POST".equals(httpMethod)) {
                for (int i = 0; i < params.length; i++) {
                    String paramName = params[i].getName();
                    String value = request.getParameter(paramName);
                    if (value != null) {
                        Class<?> type = params[i].getType();
                        if (type == int.class || type == Integer.class) args[i] = Integer.parseInt(value);
                        else args[i] = value;
                    }
                }
            }

            Object instance = method.getDeclaringClass().getDeclaredConstructor().newInstance();
            Object result = method.invoke(instance, args);

            response.setContentType("text/html;charset=UTF-8");
            PrintWriter out = response.getWriter();

            if (result instanceof String str) out.println(str);
            else if (result instanceof model.ModelVue mv) {
                mv.getData().forEach(request::setAttribute);
                RequestDispatcher dispatcher = request.getRequestDispatcher("/" + mv.getVue());
                dispatcher.forward(request, response);
            } else {
                out.println("<h2>💡 Méthode trouvée :</h2>");
                out.println("<p>Classe : " + method.getDeclaringClass().getSimpleName() + "</p>");
                out.println("<p>Méthode : " + method.getName() + "</p>");
                out.println("<p>Paramètres : " + Arrays.toString(args) + "</p>");
            }

        } catch (Exception e) {
            throw new ServletException("Erreur lors de l'exécution de la méthode pour l'URL: " + fullRoute, e);
        }
    }

    private void showFrameworkPage(HttpServletResponse response, String requestedPath)
            throws IOException {
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        out.println("<!DOCTYPE html><html lang='fr'><head><meta charset='UTF-8'><title>Framework Java</title></head><body>");
        out.println("<h1>Framework Java</h1>");
        out.println("<p>Oups! Route pas encore gérée!</p>");
        out.println("<p>URL demandée : <strong>" + requestedPath + "</strong></p>");
        out.println("</body></html>");
    }
}
