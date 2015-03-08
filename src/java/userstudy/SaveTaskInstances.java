/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package userstudy;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author Mershack
 */
public class SaveTaskInstances extends HttpServlet {

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
       
        
          try {
            /* TODO output your page here. You may use following sample code. */
            System.out.println("I'm here at last");

            String jsonString = new String(); // this is your data sent from client
            try {            
                
                String taskInstancesFileName = request.getParameter("taskInstancesFileName");
                
                String datasetName = request.getParameter("datasetName");
                jsonString = request.getParameter("taskInstancesArray");               
               
                Gson gson = new Gson();
                JsonParser jsonParser = new JsonParser();
                JsonArray userArray = jsonParser.parse(jsonString).getAsJsonArray();
                List<TaskInstance> taskInstances = new ArrayList<TaskInstance>();
                for (JsonElement aUser : userArray) {
                    TaskInstance anInstance = gson.fromJson(aUser, TaskInstance.class);
                    taskInstances.add(anInstance);
                }

             //   String filename = "taskInstances_miserables.txt";
                //String filename = "positions_imdb_large.txt";
                
                File file = new File(getServletContext().getRealPath( "datasets"+ File.separator 
                        + datasetName+ File.separator + taskInstancesFileName +".xml"));
                
                BufferedWriter bw = new BufferedWriter(new FileWriter(file));
                PrintWriter pw = new PrintWriter(bw);
                
                
                
                  pw.println("<?xml version=\"1.0\"?>");   //first line of the xml                
                  pw.println("<taskFile>");
                  
                for (int i = 0; i < taskInstances.size(); i++) {
                    
                    pw.println("\t<question>");
                    String nodes = taskInstances.get(i).getNodes();
                    String nodesSplit[] = nodes.split("::");
                    
                    for(int j=0; j<nodesSplit.length; j++){
                        pw.println("\t\t<node>"+nodesSplit[j] +"</node>");                        
                    }
                    String answer = taskInstances.get(i).getAnswer();
                    pw.println("\t\t<answer>"+answer +"</answer>");
                    pw.println("\t</question>");
                }
                pw.println("</taskFile>");
                pw.close();
                bw.close();              
                out.print("finished");
            } catch (Exception e) {
                e.printStackTrace();
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            out.close();
        }
        
        
        
        
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
