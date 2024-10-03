package mg.itu.prom16;

import java.io.File;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.*;
import java.net.URL;
import java.util.*;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class Scanner {
    public void scann(HttpServlet svr, List<String> controllerList, HashMap<String, Mapping> urlMethod) {
        try {
            ServletContext context = svr.getServletContext();
            String packageName = context.getInitParameter("Controller");

            if (!"controller".equals(packageName)) {
                throw new Exception("Invalid package configuration in web.xml. Expected 'controller' but found '" + packageName + "'");
            }

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

    public void scanControllers(File directory, String packageName, List<String> controllerList, HashMap<String, Mapping> urlMethod) throws Exception {
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
                Class<?> clazz = Class.forName(className);
                if (clazz.isAnnotationPresent(AnnotedController.class)) {
                    controllerList.add(className);
    
                    checkMethods(clazz);
    
                    Method[] methods = clazz.getDeclaredMethods();
                    for (Method method : methods) {
                        String httpMethod = "GET";  // Par défaut, GET

                        // Si la méthode est annotée avec @POST, on change le verbe en POST
                        if (method.isAnnotationPresent(POST.class)) {
                            POST postAnnotation = method.getAnnotation(POST.class);
                            httpMethod = "POST";  // La méthode est POST

                            // Ajout dans urlMethod avec POST pour @POST
                            Mapping map = new Mapping();
                            map.add(clazz.getName(), method.getName(), "POST", "post");
                            urlMethod.put(postAnnotation.value(), map);
                        }

                        // Gestion de @AnnotedMth avec le verbe HTTP défini (GET par défaut, POST si annotée avec @POST)
                        if (method.isAnnotationPresent(AnnotedMth.class)) {
                            AnnotedMth annotedMthAnnotation = method.getAnnotation(AnnotedMth.class);

                            // Utiliser le verbe HTTP défini par la vérification précédente
                            Mapping map = new Mapping();
                            map.add(clazz.getName(), method.getName(), "AnnotedMth", httpMethod.toLowerCase());
                            urlMethod.put(annotedMthAnnotation.value(), map);
                        }

                        // Gestion de @Restapi avec le verbe HTTP défini (GET par défaut, POST si annotée avec @POST)
                        if (method.isAnnotationPresent(Restapi.class)) {
                            Restapi restapiAnnotation = method.getAnnotation(Restapi.class);

                            // Utiliser le verbe HTTP défini par la vérification précédente
                            Mapping map = urlMethod.get(restapiAnnotation.value());
                            if (map == null) {
                                map = new Mapping();
                            }
                            map.add(clazz.getName(), method.getName(), "Restapi", httpMethod.toLowerCase());
                            urlMethod.put(restapiAnnotation.value(), map);
                        }
                    }
                }
            }
        }
    }
    
   public static void checkMethods(Class<?> controllerClass) throws Exception {
        Map<String, String> annotatedMethods = new HashMap<>();

        for (Method method : controllerClass.getDeclaredMethods()) {
            if (method.isAnnotationPresent(AnnotedMth.class)) {
                AnnotedMth annotation = method.getAnnotation(AnnotedMth.class);
                String url = annotation.value();

                if (annotatedMethods.containsKey(url)) {
                    throw new Exception("Duplicate annotation found for URL: " + url +
                            " in methods: " + annotatedMethods.get(url) + " and " + method.getName());
                }

                annotatedMethods.put(url, method.getName());
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
        return urlMethod.get(method);
    }
    
    public Object callMethod(Mapping mapping, HttpServletRequest request) throws Exception {
        System.out.println("Hello World!");
        Class<?> clazz = Class.forName(mapping.getKey());
        Method method = findMethod(clazz, mapping.getValue());
        Object controllerInstance = clazz.getDeclaredConstructor().newInstance();
        Object[] params = getMethodParameters(method, request);
        return method.invoke(controllerInstance, params);
    }

    public Method findMethod(Class<?> clazz, String methodName) throws NoSuchMethodException {
        Method[] methods = clazz.getMethods();
        for (Method method : methods) {
            if (method.getName().equals(methodName)) {
                return method;
            }
        }
        throw new NoSuchMethodException("Method " + methodName + " not found in " + clazz.getName());
    }

    public Object[] getMethodParameters(Method method, HttpServletRequest request) throws Exception {
        Parameter[] parameters = method.getParameters();
        Object[] params = new Object[parameters.length];
    
        for (int i = 0; i < parameters.length; i++) {
            Parameter parameter = parameters[i];
            Class<?> paramType = parameter.getType();
    
            if (parameter.isAnnotationPresent(Param.class)) {
                Param param = parameter.getAnnotation(Param.class);
                String paramName = param.name();
                String paramValue = request.getParameter(paramName);
                params[i] = convertParameter(paramValue, paramType);
                System.out.println("Valeur du paramètre : " + params[i]);
    
                if (paramType != String.class) {
                    System.out.println("Appel de createModelObject pour le type : " + paramType.getName());
                    params[i] = createModelObject(request, paramType.getName());
                    System.out.println("Objet créé : " + params[i]);
                }                           
            } else if (paramType == CurrentSession.class) {
                System.out.println("CurrentSession");
                params[i] = new CurrentSession(request.getSession());
            } else {
                System.out.println("shhhhhhhhh");
                throw new Exception("ETU002381 pas de parametre");
            }
        }
        return params;
    }    
    public Object createModelObject(HttpServletRequest request, String nameObject) throws Exception {
        System.out.println("createModelObject appelé pour : " + nameObject);
        
        Class<?> clazz = Class.forName(nameObject);
        Object obj = clazz.getDeclaredConstructor().newInstance();
        
        Enumeration<String> parameterNames = request.getParameterNames();
    
        if (!parameterNames.hasMoreElements()) {
            System.out.println("Aucun paramètre trouvé");
        }
        
        while (parameterNames.hasMoreElements()) {
            String parameterName = parameterNames.nextElement();
            System.out.println("Paramètre trouvé : " + parameterName);
    
                Enumeration<String> innerParameterNames = request.getParameterNames();
    
                if (!innerParameterNames.hasMoreElements()) {
                    System.out.println("Aucun attribut trouvé");
                }
    
                while (innerParameterNames.hasMoreElements()) {
                    String attrib = innerParameterNames.nextElement();
                    System.out.println("Attribut trouvé : " + attrib);
    
                        String value = request.getParameter(attrib);
                        System.out.println("Valeur de l'attribut : " + value);

                        String[] nameAttribute = attrib.trim().split("\\.");
                        String firstLetter = nameAttribute[1].substring(0, 1).toUpperCase();
                        String restOfTheWord = nameAttribute[1].substring(1);
                        String formattedName = firstLetter + restOfTheWord;
                        System.out.println(formattedName);
                        String setterMethodName = "set" + formattedName;
                        System.out.println("Méthode setter recherchée : " + setterMethodName);
    
                            Method[] methods = clazz.getMethods();
                            Method setterMethod = null;
                            for (Method method : methods) {
                                if (method.getName().equalsIgnoreCase(setterMethodName)) {
                                    setterMethod = method;
                                    break;
                                }
                            }
    
                            if (setterMethod != null) {
                                Class<?>[] parameterTypes = setterMethod.getParameterTypes();
                                Object convertedValue = convertParameter(value, parameterTypes[0]);
                                System.out.println("Valeur convertie : " + convertedValue + " pour le type : " + parameterTypes[0].getName());
                                setterMethod.invoke(obj, convertedValue);
                                System.out.println("Valeur définie dans l'objet : " + convertedValue);
                            } else {
                                throw new Exception("Méthode setter non trouvée pour l'attribut : " + nameAttribute);
                            }
                }
        }
        System.out.println("Objet final : " + obj);
        return obj;
    }
        
    private Object convertParameter(String value, Class<?> targetType) {
        if (value == null) {
            return null;
        }
    
        if (targetType == String.class) {
            return value;
        } else if (targetType == int.class || targetType == Integer.class) {
            return Integer.parseInt(value);
        } else if (targetType == long.class || targetType == Long.class) {
            return Long.parseLong(value);
        } else if (targetType == double.class || targetType == Double.class) {
            return Double.parseDouble(value);
        } else if (targetType == boolean.class || targetType == Boolean.class) {
            return Boolean.parseBoolean(value);
        }
    
        // Ajoutez d'autres types de conversion si nécessaire
    
        return value;
    }    

} 

