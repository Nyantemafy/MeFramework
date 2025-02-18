package mg.itu.prom16;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.lang.reflect.*;
import java.net.URL;
import java.util.*;
import java.util.regex.Pattern;
import java.io.IOException;
import java.io.InputStream;
import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;

public class Scanner {
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");

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
                    
                    String url = null;
                    Mapping mapping = urlMethod.get(url);
                    String annotation = null;
                    Method[] methods = clazz.getDeclaredMethods();
                    if (clazz.isAnnotationPresent(Autorisation.class)) {
                        Autorisation autorisationClass = clazz.getAnnotation(Autorisation.class);
                        String roleController = autorisationClass.role();
                        mapping.setRole(roleController);
                        System.out.println("Contrôleur : " + clazz.getName() + " - Role requis : " + roleController);
                    }

                    for (Method method : methods) {
                        String httpMethod = "GET";  

                        if (method.isAnnotationPresent(POST.class)) {
                            httpMethod = "POST";
                        }

                        if (method.isAnnotationPresent(AnnotedMth.class)) {
                            AnnotedMth annotedMthAnnotation = method.getAnnotation(AnnotedMth.class);
                            url = annotedMthAnnotation.value();
                            annotation = "AnnotedMth";
                        }

                        if (method.isAnnotationPresent(Restapi.class)) {
                            Restapi restapiAnnotation = method.getAnnotation(Restapi.class);
                            url = restapiAnnotation.value();
                            annotation = "Restapi";
                        }
                        
                        if (url != null) {
                            Class<?>[] paramTypes = method.getParameterTypes();
                            VerbAction verbAction = new VerbAction(method.getName(), httpMethod, paramTypes);
    
                            if (mapping != null) {
                                ifDuplicate(mapping, url, verbAction);
                                mapping.addVerbAction(verbAction);
                            } else {
                                mapping = new Mapping(clazz.getName(), annotation);
                                mapping.addVerbAction(verbAction);
                                urlMethod.put(url, mapping);
                            }
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

    public String relativePath(HttpServletRequest request) {
        String fullUrl = request.getRequestURL().toString();
        String[] relativePath = fullUrl.split("/");
        return relativePath[relativePath.length - 1];
    }

    public Mapping getMethode(HttpServletRequest request, HashMap<String, Mapping> urlMethod) {
        String method = this.relativePath(request);
        return urlMethod.get(method);
    }
    
    public Object invokeMethod(Mapping mapping, HttpServletRequest request) throws Exception {
        Class<?> clazz = Class.forName(mapping.getKey());
        Set<VerbAction> verbActions = mapping.getVerbActions();
        Method method = null;
        if (!verbActions.isEmpty()) {
            VerbAction action = verbActions.iterator().next();
            String methodName = action.getAction();
            method = findMethod(clazz, methodName);
        } else {
            System.out.println("Aucune action trouvée pour la classe " + clazz.getName());
        }
    
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

    public void ifDuplicate(Mapping mapping, String nameUrl, VerbAction action) throws Exception {
        for (VerbAction act : mapping.getVerbActions()) {
            if (act.getVerb().toUpperCase().trim().equals(action.getVerb().toUpperCase().trim())) {
                throw new Exception("methode et verb incorrect");
            }
        }
    }

    public void redirigeException(HttpServletRequest request, HttpServletResponse response, String errorMessage, int statusCode)
        throws IOException {
        response.setContentType("text/html;charset=UTF-8");
        response.setStatus(statusCode);

        PrintWriter out = response.getWriter();
        try {
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Error</title>");
            out.println("<style>");
            out.println(".error-container {");
            out.println("  background-color: red;");
            out.println("  color: black;");
            out.println("  padding: 20px;");
            out.println("  text-align: center;");
            out.println("  margin: 100px auto;");
            out.println("  width: 50%;");
            out.println("  border-radius: 10px;");
            out.println("}");
            out.println("</style>");
            out.println("</head>");
            out.println("<body>");
            out.println("<div class='error-container'>");
            out.println("<h1>Error " + statusCode + "</h1>");
            out.println("<p>" + errorMessage + "</p>");
            out.println("</div>");
            out.println("</body>");
            out.println("</html>");
        } finally {
            out.close();
        }
    } 

    public Object[] getMethodParameters(Method method, HttpServletRequest request) throws Exception {
        Parameter[] parameters = method.getParameters();
        Object[] params = new Object[parameters.length];
    
        for (int i = 0; i < parameters.length; i++) {
            Parameter parameter = parameters[i];
            Class<?> paramType = parameter.getType();
    
            if (parameter.isAnnotationPresent(Param.class)) {
                Param param = parameter.getAnnotation(Param.class);
                System.out.println("ici c'est param");
                String paramName = param.name();
                System.out.println("Nom du paramètre : " + paramName);
                String paramValue = null;
                if(paramType == String.class){
                    paramValue = request.getParameter(paramName);
                } else if (paramType == Part.class) {
                    Part file = request.getPart(paramName);  
                    if (file != null) {
                        String fileName = this.extractFileName(file);
                        saveFile(file, fileName); 
                        params[i] = file;  
                        System.out.println("Hita i :" + fileName);
                    } else {
                        throw new Exception("Fichier non reçu");
                    }
                }
                
                System.out.println("paramValue : " + paramValue);
                params[i] = convertParameter(paramValue, paramType);
                System.out.println("Valeur du paramètre : " + params[i]);
                
                if (paramType != String.class && paramType != Part.class) {
                    System.out.println("Appel de createModelObject pour le type : " + Part.class);
                    System.out.println("Appel de paramType pour le type : " + paramType);
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
            return obj;
        }
    
        List<ValidationResult> validationResults = new ArrayList<>();
    
        while (parameterNames.hasMoreElements()) {
            String parameterName = parameterNames.nextElement();
            String value = request.getParameter(parameterName);
            System.out.println("Paramètre trouvé : " + parameterName + " avec la valeur : " + value);
    
            String[] nameAttribute = parameterName.trim().split("\\.");
            if (nameAttribute.length < 2) {
                System.out.println("Nom de l'attribut invalide, ignoré : " + parameterName);
                continue;
            }
    
            String attributeName = nameAttribute[1];
            String formattedName = attributeName.substring(0, 1).toUpperCase() + attributeName.substring(1);
            String setterMethodName = "set" + formattedName;
    
            Field field;
            try {
                field = clazz.getDeclaredField(attributeName);
            } catch (NoSuchFieldException e) {
                System.out.println("Attribut non trouvé : " + attributeName + ", ignoré.");
                continue;
            }
    
            boolean hasRequiredAnnotation = field.isAnnotationPresent(Required.class);
            boolean hasEmailAnnotation = field.isAnnotationPresent(Email.class);
    
            ValidationResult validationResult = new ValidationResult(value, null);
    
            if (hasRequiredAnnotation) {
                if (value == null || value.trim().isEmpty()) {
                    validationResult.addError("Le champ " + attributeName + " est requis mais n'a pas de valeur.");
                }
            }
    
            if (hasEmailAnnotation) {
                if (value != null && !value.trim().isEmpty() && !value.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
                    validationResult.addError("Le champ " + attributeName + " doit contenir une adresse email valide.");
                }
            }
    
            if (validationResult.hasErrors()) {
                validationResults.add(validationResult);
                continue;
            }

            Method setterMethod = null;
            for (Method method : clazz.getMethods()) {
                if (method.getName().equalsIgnoreCase(setterMethodName)) {
                    setterMethod = method;
                    break;
                }
            }
    
            if (setterMethod != null) {
                Class<?> parameterType = setterMethod.getParameterTypes()[0];
                Object convertedValue = convertParameter(value, parameterType);
                setterMethod.invoke(obj, convertedValue);
                System.out.println("Valeur définie dans l'objet : " + convertedValue);
            } else {
                System.out.println("Méthode setter non trouvée pour : " + attributeName);
            }
        }

        if (!validationResults.isEmpty()) {
            request.setAttribute("validationResults", validationResults);
    
            Map<String, String> formData = new HashMap<>();
            while (parameterNames.hasMoreElements()) {
                String paramName = parameterNames.nextElement();
                formData.put(paramName, request.getParameter(paramName));
            }
            request.setAttribute("formData", formData);
            
            return null;
        }
    
        System.out.println("Objet final : " + obj);
        return obj;
    }
       
    private Object convertParameter(String value, Class<?> targetType) {
        if (value == null || value.trim().isEmpty()) {
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
        
        return value;
    }    

    private String extractFileName(Part part) {
        String contentDisposition = part.getHeader("content-disposition");
        if (contentDisposition == null) {
            return null;  
        }
    
        String[] elements = contentDisposition.split(";");
        
        for (String element : elements) {
            String trimmedElement = element.trim();  
            if (trimmedElement.startsWith("filename")) {
                return trimmedElement.substring(trimmedElement.indexOf('=') + 1).trim().replace("\"", "");
            }
        }
        
        return null;  
    }

    public void saveFile(Part file, String fileName) throws IOException {
        String uploads = "C:\\Users\\Ny Antema\\Documents\\Antema\\s4\\s4\\Mr_Naina\\upload\\"; 
        File uploadDir = new File(uploads);

        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
        }

        File uploadedFile = new File(uploadDir, fileName);
        try (InputStream input = file.getInputStream(); 
             FileOutputStream output = new FileOutputStream(uploadedFile)) {
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = input.read(buffer)) != -1) {
                output.write(buffer, 0, bytesRead);
            }
        }
    }

} 

