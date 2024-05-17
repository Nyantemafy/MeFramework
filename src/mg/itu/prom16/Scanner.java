package src.mg.itu.prom16;

import java.util.Map;
import java.util.HashMap;

public class Scanner {
    public static Map<String,Class> scanCurrentProjet(String packageName){
        Map<String,Class> res = new HashMap<>();
        try{
            Thread currentThread = Thread.currentThread();
            ClassLoader classLoader = currentThread.getContextClassLoader();
            String path = packageName.replace(".", "/");
            java.net.URL ressource = classLoader.getResource(path);
            java.io.File directory = new java.io.File(ressource.getFile());

            for(java.io.File file : directory.listFiles()){
                if(file.getName().endsWith(".class")){
                    String className = packageName + "."+ file.getName().substring(0,file.getName().length() - 6);
                    Class<?> cl = Class.forName(className);
                    AnnotedController annot = cl.getAnnotation(AnnotedController.class);
                    if(annot != null){
                        res.put(annot.iscontroller(),cl);
                    }
                }
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return res;
    }
}
