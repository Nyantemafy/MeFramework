package sprint2.mg.itu.sprint;

import java.util.Map;
import java.util.HashMap;

import sprint2.mg.itu.annotation.AnnotedController;

public class Scanner {
    public static Map<String, Class<?>> scanCurrentProjet(String packageName) {
        Map<String, Class<?>> res = new HashMap<>();
        try {
            Thread currentThread = Thread.currentThread();
            ClassLoader classLoader = currentThread.getContextClassLoader();
            String path = packageName.replace(".", "/");
            java.net.URL resource = classLoader.getResource(path);
            java.io.File directory = new java.io.File(resource.getFile());

            for (java.io.File file : directory.listFiles()) {
                if (file.getName().endsWith(".class")) {
                    String className = packageName + "." + file.getName().substring(0, file.getName().length() - 6);
                    Class<?> cl = Class.forName(className);
                    AnnotedController annot = cl.getAnnotation(AnnotedController.class);
                    if (annot != null) {
                        res.put(annot.url(), cl);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return res;
    }
}
