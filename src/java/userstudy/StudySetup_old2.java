/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package userstudy;

import com.amazon.mturk.requester.MTurkRequestsMgr;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.util.ArrayList;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author Mershack
 */
public class StudySetup_old2 extends HttpServlet {

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    ArrayList<String> viewerConditions = new ArrayList<String>();
    ArrayList<String> quantitativeQuestions = new ArrayList<String>();
    ArrayList<String> quantQuestionSizes = new ArrayList<String>();
    ArrayList<String> quantQuestionTime = new ArrayList<String>();
    ArrayList<String> qualitativeQuestions = new ArrayList<String>();

    String dataset;
    String expType;
    private final String DATA_DIR = "data";

    String questionTemplateName = "graphQuestionForm.xml";

    MyUtils utils = new MyUtils();

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        try {
            //get the ViewerConditions
            String vc[] = request.getParameterValues("conditions");

            viewerConditions = new ArrayList<String>();
            if (vc != null) {
              //  System.out.println(" VC size:: "+ vc.length);

                for (int i = 0; i < vc.length; i++) {
                    if (!vc[i].isEmpty()) {
                        viewerConditions.add(vc[i]);
                    }
                }
            }
            System.out.println("ViewerConditions +++ " + viewerConditions.size());
            //get the dataset
            dataset = request.getParameter("dataset");
            //get the experiment type;
            expType = request.getParameter("expType");
            //get the quantitative tasks, task sizes, and task times            
            String qt[] = request.getParameterValues("quantitativeTasks");
            String qts[] = request.getParameterValues("quantitativeTaskSize");
            String qtt[] = request.getParameterValues("quantitativeTaskTime");

            quantitativeQuestions = new ArrayList<String>();
            quantQuestionSizes = new ArrayList<String>();
            quantQuestionTime = new ArrayList<String>();
            qualitativeQuestions = new ArrayList<String>();

            if (qt != null && qts != null & qtt != null) {
                for (int i = 0; i < qt.length; i++) {
                    //add the task if task, size and time are not empty.
                    if (!qt[i].isEmpty() && !qts[i].isEmpty() && !qtt[i].isEmpty()) {
                        //  System.out.println("-- Task: "+ qt[i] + "   ---Size: "+ "  --Time: "+qtt[i]); 
                        quantitativeQuestions.add(qt[i]);
                        quantQuestionSizes.add(qts[i]);
                        quantQuestionTime.add(qtt[i]);
                    }
                }
            }
            //qet the qualitative task details as well                       
            String qlt[] = request.getParameterValues("qualitativeTasks");
            if (qlt != null) {
                for (int i = 0; i < qlt.length; i++) {
                    System.out.println("QUAL:: " + qlt[i]);
                }
            }

            writeTasksToFile(); //writing the tasks to file
            deleteAllExistingResultsFile(); //delete the existing results file

            //get the command: if it is demo, we will not do anything extra otherwise
            // we will delete any existing result files and do other cleanups before the study starts.            
            String command = request.getParameter("command");

            if (command != null && command.equalsIgnoreCase("Demo")) {  //demo
                out.print("Demo--- Yea");
            } else {
                    //get the details to be used for mechanical turk
                String hitTitle = request.getParameter("hitTitle");
                String awsAccessKey = request.getParameter("awsAccessKey");
                String awsSecretKey = request.getParameter("awsSecretKey");

                String maxAssignments = request.getParameter("maxAssignments");
                String hitReward = request.getParameter("hitReward");

                String questionTemplatePath = getServletContext().getRealPath(DATA_DIR + File.separator + questionTemplateName);
//if(title.)
                //put the study on Mechanical Turk

                createMTurkHIT(hitTitle, awsAccessKey, awsSecretKey, hitReward.trim(), Integer.parseInt(maxAssignments), questionTemplatePath);

                //redirect to the setup-completed page
                //TODO: Check if the HIT was successfully created otherwise redirect to an error acknowledgement page.
                response.sendRedirect("setup-completed.html");
            }

        } finally {
            out.close();
        }
    }

    /**
     * This method will delete all existing result files if any, before the
     * study starts
     */
    public void deleteAllExistingResultsFile() {
        //delete the accuracy and time files for each of the given conditions
        //get the filenames

        System.out.println("Viewer Conditions size in Delete:: " + viewerConditions.size());

        ArrayList<String> accuracyFilenames = utils.getAccuracyFileNames(viewerConditions.size());
        ArrayList<String> timeFilenames = utils.getTimeFileNames(viewerConditions.size());
        String filename;
        File file;

        //delete the files if they exist.
        for (int i = 0; i < accuracyFilenames.size(); i++) {
            filename = accuracyFilenames.get(i);

            file = new File(getServletContext().getRealPath(DATA_DIR + File.separator + filename));
            if (file.exists()) {
                file.delete();
            }

            filename = timeFilenames.get(i);
            file = new File(getServletContext().getRealPath(DATA_DIR + File.separator + filename));

            if (file.exists()) {
                file.delete();
            }
        }
    }

    public void writeTasksToFile() {
        String quantTaskFilename = "quantitativeTasks.xml";

        try {
            // File quantTaskFile = new File()
            File quanTaskFile = new File(getServletContext().getRealPath(DATA_DIR + File.separator + quantTaskFilename));

            BufferedWriter bw1 = new BufferedWriter(new FileWriter(quanTaskFile));

            PrintWriter pw1 = new PrintWriter(bw1);

            pw1.println("<?xml version=\"1.0\"?>");   //first line of the xml
            pw1.println("<taskFile>");
            pw1.println("<dataset>" + dataset + "</dataset>");
            pw1.println("<experimenttype>" + expType + "</experimenttype>");

            String taskName = "";

            for (int i = 0; i < quantitativeQuestions.size(); i++) {
                //get the TaskCode
                taskName = getTaskCode(quantitativeQuestions.get(i));

                pw1.println("<task>");
                pw1.println("<name>" + taskName + "</name>");
                pw1.println("<question>" + quantitativeQuestions.get(i) + "</question>");                
                pw1.println("<size>" + quantQuestionSizes.get(i) + "</size>");
                pw1.println("<time>" + quantQuestionTime.get(i) + "</time>");
                pw1.println("</task>");

                //pw1.println(taskName + "::" + quantitativeQuestions.get(i) + "::" + quantQuestionSizes.get(i) + "::" + quantQuestionTime.get(i));
            }

            pw1.println("</taskFile>");
            // pw1.println("Finished");            
            pw1.close();

            //Write ExperimentType to File
          /*  String expTypeFilename = "expType.txt";
             File expTypeFile = new File(getServletContext().getRealPath(DATA_DIR + File.separator + expTypeFilename));

             BufferedWriter bw2 = new BufferedWriter(new FileWriter(expTypeFile));
             PrintWriter pw2 = new PrintWriter(bw2);

             System.out.println("The Experiment Type is:: " + expType);
             pw2.println(expType);
             pw2.close();*/
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    public String getTaskCode(String task) {
        String taskCode = "";

        if (task.equalsIgnoreCase("Are the two highlighted nodes directly connected?")) {
            taskCode = "neighbor";
        } else if (task.equalsIgnoreCase("Are the three highlighted nodes directly connected?")) {
            taskCode = "path_three_nodes";
        }

        return taskCode;
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
