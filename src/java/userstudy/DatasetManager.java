/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package userstudy;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

/**
 *
 * @author Mershack
 */
public class DatasetManager extends HttpServlet {

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
            //

            String command = request.getParameter("command").toString();
            String userid = request.getParameter("userid").toString();

            System.out.println("command is " + command);

            String studyDirUrl = "users" + File.separator + userid + File.separator
                    + "datasets";

            if (command.equalsIgnoreCase("add")) {
              //we will be adding a new dataset

                //process only if its multipart content
                if (ServletFileUpload.isMultipartContent(request)) {
                    try {
                        List<FileItem> multiparts = new ServletFileUpload(
                                new DiskFileItemFactory()).parseRequest(request);

                        for (FileItem item : multiparts) {
                            if (!item.isFormField()) {

                                String name = new File(item.getName()).getName();

                                //get the name for the dataset without the file extensions
                                String dsName = name.substring(0, name.lastIndexOf("."));

                                //  System.out.println("dataset name is " + dsName);
                                String dsDirPath = getServletContext()
                                        .getRealPath(studyDirUrl + File.separator + dsName);

                                File datasetDir = new File(dsDirPath);

                                if (!datasetDir.exists()) {
                                    datasetDir.mkdir();
                                }
                                //now write the dataset in that directory
                                item.write(new File(datasetDir + File.separator + name));
                            }
                        }
                    } catch (Exception ex) {
                        request.setAttribute("message", "File Upload Failed due to " + ex);
                    }

                } else {
                    System.out.println("The request did not include files");

                }
            } else if (command.equalsIgnoreCase("loadDatasets")) {

                String sysDatasets = getSystemDatasets();

                String userDatasets = getUserDatasets(userid);

                String allDatasets = sysDatasets;

                if (!userDatasets.trim().isEmpty()) {
                    allDatasets += "::::::" + userDatasets;
                }

                out.print(allDatasets);

            }

        } finally {
            out.close();
        }
    }

    public String getSystemDatasets() {

        //we will read the datasets the user has
        File f = new File(getServletContext().getRealPath("datasets"));

        int count = 0;
        File[] files = f.listFiles();

        count = files.length;

        String datasetNamesAndExtensions = "";

        ArrayList<String> datasetNamesList = new ArrayList<String>();

        //get all the dataset names
        for (int i = 0; i < count; i++) {
            datasetNamesList.add(files[i].getName());
        }

        ArrayList<String> datasetExtensions = new ArrayList<String>();

        int cnt = 0;

        //get the file extensions of the datasets
        for (int i = 0; i < datasetNamesList.size(); i++) {
            String fname = datasetNamesList.get(i);

            File dsF = new File(getServletContext().getRealPath("datasets"
                    + File.separator + fname.trim()));

            String dsFileExts = "";
            File[] dsFiles = dsF.listFiles();

            //System.out.println("*** " + fname);
            cnt = 0;
            for (int j = 0; j < dsFiles.length; j++) {
                String filename = dsFiles[j].getName();
                String ext = "";
                if (filename.lastIndexOf(".") > 0) {
                    ext = filename.substring(filename.lastIndexOf("."));

                    if (!ext.trim().isEmpty() && !ext.trim().equalsIgnoreCase(".txt")) {
                        //System.out.println("^^^ " + filename);
                        if (cnt == 0) {
                            dsFileExts = ext;
                        } else {
                            dsFileExts += "," + ext;
                        }
                        cnt++;
                    }
                }

            }
            datasetExtensions.add(dsFileExts);
        }

        //now combine the datasets with their extensions                 
        for (int i = 0; i < datasetNamesList.size(); i++) {

            String name = datasetNamesList.get(i);
            String ext = datasetExtensions.get(i);
            if (i == 0) {
                datasetNamesAndExtensions = name + "::" + ext;
            } else {
                datasetNamesAndExtensions += "::::" + name + "::" + ext;
            }
        }

        //System.out.println("___" + datasetNamesAndExtensions);

        return datasetNamesAndExtensions;

    }

    public String getUserDatasets(String userid) {
        //we will read the datasets the user has
        File f = new File(getServletContext().getRealPath("users"
                + File.separator + userid + File.separator + "datasets"));

        int count = 0;
        File[] files = f.listFiles();

        count = files.length;

        String datasetNamesAndExtensions = "";

        ArrayList<String> datasetNamesList = new ArrayList<String>();

        //get all the dataset names
        for (int i = 0; i < count; i++) {
            datasetNamesList.add(files[i].getName());

        }

        ArrayList<String> datasetExtensions = new ArrayList<String>();

        int cnt = 0;
        //get the file extensions of the datasets
        for (int i = 0; i < datasetNamesList.size(); i++) {
            String fname = datasetNamesList.get(i);

            File dsF = new File(getServletContext().getRealPath("users"
                    + File.separator + userid + File.separator + "datasets"
                    + File.separator + fname.trim()));

            String dsFileExts = "";
            File[] dsFiles = dsF.listFiles();
            
            cnt = 0;
            for (int j = 0; j < dsFiles.length; j++) {
                String filename = dsFiles[j].getName();
                String ext = "";

                if (filename.lastIndexOf(".") > 0) {
                    ext = filename.substring(filename.lastIndexOf("."));
                }

                if (!ext.trim().isEmpty() && !ext.trim().equalsIgnoreCase(".txt")) {
                    if (cnt == 0) {
                        dsFileExts = ext;
                    } else {
                        dsFileExts += "," + ext;
                    }
                    cnt++;
                }
            }
            datasetExtensions.add(dsFileExts);
        }

        //now combine the datasets with their extensions                 
        for (int i = 0; i < datasetNamesList.size(); i++) {

            String name = datasetNamesList.get(i);
            String ext = datasetExtensions.get(i);
            if (i == 0) {
                datasetNamesAndExtensions = name + "::" + ext;
            } else {
                datasetNamesAndExtensions += "::::" + name + "::" + ext;
            }
        }

        return datasetNamesAndExtensions;
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
