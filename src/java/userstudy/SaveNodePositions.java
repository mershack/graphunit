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
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.JSONObject;

/**
 *
 * @author Mershack
 */
public class SaveNodePositions extends HttpServlet {

    private final String DATA_DIR = "data";

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
             
                jsonString = request.getParameter("nodePositionsArray");
                if (request.getParameter("nodePositionsArray") == null) {
                    System.out.println("OBJARRAY IS NULL");
                } else {
                    System.out.println("OBJARRAY IS NOT NULL!!");
                }

                Gson gson = new Gson();
                JsonParser jsonParser = new JsonParser();
                JsonArray userArray = jsonParser.parse(jsonString).getAsJsonArray();
                List<NodePositions> nodePositions = new ArrayList<NodePositions>();
                for (JsonElement aUser : userArray) {
                    NodePositions aPosition = gson.fromJson(aUser, NodePositions.class);
                    nodePositions.add(aPosition);
                }

                //write the node positions to file
                //String filename = "positions2.txt";
                String filename = "positions_imdb_small.txt";
                //String filename = "positions_imdb_large.txt";

                File file = new File(getServletContext().getRealPath(DATA_DIR + File.separator + filename));

                BufferedWriter bw = new BufferedWriter(new FileWriter(file));

                PrintWriter pw = new PrintWriter(bw);
                pw.println("name\tx\ty");
                for (int i = 0; i < nodePositions.size(); i++) {
                    //get the TaskCode
                    String name = nodePositions.get(i).getName();
                    String x = nodePositions.get(i).getX();
                    String y = nodePositions.get(i).getY();
                    pw.println(name + "\t" + x + "\t" +y);
                    System.out.println(name + "\t" + x + "\t" +y);
                }
                pw.close();
                bw.close();
                System.out.println("The size of the nodePositions is :: "+ nodePositions.size());
                System.out.println("FINISHED!!");
              
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
