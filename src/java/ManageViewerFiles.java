/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

/**
 *
 * @author Mershack
 */
@WebServlet(urlPatterns = {"/ManageViewerFiles"})
public class ManageViewerFiles extends HttpServlet {

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
            //get the viewer files
            //add more files
            //delete some files
            /* TODO output your page here. You may use following sample code. */
            String command = request.getParameter("command");
            String viewerid = request.getParameter("viewerid");
            //System.out.println("Hello World");
            //System.out.println(command);
            //System.out.println(studyid);
            if (command.equalsIgnoreCase("getViewerFiles")) {
                //getting viewer files
                //first get the filenames in the directory.
                //I will get the list of files in this directory and send it

                String studyPath = getServletContext().getRealPath("/viewer/" + viewerid);

                File root = new File(studyPath);
                File[] list = root.listFiles();

                ArrayList<String> fileItemList = new ArrayList<String>();
                if (list == null) {
                    System.out.println("List is null");
                    return;
                }

                for (File f : list) {
                    if (f.isDirectory()) {
                        ArrayList<String> dirItems = getFileItems(f.getAbsolutePath(), f.getName());

                        for (int i = 0; i < dirItems.size(); i++) {
                            fileItemList.add(dirItems.get(i));
                        }

                    } else {
                        //System.out.println(f.getName());
                        fileItemList.add(f.getName());
                    }
                }

                System.out.println("**** Printing the fileItems now **** ");
                String outputStr = "";
                for (int i = 0; i < fileItemList.size(); i++) {
                    outputStr += fileItemList.get(i) + "::::";
                }

                out.println(outputStr);
            } else if (command.equalsIgnoreCase("addViewerFiles")) {
                //add the files
                //get the files and add them

                String studyFolderPath = getServletContext().getRealPath("/viewer/" + viewerid);
                File studyFolder = new File(studyFolderPath);

                //process only if its multipart content
                if (ServletFileUpload.isMultipartContent(request)) {
                    try {
                        List<FileItem> multiparts = new ServletFileUpload(
                                new DiskFileItemFactory()).parseRequest(request);
                        int cnt = 0;
                        for (FileItem item : multiparts) {
                            if (!item.isFormField()) {
                                // cnt++;
                                String name = new File(item.getName()).getName();
                                //write the file to disk  
                                if (!studyFolder.exists()) {
                                    studyFolder.mkdir();
                                    System.out.println("The Folder has been created");
                                }
                                item.write(new File(studyFolder + File.separator + name));
                                System.out.println("File name is :: " + name);
                            }
                        }

                        out.print("Files successfully uploaded");
                    } catch (Exception ex) {
                        //System.out.println("File Upload Failed due to " + ex);
                        out.print("File Upload Failed due to " + ex);
                    }

                } else {
                    // System.out.println("The request did not include files");
                    out.println("The request did not include files");
                }

            } else if (command.equalsIgnoreCase("deleteViewerFiles")) {
                //get the array of files and delete thems
                String[] mpk;

                //get the array of file-names
                mpk = request.getParameterValues("fileNames");
                for (int i = 0; i < mpk.length; i++) {
                    String filePath = getServletContext().getRealPath("/viewer/" + viewerid + "/" + mpk[i]);

                    File f = new File(filePath);
                    f.delete();

                }
            }
            //out.println("</html>");
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            out.close();
        }
    }

    public String getServerUrl(HttpServletRequest request) {
        String uri = request.getScheme() + "://"
                + // "http" + "://
                request.getServerName()
                + // "myhost"
                ":"
                + // ":"
                request.getServerPort()
                + // "8080"
                request.getRequestURI();//+       // "/people"

        int lastbackslash = uri.lastIndexOf("/");
        return uri.substring(0, lastbackslash);
    }

    public ArrayList<String> getFileItems(String studyurl, String dir) {
        ArrayList<String> fileNames = new ArrayList<String>();
        try {
            //System.out.println(studyurl);
            File root = new File(studyurl);
            File[] list = root.listFiles();

            //ArrayList<String> fileItems = new ArrayList<String>();
            if (list == null) {
                System.out.println("It is null");
                return null;
            }

            for (File f : list) {
                if (f.isDirectory()) {
                    getFileItems(f.getAbsolutePath(), dir);
                    System.out.println("Dir:" + f.getName());
                } else {

                    fileNames.add(dir + "/" + f.getName());

                    ///System.out.println("File:" + dir + "/" + f.getName());
                }
                //fileNames.add(f.getAbsolutePath());
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return fileNames;
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
