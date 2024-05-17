package src.mg.itu.prom16;

import java.io.IOException;
import java.io.PrintWriter;

import java.util.Map;
import java.util.HashMap;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class FrontController extends HttpServlet{
    Map<String,Class> urlMap;
    boolean dejaPasser;
    public void init() throws ServletException{
        String controllPackage = this.getInitParameter("PackageController");
        this.urlMap = Scanner.scanCurrentProjet(controllPackage);
        dejaPasser = false;
    }
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // response.setContentType("text/html;charset=UTF-8");
        // try (PrintWriter out = response.getWriter()) {
        //     out.println("URL : "+request.getRequestURL());
        // }
        PrintWriter out = response.getWriter();
        String url = request.getRequestURL().toString();
        out.print("Vous avez entrez cet url :"+url+"\n");
        out.print("Liste des controlleurs du projet : \n");
        for(String key : this.urlMap.keySet()){
            out.print("Cet url : "+ key +" est associé à la class "+ this.urlMap.get(key));
        }
        dejaPasser = true;
    }
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request,response);
    }
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }
}