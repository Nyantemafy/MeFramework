package mg.itu.prom16;

import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import mg.itu.prom16.ModelView;

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
    }

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException, Exception {
        response.setContentType("text/html;charset=UTF-8");

         try (PrintWriter out = response.getWriter()) {
            out.println("<p>" + request.getRequestURL() + "</p>");
            Mapping mapping = scanne.ifMethod(request, this.urlMethod);
            if (mapping != null) {
                out.println("<p> Classe : " + mapping.getKey() + "</p>");
                out.println("<p> Methode: " + mapping.getValue() + "</p>");

                Object result = this.scanne.callMethod(mapping);
                if (result instanceof String) {
                    out.println("<p> Value returned : " + result + "</p>");
                } else if (result instanceof ModelView) {
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
