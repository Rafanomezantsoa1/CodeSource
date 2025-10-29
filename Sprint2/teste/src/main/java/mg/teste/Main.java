package mg.teste;

import java.lang.reflect.Method;
import mg.framework.annotations.HandleURL;

public class Main {
    public static void main(String[] args) {
        Class<?> clazz = Teste.class;

        System.out.println("🔍 Méthodes annotées avec @HandleURL dans la classe " + clazz.getName() + " :");

        for (Method method : clazz.getDeclaredMethods()) {
            if (method.isAnnotationPresent(HandleURL.class)) {
                HandleURL annotation = method.getAnnotation(HandleURL.class);
                System.out.println("➡️  " + method.getName() + " -> " + annotation.value());
            }
        }
    }
}
