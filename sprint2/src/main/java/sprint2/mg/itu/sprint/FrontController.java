package sprint2.mg.itu.sprint;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.ServletException;
import org.reflections.Reflections;

import sprint2.mg.itu.annotation.AnnotedController;
import sprint2.mg.itu.annotation.GET;

public class FrontController extends HttpServlet {
    private Map<String, Mapping> urlMap;

    public void init() throws ServletException {
        String controllPackage = this.getInitParameter("controllPackage");
        this.urlMap = new HashMap<>();
        scanControllers(controllPackage);
    }

    private void scanControllers(String packageName) {
        Reflections reflections = new Reflections(packageName);
        Set<Class<?>> controllerClasses = reflections.getTypesAnnotatedWith(AnnotedController.class);

        for (Class<?> controllerClass : controllerClasses) {
            Method[] methods = controllerClass.getDeclaredMethods();
            for (Method method : methods) {
                if (method.isAnnotationPresent(GET.class)) {
                    GET getAnnotation = method.getAnnotation(GET.class);
                    String url = getAnnotation.value();
                    Mapping mapping = new Mapping(controllerClass.getName(), method.getName());
                    urlMap.put(url, mapping);
                }
            }
        }
    }

    protected void doGet(HttpServletRequest req, HttpServletResponse rep) throws ServletException, IOException {
        processRequest(req, rep);
    }

    protected void doPost(HttpServletRequest req, HttpServletResponse rep) throws ServletException, IOException {
        processRequest(req, rep);
    }

    protected void processRequest(HttpServletRequest req, HttpServletResponse rep) throws ServletException, IOException {
        PrintWriter out = rep.getWriter();
        String url = req.getRequestURL().toString();
        out.print("Vous avez entré cette URL : " + url + "\n");

        Mapping mapping = urlMap.get(url);
        if (mapping != null) {
            out.print("URL: " + url + " - Mapping: " + mapping + "\n");
        } else {
            out.print("Aucune méthode associée à cette URL: " + url + "\n");
        }
    }
}
