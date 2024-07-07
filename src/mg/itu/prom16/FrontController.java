package mg.itu.prom16;

import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.HashMap;

public class FrontController extends HttpServlet {
    List<String> ListController;
    HashMap<String, Mapping> urlMethod;
    Scanner scanne;

    @Override
    public void init() throws ServletException {
        ListController = new ArrayList<>();
        urlMethod = new HashMap<>();
        scanne = new Scanner();
        scanne.scann(this, this.ListController, urlMethod);
        getServletContext().setAttribute("controllerList", ListController);
        getServletContext().setAttribute("urlMethod", urlMethod);
    }
    
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException, Exception {
        response.setContentType("text/html;charset=UTF-8");

        try (PrintWriter out = response.getWriter()) {
            String uri = request.getRequestURI();
            String contextPath = request.getContextPath();
            String path = uri.substring(contextPath.length());

            System.out.println(path);
            
            if ("/".equals(path)) {
                List<String> controllerList = (List<String>) getServletContext().getAttribute("controllerList");
                HashMap<String, Mapping> urlMethod = (HashMap<String, Mapping>) getServletContext().getAttribute("urlMethod");

                out.println("<html>");
                out.println("<head>");
                out.println("<title>Liste des contrôleurs et méthodes</title>");
                out.println("</head>");
                out.println("<body>");
                out.println("<h1>Liste des contrôleurs et méthodes annotées</h1>");
                out.println("<ul>");
                for (String controller : controllerList) {
                    out.println("<li><strong>" + controller + "</strong>");
                    out.println("<ul>");
                    for (Map.Entry<String, Mapping> entry : urlMethod.entrySet()) {
                        if (entry.getValue().getKey().equals(controller)) {
                            out.println("<li>URL: " + entry.getKey() + " - Méthode: " + entry.getValue().getValue() + "</li>");
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
                Mapping mapping = scanne.ifMethod(request, this.urlMethod);
                if (mapping != null) {
                    out.println("<p> Classe : " + mapping.getKey() + "</p>");
                    out.println("<p> Methode: " + mapping.getValue() + "</p>");

                    Object result = this.scanne.callMethod(mapping, request);
                    if (result instanceof ModelView) {
                        ModelView modelView = (ModelView) result;
                        String url = modelView.getUrl();
                        HashMap<String, Object> data = modelView.getData();

                        for (String key : data.keySet()) {
                            request.setAttribute(key, data.get(key));
                        }

                        request.getRequestDispatcher(url).forward(request, response);
                    } else {
                        out.println("<p> Type de retour non reconnu </p>");
                    }
                } else {
                    out.println("<p> Error 404 : Not found </p>");
                }
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
