package mg.itu.prom16;

import java.io.File;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.net.URL;
import java.util.*;
import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;

public class Scanner {

    public void scann(HttpServlet svr, List<String> controllerList, HashMap<String, Mapping> urlMethod) {
        try {
            ServletContext context = svr.getServletContext();
            String packageName = context.getInitParameter("Controller");

            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
            Enumeration<URL> resources = classLoader.getResources(packageName.replace('.', '/'));

            while (resources.hasMoreElements()) {
                URL resource = resources.nextElement();
                if (resource.getProtocol().equals("file")) {
                    File file = new File(resource.toURI());
                    scanControllers(file, packageName, controllerList, urlMethod);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void scanControllers(File directory, String packageName, List<String> controllerList, HashMap<String, Mapping> urlMethod) {
        if (!directory.exists()) {
            return;
        }

        File[] files = directory.listFiles();
        if (files == null) {
            return;
        }

        for (File file : files) {
            if (file.isDirectory()) {
                scanControllers(file, packageName + "." + file.getName(), controllerList, urlMethod);
            } else if (file.getName().endsWith(".class")) {
                String className = packageName + '.' + file.getName().substring(0, file.getName().length() - 6);
                try {
                    Class<?> clazz = Class.forName(className);
                    if (clazz.isAnnotationPresent(AnnotedController.class)) {
                        controllerList.add(className);
                        Method[] methods = clazz.getDeclaredMethods();
                        for (Method method : methods) {
                            if (method.isAnnotationPresent(AnnotedMth.class)) {
                                AnnotedMth annt = method.getAnnotation(AnnotedMth.class);
                                Mapping map = new Mapping();
                                map.add(clazz.getName(), method.getName());
                                urlMethod.put(annt.value(), map);
                            }
                        }
                    }
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public String extractRelativePath(HttpServletRequest request) {
        String fullUrl = request.getRequestURL().toString();
        String[] relativePath = fullUrl.split("/");
        return relativePath[relativePath.length - 1];
    }

    public Mapping ifMethod(HttpServletRequest request, HashMap<String, Mapping> urlMethod) {
        String method = this.extractRelativePath(request);
        if (urlMethod.containsKey(method)) {
            return urlMethod.get(method);
        }
        return null;
    }

    public Object callMethod(Mapping mapping, HttpServletRequest request) throws Exception {
        Class<?> clazz = Class.forName(mapping.getKey());
        Method method = findMethod(clazz, mapping.getValue());
        Object controllerInstance = clazz.getDeclaredConstructor().newInstance();
        Object[] params = getMethodParameters(method, request);
        return method.invoke(controllerInstance, params);
    }

    private Method findMethod(Class<?> clazz, String methodName) throws NoSuchMethodException {
        Method[] methods = clazz.getMethods();
        for (Method method : methods) {
            if (method.getName().equals(methodName)) {
                return method;
            }
        }
        throw new NoSuchMethodException("Method " + methodName + " not found in " + clazz.getName());
    }

    private Object[] getMethodParameters(Method method, HttpServletRequest request) {
        Parameter[] parameters = method.getParameters();
        Object[] params = new Object[parameters.length];
        for (int i = 0; i < parameters.length; i++) {
            if (parameters[i].isAnnotationPresent(Param.class)) {
                Param param = parameters[i].getAnnotation(Param.class);
                String paramName = param.name();
                String paramValue = request.getParameter(paramName);
                params[i] = convertParameter(paramValue, parameters[i].getType());
            } else {
                params[i] = null; // Ou toute autre valeur par défaut que vous souhaitez
            }
        }
        return params;
    }

    private Object convertParameter(String parameter, Class<?> targetType) {
        if (parameter == null) {
            return null;
        }
        if (targetType == String.class) {
            return parameter;
        } else if (targetType == int.class || targetType == Integer.class) {
            return Integer.parseInt(parameter);
        } else if (targetType == long.class || targetType == Long.class) {
            return Long.parseLong(parameter);
        }
        // Ajoutez ici d'autres conversions si nécessaire
        return null;
    }
}
