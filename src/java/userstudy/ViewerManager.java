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
public class ViewerManager extends HttpServlet {

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

            String command = request.getParameter("command").toString();
            String userid = request.getParameter("userid").toString();

            System.out.println("command is " + command);

            String viewerDirUrl = "users" + File.separator + userid + File.separator
                    + "viewers";

            if (command.equalsIgnoreCase("add")) {
                //we will be adding viewer files

                String viewerDirectoryName = request.getParameter("directoryName").toString();

                String viewerDirPath = getServletContext()
                        .getRealPath(viewerDirUrl + File.separator + viewerDirectoryName);
                File viewerDir = new File(viewerDirPath);

                if (!viewerDir.exists()) {
                    viewerDir.mkdir();
                }
                //process only if its multipart content
                if (ServletFileUpload.isMultipartContent(request)) {
                    try {
                        List<FileItem> multiparts = new ServletFileUpload(
                                new DiskFileItemFactory()).parseRequest(request);

                        for (FileItem item : multiparts) {
                            if (!item.isFormField()) {

                                String name = new File(item.getName()).getName();

                                //now write the dataset in that directory
                                item.write(new File(viewerDir + File.separator + name));
                            }
                        }
                    } catch (Exception ex) {
                        request.setAttribute("message", "File Upload Failed due to " + ex);
                    }

                } else {
                    System.out.println("The request did not include files");

                }
            } else if (command.equalsIgnoreCase("getExistingDirectoryNames")) {
                //get the names of the existing viewer directories

                File f = new File(getServletContext().getRealPath("users"
                        + File.separator + userid + File.separator + "viewers"));

                int count = 0;
                File[] files = f.listFiles();

                count = files.length;

                String viewerDirNames = "";

                //ArrayList<String> datasetNamesList = new ArrayList<String>();
                //get all the dataset names
                for (int i = 0; i < count; i++) {

                    if (i == 0) {
                        viewerDirNames = files[i].getName();
                    } else {
                        viewerDirNames += "::::" + files[i].getName();
                    }

                }

                System.out.println("The viewer directory names is " + viewerDirNames);

                out.print(viewerDirNames);

            } else if (command.equalsIgnoreCase("getExistingDirectoryNamesAndHTMLFilenames")) {
                //get the names of the existing viewer directories
                // and the list of all the html files, that exist in that file.                

                File f = new File(getServletContext().getRealPath("users"
                        + File.separator + userid + File.separator + "viewers"));

                int count = 0;
                File[] files = f.listFiles();

                count = files.length;

               // String viewerDirNames = "";
                ArrayList<String> viewerDirNameList = new ArrayList<String>();
                //get all the dataset names
                for (int i = 0; i < count; i++) {

                    viewerDirNameList.add(files[i].getName());

//                    if (i == 0) {
//                        viewerDirNames = files[i].getName();
//                    } else {
//                        viewerDirNames += "::::" + files[i].getName();
//                    }
                }

                //System.out.println("The viewer directory names is " + viewerDirNames);
                //Now get the html pages in each directory.
                ArrayList<String> viewerHTMLFilenames = new ArrayList<String>();

                int cnt = 0;
                //get the file extensions of the datasets
                for (int i = 0; i < viewerDirNameList.size(); i++) {
                    String dirname = viewerDirNameList.get(i);

                    File viewerF = new File(getServletContext().getRealPath("users"
                            + File.separator + userid + File.separator + "viewers"
                            + File.separator + dirname.trim()));

                    String htmlFilenames = "no html files in directory";
                    File[] viewerFiles = viewerF.listFiles();

                    cnt = 0;
                    for (int j = 0; j < viewerFiles.length; j++) {
                        String filename = viewerFiles[j].getName();
                        String ext = "";

                        if (filename.lastIndexOf(".") > 0) {
                            ext = filename.substring(filename.lastIndexOf("."));
                        }

                        if (ext.trim().equalsIgnoreCase(".html")
                                || ext.trim().equalsIgnoreCase(".htm")
                                || ext.trim().equalsIgnoreCase(".xhtml")) {

                            if (cnt == 0) {
                                htmlFilenames = filename;
                            } else {
                                htmlFilenames += "," + filename;
                            }
                            cnt++;
                        }
                    }
                    viewerHTMLFilenames.add(htmlFilenames);
                }

                String viewerDirNames = "";
                //now combine the directory names and the html files that they have.          
                for (int i = 0; i < viewerDirNameList.size(); i++) {
                    String dirname = viewerDirNameList.get(i);
                    if (i == 0) {
                        viewerDirNames = dirname;
                    } else {
                        viewerDirNames += "::::" + dirname;
                    }
                }

                String htmlFileNamesList = "";

                for (int i = 0; i < viewerHTMLFilenames.size(); i++) {
                    String filename = viewerHTMLFilenames.get(i);
                    if (i == 0) {
                        htmlFileNamesList = filename;
                    } else {
                        htmlFileNamesList += "::::" + filename;
                    }
                }

                
                //now combine both the dirnames and htmlFiles
                String viewerDirNamesAndHTMLFiles = viewerDirNames + "::::::" + htmlFileNamesList;
                
                System.out.println("-- "+viewerDirNamesAndHTMLFiles);

                out.print(viewerDirNamesAndHTMLFiles);

            }

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
