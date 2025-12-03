package methods;

import java.io.File;
import java.lang.reflect.Method;
import java.net.JarURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import annotation.AnnotationController;
import annotation.GetMapping;
import annotation.PostMapping;

public class ScannerPackage {

    public static List<Class<?>> getClasses(String packageName, ClassLoader loader) throws Exception {
        List<Class<?>> classes = new ArrayList<>();
        String path = packageName.replace('.', '/');
        Enumeration<URL> resources = loader.getResources(path);

        while (resources.hasMoreElements()) {
            URL resource = resources.nextElement();
            if (resource.getProtocol().equals("file")) {
                File folder = new File(resource.toURI());
                for (File file : folder.listFiles()) {
                    if (file.getName().endsWith(".class")) {
                        String className = packageName + "." + file.getName().replace(".class", "");
                        classes.add(Class.forName(className, true, loader));
                    }
                }
            } else if (resource.getProtocol().equals("jar")) {
                JarURLConnection conn = (JarURLConnection) resource.openConnection();
                JarFile jarFile = conn.getJarFile();
                Enumeration<JarEntry> entries = jarFile.entries();
                while (entries.hasMoreElements()) {
                    JarEntry entry = entries.nextElement();
                    String name = entry.getName();
                    if (name.startsWith(path) && name.endsWith(".class") && !entry.isDirectory()) {
                        String className = name.replace('/', '.').replace(".class", "");
                        classes.add(Class.forName(className, true, loader));
                    }
                }
            }
        }
        return classes;
    }

    public static List<Class<?>> getAnnotatedClasses(String packageName, ClassLoader loader) throws Exception {
        List<Class<?>> annotated = new ArrayList<>();
        for (Class<?> clazz : getClasses(packageName, loader)) {
            if (clazz.isAnnotationPresent(AnnotationController.class)) {
                annotated.add(clazz);
            }
        }
        return annotated;
    }

    public static Map<String, Map<String, Method>> getUrlMethodMap(String packageName, ClassLoader loader) throws Exception {
        Map<String, Map<String, Method>> urlMethodMap = new HashMap<>();
        List<Class<?>> controllers = getAnnotatedClasses(packageName, loader);

        for (Class<?> clazz : controllers) {
            AnnotationController ctrl = clazz.getAnnotation(AnnotationController.class);
            String baseUrl = ctrl.annotationName();

            for (Method method : clazz.getDeclaredMethods()) {
                String fullUrl = null;
                String httpMethod = null;

                if (method.isAnnotationPresent(GetMapping.class)) {
                    fullUrl = baseUrl + method.getAnnotation(GetMapping.class).url();
                    httpMethod = "GET";
                } else if (method.isAnnotationPresent(PostMapping.class)) {
                    fullUrl = baseUrl + method.getAnnotation(PostMapping.class).url();
                    httpMethod = "POST";
                }

                if (fullUrl != null && httpMethod != null) {
                    urlMethodMap.computeIfAbsent(fullUrl, k -> new HashMap<>()).put(httpMethod, method);
                }
            }
        }

        return urlMethodMap;
    }
}
