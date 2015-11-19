/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package userstudy;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 *
 * @author Mershack
 */
@WebServlet(name = "TaskInstancesCreator", urlPatterns = {"/TaskInstancesCreator"})
public class TaskInstancesCreator extends HttpServlet {

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

        HttpSession session = request.getSession();
        try {
            String command = request.getParameter("command");
            if (command.equalsIgnoreCase("getTempName")) {
                String tempDirName = getTempDirName();
                String tempDirPath = getServletContext().getRealPath("temp-files" + File.separator + tempDirName);
                out.print(tempDirName);
            } else if (command.equalsIgnoreCase("submitForm")) {
                String tempname = request.getParameter("tempname");
                String tempDirPath = getServletContext().getRealPath("temp-files" + File.separator + tempname);
                File tempDir = new File(tempDirPath);
                //create the folder if it does not exist
                if (!tempDir.exists()) {
                    tempDir.mkdir();
                }
                //process only if its multipart content
                if (ServletFileUpload.isMultipartContent(request)) {
                    try {
                        List<FileItem> multiparts = new ServletFileUpload(
                                new DiskFileItemFactory()).parseRequest(request);
                        for (FileItem item : multiparts) {
                            if (!item.isFormField()) {
                                String name = new File(item.getName()).getName();
                                //write the file to disk
                                item.write(new File(tempDir + File.separator + name));
                            }
                        }
                    } catch (Exception ex) {
                        request.setAttribute("message", "File Upload Failed due to " + ex);
                    }
                    String dataset = request.getParameter("dataset");
                    String viewer = request.getParameter("viewer");
                    String taskQn = request.getParameter("task");
                    //System.out.println("dataset - " + dataset + " :: viewer - " + viewer + " :: taskQn - " + taskQn);

                    //writeTaskInstXML(tempname, dataset, taskQn, viewer);
                    //set the variables as session variables                   
                    session.setAttribute("tempname", tempname);
                    session.setAttribute("viewer", viewer);
                    session.setAttribute("taskQn", taskQn);
                    session.setAttribute("dataset", dataset);

                } else {
                    System.out.println("The request did not include files");
                }
            } else if (command.equalsIgnoreCase("getTempId")) {
                String tempname = session.getAttribute("tempname").toString();

                out.write(tempname);
            } else if (command.equalsIgnoreCase("showVis")) {
                RequestDispatcher view = request.getRequestDispatcher("visualizationForTaskInstances.html");
                view.forward(request, response);
            } else if (command.equalsIgnoreCase("getViewerUrl")) {
                String viewer = session.getAttribute("viewer").toString();
                String tempname = session.getAttribute("tempname").toString();
                String viewerUrl = "temp-files/" + tempname + "/" + viewer;

                out.write(viewerUrl);
            } else if (command.equalsIgnoreCase("getDataset")) {
                String dataset = session.getAttribute("dataset").toString();
                String datasetUrl = getServerUrl(request) + ("/datasets/" + dataset + "/" + dataset);
                out.write(datasetUrl);
            } else if (command.equalsIgnoreCase("getNodePositions")) {
                String dataset = session.getAttribute("dataset").toString();
                String nodePositionsUrl = "datasets" + File.separator + dataset + File.separator + "positions.txt";

                File posFile = new File(getServletContext().getRealPath(nodePositionsUrl));
                BufferedReader br = new BufferedReader(new FileReader(posFile));
                String line = "";

                int cnt = 0;
                String alldata = "";
                line = br.readLine(); //this is the header. For now I will not be including it.
                while ((line = br.readLine()) != null) {
                    String[] split = line.split("\t");
                    //String name = split[0];
                    if (cnt > 0) {
                        alldata += "::::" + split[0] + "::" + split[1] + "::" + split[2];
                    } else {
                        alldata = split[0] + "::" + split[1] + "::" + split[2];
                    }
                    cnt++;
                }

                br.close();
                out.write(alldata);

            } else if (command.equalsIgnoreCase("getTask")) {
                String task = session.getAttribute("taskQn").toString();
                out.write(task);
            } else if (command.equalsIgnoreCase("getAnswerType")) {
                String taskQn = session.getAttribute("taskQn").toString();
                String answertype = "";
                //read the quanttasks file and return the answer type for the current task.
                //read the quant-task-files.
                String filename = getServletContext().getRealPath("quanttasks" + File.separator + "quanttasks.xml");
                File fXmlFile = new File(filename);
                DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
                DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
                Document doc = dBuilder.parse(fXmlFile);
                doc.getDocumentElement().normalize();

                //System.out.println("Root element :" + doc.getDocumentElement().getNodeName());
                NodeList taskNode = doc.getElementsByTagName("task");
                //get the condition urls and shortnames
                for (int i = 0; i < taskNode.getLength(); i++) {
                    Node nNode = taskNode.item(i);

                    if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                        Element eElement = (Element) nNode;
                        String qn = eElement.getElementsByTagName("taskquestion").item(0).getTextContent();
                        String anstype = eElement.getElementsByTagName("answertype").item(0).getTextContent();
                        String inputInterface = eElement.getElementsByTagName("inputinterface").item(0).getTextContent();
                        String outputInterface = eElement.getElementsByTagName("outputinterface").item(0).getTextContent();
                        //String instanceInterface = eElement.getElementsByTagName("instanceinterface").item(0).getTextContent();
                        //check if the question is similar                        
                        if (taskQn.trim().equalsIgnoreCase(qn.trim())) {
                            answertype = anstype;

                            answertype += ":::" + inputInterface + ":::" + outputInterface;
                            break;
                        }
                    }
                }
                session.setAttribute("answertype", answertype);
                out.write(answertype);
            } else if (command.equalsIgnoreCase("getInputSize")) {
                String taskQn = session.getAttribute("taskQn").toString();
                String inputSizeStr = "0";
                //read the quanttasks file and return the answer type for the current task.
                //read the quant-task-files.
                String filename = getServletContext().getRealPath("quanttasks" + File.separator + "quanttasks.xml");
                File fXmlFile = new File(filename);
                DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
                DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
                Document doc = dBuilder.parse(fXmlFile);
                doc.getDocumentElement().normalize();

                //System.out.println("Root element :" + doc.getDocumentElement().getNodeName());
                NodeList taskNode = doc.getElementsByTagName("task");
                //get the condition urls and shortnames
                for (int i = 0; i < taskNode.getLength(); i++) {
                    Node nNode = taskNode.item(i);

                    if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                        Element eElement = (Element) nNode;
                        String qn = eElement.getElementsByTagName("taskquestion").item(0).getTextContent();
                       

                        if (taskQn.trim().equalsIgnoreCase(qn.trim())) {
                             inputSizeStr = eElement.getElementsByTagName("inputsize").item(0).getTextContent();
                            break;
                        }
                    }
                }

