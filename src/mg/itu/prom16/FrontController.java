package mg.itu.prom16;

import java.io.IOException;
import java.io.PrintWriter;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import com.google.gson.Gson;
import jakarta.servlet.annotation.MultipartConfig;

@MultipartConfig
public class FrontController extends HttpServlet {
    List<String> ListController;
    HashMap<String, Mapping> urlMethod;
    Scanner scanne;

    @Override
    public void init() throws ServletException {
        ListController = new ArrayList<>();
        urlMethod = new HashMap<>();
        scanne = new Scanner();
        try {
            scanne.scann(this, this.ListController, urlMethod);
            getServletContext().setAttribute("controllerList", ListController);
            getServletContext().setAttribute("urlMethod", urlMethod);
        } catch (Exception e) {
            throw new ServletException(e.getMessage(), e);
        }
    }

    public void processRequest(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException, Exception {
        response.setContentType("text/html;charset=UTF-8");
        
        PrintWriter out = null;
        try {
            out = response.getWriter();
            String uri = request.getRequestURI();
            String method = request.getMethod();
            String contextPath = request.getContextPath();
            String path = uri.substring(contextPath.length());

            if ("/".equals(path)) {
                List<String> controllerList = (List<String>) getServletContext().getAttribute("controllerList");
                HashMap<String, Mapping> urlMethod = (HashMap<String, Mapping>) getServletContext().getAttribute("urlMethod");

                out.println("<html>");
                out.println("<head>");
                out.println("<title>Liste des contrôleurs et méthodes</title>");
                out.println("<style>");
                out.println(".admin-url { color: red; font-weight: bold; }"); 
                out.println("</style>");
                out.println("</head>");
                out.println("<body>");
                out.println("<h1>Liste des contrôleurs et méthodes annotées</h1>");
                out.println("<ul>");

                for (String controller : controllerList) {
                    out.println("<li><strong>" + controller + "</strong>");
                    out.println("<ul>");
                    for (Map.Entry<String, Mapping> entry : urlMethod.entrySet()) {
                        if (entry.getValue().getKey().equals(controller)) {
                            Set<VerbAction> actions = entry.getValue().getVerbActions();
                            for (VerbAction action : actions) {
                                boolean isAdmin = "Autorisation".equals(entry.getValue().getAnnotation());

                                if (isAdmin) {
                                    out.println("<li class='admin-url'>");
                                    out.println("URL: " + entry.getKey() + " - Verbe: " + action.getVerb() + " - Méthode: " + action.getAction());
                                    out.println("</li>");
                                } else {
                                    String fullUrl = "http://localhost:8080" + contextPath + "/" + entry.getKey();
                                    out.println("<li>");
                                    out.println("URL: <a href='" + fullUrl + "'>" + entry.getKey() + "</a> - Verbe: " + action.getVerb() + " - Méthode: " + action.getAction());
                                    out.println("</li>");
                                }

                                out.println("<ul>");
                                out.println("<li>Paramètres : ");

                                Class<?>[] paramTypes = action.getParameterTypes();
                                if (paramTypes.length > 0) {
                                    for (Class<?> paramType : paramTypes) {
                                        out.println(paramType.getSimpleName() + " ");
                                    }
                                } else {
                                    out.println("Aucun paramètre");
                                }

                                out.println("</li>");
                                out.println("</ul>");
                            }
                        }
                    }
                    out.println("</ul>");
                    out.println("</li>");
                }

                out.println("</ul>");
                out.println("</body>");
                out.println("</html>");
            } else {
                out.println("<p>" + request.getRequestURL() + "</p>");
                Mapping mapping = scanne.getMethode(request, this.urlMethod);
                if (mapping != null) {
                    Set<VerbAction> verbActions = mapping.getVerbActions();
                    boolean verbMatched = false;

                    for (VerbAction action : verbActions) {
                            verbMatched = true;
                            Object result = this.scanne.invokeMethod(mapping, request);

                            String annotationType = mapping.getAnnotation();

                            if ("Restapi".equals(annotationType)) {
                                response.setContentType("application/json");

                                if (result instanceof ModelView) {
                                    ModelView modelView = (ModelView) result;
                                    HashMap<String, Object> data = modelView.getData();
                                    Gson gson = new Gson();
                                    String json = gson.toJson(data);
                                    out.print(json);
                                } else if (result instanceof String) {
                                    Gson gson = new Gson();
                                    String json = gson.toJson(result);
                                    out.print(json);
                                }

                            } else if ("AnnotedMth".equals(annotationType)) {
                                if (result instanceof ModelView) {
                                    ModelView modelView = (ModelView) result;
                                    HashMap<String, Object> data = modelView.getData();

                                    List<ValidationResult> validationResults = (List<ValidationResult>) request.getAttribute("validationResults");
                                    Map<String, String> formData = (Map<String, String>) request.getAttribute("formData");
                                    if (validationResults != null) {
                                        System.out.println("validationResults trouvés dans la requête:");
                                            for (ValidationResult validationResult : validationResults) {
                                                System.out.println(validationResult); 
                                            }
                                        data.put("validationResults", validationResults);
                                    }
                                    if (formData != null) {
                                        System.out.println("formData trouvé dans la requête:");
                                        for (Map.Entry<String, String> entry : formData.entrySet()) {
                                            System.out.println("Clé : " + entry.getKey() + ", Valeur : " + entry.getValue());
                                        }
                                        data.put("formData", formData);
                                    }

                                    for (String key : data.keySet()) {
                                        System.out.println("key :" + data.get(key));
                                        request.setAttribute(key, data.get(key));
                                    }

                                    String url = modelView.getUrl();
                                    request.getRequestDispatcher(url).forward(request, response);
                                } else {
                                    out.println("<p> Type de retour non reconnu </p>");
                                }
                            } else {
                                out.println("<p> Annotation non supportée </p>");
                            }
                    }
                } else {
                    this.scanne.redirigeException(request, response, "Error 404: NOT FOUND", HttpServletResponse.SC_NOT_FOUND);
                }
            }
        } catch (Exception e) {
            this.scanne.redirigeException(request, response, e.getMessage(), HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        } finally {
            if (out != null) {
                out.close();
            }
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {

            processRequest(request, response);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            processRequest(request, response);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
