package userstudy;

import com.amazon.mturk.requester.MTurkRequestsMgr;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.HashMap;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 *
 * @author Mershack
 */
public class StudySetup extends HttpServlet {

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    HashMap<String, StudySetupParameters> setupParameters = new HashMap<String, StudySetupParameters>();
    private final String DATA_DIR = "data";

    String questionTemplateName = "graphQuestionForm.xml";
    MyUtils utils = new MyUtils();

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        try {

            HttpSession session = request.getSession();

            // String nameofstudy = session.getAttribute("studyname").toString();
            //  String studyId = session.getId()+ nameofstudy;
            StudySetupParameters spmts = (StudySetupParameters) setupParameters.get(session.getId()); //get the object of that user session if it exists to avoid global variables being changed erratically

            if (spmts == null) {
                spmts = new StudySetupParameters();
            }

            String command = request.getParameter("command");

            if (command.equalsIgnoreCase("getStudyName")) {

                String studyname = getStudyName();

                spmts.studydataurl = "studies" + File.separator + studyname + File.separator + "data";

                out.print(studyname);
                //get the c
            } else if (command.equalsIgnoreCase("getCurrentStudyName")) {
                out.print(spmts.studyname);
            } else if (command.equalsIgnoreCase("Demo") || command.equalsIgnoreCase("Submit")) {
                //get the ViewerConditions
                spmts.studyname = request.getParameter("studyname");
                //System.out.println("The Study name is :: "+ studyname);

                String vc[] = request.getParameterValues("conditions");
                spmts.viewerConditions = new ArrayList<String>();
                if (vc != null) {

                    for (int i = 0; i < vc.length; i++) {
                        if (!vc[i].isEmpty()) {
                            spmts.viewerConditions.add(vc[i]);
                        }
                    }
                }

                String vcsn[] = request.getParameterValues("condition-shortnames");
                spmts.viewerConditionShortNames = new ArrayList<String>();
                if (vcsn != null) {
                    for (int i = 0; i < vcsn.length; i++) {
                        if (!vcsn[i].isEmpty()) {
                            spmts.viewerConditionShortNames.add(vcsn[i]);
                        }
                    }
                }

                //get the viewer width and height
                String viewerWidth = request.getParameter("viewerWidth");
                String viewerHeight = request.getParameter("viewerHeight");

                spmts.viewerHeight = viewerHeight;
                spmts.viewerWidth = viewerWidth;

                //get the dataset
                spmts.dataset = request.getParameter("dataset");

                //get the experiment type;
                spmts.expType = request.getParameter("expType");
                //get the quantitative tasks, task sizes, and task times            
                String qt[] = request.getParameterValues("quantitativeTasks");
                String qts[] = request.getParameterValues("quantitativeTaskSize");
                String qtt[] = request.getParameterValues("quantitativeTaskTime");

                spmts.quantitativeQuestions = new ArrayList<String>();
                spmts.quantQuestionSizes = new ArrayList<String>();
                spmts.quantQuestionTime = new ArrayList<String>();
                spmts.qualitativeQuestions = new ArrayList<String>();
                spmts.qualitativeQuestionsPositions = new ArrayList<String>();

                if (qt != null && qts != null & qtt != null) {
                    for (int i = 0; i < qt.length; i++) {
                        //add the task if task, size and time are not empty.
                        if (!qt[i].isEmpty() && !qts[i].isEmpty() && !qtt[i].isEmpty()) {
                            //  System.out.println("-- Task: "+ qt[i] + "   ---Size: "+ "  --Time: "+qtt[i]); 
                            spmts.quantitativeQuestions.add(qt[i]);
                            spmts.quantQuestionSizes.add(qts[i]);
                            spmts.quantQuestionTime.add(qtt[i]);
                        }
                    }
                }
                //get the trainingSize
                if (!request.getParameter("trainingSize").isEmpty()) //NB: if it is empty we will use the default training size
                {
                    spmts.trainingSize = Integer.parseInt(request.getParameter("trainingSize"));
                }
                //System.out.println("The training size is: "+ spmts.trainingSize);

                //qet the qualitative task details as well                       
                String qlt[] = request.getParameterValues("qualitativeTasks");
                String qltPos[] = request.getParameterValues("qualitativeTasksPositions");

                if (qlt != null && !qlt[0].isEmpty()) {
                    for (int i = 0; i < qlt.length; i++) {
                        spmts.qualitativeQuestions.add(qlt[i]);
                        // System.out.println("QUAL:: " + qlt[i] + " ---- POS:: "+qltPos[i]);
                    }
                    if (qltPos != null) {
                        for (int i = 0; i < qltPos.length; i++) {
                            spmts.qualitativeQuestionsPositions.add(qltPos[i]);
                        }
                    }
                }

                writeTasksToFile(spmts); //writing the tasks to file
                deleteAllExistingResultsFile(spmts); //delete the existing results file

                //get the command: if it is demo, we will not do anything extra otherwise
                // we will delete any existing result files and do other cleanups before the study starts.    
                if (command.equalsIgnoreCase("Submit")) {  //demo
                    String hitTitle = request.getParameter("hitTitle");
                    String awsAccessKey = request.getParameter("awsAccessKey");
                    String awsSecretKey = request.getParameter("awsSecretKey");

                    String maxAssignments = request.getParameter("maxAssignments");
                    String hitReward = request.getParameter("hitReward");

                    String questionTemplatePath = getServletContext().getRealPath(DATA_DIR + File.separator + questionTemplateName);

                    //put the study on Mechanical Turk
                    // createMTurkHIT(hitTitle, awsAccessKey, awsSecretKey, hitReward.trim(), Integer.parseInt(maxAssignments), questionTemplatePath);
                    //redirect to the setup-completed page
                    //TODO: Check if the HIT was successfully created otherwise redirect to an error acknowledgement page.
                    //write the url for the study and the url for getting the results
                    response.sendRedirect("setup-completed.html");
                }

                setupParameters.put(session.getId(), spmts); //reput the object to the hashtable for future reference

            } else if (command.equalsIgnoreCase("getTaskInterfaceMethods")) {
                String taskQuestion = request.getParameter("taskQuestion").toString();
                out.print(getTaskInterfaceMethods(taskQuestion, request));
            }

            /* String studyUrl = "StudyManager?studyname" + spmts.studyname;
             String resulturl = "StudyResults?studyname=" + spmts.studyname;

             String msg = "<html>";
             msg += "<head><title>setup-completed</title>";
             msg += "<body><p> Setup Completed Successfully, It will be placed on Mechanical Turk if correct details were provided </p>";
             msg += "<p> <b> Link to the User Study : " + studyUrl + "</b></p>";
             msg += "<p> <b> Link for accessing results : " + resulturl + "</b></p>";
             msg += "</body></html>";
            
             System.out.println(msg);

             out = response.getWriter();
             out.write(msg);
             out.flush();
             out.close();*/
        } finally {
            out.close();
        }
    }

    /**
     * This method will delete all existing result files if any, before the
     * study starts
     */
    public void deleteAllExistingResultsFile(StudySetupParameters spmts) {
        //delete the accuracy and time files for each of the given conditions
        //get the filenames

        System.out.println("Viewer Conditions size in Delete:: " + spmts.viewerConditions.size());

        ArrayList<String> accuracyFilenames = utils.getAccuracyFileNames(spmts.viewerConditions.size());
        ArrayList<String> timeFilenames = utils.getTimeFileNames(spmts.viewerConditions.size());
        String filename;
        File file;

        //delete the files if they exist.
        for (int i = 0; i < accuracyFilenames.size(); i++) {
            filename = accuracyFilenames.get(i);

            file = new File(getServletContext().getRealPath(spmts.studydataurl + File.separator + filename));
            if (file.exists()) {
                file.delete();
            }

            filename = timeFilenames.get(i);
            file = new File(getServletContext().getRealPath(spmts.studydataurl + File.separator + filename));

            if (file.exists()) {
                file.delete();
            }
        }
    }

    /**
     * This method will go through the study folders and return a symbolic name
     * for the current study e.g study1, study2, etc. by counting the number of
     * files in the directory
     *
     * @return
     */
    public String getStudyName() {
        String studyname = "";
        File f = new File(getServletContext().getRealPath("studies"));
        int count = 0;
        File[] files = f.listFiles();
        count = files.length;

        studyname = "study" + (count + 1);
        return studyname;
    }

    public void writeTasksToFile(StudySetupParameters spmts) {
        System.out.println("##over here");
        String quantTaskFilename = "quantitativeQuestions.txt";
        try {
            //////////////////////////////////////////////////////////////////////////////////
            //write the xml file now "eventually it might be the only one we will be using
            quantTaskFilename = "quantitativeTasks.xml";

            // File quantTaskFile = new File()
            // quanTaskFile = new File(getServletContext().getRealPath(DATA_DIR + File.separator + quantTaskFilename));
            File datadir = new File(getServletContext().getRealPath("studies" + File.separator + spmts.studyname + File.separator + DATA_DIR));
            if (!datadir.exists()) {
                datadir.mkdir();
            }
            File quanTaskFile = new File(getServletContext().getRealPath("studies" + File.separator + spmts.studyname + File.separator + DATA_DIR + File.separator + quantTaskFilename));

            BufferedWriter bw1 = new BufferedWriter(new FileWriter(quanTaskFile));

            PrintWriter pw1 = new PrintWriter(bw1);

            pw1.println("<?xml version=\"1.0\"?>");   //first line of the xml
            pw1.println("<study_specification>");
            pw1.println("<dataset>" + spmts.dataset + "</dataset>");
            //pw1.println("<datasetType>" + spmts.datasetType + "</datasetType>");
            pw1.println("<studyname>" + spmts.studyname + "</studyname>");
            pw1.println("<experimenttype>" + spmts.expType + "</experimenttype>");
            pw1.println("<trainingsize>" + spmts.trainingSize + "</trainingsize>");

            //the conditions
            for (int i = 0; i < spmts.viewerConditions.size(); i++) {
                pw1.println("<condition>");
                pw1.println("<conditionurl>" + spmts.viewerConditions.get(i) + "</conditionurl>");
                pw1.println("<conditionshortname>" + spmts.viewerConditionShortNames.get(i) + "</conditionshortname>");
                pw1.println("</condition>");
            }

            //the viewer dimensions
            pw1.println("<viewerwidth>" + spmts.viewerWidth + "</viewerwidth>");
            pw1.println("<viewerheight>" + spmts.viewerHeight + "</viewerheight>");

            //the tasks
            String taskName = "";
            for (int i = 0; i < spmts.quantitativeQuestions.size(); i++) {
                taskName = getTaskCode(spmts.quantitativeQuestions.get(i));

                pw1.println("<task>");
                pw1.println("<name>" + taskName + "</name>");
                pw1.println("<question>" + spmts.quantitativeQuestions.get(i) + "</question>");
                pw1.println("<size>" + spmts.quantQuestionSizes.get(i) + "</size>");
                pw1.println("<time>" + spmts.quantQuestionTime.get(i) + "</time>");
                pw1.println("</task>");

                //pw1.println(taskName + "::" + quantitativeQuestions.get(i) + "::" + quantQuestionSizes.get(i) + "::" + quantQuestionTime.get(i));
            }
            //write the qualitative tasks also
            for (int i = 0; i < spmts.qualitativeQuestions.size(); i++) {
                taskName = getQualitativeTaskCode(spmts.qualitativeQuestions.get(i));
                pw1.println("<qualtask>");
                pw1.println("<name>" + taskName + "</name>");
                pw1.println("<question>" + spmts.qualitativeQuestions.get(i) + "</question>");

                String qualtaskPos = spmts.qualitativeQuestionsPositions.get(i);
                if (!qualtaskPos.equalsIgnoreCase("before")) {
                    qualtaskPos = "after";
                }
                pw1.println("<qualtaskPos>" + qualtaskPos + "</qualtaskPos>");
                pw1.println("</qualtask>");
            }

            pw1.println("</study_specification>");
            // pw1.println("Finished");            
            pw1.close();

            //copy the userstudy.html and result-graphs.html files to that folder as well
           /* Path FROM = Paths.get(getServletContext().getRealPath("userstudy.html"));
             Path TO = Paths.get(getServletContext().getRealPath("studies" + File.separator + studyname + File.separator + "userstudy.html"));
             //overwrite existing file, if exists
             CopyOption[] options = new CopyOption[]{
             StandardCopyOption.REPLACE_EXISTING,
             StandardCopyOption.COPY_ATTRIBUTES
             };
             Files.copy(FROM, TO, options);

             FROM = Paths.get(getServletContext().getRealPath("result-graphs.html"));
             TO = Paths.get(getServletContext().getRealPath("studies" + File.separator + studyname + File.separator + "result-graphs.html"));

             Files.copy(FROM, TO, options);  */
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    public String getTaskCode(String task) {
               
        
        /*String taskCode = "";

        //System.out.println("::::::::"+task+"::::::::::::");
        if (task.equalsIgnoreCase("Are the two highlighted nodes directly connected?")) {
            taskCode = "neighborOneStep";
        } else if (task.equalsIgnoreCase("Can you get from one of the highlighted nodes to the other with exactly 2 steps?")) {
            taskCode = "neighborTwoStep";
        } else if (task.equalsIgnoreCase("How many nodes can be reached in one step from the highlighted node?")) {
            taskCode = "neighborCount";
        } else if (task.equalsIgnoreCase("Are the three highlighted nodes directly connected?")) {
            taskCode = "neighborThreeNodes";
        } else if (task.equalsIgnoreCase("Select the most connected node")) {
            taskCode = "mostConnectedNode";
        }
        else if(task.equalsIgnoreCase("Given the two highlighted nodes, select the one with the highest degree")){
            taskCode = "selectNodeWithHighestDegree2";
        }*/

        
        
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
                    if (task.trim().equalsIgnoreCase(qn.trim())) {
                        shortname = sn;
                        break;
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        
        System.out.println("The short name is: "+ shortname);
        
        return shortname;
        
        
        
        
        //return taskCode;
    }

    public String getQualitativeTaskCode(String task) {
        String taskCode = "";
        if (task.equalsIgnoreCase("Rate the easiness of the visualization tasks  from 1-Not easy to 5-Very Easy")) {
            taskCode = "rate_vis_easiness";
        } else if (task.equalsIgnoreCase("Have you worked with this type of visualization before?")) {
            taskCode = "worked_with_vis_before";
        } else if (task.equalsIgnoreCase("How will you Rate your familiarity with this type of visualization prior to this study?")) {
            taskCode = "rate_vis_familiarity";
        } else if (task.equalsIgnoreCase("Please enter your Mechanical Turk ID")) {
            taskCode = "enter_turk_id";
        }
        else if(task.equalsIgnoreCase("Do you have any feedback,comment or what issue did you had in this study?")){
            taskCode="feedback_comment_issues";
        }

        return taskCode;
    }

    public String getTaskInterfaceMethods(String taskQn, HttpServletRequest request) {

        //read the quantitative tasks file and get the taskNode
        Node tasknode = getTaskNodeFromTaskFile(request, taskQn);
          //get the interface methods
        String inInterface = ((Element) tasknode).getElementsByTagName("inputinterface").item(0).getTextContent();
        String outInterface = ((Element) tasknode).getElementsByTagName("outputinterface").item(0).getTextContent();

        return inInterface + "::" + outInterface;
    }

    public Node getTaskNodeFromTaskFile(HttpServletRequest request, String taskname) {
        String taskFilename = "quanttasks.xml";
        String taskFileDir = "quanttasks";

        Node node = null;
        try {

            File xmlFile = new File(getServletContext().getRealPath(taskFileDir + File.separator + taskFilename));
            DocumentBuilderFactory dbFactory2 = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder2 = dbFactory2.newDocumentBuilder();
            Document doc = dBuilder2.parse(xmlFile);
            doc.getDocumentElement().normalize();
            NodeList taskNodes = doc.getElementsByTagName("task");

            for (int i = 0; i < taskNodes.getLength(); i++) {
                Node tNode = taskNodes.item(i);
                if (tNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element eElement = (Element) tNode;

                    String curTaskName = eElement.getElementsByTagName("taskquestion").item(0).getTextContent();

                    if (taskname.trim().equals(curTaskName.trim())) {
                        node = tNode;
                        break;
                    }
                }

            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return node;

    }

    /**
     * prepare and call a method to create the mturk HIT
     */
    public void createMTurkHIT(String title, String awsAccessKey, String secretKey, String reward, int maxAssignments, String questionTemplatePath) {

        /*  if (dataPath.indexOf("\\") >= 0) {
         dataPath = dataPath.replaceAll("\\\\", "\\\\\\\\");
         }*/
        //System.out.println("THE FILE PATH IS ::: "+questionTemplatePath);
        MTurkRequestsMgr mturkrequestMgr = new MTurkRequestsMgr(title, awsAccessKey, secretKey, reward, maxAssignments, questionTemplatePath);
        //MTurkRequestsMgr mturkrequestMgr = new MTurkRequestsMgr(title, awsAccessKey, secretKey, reward, maxAssignments, questionTemplatePath, "");
        //MTurkRequestsMgr mturkrequestMgr = new MTurkRequestsMgr(questionTemplatePath);
        //request the hit to e created
        mturkrequestMgr.createHITRequest();

        //set the other variables if they are not null or empty
    /*    if (!title.isEmpty()) {
         mturkrequestMgr.setTitle(title);
         }
         if (reward >= 0.0) {
         mturkrequestMgr.setReward(reward + "");
         }
         if (!awsAccessKey.isEmpty()) {
         mturkrequestMgr.setAwsAccessKey(awsAccessKey);
         }
         if (!secretKey.isEmpty()) {
         mturkrequestMgr.setSecretKey(secretKey);
         }
         //request the hit to e created
         mturkrequestMgr.createHITRequest();
         */
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