                out.write(inputSizeStr);

            } else if (command.equalsIgnoreCase("saveAllTaskInstances")) {
                //get the taskinstances and answers as selected

                String tiStr = request.getParameter("taskInstances").toString();
                String ansStr = request.getParameter("answers").toString();

                String taskInstances[] = tiStr.split("::::");
                String answers[] = ansStr.split("::::");
                String graphType = request.getParameter("graphType");
                String dataset = session.getAttribute("dataset").toString();
                String taskQn = session.getAttribute("taskQn").toString();

                String answertypeStr = session.getAttribute("answertype").toString();
                //we will create an xml file for taskInstances.

                // String datasetUrl = getServerUrl(request) + ("datasets" +File.separator+ dataset);
                writeTaskInstancesToFile(dataset, graphType, taskInstances, answers, taskQn, answertypeStr);
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            out.close();
        }
    }

    public String getTempDirName() {
        String studyname = "";
        File f = new File(getServletContext().getRealPath("temp-files"));
        int count = 0;
        File[] files = f.listFiles();
        count = files.length;
        studyname = "temp" + (count + 1);
        return studyname;
    }

    public void writeTaskInstancesToFile(String dataset, String edgeType,
            String[] taskInstances, String[] answers, String taskQn, String answertypeStr) {

        try {
            System.out.println("dataset: " + dataset + " edgeType: " + edgeType
                    + " taskInstances-length: " + taskInstances.length + " answers-length: " + answers.length);

            String taskShortName = getTaskShortName(taskQn);
            taskShortName += ".xml";

            String dataseturl = "datasets" + File.separator + dataset + File.separator + edgeType;
            File file = new File(getServletContext().getRealPath(dataseturl + File.separator + taskShortName));

            System.out.println(dataseturl);
            
           // if (!file.exists()) {
            file.createNewFile();
            // }
            //do the actual writings of the results to the file
            FileWriter fw1 = new FileWriter(file);

            BufferedWriter bw1 = new BufferedWriter(fw1);
            PrintWriter pw = new PrintWriter(bw1);

            pw.println("<?xml version=\"1.0\"?>");
            pw.println("<taskFile>");

            System.out.println("answertype string ::  " + answertypeStr);

            //write the task instances. remember we currently have it as either answertype, 
            //or in case of options answertype, answertype:::option1::option2
            String anstypeStrSplit[] = answertypeStr.split(":::");
//            String answertype = anstypeStrSplit[0];

            /* System.out.println("[0] -- "+anstypeStrSplit[0]);
             System.out.println("[1] -- "+anstypeStrSplit[1]); */
            //pw.println("\t<answertype>" + answertype + "</answertype>");
            for (int i = 0; i < taskInstances.length; i++) {
                pw.println("\t<question>");
                String split[] = taskInstances[i].split("::");

                for (int j = 0; j < split.length; j++) {
                    pw.println("\t\t<input>" + split[j] + "</input>");
                }                
                pw.println("\t\t<answer>" + answers[i] + "</answer>");
                pw.println("\t</question>");
            }
            pw.println("</taskFile>");

            pw.close();
            bw1.close();

        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    public String getTaskShortName(String taskQn) {
        String shortname = "";

        try {
            //read the quant-task-files.
            String filename = getServletContext().getRealPath("quanttasks" + File.separator + "quanttasks.xml");
            File fXmlFile = new File(filename);
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(fXmlFile);
            doc.getDocumentElement().normalize();

            //System.out.println("Root element :" + doc.getDocumentElement().getNodeName());
            NodeList taskNode = doc.getElementsByTagName("task");
            //get the condition urls and shortnames
            for (int i = 0; i < taskNode.getLength(); i++) {
                Node nNode = taskNode.item(i);

                if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element eElement = (Element) nNode;
                    String sn = eElement.getElementsByTagName("taskname").item(0).getTextContent();
                    String qn = eElement.getElementsByTagName("taskquestion").item(0).getTextContent();
                    //check if the question is similar                        
                    if (taskQn.trim().equalsIgnoreCase(qn.trim())) {
                        shortname = sn;
                        break;
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return shortname;

    }

//     public void writeTaskInstXML(String tempname, String dataset, String taskQn, String viewer) {
//        try {
//            String tiSetupFilename = "taskInstanceSetup.xml";
//            File datadir = new File(getServletContext().getRealPath("temp-files" + File.separator + tempname + File.separator + "data"));
//
//            if (!datadir.exists()) {
//                datadir.mkdir();
//            }
//            File tiSetupFile = new File(getServletContext().getRealPath("temp-files" + File.separator + tempname + File.separator + "data" + File.separator + tiSetupFilename));
//            BufferedWriter bw1 = new BufferedWriter(new FileWriter(tiSetupFile));
//            PrintWriter pw1 = new PrintWriter(bw1);
//            pw1.println("<?xml version=\"1.0\"?>");   //first line of the xml
//            pw1.println("<taskInstaceSetup>");
//            pw1.println("\t<tempname>" + tempname + "</tempname>");
//            pw1.println("\t<dataset>" + dataset + "</dataset>");
//            pw1.println("\t<viewer>" + viewer + "</viewer>");
//            pw1.println("\t<taskQuestion>" + taskQn + "</taskQuestion>");
//            pw1.println("</taskInstaceSetup>");
//            pw1.close();
//
//        } catch (Exception ex) {
//            ex.printStackTrace();
//        }
//    }
    public String getServerUrl(HttpServletRequest request) {
        String uri = request.getScheme() + "://" + // "http" + "://
                request.getServerName() + // "myhost"
                ":" + // ":"
                request.getServerPort() + // "8080"
                request.getRequestURI();//+       // "/people"
        // "?" +                           // "?"
        // request.getQueryString(); 

        int lastbackslash = uri.lastIndexOf("/");
        return uri.substring(0, lastbackslash);
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
