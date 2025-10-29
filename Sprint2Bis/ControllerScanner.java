package classe;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class ControllerScanner {

    public static List<Class<?>> findControllers(String packageName) {
        List<Class<?>> controllers = new ArrayList<>();

        //  On transforme le nom du package en chemin
        String path = packageName.replace('.', '/');

        //  On récupère le classloader courant
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        URL resource = classLoader.getResource(path);

        if (resource == null) {
            System.err.println("Package introuvable : " + packageName);
            return controllers;
        }

        //  On parcourt le dossier correspondant
        File directory = new File(resource.getFile());
        if (!directory.exists()) {
            System.err.println("Dossier introuvable : " + directory);
            return controllers;
        }

        for (File file : directory.listFiles()) {
            if (file.getName().endsWith(".class")) {
                try {
                    //  On retire ".class" du nom
                    String className = packageName + '.' + file.getName().replace(".class", "");

                    //  On charge la classe
                    Class<?> clazz = Class.forName(className);

                    //  On vérifie l’annotation
                    if (clazz.isAnnotationPresent(Controller.class)) {
                        controllers.add(clazz);
                        System.out.println("✅ Classe détectée : " + clazz.getName());
                    } else {
                        System.out.println("⛔ Ignorée : " + clazz.getName());
                    }

                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }

        return controllers;
    }

    // Exemple d’utilisation
    public static void main(String[] args) {
        List<Class<?>> list = findControllers("classe");
        System.out.println("Controllers trouvés : " + list.size());
    }
}
