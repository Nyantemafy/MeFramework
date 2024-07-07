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
        } catch (Exception e) {
            throw new ServletException(e.getMessage(), e);
        }
    }

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");

        PrintWriter out = null;
        try {
            out = response.getWriter();
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

                    out.close();
                    request.getRequestDispatcher(url).forward(request, response);
                } else {
                    throw new IllegalArgumentException("Type de retour non reconnu");
                }
            } else {
                throw new IllegalStateException("Error 404 : Not found");
            }
        } catch (IllegalArgumentException | IllegalStateException e) {
            if (out != null) {
                out.println("<p> Exception : " + e.getMessage() + "</p>");
                e.printStackTrace(out);
            }
        } catch (Exception e) {
            if (out != null) {
                out.println("<p> Exception : " + e.getMessage() + "</p>");
                e.printStackTrace(out);
            }
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
