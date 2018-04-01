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
import java.util.ArrayList;
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
    
    MyUtils utils = new MyUtils();
    
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
            } else if (command.equalsIgnoreCase("getQuantitativeTasks")) {
                //read the tasks from the file and send it.
                //msg = "task1"

                String userid = request.getParameter("userid").toString();

                //first get the system quantitative tasks
                File sysTasklistFile = new File(getServletContext().getRealPath(
                        "quanttasks" + File.separator + "quanttasklist.txt"));

                String line = "";
                BufferedReader br1 = new BufferedReader(new FileReader(sysTasklistFile));
                //read the header
                br1.readLine();

                String allTasks = "";
                ArrayList<String> taskGroups = new ArrayList<String>();
                ArrayList<String> AllTaskLines = new ArrayList<String>();

                while ((line = br1.readLine()) != null) {
                    String group = line.split(":::")[2];
                    boolean groupExists = false;
                    //check if the group already exists in the array
                    for (int i = 0; i < taskGroups.size(); i++) {
                        if (group.trim().equalsIgnoreCase(taskGroups.get(i))) {
                            groupExists = true;
                            break;
                        }
                    }

                    if (!groupExists) {
                        taskGroups.add(group.trim());
                    }
                    AllTaskLines.add(line);
                }

                String taskQnLines[] = new String[taskGroups.size()];
                //initialize with empty strings
                for (int j = 0; j < taskQnLines.length; j++) {
                    taskQnLines[j] = "";
                }

                for (int i = 0; i < AllTaskLines.size(); i++) {
                    String group = AllTaskLines.get(i).split(":::")[2];
                    String qn = AllTaskLines.get(i).split(":::")[1];

                    //check the index of the group
                    int indx = 0;
                    for (int j = 0; j < taskGroups.size(); j++) {
                        if (group.trim().equalsIgnoreCase(taskGroups.get(j))) {
                            indx = j;
                            break;
                        }
                    }
                    if (taskQnLines[indx].isEmpty()) {
                        taskQnLines[indx] = group.trim() + "::::" + qn.trim();
                    } else {
                        taskQnLines[indx] += ":::" + qn.trim();
                    }
                }

                //now get the user-defined tasks
                File usertaskListFile = new File(getServletContext().getRealPath(
                        "users" + File.separator + userid + File.separator
                        + "quanttasks" + File.separator + "quanttasklist.txt"));
                BufferedReader br2 = new BufferedReader(new FileReader(usertaskListFile));

                AllTaskLines = new ArrayList<String>();
                taskGroups = new ArrayList<String>();
                //read the header again for this file too.
                br2.readLine();
                while ((line = br2.readLine()) != null) {
                    
                    if (line.split(":::").length > 2) {
                        String group = userid + "_" + line.split(":::")[2].trim();

                        boolean groupExists = false;
                        //check if the group already exists in the array
                        for (int i = 0; i < taskGroups.size(); i++) {
                            if (group.trim().equalsIgnoreCase(taskGroups.get(i))) {
                                groupExists = true;
                                break;
                            }
                        }

                        if (!groupExists) {
                            taskGroups.add(group.trim());
                        }
                    }
                    
                    AllTaskLines.add(line);
                }

                String usrTaskQnLines[] = new String[taskGroups.size()];
                //first initialize the elements with empty strings.
                for (int i = 0; i < usrTaskQnLines.length; i++) {
                    usrTaskQnLines[i] = "";
                }

                for (int i = 0; i < AllTaskLines.size(); i++) {
                    String group = userid + "_" + AllTaskLines.get(i).split(":::")[2].trim();
                    String qn = AllTaskLines.get(i).split(":::")[1];

                    //check the index of the group
                    int indx = 0;
                    for (int j = 0; j < taskGroups.size(); j++) {
                        if (group.trim().equalsIgnoreCase(taskGroups.get(j))) {
                            indx = j;
                            break;
                        }
                    }
                    if (usrTaskQnLines[indx].isEmpty()) {
                        usrTaskQnLines[indx] = group.trim() + "::::" + qn.trim();
                    } else {
                        usrTaskQnLines[indx] += ":::" + qn.trim();
                    }
                }

                //now get all of them as one string that we will send to the 
                //client
                String allTasksString = "";
                //this is for the system tasks
                for (int i = 0; i < taskQnLines.length; i++) {
                    //System.out.println("*** "+ taskQnLines[i]);
                    if (allTasksString.isEmpty()) {
                        allTasksString = taskQnLines[i];
                    } else {
                        allTasksString += ":::::" + taskQnLines[i];
                    }
                }
                //and this is for the user defined tasks
                for (int i = 0; i < usrTaskQnLines.length; i++) {
                    if (allTasksString.isEmpty()) {
                        allTasksString = usrTaskQnLines[i];
                    } else {
                        allTasksString += ":::::" + usrTaskQnLines[i];
                    }
                }

                br1.close();
                br2.close();
                out.write(allTasksString);
            } else if (command.equalsIgnoreCase("submitParametersToBegin")) {
                
                
                   String viewerDir = request.getParameter("viewerDirectory").toString();
                   String viewerURL = request.getParameter("viewerURL").toString();
                   String userid = request.getParameter("userid").toString();
                   String task = request.getParameter("task").toString();
                   String dataset = request.getParameter("dataset").toString();
                  String datasetFormat = request.getParameter("datasetFormat").toString();
                 
                   //save these parameters to file
                    session.setAttribute("viewerDirectory", viewerDir);
                    session.setAttribute("viewerURL", viewerDir+"/"+viewerURL);
                    session.setAttribute("taskQn", task);
                    session.setAttribute("dataset", dataset);
                    session.setAttribute("datasetFormat", datasetFormat);
                    session.setAttribute("userid", userid);
            
            } else if (command.equalsIgnoreCase("getCurrentInstanceCreationTempName")) {
                String tempname = session.getAttribute("tempname").toString();

                out.write(tempname);
            } else if (command.equalsIgnoreCase("showVis")) {
                RequestDispatcher view = request.getRequestDispatcher("visualizationForTaskInstances.html");
                view.forward(request, response);
            } else if (command.equalsIgnoreCase("getViewerUrl")) {
                String viewerDir = session.getAttribute("viewerDirectory").toString();
                String viewerURL = session.getAttribute("viewerURL").toString();
                String userid = session.getAttribute("userid").toString();
                String viewerUrl = "users/" + userid + "/viewers/" + viewerURL;
                
                out.write(viewerUrl);
            } else if (command.equalsIgnoreCase("getDataset")) {
                String dataset = session.getAttribute("dataset").toString();
                String datasetFormat = session.getAttribute("datasetFormat").toString();
                String userid = session.getAttribute("userid").toString();
                 
                String datasetUrl = getServerUrl(request) + ("/users/" +userid 
                        +"/datasets/" + dataset + "/" + dataset+datasetFormat);
                out.write(datasetUrl);
            } else if (command.equalsIgnoreCase("submitInstanceFile")){
                String userid = request.getParameter("userid").toString();    
                String dataset = request.getParameter("dataset").toString();    
                String task = request.getParameter("task").toString();                 
                
                System.out.println("task: " + task);
                System.out.println("dataset: " + dataset);
                
                String taskCode = utils.getTaskCode(request, task, userid);
                
                //process only if its multipart content
                if (ServletFileUpload.isMultipartContent(request)) {
                    try {
                        List<FileItem> multiparts = new ServletFileUpload(
                                new DiskFileItemFactory()).parseRequest(request);

                        for (FileItem item : multiparts) {
                            if (!item.isFormField()) {
                                
                                String taskInstancesDir = "users" + File.separator + userid + File.separator
                    + "taskInstances";
                                    
                                if (!dataset.trim().isEmpty()) {
                                     taskInstancesDir = "users" + File.separator 
                                             + userid + File.separator
                                             +  "taskInstances" +  File.separator
                                             + dataset;
                                 }                              
                      
                                 String instanceDirPath = getServletContext()
                                        .getRealPath(taskInstancesDir);
                                 
                               
                                 
                                 String name = taskCode + ".xml";
                                 
                                 
                                 

                                File instanceDir = new File(instanceDirPath);

                                if (!instanceDir.exists()) {
                                    instanceDir.mkdir();
                                }
                                
                                System.out.println(".... " + instanceDir + File.separator + name);
                                
                                
                                //now write the dataset in that directory
                                item.write(new File(instanceDir + File.separator + name));
                            }
                        }
                    } catch (Exception ex) {
                        request.setAttribute("message", "File Upload Failed due to " + ex);
                    }

                } else {
                    System.out.println("The request did not include files");
                }
                
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
                //String task = session.getAttribute("taskQn").toString();

                //load the task details once.
                String taskQn = session.getAttribute("taskQn").toString();
                String answertype = "";
                String outputType = "";
                String outputTypeDescription = "";
                String inputSize = "";
                String taskname = "";
                String hasCorrectAnswer = "";

                String userid = session.getAttribute("userid").toString();

                //we will read the task either from the main quant tasks, remember the tasks are saved as separate files.
                //Or we will read it from the user's quantitative tasks.
                //first read the quanttasklist file to find the short name of the task, then we will actually read it from the file
                File sysFile_QuanttaskList = new File(getServletContext().getRealPath("quanttasks" + File.separator + "quanttasklist.txt"));

                BufferedReader br = new BufferedReader(new FileReader(sysFile_QuanttaskList));
                String line = "";
                String sys_taskShortname = "";
                while ((line = br.readLine()) != null) {
                    if ((line.split(":::")[1].trim()).equalsIgnoreCase(taskQn.trim())) {
                        sys_taskShortname = line.split(":::")[0].trim();
                        break;
                    }
                }
                br.close();

                //check for the user tasks, if the user also has such a task{
                File usrFile_QuanttaskList = new File(getServletContext().getRealPath(
                        "users" + File.separator + userid + File.separator + "quanttasks" + File.separator + "quanttasklist.txt"));

                BufferedReader br2 = new BufferedReader(new FileReader(usrFile_QuanttaskList));
                line = "";
                String usr_taskShortname = "";
                while ((line = br2.readLine()) != null) {
                    if ((line.split(":::")[1].trim()).equalsIgnoreCase(taskQn.trim())) {
                        usr_taskShortname = line.split(":::")[0].trim();
                        break;
                    }
                }
                
                br2.close();

                String taskFileName = "";
                if (!usr_taskShortname.isEmpty()) {
                    //we will read the file from the user's directory
                    taskFileName = getServletContext().getRealPath("users"
                            + File.separator + userid + File.separator + "quanttasks" + File.separator + usr_taskShortname + ".xml");
                } else {
                    //we will read the file from the main quanttask directory.\
                    taskFileName = getServletContext().getRealPath("quanttasks" + File.separator + sys_taskShortname + ".xml");
                }

                //read the quanttasks file and return the answer type for the current task.
                //read the quant-task-files.
                //     String filename = getServletContext().getRealPath("quanttasks" + File.separator + "quanttasks.xml");
                File fXmlFile = new File(taskFileName);
                DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
                DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
                Document doc = dBuilder.parse(fXmlFile);
               // doc.getDocumentElement().normalize();

                //System.out.println("Root element :" + doc.getDocumentElement().getNodeName());
                NodeList taskNode = doc.getElementsByTagName("task_details");

                ArrayList<String> inputTypes = new ArrayList<String>();
                ArrayList<String> inputDescriptions = new ArrayList<String>();
                ArrayList<String> inputMediums = new ArrayList<String>();

                //System.out.println("Length of the nodelist is " + taskNode.getLength());
                //System.out.println("The task name is "+ taskFileName);
                //get the condition urls and shortnames
                //  for (int i = 0; i < taskNode.getLength(); i++) {
                Node nNode = taskNode.item(0);

                if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element eElement = (Element) nNode;
                    taskname = eElement.getElementsByTagName("taskname").item(0).getTextContent();
                    String qn = eElement.getElementsByTagName("taskquestion").item(0).getTextContent();
                    answertype = eElement.getElementsByTagName("answertype").item(0).getTextContent();
                    outputType = eElement.getElementsByTagName("outputtype").item(0).getTextContent();
                    outputTypeDescription = eElement.getElementsByTagName("outputTypeDescription").item(0).getTextContent();
                    inputSize = eElement.getElementsByTagName("inputsize").item(0).getTextContent();
                    inputSize = eElement.getElementsByTagName("inputsize").item(0).getTextContent();
                    hasCorrectAnswer = eElement.getElementsByTagName("hasCorrectAnswer").item(0).getTextContent();

                    NodeList inputs = eElement.getElementsByTagName("input");

                    for (int j = 0; j < inputs.getLength(); j++) {
                        Node iNode = inputs.item(j);
                        Element iElement = (Element) iNode;
                        String inputtype = iElement.getElementsByTagName("inputtype").item(0).getTextContent();
                        String inputdescription = iElement.getElementsByTagName("inputdescription").item(0).getTextContent();

                        String inputmedium = iElement.getElementsByTagName("inputmedium").item(0).getTextContent();

                        //System.out.println("the input medium is :: "+ inputmedium);
                        inputTypes.add(inputtype);
                        inputDescriptions.add(inputdescription);
                        inputMediums.add(inputmedium);
                    }
                }

                System.out.println("the output type is ::: " + outputType);
                System.out.println("the output type description is ::: " + outputTypeDescription);

                String inputsStr = "";
                for (int j = 0; j < inputTypes.size(); j++) {
                    if (inputsStr.isEmpty()) {
                        inputsStr = inputTypes.get(j) + "::"
                                + inputDescriptions.get(j)
                                + "::" + inputMediums.get(j);
                    } else {
                        inputsStr += ":::" + inputTypes.get(j) + "::" + inputDescriptions.get(j)
                                + "::" + inputMediums.get(j);
                    }
                }

                if (outputType.trim().isEmpty()) {
                    outputType = ""
                            + "";
                }

                String outputStr = outputType + "::" + outputTypeDescription;

                answertype += ":::" + outputStr;

                //  System.out.println(" output type---> " + outputStr);
                //   System.out.println("hello " + inputsStr);
                session.setAttribute("answertype", answertype);
                session.setAttribute("inputSize", inputSize);
                session.setAttribute("inputTypesAndDescriptions", inputsStr);
                session.setAttribute("outputType", outputType);
                session.setAttribute("outputTypeDescription", outputTypeDescription);
                session.setAttribute("taskname", taskname);
                session.setAttribute("hasCorrectAnswer", hasCorrectAnswer);
                out.write(taskQn);
            } else if (command.equalsIgnoreCase("getInputTypesAndDescriptions")) {
                String inputsStr = session.getAttribute("inputTypesAndDescriptions").toString();
                System.out.println("inputTypesAndDescriptions is " + inputsStr);

                out.write(inputsStr);
            } else if (command.equalsIgnoreCase("getAnswerType")) {
                String answertype = session.getAttribute("answertype").toString();
                System.out.println("The answer---type is " + answertype);

                out.write(answertype);
            } else if (command.equalsIgnoreCase("getInputSize")) {
                String inputSize = session.getAttribute("inputSize").toString();
                out.write(inputSize);
            } else if (command.equalsIgnoreCase("getTaskHasCorrectAnswer")) {
                //get the task has correct answer node and send it .
                String hasCorrectAnswer = session.getAttribute("hasCorrectAnswer").toString();
                out.write(hasCorrectAnswer);
            } else if (command.equalsIgnoreCase("saveAllTaskInstances")) {
                //System.out.println(" we are saving instances now");

                //get the taskinstances and answers as selected
                String tiStr = request.getParameter("taskInstances").toString();
                String ansStr = request.getParameter("answers").toString();
                String taskOptionsStr = request.getParameter("taskOptions").toString();

                String taskInstances[] = tiStr.split("::::");
                String answers[] = ansStr.split("::::");
                String taskOptions[] = taskOptionsStr.split("::::");
                //String graphType = request.getParameter("graphType");
                String userid = request.getParameter("userid").toString();

                String dataset = session.getAttribute("dataset").toString();
                String taskQn = session.getAttribute("taskQn").toString();

                String answertypeStr = session.getAttribute("answertype").toString();

                String taskname = session.getAttribute("taskname").toString();

                //we will create an xml file for taskInstances.
                // String datasetUrl = getServerUrl(request) + ("datasets" +File.separator+ dataset);
                writeTaskInstancesToFile(dataset, userid, taskInstances,
                        answers, taskQn, taskname, answertypeStr, taskOptions);
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

    public void writeTaskInstancesToFile(String dataset, String userid,
            String[] taskInstances, String[] answers, String taskQn, String taskname, String answertypeStr,
            String[] taskOptions) {

        try {

            //    System.out.println("dataset: " + dataset + " userid: " + userid
            //          + " taskInstances-length: " + taskInstances.length + " answers-length: " + answers.length);
            //create taskinstances directory if it does'nt exist
            //now get the user-defined tasks
            File taskInstancesDir = new File(getServletContext().getRealPath(
                    "users" + File.separator + userid + File.separator
                    + "taskInstances"));

            if (!taskInstancesDir.exists()) {
                taskInstancesDir.mkdir();
            }
            File taskInstancesDBDir = new File(getServletContext().getRealPath(
                    "users" + File.separator + userid + File.separator
                    + "taskInstances" + File.separator + dataset));

            if (!taskInstancesDBDir.exists()) {
                taskInstancesDBDir.mkdir();
            }
            //now we will be creating the file.

            File taskInstancesDBFile = new File(getServletContext().getRealPath(
                    "users" + File.separator + userid + File.separator
                    + "taskInstances" + File.separator + dataset
                    + File.separator + taskname + ".xml"));
            
            taskInstancesDBFile.createNewFile();

            //do the actual writings of the results to the file
            FileWriter fw1 = new FileWriter(taskInstancesDBFile);

            BufferedWriter bw1 = new BufferedWriter(fw1);
            PrintWriter pw = new PrintWriter(bw1);

            pw.println("<?xml version=\"1.0\"?>");
            pw.println("<taskFile>");

            //System.out.println("answertype string ::  " + answertypeStr);
            //write the task instances. remember we currently have it as either answertype, 
            //or in case of options answertype, answertype:::option1::option2
            String anstypeStrSplit[] = answertypeStr.split(":::");
             //            String answertype = anstypeStrSplit[0];

            /* System.out.println("[0] -- "+anstypeStrSplit[0]);
             System.out.println("[1] -- "+anstypeStrSplit[1]); */
            //pw.println("\t<answertype>" + answertype + "</answertype>");
            System.out.println("taskinstances length:: " + taskInstances.length);

            if (taskInstances.length > 0 && !taskInstances[0].isEmpty()) {
                //if there are task instances (i.e. inputs).
                for (int i = 0; i < taskInstances.length; i++) {
                    pw.println("\t<question>");
                    String inputs[] = taskInstances[i].split(":::");
                    for (int j = 0; j < inputs.length; j++) {
                        pw.println("\t\t<input>" + inputs[j] + "</input>");
                    }
                    if (taskOptions.length > i) {
                        pw.println("\t\t<taskoptions>" + taskOptions[i] + "</taskoptions>");
                    }
                    else{
                        pw.println("\t\t<taskoptions></taskoptions>");
                    }
                    if (answers.length > i) {
                        pw.println("\t\t<answer>" + answers[i] + "</answer>");
                    }
                    else{
                        pw.println("\t\t<answer></answer>");
                    }
                    pw.println("\t</question>");
                }
            } else if (answers.length > 0 && !answers[0].isEmpty()) {   //when there are answers but no inputs.
                //if there are task instances (i.e. inputs).
                for (int i = 0; i < answers.length; i++) {
                    pw.println("\t<question>");

                    pw.println("\t\t<input></input>");

                    if (taskOptions.length > i) {
                        pw.println("\t\t<taskoptions>" + taskOptions[i] + "</taskoptions>");
                    }
                    pw.println("\t\t<answer>" + answers[i] + "</answer>");
                    pw.println("\t</question>");
                }
            } else if (taskOptions.length > 0 && !taskOptions[0].isEmpty()) { //when there are answer options but no inputs or answers.
                for (int i = 0; i < taskOptions.length; i++) {
                    pw.println("\t<question>");

                    pw.println("\t\t<input></input>");

                    pw.println("\t\t<taskoptions>" + taskOptions[i] + "</taskoptions>");
               
                    pw.println("\t\t<answer></answer>");
                    pw.println("\t</question>");
                }
            }

            pw.println("</taskFile>");

            pw.close();
            bw1.close();

            //BufferedReader br2 = new BufferedReader(new FileReader(usertaskListFile));
            //create dataset directory if it doesn't already exist
            //now create the task instances file
            //String taskShortName = getTaskShortName(taskQn);
            //   taskShortName += ".xml";

            /*  String dataseturl = "datasets" + File.separator + dataset + File.separator + edgeType;
             File file = new File(getServletContext().getRealPath(dataseturl + File.separator + taskShortName));

             System.out.println(dataseturl);  */
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
