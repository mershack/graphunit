
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */


import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.imageio.ImageIO;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.fileupload.FileItemFactory;


@WebServlet(name = "Upload", urlPatterns = {"/Upload"})
public class Upload extends HttpServlet {

    protected PrintWriter out = null;

    public void init() {
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        processRequest(request, response);
    	        
    }

    public void doGet(HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, java.io.IOException {
        processRequest(request, response);

    }

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        out = response.getWriter();

        String thepage = request.getParameter("page");        

        HttpSession session = request.getSession();    
     
    	response.setHeader("Access-Control-Allow-Origin", "*");
    	response.setHeader("Access-Control-Allow-Credentials", "true");     

            try {
               if (thepage.equalsIgnoreCase("uploadFile"))
               {
            	   String study = request.getParameter("studyName");
            	   String folder = getServletContext().getRealPath("/Studies/") + "/" + study + "/";
            	   if (!(new File(folder)).exists()){
                       response.getWriter().write("study doesn't exist");
                       response.getWriter().flush();
                       response.getWriter().close();
            	   }
            	   else
            		   uploadFileToFolder(request, folder);
               } 
               
               else if (thepage.equalsIgnoreCase("createStudy")){
            	   
            	   String study = request.getParameter("studyName");
            	   String folder = getServletContext().getRealPath("/Studies/") + "/" + study + "/";
            	   if (!(new File(folder)).exists()){
                       response.getWriter().write("study name unavailable");
                       response.getWriter().flush();
                       response.getWriter().close();          		   
            	   }
            	   else{
            		   (new File(folder)).mkdir();
                       response.getWriter().write("study created");
                       response.getWriter().flush();
                       response.getWriter().close();
            	   }
            	   
               }
              

            } catch (Exception ex) {
                ex.printStackTrace();
            }
    }
       
   
    public void uploadFileToFolder(HttpServletRequest request, String folder)
    {
        FileItemFactory factory = new DiskFileItemFactory();
        ServletFileUpload upload = new ServletFileUpload(factory);
        List uploadedItems = null;
        FileItem fileItem = null;

        String myFileName = "";
    	
        try {
			uploadedItems = upload.parseRequest(request);
		} catch (FileUploadException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}                	

        Iterator i = uploadedItems.iterator();
        String uniqueId = null;
        while (i.hasNext()) {
            fileItem = (FileItem) i.next();

            if (fileItem.isFormField() == false) {
                if (fileItem.getSize() > 0) {
                    File uploadedFile = null;
                    String myFullFileName = fileItem.getName(), slashType = (myFullFileName.lastIndexOf("\\") > 0) ? "\\" : "/";
                    int startIndex = myFullFileName.lastIndexOf(slashType);
                    myFileName = myFullFileName.substring(startIndex + 1, myFullFileName.length());

                   
                    try {
						fileItem.write(new File(folder,myFileName));
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
                    
                  
                }
              break;
                
            }
        }
    }
   
}