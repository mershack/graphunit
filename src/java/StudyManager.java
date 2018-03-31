
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.apache.el.lang.EvaluationContext;
import userstudy.EvaluationQuestion;
import userstudy.MyUtils;
//for xml file reading stuff
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import userstudy.Introduction;
import userstudy.ListOfConditionsAndTheirCounters;
import userstudy.QualitativeQuestion;
import userstudy.StudyParameters;
import userstudy.TaskDetails;
import userstudy.ViewerManager;

/**
 *
 * @author Mershack
 */
@WebServlet(urlPatterns = {"/StudyManager"})
public class StudyManager extends HttpServlet {

    private final String DATA_DIR = "data";

    private final String QUANT_QNS_FILENAME = "quantitativeQuestions.txt";
    private final String TASKS_NODES_FILENAME = "taskNodesIndexes.txt";//"tasksNodes.txt";

    HashMap<String, StudyParameters> usersStudyParameters = new HashMap<String, StudyParameters>();
    HashMap<String, ListOfConditionsAndTheirCounters> onGoingStudyCounts = new HashMap<String, ListOfConditionsAndTheirCounters>();//this will be used to hold the ongoing study counts

//    /ArrayList<String> taskNodes = new ArrayList<String>();
    //HashMap<Integer, ArrayList<String>> allTaskNodes = new HashMap<Integer, ArrayList<String>>(); //this hashmap will be used
    //HashMap<Integer, String> allQuestions = new HashMap<Integer, String>();
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

            HttpSession session = request.getSession();
            String message = "";
            //    Environment e = null;
            //get the environment from the session or create a new one if the session doesn't have the environment initialized
            RequestDispatcher view = request.getRequestDispatcher("userstudy.html");

            if (request.getParameter("studyname") != null) {
                String studyname = request.getParameter("studyname");
                String userid = request.getParameter("userid");

                // System.out.println("The user id is "+ userid);
                //  System.out.println("///////NULL SESSION");c
                session.setAttribute("studyname", studyname);
                session.setAttribute("userid", userid);

                // don't add your web-app name to the path
                view.forward(request, response);

                // }
            } else {

                String msg = "Finished";
                String command = request.getParameter("command");
                String nameofstudy;
                String studyId;
                String userid = session.getAttribute("userid").toString();
                System.out.println("__the command is " + command);
                if (command.equalsIgnoreCase("instruction")) {
                    //for the first time, get the name of the study from the session otherwise expect it to be passed

                    //append it to the end of it and see how far that goes.
                    nameofstudy = session.getAttribute("studyname").toString();
                } else {
                    nameofstudy = request.getParameter("studyid").toString();
                }

                //String nameofstudy = session.getAttribute("studyname").toString();
                studyId = session.getId() + userid + "_" + nameofstudy;

                //System.out.println("study id is :: "+studyId);
                //get the user's study parameters.
                StudyParameters upmts = (StudyParameters) usersStudyParameters.get(studyId); // get the user's specific parameters

                if (upmts == null) {//first time, initialize the variable
                    upmts = new StudyParameters();
                }

                upmts.studyname = nameofstudy;

                if (command.equalsIgnoreCase("instruction")) {
                    loadStudyDetails(request, upmts, userid);  //load the study details
                    upmts.viewersChanged = false;
                    upmts.testCounter = 0;
                    upmts.tutorialCounter = 0;
                    upmts.viewerConditionCounter = 0;
                    upmts.tutorialViewerConditionCounter = 0;
                    upmts.datasetConditionCounter = 0;
                    upmts.miscellaneousInfoSaved = false;
                    upmts.quantitativeAnswersSaved = false;
                    upmts.qualitativeQnsSent = false;
                    upmts.setQuestionGroupCounter(0);

                    msg = getInstruction(upmts); //get the instruction
                    //now append the study name to the study so that it can be returned later.
                    msg += "::" + nameofstudy;
                    msg += "::" + upmts.tutorialQuestions.size();
                    // System.out.println("INSTRUCTION IS " + msg);
                } else if (command.equalsIgnoreCase("getPreQualitativeQuestions")) {
                    //send the qualitative questions if there is some, otherwise send an empty string
                    String allqualQuestions = "";

                    System.out.println("--------"+upmts.preStudyEvalQuestions.size());
                    
                    for (int i = 0; i < upmts.preStudyEvalQuestions.size(); i++) {

                        if (i == 0) {
                            allqualQuestions = upmts.preStudyEvalQuestions.get(i).getQuestion() + ":::" + upmts.preStudyEvalQuestions.get(i).getAnswerTypeAndOutputType();
                        } else {
                            allqualQuestions += "::::" + upmts.preStudyEvalQuestions.get(i).getQuestion() + ":::" + upmts.preStudyEvalQuestions.get(i).getAnswerTypeAndOutputType();
                        }
                    }
                    
                    
                   
                    if (!allqualQuestions.isEmpty()) {
                        allqualQuestions = "Qualitative::::" + allqualQuestions;
                    }
                    
                    System.out.println("All questions is:: "+ allqualQuestions);
                    
                    msg = allqualQuestions;
                    
                } else if (command.equalsIgnoreCase("firstViewerUrl")) {
                    msg = upmts.viewerConditionUrls.get(upmts.viewerConditionCounter);
                    upmts.viewerConditionCounter++;
                } else if (command.equalsIgnoreCase("getDataset")) {
                    //if we have only one dataset, just be returning that dataset
                    //otherwise, we will be returning the right dataset.
                    String ds = "";                    
                    
                    if (upmts.getDatasetConditionUrls().size() == 1) {
                        ds = upmts.getDatasetConditionUrls().get(0);
                    } else if (upmts.getDatasetConditionUrls().size() > 1){

                        if (upmts.datasetConditionCounter == upmts.getDatasetConditionUrls().size()) {
                            //reset it
                            upmts.datasetConditionCounter = 0;
                        }

                        ds = upmts.getDatasetConditionUrls()
                                .get(upmts.datasetConditionCounter);
                        upmts.datasetConditionCounter++;
                    }

                    msg = ds;
                } else if (command.equalsIgnoreCase("prepareQuestions")) {
                    System.out.println("about to prepare questions");
                    prepareActualStudyQuestions(request, upmts, userid);
                    preparePreStudyQuestions(request, upmts, userid);
                    preparePostStudyQuestions(request, upmts, userid);

                    //System.out.println("the size of a condition is ++" + upmts.sizeOfACondition);
                } else if (command.equalsIgnoreCase("getViewerDimensions")) {
                    String width = upmts.viewerWidth;
                    String height = upmts.viewerHeight;
                    msg = width + "x" + height;  //i.e. w x h  e.g. 800x600   
                } else if (command.equalsIgnoreCase("getQuestion")) {

                    if (upmts.tutorialCounter < upmts.tutorialQuestions.size()) {
                        upmts.isTutorial = true;

                        if (upmts.tutorialCounter == 1) {
                            upmts.tutorialViewerConditionCounter = 1;
                        }
                      
                        if (upmts.expType_vis.equalsIgnoreCase("Within")
                                && upmts.tutorialCounter % (upmts.sizeOfATrainingCondition) == 0
                                && upmts.tutorialViewerchanged != true) {

                            
                            System.out.println("@@The condition Counter is "+ upmts.tutorialViewerConditionCounter);
                            
                            System.out.println("-- " +upmts.tutorialViewerUrls.size());
                            String url = upmts.tutorialViewerUrls.get(upmts.tutorialViewerConditionCounter);
                            System.out.println("&& url is " + url);
                            msg = "ChangeViewers:: " + url;
                            upmts.tutorialViewerConditionCounter++;

                            upmts.tutorialViewerchanged = true;
                        } else {
                            upmts.tutorialCounter++;

                            msg = "Training Question (" + upmts.tutorialCounter + "/" + upmts.tutorialQuestions.size() + ")"
                                    + ":: " + upmts.tutorialQuestions.get(upmts.tutorialCounter - 1).getQuestion();
                            msg += "::" + upmts.tutorialQuestions.get(upmts.tutorialCounter - 1).getMaxTimeInSeconds();  //add the time also

                            upmts.tutorialViewerchanged = false;

                        }

                    } else if (((upmts.expType_vis.equalsIgnoreCase("Within") && upmts.testCounter % upmts.sizeOfACondition == 0)
                            || ((upmts.getDatasetConditionNames().size() > 1) && upmts.testCounter % upmts.sizeOfADatasetCondition == 0))
                            && (upmts.testCounter > 0 && upmts.testCounter < upmts.evalQuestions.size())
                            && !upmts.viewersChanged) {

                        String url = upmts.viewerConditionUrls.get(upmts.viewerConditionCounter);

                        if (upmts.expType_vis.equalsIgnoreCase("Between")) {
                            url = upmts.firstConditionUrl;
                        }
                        msg = "ChangeViewers:: " + url;
                        //System.out.println("ChangeViewer string is ::: " + msg);
                        upmts.viewerConditionCounter++;
                        upmts.viewersChanged = true;
                    } else if (upmts.testCounter < upmts.evalQuestions.size()) {
                        //System.out.println("over--here");
                        if (upmts.isTutorial) {
                            //change viewers again
                            msg = "ChangeViewers:: " + upmts.firstConditionUrl;

                            upmts.isTutorial = false;
                            
                        } else {
                            
                            String prevAnswera = request.getParameter("previousAnswer");
                            String prevTimea = request.getParameter("previousTime");
                            
                            
                                
                            System.out.println("__________" + upmts.testCounter);
                            System.out.println("@@@ prevAns, prevtime: " + prevAnswera + " " + prevTimea);

                            if (upmts.testCounter > 0) {//get the previousAnswer

                                //now change viewers now.
                                if (upmts.testCounter == 1) { //let's label this as an ongoing study                                      
                                    ListOfConditionsAndTheirCounters ongoing_studyCounts
                                            = (ListOfConditionsAndTheirCounters) onGoingStudyCounts.get(upmts.studyname);
                                    if (ongoing_studyCounts != null) {
                                        ongoing_studyCounts.incrementCondCounter(upmts.firstConditionShortName);
                                        onGoingStudyCounts.put(upmts.studyname, ongoing_studyCounts);
                                    }
                                }
                                String prevAnswer = request.getParameter("previousAnswer");
                                String prevTime = request.getParameter("previousTime");
                                String acc = request.getParameter("accuracy");
                                double accuracy = 0.0;

                                if (!acc.trim().isEmpty()) {
                                    accuracy = Double.parseDouble(acc);
                                }

                                System.out.println("Previous Time is ::: " + prevTime);
                                int previousTime = Integer.parseInt(prevTime);

                                if (upmts.evalQuestions.get(upmts.testCounter - 1).hasCorrectAnswer()) {
                                    upmts.evalQuestions.get(upmts.testCounter - 1).setAverageCorrect(accuracy);
                                }

                                System.out.println("testCounter, previos answer " + upmts.testCounter + " " + prevAnswer);
                                upmts.evalQuestions.get(upmts.testCounter - 1).setGivenAnswer(prevAnswer.trim());
                                upmts.evalQuestions.get(upmts.testCounter - 1).setTimeInSeconds(previousTime);

                            }
                            upmts.isTutorial = false;
                            upmts.testCounter++;
                            upmts.viewersChanged = false;
                            msg = "Study Question (" + upmts.testCounter + "/" + upmts.evalQuestions.size() + ")";
                            msg += "::  " + upmts.evalQuestions.get(upmts.testCounter - 1).getQuestion(); //the question
                            msg += "::" + upmts.evalQuestions.get(upmts.testCounter - 1).getMaxTimeInSeconds();  //add the time also

                            System.out.println("Testcounter is " + upmts.testCounter);
                        }

                    } else {
                        // System.out.println("  way --- back");
                        String prevAnswer = request.getParameter("previousAnswer");
                        String prevTime = request.getParameter("previousTime");

                        String acc = request.getParameter("accuracy");
                        double accuracy = 0.0;

                        if (!acc.trim().isEmpty()) {
                            accuracy = Double.parseDouble(acc);
                        }

                        //  System.out.println("Previous Time is ::: " + prevTime);
                        int previousTime = Integer.parseInt(prevTime);

                        if (upmts.testCounter > 0 && upmts.testCounter - 1 < upmts.evalQuestions.size()) {//save it only now
                           

                            if (upmts.evalQuestions.get(upmts.testCounter - 1).hasCorrectAnswer()) {
                                upmts.evalQuestions.get(upmts.testCounter - 1).setAverageCorrect(accuracy);
                            }

                            upmts.evalQuestions.get(upmts.testCounter - 1).setGivenAnswer(prevAnswer.trim());
                            upmts.evalQuestions.get(upmts.testCounter - 1).setTimeInSeconds(previousTime);

                            upmts.testCounter++;
                        }

                        // writeAnswersToFile(upmts);
                        //find the qualitative questions and send them out, if there are none then send the finished msg
                        //check if the miscellaneous information have been saved                        
                        if (!upmts.miscellaneousInfoSaved) {
                            msg = "EndOfQuantitative:: ";

                            upmts.miscellaneousInfoSaved = true;

                        } else {
                            // check if there are no more qualitative questions
                            // and then write answers to file

                            //write answers to file:
                            if (!upmts.quantitativeAnswersSaved) {
                                writeQuantitativeAnswersToFile(upmts, userid);
                                upmts.quantitativeAnswersSaved = true;
                            }

                            if (upmts.postStudyEvalQuestions.size() == 0) {
                                //writeAnswersToFile(upmts);
                                String studyNameReverse = new StringBuffer(upmts.studyname.toUpperCase()).reverse().toString();

                                //System.out.println("Name Reversed is:: "+studyNameReverse);
                                msg = "Finished::" + upmts.turkCode + studyNameReverse;
                            } else if (!upmts.qualitativeQnsSent) {
                                //send the qualitative questions                          

                                //get the qualitative questions
                                String allqualQuestions = "";
                                for (int i = 0; i < upmts.postStudyEvalQuestions.size(); i++) {

                                    if (i == 0) {
                                        allqualQuestions = upmts.postStudyEvalQuestions.get(i).getQuestion()
                                                + ":::" + upmts.postStudyEvalQuestions.get(i).getAnsType();
                                    } else {
                                        allqualQuestions += "::::" + upmts.postStudyEvalQuestions.get(i).getQuestion() 
                                                + ":::" + upmts.postStudyEvalQuestions.get(i).getAnsType();
                                    }

                                }
                                upmts.qualitativeQnsSent = true;
                                msg = "Feedback::::" + allqualQuestions;
                            } else {
                                String studyNameReverse = new StringBuffer(upmts.studyname.toUpperCase()).reverse().toString();
                                msg = "Finished::" + upmts.turkCode + studyNameReverse;
                            }

                        }

                    }

                } else if (command.equalsIgnoreCase("saveMiscellaneousInfo")) {
                    System.out.println("We are about to store the Miscellaneous information");
                    //first get the miscellaneous info and then do the following
                    String windowWidth = request.getParameter("windowWidth");
                    String windowHeight = request.getParameter("windowHeight");
                    String dateAndTime = request.getParameter("dateAndTime");
                    String offFocusTime = request.getParameter("offFocusTime");
                    String colorBlindTestAnswers = request.getParameter("colorBlindnessTestAnswers");
                    //set these values in the upmts so that we can access them later.
                    upmts.userViewerWidth = windowWidth;
                    upmts.userViewerHeight = windowHeight;
                    upmts.offFocusTime = offFocusTime;
                    upmts.dateAndTime = dateAndTime;
                    upmts.colorBlindnessTestAnswers = colorBlindTestAnswers;

                } else if (command.equalsIgnoreCase("setPreQualitativeAnswers")) {
                    System.out.println("---- Setting pre-qualitative answers");
                    
                    
                    String qualAnswers = request.getParameter("preQualitativeAnswers");
                    String split[] = qualAnswers.split("::::");
                    System.out.println("__PRE-QUALITATIVE ANSWERS ::: " + qualAnswers);
                    
                    for (int i = 0; i < upmts.preStudyEvalQuestions.size(); i++) {
                        upmts.preStudyEvalQuestions.get(i).setGivenAnswer(split[i]);                        
                        //System.out.println(upmts.qualEvalQuestionBefore.get(i).getAnswer());
                    }
                } else if (command.equalsIgnoreCase("setQualitativeAnswers")) {
                    //set the qualitative answer and send the turk code
                    String qualAnswers = request.getParameter("qualitativeAnswers");

                    System.out.println("_____ Post-Qualitative Answers are ::: " + qualAnswers);

                    String split[] = qualAnswers.split("::::");

                    for (int i = 0; i < upmts.postStudyEvalQuestions.size(); i++) {
                        upmts.postStudyEvalQuestions.get(i).setGivenAnswer(split[i]);                        
                    }
                    String studyNameReverse = new StringBuffer(upmts.studyname.toUpperCase()).reverse().toString();
                    writeQualitativeAnswersToFile(upmts, userid);
                    msg = "Finished::" + upmts.turkCode + studyNameReverse;

                } else if (command.equalsIgnoreCase("getNodes")) {
                    //get the nodes for that question as string.      
                    msg = "";
                    
                    if (upmts.isTutorial && upmts.tutorialCounter > 0) {
                        //System.out.println("**** "+ (tutorialCounter - 1));
                        //msg = upmts.tutorialQuestions.get(upmts.tutorialCounter - 1).getNodesAsString();
                        msg = upmts.tutorialQuestions.get(upmts.tutorialCounter - 1).getInputsAsString();
                    } else {
                        //msg = upmts.evalQuestions.get(upmts.testCounter - 1).getNodesAsString();
                        
                        if (upmts.testCounter > 0) {
                            msg = upmts.evalQuestions.get(upmts.testCounter - 1).getInputsAsString();

                        }
                    }
                } else if (command.equalsIgnoreCase("getAnswerControllers")) {
                    msg = "";
                    
                    if (upmts.isTutorial && upmts.tutorialCounter > 0) {
                        msg = upmts.tutorialQuestions.get(upmts.tutorialCounter - 1).getAnswerTypeAndOutputType();
                        System.out.println("**** ");

                    } else {
                        if (upmts.testCounter > 0) {
                            msg = upmts.evalQuestions.get(upmts.testCounter - 1).getAnswerTypeAndOutputType();
                        }                        
                    }
                } else if (command.equalsIgnoreCase("getHasCorrectAnswer")) {
                    if (upmts.isTutorial && 
                            (upmts.tutorialCounter > 0) &&                            
                            (upmts.tutorialQuestions.get(upmts.tutorialCounter - 1) != null)) {

                        msg = upmts.tutorialQuestions.get(upmts.tutorialCounter - 1).getHasCorrectAnswer();
                    } else {
                         msg = "";
                        if (upmts.testCounter > 0) {
                            msg = upmts.evalQuestions.get(upmts.testCounter - 1).getHasCorrectAnswer();
                        }
                    }
                } else if (command.equalsIgnoreCase("getInterfaceForValidatingAnswers")) {
                    //we will be getting the interface for validating answers. 
                    //This is normally the case for interface type of questions
                    if (upmts.isTutorial && upmts.tutorialCounter > 0) {
                        msg = upmts.tutorialQuestions.get(upmts.tutorialCounter - 1).getInterfaceForValidatingAnswers();
                    } else {
                        msg = "";

                        if (upmts.testCounter > 0) {
                            msg = upmts.evalQuestions.get(upmts.testCounter - 1).getInterfaceForValidatingAnswers();

                        }

                    }
                } else if (command.equalsIgnoreCase("getCorrectAnswerForInterfaceAnswerTypes")) {
                    //we will be getting the correct answer for this task
                    msg = "";
                    
                    if (upmts.isTutorial && upmts.tutorialCounter > 0) {
                        msg = upmts.tutorialQuestions.get(upmts.tutorialCounter - 1).getCorrectAns();
                    } else {
                        
                        if (upmts.testCounter > 0) {
                            msg = upmts.evalQuestions.get(upmts.testCounter - 1).getCorrectAns();

                        }
                        
                    }
                } else if (command.equalsIgnoreCase("getInputTypes")) {
                    //we will be getting the input types and sending it from here
                    msg = "";
                    
                    if (upmts.isTutorial && upmts.tutorialCounter > 0) {
                        msg = upmts.tutorialQuestions.get(upmts.tutorialCounter - 1).getInputTypes();
                    } else {
                        
                        if (upmts.testCounter > 0) {
                            msg = upmts.evalQuestions.get(upmts.testCounter - 1).getInputTypes();

                        }
                    }

                    System.out.println("The inputtypes are :: " + msg);
                } else if (command.equalsIgnoreCase("checkIsTutorial")) {
                    if (upmts.tutorialCounter < upmts.tutorialQuestions.size()) {
                        msg = "true";
                    } else {
                        msg = "false";
                    }
                } else if(command.equalsIgnoreCase("getIntroduction")) {  
                    // TODO: 
                    
                    /*System.out.println("******____***" + upmts.currentCondition);
                    
                    msg = upmts.getIntroduction(upmts.currentCondition);     */               
                }  else if (command.equalsIgnoreCase("checkAnswer")) {

                    //check if the given answer is right, return "Correct" if right or "Wrong" if wrong
                    String givenAns = request.getParameter("givenAnswer").trim();

                  /*  String acc = request.getParameter("accuracy");
                    double accuracy = 0.0;

                    if (!acc.trim().isEmpty()) {
                        accuracy = Double.parseDouble(acc);
                    }  */

                 /*   if (upmts.tutorialQuestions.get(upmts.tutorialCounter - 1).hasCorrectAnswer()) {
                        upmts.tutorialQuestions.get(upmts.tutorialCounter - 1).setAverageCorrect(accuracy);
                    } */

                    //upmts.tutorialQuestions.get(upmts.tutorialCounter - 1).setAverageCorrect(givenAns);
                    upmts.tutorialQuestions.get(upmts.tutorialCounter - 1).setGivenAnswer(givenAns);

                    String correctAns = upmts.tutorialQuestions.get(upmts.tutorialCounter - 1).getCorrectAns(); //NB: check answer is only for tutorials
                    double averageCorrect = upmts.tutorialQuestions.get(upmts.tutorialCounter - 1).getIsGivenAnsCorrect();

                   // int numOfErrors = upmts.tutorialQuestions.get(upmts.tutorialCounter - 1).getNumberOfErrors();
                   // int numberMissed = upmts.tutorialQuestions.get(upmts.tutorialCounter - 1).getNumberMissed();

                    //  System.out.println("here");
                  /*  if (numOfErrors > 0 && numberMissed > 0) {
                        msg = "You missed " + numberMissed + " element(s)  and you made " + numOfErrors + " erroneous selection(s).";
                    } else if (numOfErrors > 0) {
                        msg = "You made " + numOfErrors + " erroneous selection(s). ";
                    } else if (numberMissed > 0) {

                        msg = "You missed " + numberMissed + " element(s).";
                    } else*/
                    if (averageCorrect == 1.0) {
                        msg = "Correct!";
                    } else {
                        //it is plain wrong
                        msg = "Wrong";
                    }

                    //  System.out.println("GivenAnswer is: " + givenAns + "  and correctAns is: " + correctAns);
                    // System.out.println("The correctness message is ::: "+ msg);
                } else if (command.equalsIgnoreCase("getNodePositions")) {
                    //read the node positions file as a string and send it to the vie
                    File posFile = new File(getServletContext().getRealPath(upmts.nodePositions));

                    System.out.println("The positions file can be found here :: " + getServletContext().getRealPath(upmts.nodePositions));

                    BufferedReader br = new BufferedReader(new FileReader(posFile));
                    String line = "";
                    //ArrayList<String> taskAccuracy = new ArrayList<String>();
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

                    msg = alldata;
                }

                //put the user study paramters object into the hashtable
                usersStudyParameters.put(studyId, upmts);

                out = response.getWriter();
                out.write(msg);
                out.flush();
                out.close();
            }

        } finally {
            out.close();
        }
    }

    public void printEvaluationAnswers(StudyParameters upmts) {
        System.out.println("Evaluation Answers");
        for (int i = 0; i < upmts.evalQuestions.size(); i++) {
            System.out.println(":::" + upmts.evalQuestions.get(i).getIsGivenAnsCorrect());
        }
    }

    public String getInstruction(StudyParameters upmts) {
        String instruction = "";

        int size_sizeOf_training = upmts.getQuestionsWithTraining();

        if (upmts.questionCodes.size() > 1) {
            instruction = "Instruction about the tasks::In this study there are " + upmts.questionCodes.size() + " types of questions.\n\n";

            if (upmts.getQuestionsWithTraining() < upmts.questionCodes.size()) {

                if (upmts.getQuestionsWithTraining() >= 1) {

                    if (upmts.getQuestionsWithTraining() == 1) {
                        instruction += upmts.getQuestionsWithTraining() + " of the questions requires training and you will be given a simple training with "
                                + upmts.trainingSize + " sample questions.";
                    } else {
                        instruction += upmts.getQuestionsWithTraining() + " of the questions requires training and you will be given a simple training with "
                                + upmts.trainingSize + " sample questions for each.";
                    }

                }

            } else {
                instruction += "You will be given a simple training  with " + upmts.trainingSize + " sample questions of each type. ";
            }

            if (upmts.getQuestionsWithTraining() >= 1) {
                instruction += "You can check whether your chosen answer is correct or not during the training session.\n\n"
                        + "For the main study, there are " + upmts.totalNumOfQuestions + " questions in total.";
            } else {
                instruction += "There are " + upmts.totalNumOfQuestions + " questions in total for the whole study";
            }

        } else {
            if (upmts.getQuestionsWithTraining() == 1) {
                instruction = "Instruction about the tasks::In this study there is " + upmts.questionCodes.size() + " type of question.\n\n"
                        + "You will be given a simple training  with " + upmts.trainingSize + " sample questions. "
                        + "You can check whether your chosen answer is correct or not during the training session.\n\n"
                        + "There are " + upmts.totalNumOfQuestions + " questions in total for the  main study";
            } else {
                instruction = "Instruction about the tasks::In this study there is " + upmts.questionCodes.size() + " type of question.\n\n"
                        + "There are " + upmts.totalNumOfQuestions + " questions in total for the  whole study";
            }

        }
        instruction += " Thank you for participating in this study.";
        return instruction;
    }

    public void loadStudyDetails(HttpServletRequest request, StudyParameters upmts, String userid) {
        //load study type file
        //load quantitative questions file        
        upmts.questionCodes = new ArrayList<String>();
        upmts.questions = new ArrayList<String>();
        upmts.questionSizes = new ArrayList<Integer>();
        upmts.questionMaxTimes = new ArrayList<Integer>();

        //for qual qns after
        upmts.qualQuestionCodesAfter = new ArrayList<String>();
        upmts.qualQuestionsAfter = new ArrayList<String>();
        upmts.qualEvalQuestionAfter = new ArrayList<QualitativeQuestion>();
        
        upmts.postStudyEvalQuestions = new ArrayList<EvaluationQuestion>();
        upmts.postStudyQuestionCodes = new ArrayList<String>();
        upmts.postStudyQuestions =  new ArrayList<String>();
        upmts.postStudyTaskDetails = new ArrayList<TaskDetails>();
        

        //for qual qns before
        upmts.qualQuestionCodesBefore = new ArrayList<String>();
        upmts.qualQuestionsBefore = new ArrayList<String>();
        upmts.qualEvalQuestionBefore = new ArrayList<QualitativeQuestion>();

        upmts.preStudyEvalQuestions = new ArrayList<EvaluationQuestion>();
        upmts.preStudyQuestionCodes = new ArrayList<String>();
        upmts.preStudyQuestions =  new ArrayList<String>();
        upmts.preStudyTaskDetails = new ArrayList<TaskDetails>();
        
        
        upmts.evalQuestions = new ArrayList<EvaluationQuestion>();     
        
        upmts.tutorialQuestions = new ArrayList<EvaluationQuestion>();
        upmts.viewerConditionShortnames = new ArrayList<String>();
        upmts.viewerConditionUrls = new ArrayList<String>();

        upmts.orderOfConditionShortNames = new ArrayList<String>();
        upmts.orderOfConditionUrls = new ArrayList<String>();

        upmts.taskDetails = new ArrayList<TaskDetails>();

        String datasetname = "";
        try {
            //read the xml file that contains the details about the quantitative questions  

            //get the studydata url it will be in the user directory
            String studydataurl = "users" + File.separator + userid + File.separator
                    + "studies" + File.separator + upmts.studyname + File.separator + "data";
            //+ "quanttasks" + File.separator + "quanttasklist.txt");
            //  "studies" + File.separator + upmts.studyname + File.separator + "data";

            String filename = getServletContext().getRealPath(studydataurl + File.separator + "quantitativeTasks.xml");
            File fXmlFile = new File(filename);
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(fXmlFile);

            //optional, but recommended
            //read this - http://stackoverflow.com/questions/13786607/normalization-in-dom-parsing-with-java-how-does-it-work
            doc.getDocumentElement().normalize();

            //System.out.println("Root element :" + doc.getDocumentElement().getNodeName());
            NodeList taskNode = doc.getElementsByTagName("task");

            NodeList preStudyTaskNode = doc.getElementsByTagName("preStudyTask");
            NodeList postStudyTaskNode = doc.getElementsByTagName("postStudyTask");

            NodeList datasetNode = doc.getElementsByTagName("dataset");
            NodeList datasetTypeNode = doc.getElementsByTagName("datasetType");
            NodeList experimentTypeNode_vis = doc.getElementsByTagName("experimenttype_vis");
            NodeList conditionNode = doc.getElementsByTagName("condition");
            NodeList studynameNode = doc.getElementsByTagName("studyname");
            NodeList qualtaskNode = doc.getElementsByTagName("qualtask");
            NodeList viewerwidthNode = doc.getElementsByTagName("viewerwidth");
            NodeList viewerheightNode = doc.getElementsByTagName("viewerheight");
            NodeList trainingSizeNode = doc.getElementsByTagName("trainingsize");

            NodeList datasetConditionsNode = doc.getElementsByTagName("datasetCondition");
            NodeList introductionsNode = doc.getElementsByTagName("introFile");

            ArrayList<String> dcn = new ArrayList<String>();
            ArrayList<String> dt = new ArrayList<String>();
            ArrayList<String> durl = new ArrayList<String>();

            for (int i = 0; i < datasetConditionsNode.getLength(); i++) {

                Node nNode = datasetConditionsNode.item(i);

                if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element eElement = (Element) nNode;

                    String datasetName = eElement.getElementsByTagName("dataset").item(0).getTextContent();
                    String datasetFormat = eElement.getElementsByTagName("datasetFormat").item(0).getTextContent();
                    String datasetType = eElement.getElementsByTagName("datasetType").item(0).getTextContent();

                    if (!datasetName.trim().isEmpty() && !datasetType.trim().isEmpty()) {
                       dcn.add(datasetName);
                        dt.add(datasetType);

                        //the url will depend on the type of dataset
                        if (datasetType.trim().equalsIgnoreCase("System_Datasets")) {
                            durl.add(getServerUrl(request) + ("/datasets/" + datasetName + "/" + datasetName + datasetFormat));
                        } else {
                            durl.add(getServerUrl(request) + ("/users/" + userid + "/datasets/" + datasetName + "/" + datasetName + datasetFormat));
                        } 
                    }
                    
                }
            }
            upmts.setDatasetConditionNames(dcn);
            upmts.setDatasetTypes(dt);
            upmts.setDatasetConditionUrls(durl);
            
            //get the dataseturl
            String datasetUrl = "";
            if (datasetNode.item(0) != null) {
                datasetname = ((Element) datasetNode.item(0)).getTextContent();
                datasetUrl = getServerUrl(request) + ("/datasets/" + datasetname + "/" + datasetname);
            }
            upmts.dataseturl = datasetUrl;
            // nodePositions = getServerUrl(request) + ("/datasets/" + datasetname + "/positions.txt");

            upmts.datasetname = datasetname;
            upmts.nodePositions = "datasets" + File.separator + datasetname + File.separator + "positions.txt";

            //get the studyname
            upmts.studyname = ((Element) studynameNode.item(0)).getTextContent();

            //get the experiment type
            upmts.expType_vis = ((Element) experimentTypeNode_vis.item(0)).getTextContent();
            //get the training size
            if (trainingSizeNode != null && trainingSizeNode.item(0) != null) {
                upmts.trainingSize = Integer.parseInt(((Element) trainingSizeNode.item(0)).getTextContent());
            }

            //NB: We do not want training size to be less than 2.
            //note for now I'm disabling the following line to allow traininigs of size 1.
//            if (upmts.trainingSize < 2) {
//                upmts.trainingSize = 2;
//            }
            
            

            //get the condition urls and shortnames
            for (int i = 0; i < conditionNode.getLength(); i++) {
                Node nNode = conditionNode.item(i);

                if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element eElement = (Element) nNode;

                    String conditionurl = eElement.getElementsByTagName("conditionurl").item(0).getTextContent();
                    String conditionshortname = eElement.getElementsByTagName("conditionshortname").item(0).getTextContent();

                    upmts.viewerConditionShortnames.add(conditionshortname);

                    String url = "users/" + userid + "/viewers/" + conditionurl;

                    upmts.viewerConditionUrls.add(url);
                    System.out.println("The url is" + url);

                }
            }

            //get the task name, question, size, and time
            upmts.totalNumOfQuestions = 0;
            for (int temp = 0; temp < taskNode.getLength(); temp++) {
                Node nNode = taskNode.item(temp);

                if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element eElement = (Element) nNode;

                    String questionCode = eElement.getElementsByTagName("name").item(0).getTextContent();

                    //read the task details from file and do something with it.
                    String question = eElement.getElementsByTagName("question").item(0).getTextContent();
                    String questionSize = eElement.getElementsByTagName("size").item(0).getTextContent();
                    String questionTime = eElement.getElementsByTagName("time").item(0).getTextContent();
                    //taskTypes.add(questionCode);
                    upmts.questionCodes.add(questionCode);
                    upmts.questions.add(question);
                    upmts.questionSizes.add(Integer.parseInt(questionSize));
                    upmts.totalNumOfQuestions += Integer.parseInt(questionSize);
                    upmts.questionMaxTimes.add(Integer.parseInt(questionTime));
                    
                    //read the task details
                    TaskDetails td = readTaskDetails(request, userid, question,
                            questionCode);
                    
                    System.out.println("___qustion code:  " +questionCode);

                    if (td.hasCorrectAnswer()) {
                        //TODO: We will be providing training for those tasks that 
                        // that will specify that they need training.
                        upmts.incrementQuestionsWithTraining();
                    }

                    upmts.taskDetails.add(td);

                }
            }

            if (upmts.expType_vis.equalsIgnoreCase("within")) {
                //     System.out.println("****Within "+ upmts.viewerConditionShortnames.size());
                upmts.totalNumOfQuestions *= upmts.viewerConditionShortnames.size();
            }

            //get the information about the pre-study tasks as well.
            for (int temp = 0; temp < preStudyTaskNode.getLength(); temp++) {
                Node nNode = preStudyTaskNode.item(temp);
                if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element eElement = (Element) nNode;
                    String questionCode = eElement.getElementsByTagName("name").item(0).getTextContent();

                    //read the task details from file and do something with it.
                    String question = eElement.getElementsByTagName("question").item(0).getTextContent();

                    //taskTypes.add(questionCode);
                    upmts.preStudyQuestionCodes.add(questionCode);
                    upmts.preStudyQuestions.add(question);

                    //read the task details
                    TaskDetails td = readTaskDetails(request, userid, question,
                            questionCode);

                    upmts.preStudyTaskDetails.add(td);
                }
            }

            //get the information about the post-study tasks as well.
            for (int temp = 0; temp < postStudyTaskNode.getLength(); temp++) {
                Node nNode = postStudyTaskNode.item(temp);
                if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element eElement = (Element) nNode;
                    String questionCode = eElement.getElementsByTagName("name").item(0).getTextContent();

                    //read the task details from file and do something with it.
                    String question = eElement.getElementsByTagName("question").item(0).getTextContent();

                    //taskTypes.add(questionCode);
                    upmts.postStudyQuestionCodes.add(questionCode);
                    upmts.postStudyQuestions.add(question);

                    //read the task details
                    TaskDetails td = readTaskDetails(request, userid, question,
                            questionCode);
                    upmts.postStudyTaskDetails.add(td);
                }
            }
            
            //get the information about the intro files as well.
            for (int temp = 0; temp < introductionsNode.getLength(); temp++) {
                Node nNode = introductionsNode.item(temp);
                if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element eElement = (Element) nNode;
                    String url = eElement.getElementsByTagName("introURL").item(0).getTextContent();
                    String condition = eElement.getElementsByTagName("introCond").item(0).getTextContent();
                    
                    Introduction introduction = new Introduction(url, condition);
                    
                    upmts.addIntroduction(introduction);  
                    
                    System.out.println("******** url " + url + " *** *cond: " + condition);

                }
            }

            //TODO: get the introduction files too
            //TODO: get the standard test files as well
            //viewer dimensions
            String viewerWidth = ((Element) viewerwidthNode.item(0)).getTextContent();
            String viewerHeight = ((Element) viewerheightNode.item(0)).getTextContent();
            upmts.viewerWidth = viewerWidth;
            upmts.viewerHeight = viewerHeight;

            /**
             * ********************************************************************************
             */
            /**
             * * For qualitative Questions ***
             */
            //get the task name, question,  and other details
            for (int temp = 0; temp < qualtaskNode.getLength(); temp++) {
                Node nNode = qualtaskNode.item(temp);
                if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element eElement = (Element) nNode;

                    String questionCode = eElement.getElementsByTagName("name").item(0).getTextContent();
                    String question = eElement.getElementsByTagName("question").item(0).getTextContent();
                    String position = eElement.getElementsByTagName("qualtaskPos").item(0).getTextContent();

                    if (position.equalsIgnoreCase("after")) {
                        upmts.qualQuestionCodesAfter.add(questionCode);
                        upmts.qualQuestionsAfter.add(question);
                    } else {
                        upmts.qualQuestionCodesBefore.add(questionCode);
                        upmts.qualQuestionsBefore.add(question);
                    }
                }
            }

            //read the qualitative question files
            //after quant task questions
            for (int i = 0; i < upmts.qualQuestionCodesAfter.size(); i++) {
                String xmlname = upmts.qualQuestionCodesAfter.get(i) + ".xml";
                filename = getServletContext().getRealPath("qualtasks" + File.separator + xmlname);

                File xmlFile = new File(filename);
                DocumentBuilderFactory dbFactory2 = DocumentBuilderFactory.newInstance();
                DocumentBuilder dBuilder2 = dbFactory2.newDocumentBuilder();
                Document doc2 = dBuilder2.parse(xmlFile);
                doc.getDocumentElement().normalize();

                taskNode = doc2.getElementsByTagName("answertype");
                String answerType = ((Element) taskNode.item(0)).getTextContent(); //answerType

                //  System.out.println("---Answer type is "+ answerType)
                int min = -1, max = -1;
                ArrayList<String> mchoices = new ArrayList<String>();
                //if answer type is rating, then get the minimum and maximum values
                if (answerType.equalsIgnoreCase("range")) {
                    NodeList minNode = doc2.getElementsByTagName("minimum");
                    NodeList maxNode = doc2.getElementsByTagName("maximum");
                    min = Integer.parseInt(((Element) minNode.item(0)).getTextContent());
                    max = Integer.parseInt(((Element) maxNode.item(0)).getTextContent());
                } else if (answerType.equalsIgnoreCase("multiplechoice")) {
                    //get the different choices
                    NodeList choiceNodeList = doc2.getElementsByTagName("choice");

                    for (int temp = 0; temp < choiceNodeList.getLength(); temp++) {
                        String choice = ((Element) choiceNodeList.item(temp)).getTextContent();
                        mchoices.add(choice);
                    }
                }

                //get the values of the variables
                String question = upmts.qualQuestionsAfter.get(i);
                QualitativeQuestion qualEvalQn = new QualitativeQuestion(question, answerType);
                if (min > -1 && max > 0) {
                    qualEvalQn.setRangeMinimum(min);
                    qualEvalQn.setRangeMaximum(max);
                }
                qualEvalQn.setMChoices(mchoices);
                //add the qualitativequestion to file
                upmts.qualEvalQuestionAfter.add(qualEvalQn);

            }

            //before quant tasks
            for (int i = 0; i < upmts.qualQuestionCodesBefore.size(); i++) {
                String xmlname = upmts.qualQuestionCodesBefore.get(i) + ".xml";
                filename = getServletContext().getRealPath("qualtasks" + File.separator + xmlname);

                File xmlFile = new File(filename);
                DocumentBuilderFactory dbFactory2 = DocumentBuilderFactory.newInstance();
                DocumentBuilder dBuilder2 = dbFactory2.newDocumentBuilder();
                Document doc2 = dBuilder2.parse(xmlFile);
                doc.getDocumentElement().normalize();

                taskNode = doc2.getElementsByTagName("answertype");
                String answerType = ((Element) taskNode.item(0)).getTextContent(); //answerType
                int min = -1, max = -1;
                ArrayList<String> mchoices = new ArrayList<String>();
                //if answer type is rating, then get the minimum and maximum values
                if (answerType.equalsIgnoreCase("range")) {

                    NodeList minNode = doc2.getElementsByTagName("minimum");
                    NodeList maxNode = doc2.getElementsByTagName("maximum");
                    min = Integer.parseInt(((Element) minNode.item(0)).getTextContent());
                    max = Integer.parseInt(((Element) maxNode.item(0)).getTextContent());
                } else if (answerType.equalsIgnoreCase("multiplechoice")) {
                    //get the different choices
                    NodeList choiceNodeList = doc2.getElementsByTagName("choice");

                    for (int temp = 0; temp < choiceNodeList.getLength(); temp++) {
                        String choice = ((Element) choiceNodeList.item(temp)).getTextContent();
                        mchoices.add(choice);
                    }
                }
                //get the values of the variables
                String question = upmts.qualQuestionsBefore.get(i);
                QualitativeQuestion qualEvalQn = new QualitativeQuestion(question, answerType);
                if (min > -1 && max > 0) {
                    qualEvalQn.setRangeMinimum(min);
                    qualEvalQn.setRangeMaximum(max);
                }
                qualEvalQn.setMChoices(mchoices);
                //add the qualitativequestion to file
                upmts.qualEvalQuestionBefore.add(qualEvalQn);
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        setFirstCondition(upmts, userid);
        upmts.orderOfConditionShortNames.add(upmts.firstConditionShortName);
        upmts.orderOfConditionUrls.add(upmts.firstConditionUrl);

        setOrderOfConditions(upmts);
        //there are more than 1 datasets, adjustConditions
        if (upmts.getDatasetConditionNames().size() > 1) {
            adjustConditionsForMoreThanOneDatasets(upmts);
        }
                
        //create the util object.
        upmts.utils = new MyUtils(upmts.viewerConditionShortnames);
    }
    
    
//    public void adjustTutorialConditions(StudyParameters upmts) {
//         //for the tutorial session
//        upmts.tutorialViewerShortnames = new ArrayList<String>();
//        upmts.tutorialViewerUrls = new ArrayList<String>();
//        for (int i = 0; i < upmts.viewerConditionShortnames.size(); i++) {
//            upmts.tutorialViewerShortnames.add(upmts.viewerConditionShortnames.get(i));
//            upmts.tutorialViewerUrls.add(upmts.viewerConditionUrls.get(i));
//            
//            System.out.println("*(___) " + upmts.tutorialViewerUrls.get(i));
//        }
//       
//    }

    /**
     * This method will be used to adjust the conditions when there are more
     * than one dataset practically, each condition will be paired with all the
     * available datasets
     *
     * @param upmts
     */
    public void adjustConditionsForMoreThanOneDatasets(StudyParameters upmts) {

        int datasetSize = upmts.getDatasetConditionNames().size();
        int originalDatasetSize = datasetSize;
        ArrayList<String> newVCShortnames = new ArrayList<String>();

        ArrayList<String> newVCUrls = new ArrayList<String>();

        ArrayList<String> newDatasetNames = new ArrayList<String>();
        ArrayList<String> newDatasetUrls = new ArrayList<String>();

        upmts.viewerSizeBeforeAdjustment = upmts.viewerConditionShortnames.size();

        //for the tutorial session
        upmts.tutorialViewerShortnames = new ArrayList<String>();
        upmts.tutorialViewerUrls = new ArrayList<String>();
        for (int i = 0; i < upmts.viewerSizeBeforeAdjustment; i++) {
            upmts.tutorialViewerShortnames.add(upmts.viewerConditionShortnames.get(i));
            upmts.tutorialViewerUrls.add(upmts.viewerConditionUrls.get(i));
        }

        for (int i = 0; i < upmts.viewerConditionShortnames.size(); i++) {

            for (int j = 0; j < datasetSize; j++) {
                //append the dataset to the condition-name
                String ds = upmts.getDatasetConditionNames().get(j);
                String newShortname = upmts.viewerConditionShortnames.get(i) + "__" + ds;

                //viewer condition adjust
                newVCShortnames.add(newShortname);
                newVCUrls.add(upmts.viewerConditionUrls.get(i));

                //dataset condition adjust
                newDatasetNames.add(upmts.getDatasetConditionNames().get(j));
                newDatasetUrls.add(upmts.getDatasetConditionUrls().get(j));
            }
        }

        //now do the updates
        upmts.viewerConditionShortnames = newVCShortnames;
        upmts.viewerConditionUrls = newVCUrls;
        upmts.setDatasetConditionNames(newDatasetNames);
        upmts.setDatasetConditionUrls(newDatasetUrls);

        //rechange the names of the order of conditions
        upmts.orderOfConditionShortNames = new ArrayList<String>();
        upmts.orderOfConditionUrls = new ArrayList<String>();

        for (int i = 0; i < upmts.viewerConditionShortnames.size(); i++) {
            upmts.orderOfConditionShortNames.add(upmts.viewerConditionShortnames.get(i));
            upmts.orderOfConditionUrls.add(upmts.viewerConditionUrls.get(i));
        }

        if (upmts.expType_vis.equalsIgnoreCase("Between")) {
            upmts.currentConditions = new ArrayList<String>();
            //if more than one dataset, set the conditions used

            for (int i = 0; i < originalDatasetSize; i++) {
                String ds = upmts.getDatasetConditionNames().get(i);
                upmts.currentConditions.add(upmts.firstConditionShortName + "__" + ds);
            }

            upmts.datasetSetSizeBeforeAdjustment = originalDatasetSize;

        }

        // System.out.println(upmts.currentConditions.size() + "99999___"+upmts.getDatasetConditionNames().size());
        //
        //System.out.println("??The size of viewer condition shortnames is " + upmts.viewerConditionShortnames.size());
    }

    public void adjustTasksForMoreThanOneDatasets(StudyParameters upmts) {
        //we will double/triple/etc the testtasks, i.e. add the same tasks to the end of the list for each of the conditions.
        int length = upmts.evalQuestions.size();
        upmts.sizeOfACondition = length;

        System.out.println("Size of a condition is " + upmts.sizeOfACondition);

        for (int i = 0; i < length; i++) {
            EvaluationQuestion evq = upmts.evalQuestions.get(i);

            //System.out.println(" ----kkiiinngg  "+evq.getQuestion() );
            EvaluationQuestion evalQ = new EvaluationQuestion(evq.getQuestion(),
                    evq.getCorrectAns(), evq.getInputs(),
                    evq.getAnsType(), evq.getMaxTimeInSeconds(),
                    evq.getInputTypes(), evq.getOutputInterface(), evq.getInputTypeStr(),
                    evq.getInterfaceForValidatingAnswers(), evq.getHasCorrectAnswer());
            upmts.evalQuestions.add(evalQ);
        }
    }

    //this method will be used to read the details of a task.
    public TaskDetails readTaskDetails(HttpServletRequest request, String userid,
            String taskQn, String taskShortName) {

        TaskDetails td = null;

        try {
            File sysFile_QuanttaskList = new File(getServletContext().getRealPath("quanttasks" + File.separator + "quanttasklist.txt"));

            BufferedReader br = new BufferedReader(new FileReader(sysFile_QuanttaskList));
            String line = "";
            String sys_taskShortname = "";
            while ((line = br.readLine()) != null) {
                if ((line.split(":::")[0].trim()).equalsIgnoreCase(taskShortName.trim())) {
                    sys_taskShortname = taskShortName.trim();
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
                if ((line.split(":::")[0].trim()).equalsIgnoreCase(taskShortName.trim())) {
                    usr_taskShortname = taskShortName.trim();
                    break;
                }
            }
            
            br2.close();

            String taskFileName = "";
            if (!usr_taskShortname.isEmpty()) {
                //we will read the file from the user's directory
                taskFileName = getServletContext().getRealPath("users"
                        + File.separator + userid + File.separator + "quanttasks" + File.separator + usr_taskShortname + ".xml");
                
                System.out.println("the task is now &&  "+taskFileName);
            } else {
                //we will read the file from the main quanttask directory.
                System.out.println("user task shortname is empty");
                taskFileName = getServletContext().getRealPath("quanttasks" + File.separator + sys_taskShortname + ".xml");
            }

           
            //read the quanttasks file and return the answer type for the current task.
            //read the quant-task-files.
            //String filename = getServletContext().getRealPath("quanttasks" + File.separator + "quanttasks.xml");
            File fXmlFile = new File(taskFileName);
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(fXmlFile);

            //System.out.println("Root element :" + doc.getDocumentElement().getNodeName());
            NodeList taskNode = doc.getElementsByTagName("task_details");

            ArrayList<String> inputTypes = new ArrayList<String>();
            ArrayList<String> inputDescriptions = new ArrayList<String>();
            ArrayList<String> inputMediums = new ArrayList<String>();
            String answertype = "";
            String outputType = "";
            String outputTypeDescription = "";
            String taskname = "";
            String inputSize = "";
            String qn = "";
            String accCheckingInterface = "";
            String taskDescription = "";
            String hasCorrectAnswer = "";

            Node nNode = taskNode.item(0);
            if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                Element eElement = (Element) nNode;
                taskname = eElement.getElementsByTagName("taskname").item(0).getTextContent();
                qn = eElement.getElementsByTagName("taskquestion").item(0).getTextContent();
                answertype = eElement.getElementsByTagName("answertype").item(0).getTextContent();

                //System.out.println(" --- Answer Type is " + answertype);
                outputType = eElement.getElementsByTagName("outputtype").item(0).getTextContent();
                outputTypeDescription = eElement.getElementsByTagName("outputTypeDescription").item(0).getTextContent();
                inputSize = eElement.getElementsByTagName("inputsize").item(0).getTextContent();
                
                //accCheckingInterface = eElement.getElementsByTagName("accuracyCheckingInterface").item(0).getTextContent();
                
                accCheckingInterface = "";
                
                taskDescription = eElement.getElementsByTagName("taskDescription").item(0).getTextContent();
                hasCorrectAnswer = eElement.getElementsByTagName("hasCorrectAnswer").item(0).getTextContent();

                NodeList inputs = eElement.getElementsByTagName("input");
                for (int j = 0; j < inputs.getLength(); j++) {
                    Node iNode = inputs.item(j);
                    Element iElement = (Element) iNode;
                    String inputtype = iElement.getElementsByTagName("inputtype").item(0).getTextContent();
                    String inputdescription = iElement.getElementsByTagName("inputdescription").item(0).getTextContent();

                    String inputmedium = iElement.getElementsByTagName("inputmedium").item(0).getTextContent();
                    inputTypes.add(inputtype);
                    inputDescriptions.add(inputdescription);
                    inputMediums.add(inputmedium);
                }
            }

            String inputsStr = "";
            for (int j = 0; j < inputTypes.size(); j++) {
                //concatenate the inputtypes input and input descriptions
                if (inputsStr.isEmpty()) {
                    inputsStr = inputTypes.get(j) + "::"
                            + inputDescriptions.get(j);
                } else {
                    inputsStr += ":::" + inputTypes.get(j) + "::" + inputDescriptions.get(j);

                }
            }

            if (outputType.trim().isEmpty()) {
                outputType = "not-interface";
            }
           
            td = new TaskDetails(taskname, qn, accCheckingInterface, outputType,
                    outputTypeDescription, inputsStr, taskDescription, answertype,
                    inputSize, hasCorrectAnswer);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return td;
    }

    public void prepareActualStudyQuestions(HttpServletRequest request, StudyParameters upmts, String userid) {
        try {
            if (upmts.getDatasetConditionNames().size() > 0) {
//                System.out.println("AAAAAAAAA");
                prepareTasksWithDatasets(request, upmts, userid);
            } else {
//                System.out.println("BBBBBBBBBBBBBB");
                String taskFilenameUrl = "";

                //for(int j=0; j<upmts.getDatasetConditionNames())
                for (int i = 0; i < upmts.questionCodes.size(); i++) {
                    String taskXmlName = upmts.questionCodes.get(i) + ".xml";
                    //the url will depend on the type of dataset if any
                    if (upmts.getDatasetTypes().size() > 0 &&
                            upmts.getDatasetTypes().get(0).trim().equalsIgnoreCase("System_Datasets")) {
                        taskFilenameUrl = getServletContext().getRealPath("taskInstances" + File.separator
                                + upmts.datasetname + File.separator + taskXmlName);
                    } else {
                        taskFilenameUrl = getServletContext().getRealPath("users" + File.separator
                                + userid + File.separator + "taskInstances" + File.separator
                                + upmts.datasetname + File.separator + taskXmlName);
                    }

                    TaskDetails td = upmts.taskDetails.get(i);
                    upmts.inputTypeList.add(td.getInputTypes());

                    int questionsize = upmts.questionSizes.get(i);               //questionSize
                    String question = upmts.questions.get(i);
                    int maxTime = upmts.questionMaxTimes.get(i);
                    String inputTypeStr = upmts.inputTypeList.get(i);

                    //if the task is a quantitative task, then we will have to read 
                    //the instances otherwise no reading of instances
                    //has a correct answer. in other words, it is a quantitative question
                    if (td.hasCorrectAnswer()) {
                        //upmts.incrementQuestionsWithTraining();

                        File xmlFile = new File(taskFilenameUrl);
                        DocumentBuilderFactory dbFactory2 = DocumentBuilderFactory.newInstance();
                        DocumentBuilder dBuilder2 = dbFactory2.newDocumentBuilder();
                        Document doc2 = dBuilder2.parse(xmlFile);
                        doc2.getDocumentElement().normalize();
                        //question instances
                        NodeList questionNode = doc2.getElementsByTagName("question");
                        int questionCount = 0;
                        int tutorialCount = 0;
                        for (int temp = 0; temp < questionNode.getLength(); temp++) {
                            Node nNode = questionNode.item(temp);
                            if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                                Element eElement = (Element) nNode;
                                //get the answers
                                String answer = eElement.getElementsByTagName("answer").item(0).getTextContent();

                                //get the nodes
                                ArrayList<String> nodes = new ArrayList<String>();
                                NodeList inputList = eElement.getElementsByTagName("input");

                                for (int j = 0; j < inputList.getLength(); j++) {
                                    String nodeText = inputList.item(j).getTextContent();
                                    nodes.add(nodeText);
                                }
                        //check if there are questionnodes i.e. nodes that will form part of the actual questions
                                //String qnodeText = "";

                                //get the taskOptions if there is some
                                String answertype = td.getAnswerType();

                                if (td.getAnswerType().trim().equalsIgnoreCase("options-dynamic")) {
                                    String taskOptions = eElement.getElementsByTagName("taskoptions").item(0).getTextContent();
                                    answertype += ":::" + taskOptions;
                                }

                                EvaluationQuestion evalQn = new EvaluationQuestion(question, answer, nodes,
                                        answertype, maxTime, td.getInputTypes(), td.getOutputType(),
                                        inputTypeStr, td.getAccuracyCheckingInterface(), td.getHasCorrectAnswer());

                                //add the question to either the tutorial list or the test list
                                if (tutorialCount < upmts.trainingSize
                                        && !upmts.questionCodes.get(i)
                                        .trim().equalsIgnoreCase("howManyClustersAreThere") //this is a hack
                                        && !upmts.questionCodes.get(i).trim().equalsIgnoreCase("areTwoNamedNodesConnected")
                                        && !upmts.questionCodes.get(i).trim().equalsIgnoreCase("rememberNodesUsedInCommonNeighbor")) {
                                    upmts.tutorialQuestions.add(evalQn);
                                    tutorialCount++;
                                } else {
                                    upmts.evalQuestions.add(evalQn);
                                    questionCount++;
                                }
                                
                                System.out.println(evalQn);
                                
                                
                                if (questionCount == questionsize) {
                                    break;
                                }
                            }
                        }

                    } else {
                        EvaluationQuestion evalQn = new EvaluationQuestion(question, "", new ArrayList<String>(),
                                td.getAnswerType(), maxTime, td.getInputTypes(), td.getOutputType(),
                                inputTypeStr, td.getAccuracyCheckingInterface(), td.getHasCorrectAnswer());

                        upmts.evalQuestions.add(evalQn);
                        // questionCount++;
                    }
                }

                upmts.sizeOfATrainingCondition = upmts.tutorialQuestions.size();
                
                
                /**
                 * adjust the tasks if it is a within user study
                 */
                if (upmts.expType_vis.equalsIgnoreCase("Within")) {
                    adjustTasksForWithinStudy(upmts);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void preparePreStudyQuestions(HttpServletRequest request, StudyParameters upmts, String userid) {
        //prepare the pre-qualitative questions here.     
        try {
            for (int i = 0; i < upmts.preStudyQuestionCodes.size(); i++) {
                
                if (!upmts.preStudyQuestionCodes.get(i).trim().isEmpty()) {
                    TaskDetails td = upmts.preStudyTaskDetails.get(i);
                    String question = upmts.preStudyQuestions.get(i);
                    EvaluationQuestion evalQn = new EvaluationQuestion(question, "", new ArrayList<String>(),
                            td.getAnswerType(), 0, td.getInputTypes(), td.getOutputType(),
                            "", "", td.getHasCorrectAnswer());

                    upmts.preStudyEvalQuestions.add(evalQn);
                }
                
                
            }
                //System.out.println("&&& The number of pre-study tasks is "+ upmts.preStudyEvalQuestions.size());                

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void preparePostStudyQuestions(HttpServletRequest request, StudyParameters upmts, String userid) {
        //prepare the quantitative questions here
         try {
             
             
            for (int i = 0; i < upmts.postStudyQuestionCodes.size(); i++) {
                
                if(!upmts.postStudyQuestionCodes.get(i).trim().isEmpty()) {
                       TaskDetails td = upmts.postStudyTaskDetails.get(i);
                        String question = upmts.postStudyQuestions.get(i);
                        EvaluationQuestion evalQn = new EvaluationQuestion(question, "", new ArrayList<String>(),
                                td.getAnswerType(), 0, td.getInputTypes(), td.getOutputType(),
                                "", "", td.getHasCorrectAnswer());
                        upmts.postStudyEvalQuestions.add(evalQn);
                }                
             
            }
            System.out.println("&&& The number of post-study tasks is "+ upmts.postStudyEvalQuestions.size());                

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void prepareTasksWithDatasets(HttpServletRequest request, StudyParameters upmts, String userid) {
        /**
         * read the question nodes *
         */
        try {
            String taskFilenameUrl = "";
            boolean tutorialPopulated = false;            

            if (upmts.getDatasetConditionNames().size() == 1 && upmts.expType_vis.equalsIgnoreCase("Within")) {

                //for the tutorial session
                upmts.tutorialViewerShortnames = new ArrayList<String>();
                upmts.tutorialViewerUrls = new ArrayList<String>();
                for (int i = 0; i < upmts.viewerSizeBeforeAdjustment; i++) {
                    upmts.tutorialViewerShortnames.add(upmts.viewerConditionShortnames.get(i));
                    upmts.tutorialViewerUrls.add(upmts.viewerConditionUrls.get(i));
                }
            }
            //if datasets is more than 1, this would have been done already.

            //if more than one dataset, the task will be repeated for each task instances.
            for (int j = 0; j < upmts.getDatasetTypes().size(); j++) {

                for (int i = 0; i < upmts.questionCodes.size(); i++) {
                    String taskXmlName = upmts.questionCodes.get(i) + ".xml";
                    //the url will depend on the type of dataset
                    if (upmts.getDatasetTypes().get(j).trim().equalsIgnoreCase("System_Datasets")) {
                        taskFilenameUrl = getServletContext().getRealPath("taskInstances" + File.separator
                                + upmts.getDatasetConditionNames().get(j) + File.separator + taskXmlName);
                    } else {
                        taskFilenameUrl = getServletContext().getRealPath("users" + File.separator
                                + userid + File.separator + "taskInstances" + File.separator
                                + upmts.getDatasetConditionNames().get(j) + File.separator + taskXmlName);
                    }

                    TaskDetails td = upmts.taskDetails.get(i);
                    upmts.inputTypeList.add(td.getInputTypes());

                    int questionsize = upmts.questionSizes.get(i);               //questionSize
                    String question = upmts.questions.get(i);
                    int maxTime = upmts.questionMaxTimes.get(i);
                    String inputTypeStr = upmts.inputTypeList.get(i);

                    //if the task is a quantitative task, then we will have to read 
                    //the instances otherwise no reading of instances
                    //has a correct answer. in other words, it is a quantitative question
                    if (td.hasCorrectAnswer()) {
                        //upmts.incrementQuestionsWithTraining();

                        File xmlFile = new File(taskFilenameUrl);
                        DocumentBuilderFactory dbFactory2 = DocumentBuilderFactory.newInstance();
                        DocumentBuilder dBuilder2 = dbFactory2.newDocumentBuilder();
                        Document doc2 = dBuilder2.parse(xmlFile);
                        doc2.getDocumentElement().normalize();
                        //question instances
                        NodeList questionNode = doc2.getElementsByTagName("question");
                        int questionCount = 0;
                        int tutorialCount = 0;
                        for (int temp = 0; temp < questionNode.getLength(); temp++) {
                            Node nNode = questionNode.item(temp);
                            if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                                Element eElement = (Element) nNode;
                                //get the answers
                                String answer = eElement.getElementsByTagName("answer").item(0).getTextContent();

                                //get the nodes
                                ArrayList<String> nodes = new ArrayList<String>();
                                NodeList inputList = eElement.getElementsByTagName("input");

                                for (int k = 0; k < inputList.getLength(); k++) {
                                    String nodeText = inputList.item(k).getTextContent();
                                    nodes.add(nodeText);
                                }
                        //check if there are questionnodes i.e. nodes that will form part of the actual questions
                                //String qnodeText = "";

                                //get the taskOptions if there is some
                                String answertype = td.getAnswerType();

                                if (td.getAnswerType().trim().equalsIgnoreCase("options-dynamic")) {
                                    String taskOptions = eElement.getElementsByTagName("taskoptions").item(0).getTextContent();
                                    answertype += ":::" + taskOptions;
                                }

                                EvaluationQuestion evalQn = new EvaluationQuestion(question, answer, nodes,
                                        answertype, maxTime, td.getInputTypes(), td.getOutputType(),
                                        inputTypeStr, td.getAccuracyCheckingInterface(), td.getHasCorrectAnswer());

                                //add the question to either the tutorial list or the test list
                                System.out.println("&&tutorial count is " + tutorialCount);

                                if ((tutorialCount < upmts.trainingSize
                                        && upmts.tutorialQuestions.size() < (upmts.trainingSize * upmts.questionCodes.size())) //we will only use the first dataset for training.
                                        && !upmts.questionCodes.get(i)
                                        .trim().equalsIgnoreCase("howManyClustersAreThere") //this is a hack
                                        && !upmts.questionCodes.get(i).trim().equalsIgnoreCase("areTwoNamedNodesConnected")
                                        && !upmts.questionCodes.get(i).trim().equalsIgnoreCase("rememberNodesUsedInCommonNeighbor")) {
                                    upmts.tutorialQuestions.add(evalQn);
                                    tutorialCount++;
                                } else {
                                    tutorialPopulated = true;
                                    upmts.evalQuestions.add(evalQn);
                                    // System.out.println("------> " + i);
                                    questionCount++;
                                }
                                if (questionCount == questionsize) {
                                    break;
                                }
                            }
                        }

                    } else {

                        EvaluationQuestion evalQn = new EvaluationQuestion(question, "", new ArrayList<String>(),
                                td.getAnswerType(), maxTime, td.getInputTypes(), td.getOutputType(),
                                inputTypeStr, td.getAccuracyCheckingInterface(), td.getHasCorrectAnswer());

                        upmts.evalQuestions.add(evalQn);
                        // questionCount++;
                    }
                }

                if (j == 0) {
                    upmts.sizeOfACondition = upmts.evalQuestions.size();
                    upmts.sizeOfADatasetCondition = upmts.evalQuestions.size();;
                }
            }

            /**
             * adjust the tasks if it is a within user study
             */
            if (upmts.expType_vis.equalsIgnoreCase("Within")) {
                adjustTasksForWithinStudy(upmts);
            }

            // System.out.println("___" + upmts.evalQuestions.size() + "__");
            //  System.out.println("-finished preparing questions");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void setOrderOfConditions(StudyParameters upmts) {
        //start from the first condition and add the other conditions in 
        //a round-robin kind of fashion
        ArrayList<String> orderdedVCurls = new ArrayList<String>();
        ArrayList<String> orderdedVCshortn = new ArrayList<String>();

        int index = -1;
        //find the index of the first condition among the conditions
        for (int i = 0; i < upmts.viewerConditionShortnames.size(); i++) {
            if (upmts.firstConditionShortName.equalsIgnoreCase(upmts.viewerConditionShortnames.get(i))) {
                index = i;
                break;
            }
        }

        int cnt = 0;
        int size = 0;
        if (index >= 0) {
            cnt = index;
            while (upmts.orderOfConditionShortNames.size() < upmts.viewerConditionShortnames.size()) {
                cnt++;
                if (cnt == upmts.viewerConditionUrls.size()) {
                    cnt = 0;
                }

                upmts.orderOfConditionShortNames.add(upmts.viewerConditionShortnames.get(cnt));
                upmts.orderOfConditionUrls.add(upmts.viewerConditionUrls.get(cnt));
            }
        }

        upmts.viewerConditionUrls = new ArrayList<String>();
        for (int i = 0; i < upmts.orderOfConditionUrls.size(); i++) {
            upmts.viewerConditionUrls.add(upmts.orderOfConditionUrls.get(i));
        }
        
        //System.out.println("the condition URLs are ::: "+ orderOfConditionUrls);
    }

    /**
     * This method will determine which condition among the other options will
     * be made first.
     */
    public void setFirstCondition(StudyParameters upmts, String userid) {
        //TODO: read a file that contains  the completed studies compare them to the condition,
        //and select the condition with the lower count
        try {

            ArrayList<String> firstConditions = new ArrayList<String>();
            int[] conditionCount = new int[upmts.viewerConditionShortnames.size()];
            //initialize the count values
            for (int i = 0; i < conditionCount.length; i++) {
                conditionCount[i] = 0;
            }

            String filename = "firstConditions.txt";

            String studydataurl = "users" + File.separator + userid + File.separator
                    + "studies" + File.separator + upmts.studyname + File.separator + "data";

            //String studydataurl = "studies" + File.separator + upmts.studyname + File.separator + "data";
            File file = new File(getServletContext().getRealPath(studydataurl + File.separator + filename));
            if (!file.exists()) {
                System.out.println("^^^^^ " + file.getAbsolutePath());
                file.createNewFile();
            }

            BufferedReader br = new BufferedReader(new FileReader(file));
            String line = "";
            //ArrayList<String> taskAccuracy = new ArrayList<String>();
            while ((line = br.readLine()) != null) {

                for (int i = 0; i < upmts.viewerConditionShortnames.size(); i++) {
                    if (line.trim().equalsIgnoreCase(upmts.viewerConditionShortnames.get(i))) {
                        conditionCount[i]++;
                        break;
                    }
                }
            }

            br.close();

            //find one of the condition with the minimum count
            int minIndex = 0;
            int minimumCount = conditionCount[0];

            ListOfConditionsAndTheirCounters ongoing_studycounts = null;

            if (this.onGoingStudyCounts.get(upmts.studyname) != null) {
                ongoing_studycounts = (ListOfConditionsAndTheirCounters) this.onGoingStudyCounts.get(upmts.studyname);
            }

            if (ongoing_studycounts == null) { //first time
                ongoing_studycounts = new ListOfConditionsAndTheirCounters(upmts.viewerConditionShortnames);
                onGoingStudyCounts.put(upmts.studyname, ongoing_studycounts);
            }

            for (int i = 0; i < ongoing_studycounts.conditionCounters.size(); i++) {
                System.out.println(ongoing_studycounts.conditionNames.get(i) + " **** " + ongoing_studycounts.conditionCounters.get(i));
            }

            for (int i = 0; i < conditionCount.length; i++) {
                if (conditionCount[i] < minimumCount) {
                    //Check to make sure the minimum does not have
                    //too many ongoing studies running already.. for now lets keep
                    //the minimum to 5
                    String cur_cshortname = upmts.viewerConditionShortnames.get(i);
                    int cur_count = ongoing_studycounts.getCondCounter(cur_cshortname);
                    String min_cshortname = upmts.viewerConditionShortnames.get(minIndex);
                    int min_count = ongoing_studycounts.getCondCounter(min_cshortname);

                    if ((cur_count < 1) || (cur_count < min_count) || ((minimumCount - conditionCount[i]) > 2)) {
                        minIndex = i;
                        minimumCount = conditionCount[i];
                    }

                }
            }
            upmts.firstConditionShortName = upmts.viewerConditionShortnames.get(minIndex);
            upmts.firstConditionUrl = upmts.viewerConditionUrls.get(minIndex);

            //int ongoingCount = upmts.onGoingConditionCount.get(minIndex);
            // upmts.onGoingConditionCount.set(minIndex, (ongoingCount + 1));
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    public void adjustTasksForWithinStudy(StudyParameters upmts) {
        //we will double/triple/etc the testtasks, i.e. add the same tasks to the end of the list for each of the conditions.
        int length = upmts.evalQuestions.size();
        int trainingSize = upmts.tutorialQuestions.size();

        //if there are more than one dataset, the size of a condition will already be set by now.
        // if(!(upmts.getDatasetConditionNames().size() > 1)){
        upmts.sizeOfACondition = length;

        upmts.sizeOfATrainingCondition = trainingSize;
        
        upmts.viewerSizeBeforeAdjustment = upmts.viewerConditionShortnames.size();
        
        upmts.viewerSizeBeforeAdjustment = upmts.viewerConditionShortnames.size();

        //for the tutorial session
        upmts.tutorialViewerShortnames = new ArrayList<String>();
        upmts.tutorialViewerUrls = new ArrayList<String>();
        for (int i = 0; i < upmts.viewerSizeBeforeAdjustment; i++) {
            upmts.tutorialViewerShortnames.add(upmts.viewerConditionShortnames.get(i));
            upmts.tutorialViewerUrls.add(upmts.viewerConditionUrls.get(i));
        }



      //  System.out.println("Length is ++ " + length);
        //  System.out.println("Training size is ++" + trainingSize);
        //}       

        /* System.out.println("Size of a condition is ++ " + upmts.sizeOfACondition);
         System.out.println("Size of viewerUrls is  ++ " + upmts.viewerConditionUrls.size());

         for (int i = 0; i < upmts.evalQuestions.size(); i++) {
         System.out.println("\t" + upmts.evalQuestions.get(i).getQuestion());
         }
         System.out.println();
         for (int i = 0; i < upmts.viewerConditionUrls.size(); i++) {
         System.out.println("\t" + upmts.viewerConditionShortnames.get(i) + " ___ " + upmts.viewerConditionUrls.get(i));
         }*/
        //we will be having the same question for all the conditions
        for (int k = 0; k < upmts.viewerSizeBeforeAdjustment - 1; k++) {
            for (int i = 0; i < length; i++) {
                EvaluationQuestion evq = upmts.evalQuestions.get(i);

                //System.out.println(" ----kkiiinngg  "+evq.getQuestion() );
                EvaluationQuestion evalQ = new EvaluationQuestion(evq.getQuestion(),
                        evq.getCorrectAns(), evq.getInputs(),
                        evq.getAnsType(), evq.getMaxTimeInSeconds(),
                        evq.getInputTypes(), evq.getOutputInterface(), evq.getInputTypeStr(),
                        evq.getInterfaceForValidatingAnswers(), evq.getHasCorrectAnswer());
                upmts.evalQuestions.add(evalQ);
            }

        }
        //do similar for the training too.
        for (int j = 0; j < upmts.viewerSizeBeforeAdjustment - 1; j++) {
            for (int i = 0; i < trainingSize; i++) {
                EvaluationQuestion evq = upmts.tutorialQuestions.get(i);

                EvaluationQuestion evalQ = new EvaluationQuestion(evq.getQuestion(),
                        evq.getCorrectAns(), evq.getInputs(),
                        evq.getAnsType(), evq.getMaxTimeInSeconds(),
                        evq.getInputTypes(), evq.getOutputInterface(), evq.getInputTypeStr(),
                        evq.getInterfaceForValidatingAnswers(), evq.getHasCorrectAnswer());
                upmts.tutorialQuestions.add(evalQ);
            }

        }
        System.out.println("Size of tutorial is now " + upmts.tutorialQuestions.size());

    }

    public void printAllNodesInvolved(StudyParameters upmts) {
        for (EvaluationQuestion eq : upmts.evalQuestions) {
            ArrayList<String> nodes = eq.getInputs();

            for (String str : nodes) {
                System.out.print(str + ", ");
            }
            System.out.println(eq.getCorrectAns());
        }
    }

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

    public void writeQuantitativeAnswersToFile(StudyParameters upmts, String userid) {
        try {
            String filename = "";
            //System.out.println("The StudyType is ***" + upmts.studyType + "*");
            if (upmts.expType_vis.equalsIgnoreCase("Between")) {
                upmts.currentCondition = upmts.firstConditionShortName;
                upmts.currentConditions = new ArrayList<String>();
                //if more than one dataset, set the conditions used
                if (upmts.getDatasetConditionNames().size() > 1) {
                    for (int i = 0; i < upmts.datasetSetSizeBeforeAdjustment; i++) {
                        String ds = upmts.getDatasetConditionNames().get(i);
                        upmts.currentConditions.add(upmts.firstConditionShortName + "__" + ds);
                    }
                } else {
                    upmts.currentConditions.add(upmts.currentCondition);
                }

                writeBetweenStudyAnwsersToFile(upmts, userid);
            } else if (upmts.expType_vis.equalsIgnoreCase("Within")) {
                writeWithinStudyAnswersToFile(upmts, userid);
            }

            //write the first condition also to file
            writeFirstConditionToFile(upmts, userid);

            //decrement ongoingStudy counter for with this first condition. 
            ListOfConditionsAndTheirCounters ongoing_studyCounts
                    = (ListOfConditionsAndTheirCounters) onGoingStudyCounts.get(upmts.studyname);

            if (ongoing_studyCounts != null) {
                //do this if it is not null.
                ongoing_studyCounts.decrementCondCounter(upmts.firstConditionShortName);
            }

            onGoingStudyCounts.put(upmts.studyname, ongoing_studyCounts);

            // write it if there is no post study tasks. For post study tasks, we will write it afterwords.
            if (upmts.postStudyEvalQuestions.isEmpty()) {
                writeQualitativeAnswersToFile(upmts, userid);
            }
//            
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    //public void 
    public void writeQualitativeAnswersToFile(StudyParameters upmts, String userid) {
        //write the pre-qualitative questions and the postqualitative questions to file
        System.out.println("*** About to write Qualitative answers to file ");

        try {

            boolean qualTaskExist = false;
            //check if there are qualitative questions
            for (int i = 0; i < upmts.questionCodes.size(); i++) {
                TaskDetails td = upmts.taskDetails.get(i);

                if (!td.hasCorrectAnswer()) {
                    qualTaskExist = true;
                    break;
                }
            }

            String filename;// = "QualitativeAnswers.txt";
            String firstcondition;

            String orderOfConditions = "";

            //set the order of conditions
            if (upmts.expType_vis.equalsIgnoreCase("Between")) {
                //it is a between, so we will only have the first condition
                orderOfConditions = upmts.currentCondition;
            } else {
                //it is a within so we will show the order of the condition
                for (int i = 0; i < upmts.orderOfConditionShortNames.size(); i++) {
                    orderOfConditions += " : " + upmts.orderOfConditionShortNames.get(i);
                }
            }

            //get the filename for the qualitative qn.
            //For between-group we will get the name for the current condition
            //for within group, we will use the first condition: TODO: change this later.
            //do this if qual task exist.
            if (qualTaskExist) {
                if (upmts.expType_vis.equalsIgnoreCase("Between")) {
                    //get the first condition name.
                    upmts.currentCondition = upmts.currentConditions.get(0);

                    firstcondition = upmts.currentCondition;
                    filename = upmts.utils.getConditionQualitativeQnFileName(firstcondition);
                } else {
                    firstcondition = upmts.orderOfConditionShortNames.get(0);
                    filename = upmts.utils.getConditionQualitativeQnFileName(firstcondition);
                }

                //  filename = "QualitativeQns-and-Misc-info.txt";
                String studydataurl = "users" + File.separator + userid + File.separator
                        + "studies" + File.separator + upmts.studyname + File.separator + "data";

                File file = new File(getServletContext().getRealPath(studydataurl + File.separator + filename));

                boolean newFile = false;

                if (!file.exists()) {
                    file.createNewFile();
                    newFile = true;
                }
                FileWriter fw = new FileWriter(file, true);
                BufferedWriter bw = new BufferedWriter(fw);
                PrintWriter pw = new PrintWriter(bw);

                //first write the questions as headers
                String question, answer;
                int qualTaskCnt = 0;
                if (newFile) {
                //print the header

                    //if study is between the headers will be different 
                    //from when they are within
                    if (upmts.expType_vis.equalsIgnoreCase("Between")) {
                        for (int i = 0; i < upmts.questionCodes.size(); i++) {
                            TaskDetails td = upmts.taskDetails.get(i);
                            //qualitative task
                            if (!td.hasCorrectAnswer()) {
                                qualTaskCnt++;

                                String qcode = upmts.questionCodes.get(i) + "_" + upmts.currentCondition;

                                if (qualTaskCnt == 1) {
                                    pw.print(qcode);
                                } else {
                                    pw.print("\t\t" + qcode);
                                }
                            }

                        }
                    } else {
                        //we will be doing it for the within
                        qualTaskCnt = 0;
                        for (int i = 0; i < upmts.numberOfConditions; i++) {
                            upmts.currentCondition = upmts.orderOfConditionShortNames.get(i);

                            for (int j = 0; j < upmts.questionCodes.size(); j++) {

                                TaskDetails td = upmts.taskDetails.get(j);
                                //qualitative task
                                if (!td.hasCorrectAnswer()) {
                                    qualTaskCnt++;                                  

                                    String qcode = upmts.questionCodes.get(j) + "_" + upmts.currentCondition;

                                    if (qualTaskCnt == 1) {
                                        pw.print(qcode);
                                    } else {
                                        pw.print("\t\t" + qcode);
                                    }
                                }
                            }
                        }
                    }

                    //  pw.print("\t\tOrderOfConditions");
                    pw.println();
                }
                //write the answers
                qualTaskCnt = 0;
                for (int i = 0; i < upmts.evalQuestions.size(); i++) {

                    //check if the question is a qualitative question. If it is, then do the following.
                    //
                    if (!upmts.evalQuestions.get(i).hasCorrectAnswer()) {
                        qualTaskCnt++;

                        answer = upmts.evalQuestions.get(i).getGivenAnswer();

                        // System.out.println("answer is "+ answer);
                        if (qualTaskCnt == 1) {
                            pw.print(answer);
                        } else {
                            pw.print("\t\t" + answer);
                        }
                    }

                }
                // System.out.println("order of conditions si == "+ orderOfConditions );
                // pw.print("\t\t" + orderOfConditions);            
                pw.println();

                pw.close();
            }

            /**
             * Now write the miscellaneous information also to file
             */
            filename = "MiscellaneousInfo.txt";

            String studydataurl = "users" + File.separator + userid + File.separator
                    + "studies" + File.separator + upmts.studyname + File.separator + "data";

            File file = new File(getServletContext().getRealPath(studydataurl + File.separator + filename));

            boolean newFile = false;

            if (!file.exists()) {
                file.createNewFile();
                newFile = true;
            }

            FileWriter fw = new FileWriter(file, true);
            BufferedWriter bw = new BufferedWriter(fw);
            PrintWriter pw = new PrintWriter(bw);

            if (newFile) {
                //write the headers
                //now write the headers of the miscellaneous info
                pw.print("\tOrderOfConditions");
                pw.print("\tWindowWidth");
                pw.print("\tWindowHeight");
                pw.print("\tOffFocusTime");
                pw.print("\tUserAccuracyInfo");
                pw.print("\tUserTimeInfo");
//                pw.print("\t\tColorBlindessTestAnswers");
                
               
                
                
                pw.print("\tDateAndTime");
                 for (int i = 0; i < upmts.preStudyEvalQuestions.size(); i++) {
                    pw.print("\t" + upmts.preStudyEvalQuestions.get(i).getQuestion());
                }
                
                for (int i = 0; i < upmts.postStudyEvalQuestions.size(); i++) {
                    pw.print("\t" + upmts.postStudyEvalQuestions.get(i).getQuestion());
                }
                pw.println();
            }
            //save the miscellaneous info now.
            //save the miscellaneous information.
            pw.print("\t\t" + orderOfConditions);
            pw.print("\t\t" + upmts.userViewerWidth);
            pw.print("\t\t" + upmts.userViewerHeight);
            pw.print("\t\t" + upmts.offFocusTime);
            pw.print("\t\t" + upmts.accuracyInfo);
            pw.print("\t\t" + upmts.timeInfo);
//            pw.print("\t\t" + upmts.colorBlindnessTestAnswers);
            pw.print("\t\t" + upmts.dateAndTime);
             
            for (int i = 0; i < upmts.preStudyEvalQuestions.size(); i++) {
                    pw.print("\t" + upmts.preStudyEvalQuestions.get(i).getGivenAnswer());
            }
                
            for (int i = 0; i < upmts.postStudyEvalQuestions.size(); i++) {
                pw.print("\t" + upmts.postStudyEvalQuestions.get(i).getGivenAnswer());
            }

            pw.println();
            pw.close();

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    //to be done.
    public void writePreStudyAnswersToFile (StudyParameters upmts, String userid) {
        //write the pre-qualitative questions and the postqualitative questions to file
        System.out.println("*** About to write Qualitative answers to file ");

        try {

            boolean qualTaskExist = false;
            //check if there are qualitative questions
            for (int i = 0; i < upmts.questionCodes.size(); i++) {
                TaskDetails td = upmts.taskDetails.get(i);

                if (!td.hasCorrectAnswer()) {
                    qualTaskExist = true;
                    break;
                }
            }

            String filename;// = "QualitativeAnswers.txt";
            String firstcondition;

            String orderOfConditions = "";

            //set the order of conditions
            if (upmts.expType_vis.equalsIgnoreCase("Between")) {
                //it is a between, so we will only have the first condition
                orderOfConditions = upmts.currentCondition;
            } else {
                //it is a within so we will show the order of the condition
                for (int i = 0; i < upmts.orderOfConditionShortNames.size(); i++) {
                    orderOfConditions += " : " + upmts.orderOfConditionShortNames.get(i);
                }
            }

            //get the filename for the qualitative qn.
            //For between-group we will get the name for the current condition
            //for within group, we will use the first condition: TODO: change this later.
            //do this if qual task exist.
            if (qualTaskExist) {
                if (upmts.expType_vis.equalsIgnoreCase("Between")) {
                    //get the first condition name.
                    upmts.currentCondition = upmts.currentConditions.get(0);

                    firstcondition = upmts.currentCondition;
                    filename = upmts.utils.getConditionQualitativeQnFileName(firstcondition);
                } else {
                    firstcondition = upmts.orderOfConditionShortNames.get(0);
                    filename = upmts.utils.getConditionQualitativeQnFileName(firstcondition);
                }

                //  filename = "QualitativeQns-and-Misc-info.txt";
                String studydataurl = "users" + File.separator + userid + File.separator
                        + "studies" + File.separator + upmts.studyname + File.separator + "data";

                File file = new File(getServletContext().getRealPath(studydataurl + File.separator + filename));

                boolean newFile = false;

                if (!file.exists()) {
                    file.createNewFile();
                    newFile = true;
                }
                FileWriter fw = new FileWriter(file, true);
                BufferedWriter bw = new BufferedWriter(fw);
                PrintWriter pw = new PrintWriter(bw);

                //first write the questions as headers
                String question, answer;
                int qualTaskCnt = 0;
                if (newFile) {
                //print the header

                    //if study is between the headers will be different 
                    //from when they are within
                    if (upmts.expType_vis.equalsIgnoreCase("Between")) {
                        for (int i = 0; i < upmts.questionCodes.size(); i++) {
                            TaskDetails td = upmts.taskDetails.get(i);
                            //qualitative task
                            if (!td.hasCorrectAnswer()) {
                                qualTaskCnt++;

                                String qcode = upmts.questionCodes.get(i) + "_" + upmts.currentCondition;

                                if (qualTaskCnt == 1) {
                                    pw.print(qcode);
                                } else {
                                    pw.print("\t\t" + qcode);
                                }
                            }

                        }
                    } else {
                        //we will be doing it for the within
                        qualTaskCnt = 0;
                        for (int i = 0; i < upmts.numberOfConditions; i++) {
                            upmts.currentCondition = upmts.orderOfConditionShortNames.get(i);

                            for (int j = 0; j < upmts.questionCodes.size(); j++) {

                                TaskDetails td = upmts.taskDetails.get(j);
                                //qualitative task
                                if (!td.hasCorrectAnswer()) {
                                    qualTaskCnt++;                                  

                                    String qcode = upmts.questionCodes.get(j) + "_" + upmts.currentCondition;

                                    if (qualTaskCnt == 1) {
                                        pw.print(qcode);
                                    } else {
                                        pw.print("\t\t" + qcode);
                                    }
                                }
                            }
                        }
                    }

                    pw.println();
                }
                //write the answers
                qualTaskCnt = 0;
                for (int i = 0; i < upmts.evalQuestions.size(); i++) {

                    //check if the question is a qualitative question. If it is, then do the following.
                    //
                    if (!upmts.evalQuestions.get(i).hasCorrectAnswer()) {
                        qualTaskCnt++;

                        answer = upmts.evalQuestions.get(i).getGivenAnswer();

                        // System.out.println("answer is "+ answer);
                        if (qualTaskCnt == 1) {
                            pw.print(answer);
                        } else {
                            pw.print("\t\t" + answer);
                        }
                    }

                }
                // System.out.println("order of conditions si == "+ orderOfConditions );
                // pw.print("\t\t" + orderOfConditions);            
                pw.println();

                pw.close();
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }   
  
    
    public void writeBetweenStudyAnwsersToFile(StudyParameters upmts, String userid) {
        //write a between study results to file
        try {
            //System.out.println("Writing Between Study Results");
            upmts.sizeOfACondition = upmts.evalQuestions.size();
            
            System.out.println("***___ size of a condition :: " + upmts.sizeOfACondition );
            
            File files[] = new File[upmts.currentConditions.size()];
            BufferedWriter bws[] = new BufferedWriter[upmts.currentConditions.size()];
            PrintWriter pws[] = new PrintWriter[upmts.currentConditions.size()];

            String filename;

            for (int k = 0; k < upmts.currentConditions.size(); k++) {
                System.out.println("currentCondion " + upmts.currentConditions.get(0)
                        + "  condition " + upmts.viewerConditionShortnames.get(0));

                filename = upmts.utils.getConditionAccuracyFileName(upmts.currentConditions.get(k));

                //get the studydata url it will be in the user directory
                String studydataurl = "users" + File.separator + userid + File.separator
                        + "studies" + File.separator + upmts.studyname + File.separator + "data";

                // String studydataurl = "studies" + File.separator + upmts.studyname + File.separator + "data";
                files[k] = new File(getServletContext().getRealPath(studydataurl + File.separator + filename));
                //  System.out.println("__* fna  "+filename_accuracy);

                boolean newFile_acc = false;
                if (!files[k].exists()) {
                    files[k].createNewFile();
                    newFile_acc = true;
                }
                //do the actual writings of the results to the file

                bws[k] = new BufferedWriter(new FileWriter(files[k], true));

                pws[k] = new PrintWriter(bws[k]);
                int quantCnt = 0;
                if (newFile_acc) {
                    //write the headers               
                    //    System.out.println("hello-");
                    for (int i = 0; i < upmts.questionCodes.size(); i++) {
                        TaskDetails td = upmts.taskDetails.get(i);
                        //System.out.println("has correct answer " + td.hasCorrectAnswer());
                        if (td.hasCorrectAnswer()) {
                            quantCnt++;
                            if (quantCnt == 1) {
                                //first one written
                                pws[k].print("Acc_" + upmts.questionCodes.get(i) + "_" + upmts.currentCondition);
                            } else {
                                pws[k].print("," + "Acc_" + upmts.questionCodes.get(i) + "_" + upmts.currentCondition);
                            }
                        }
                    }
                    pws[k].println();
                }
            }

            //write the data to a file.
            int start = 0;
            int limit = 0;

            for (int k = 0; k < upmts.currentConditions.size(); k++) {
                start = k * upmts.sizeOfACondition;
                limit = start + upmts.sizeOfACondition;//write the data to a file.
                
                

                int j = 0;
                int taskSize = upmts.questionSizes.get(j);
                //j++;
                int cnt = 0;
                int cnt_qual = 0;
                double numCorrect = 0;
                boolean quanttaskExist = false;
                boolean quanttaskBegin = false;
                int quantNumber = 0;

                //for (int i = 0; i < upmts.evalQuestions.size(); i++) {
                //if the question is a quantitative question, increment the counter
                //increment counter only if the question is quantitative.
                for (int m = start; m < limit; m++) {
                    if (m == start) {
                        taskSize = (Integer) upmts.questionSizes.get(j);
                        j++;
                        numCorrect = 0;
                        cnt = 1;
                    }

                     System.out.println("****m "+m + " *** limit: "+limit );
             
                    //do this only if it is a quantitative task or part of a quantitative task.
                    if (upmts.evalQuestions.get(m).hasCorrectAnswer()) {
                        quanttaskBegin = true;
                        quanttaskExist = true;
                        numCorrect += upmts.evalQuestions.get(m).getIsGivenAnsCorrect();
                        
                        if (m > start) {
                            cnt++;
                        }

                    } else {
                        quanttaskBegin = false;
                        quanttaskExist = false;
                    }
                    
                    System.out.println("_____Cnt "+ cnt + ":: _taskSize: " + taskSize + " : quantTaskBegin: "+quanttaskBegin);

                    if (cnt == taskSize) {

                        if (quanttaskBegin) {

                            System.out.println("&&& numcorrect is " + numCorrect + "  taskSize is: " + taskSize);

                            if (quantNumber == 0) {
                                upmts.accuracyInfo = "" + (double) numCorrect / taskSize;
                                pws[k].print((double) numCorrect / taskSize);
                            } else {
                                upmts.accuracyInfo += "," + (double) numCorrect / taskSize;
                                pws[k].print("," + (double) numCorrect / taskSize);
                            }
                            quantNumber++;
                        }

                        //check if there are still more tasks
                        if (j < (Integer) upmts.questionSizes.size()) {
                            taskSize = (Integer) upmts.questionSizes.get(j);
                            numCorrect = 0;
                            cnt = 1;
                            j++;
                        }

                        if (upmts.evalQuestions.get(m).hasCorrectAnswer()) {
                            quanttaskBegin = true;
                        } else {
                            quanttaskBegin = false;
                        }

                    }
                }

                /* int size = upmts.evalQuestions.size();

                 //  System.out.println(" >> j is "+ j + " and quanttask is "+ quanttaskExist);
                 if (j == 0 && quanttaskExist == true) { //only one question
                 upmts.accuracyInfo = "" + (double) numCorrect / taskSize;
                 pw1.print("" + (double) numCorrect / taskSize);
                 } else if (j > 0
                 && upmts.evalQuestions.get(size - 1).hasCorrectAnswer()) {
                 upmts.accuracyInfo += "," + (double) numCorrect / taskSize;
                 pw1.print("," + (double) numCorrect / taskSize);
                 }*/
                pws[k].println();

            }
            //close the printWriters
            for (int i = 0; i < pws.length; i++) {
                pws[i].close();
            }

            /*Write time to file */
            for (int k = 0; k < upmts.currentConditions.size(); k++) {

                filename = upmts.utils.getConditionTimeFileName(upmts.currentConditions.get(k));

                //get the studydata url it will be in the user directory
                String studydataurl = "users" + File.separator + userid + File.separator
                        + "studies" + File.separator + upmts.studyname + File.separator + "data";

                // String studydataurl = "studies" + File.separator + upmts.studyname + File.separator + "data";
                files[k] = new File(getServletContext().getRealPath(studydataurl + File.separator + filename));
                //  System.out.println("__* fna  "+filename_accuracy);

                boolean newFile_acc = false;
                if (!files[k].exists()) {
                    files[k].createNewFile();
                    newFile_acc = true;
                }
                //do the actual writings of the results to the file

                bws[k] = new BufferedWriter(new FileWriter(files[k], true));

                pws[k] = new PrintWriter(bws[k]);
                int quantCnt = 0;
                if (newFile_acc) {

                    upmts.currentCondition = upmts.currentConditions.get(k);

                    for (int i = 0; i < upmts.questionCodes.size(); i++) {

                        TaskDetails td = upmts.taskDetails.get(i);

                        // System.out.println("has correct answer " + td.hasCorrectAnswer());
                        if (td.hasCorrectAnswer()) {
                            quantCnt++;
                            if (quantCnt == 1) {
                                //first one written
                                pws[k].print("Time_" + upmts.questionCodes.get(i) + "_" + upmts.currentCondition);
                            } else {
                                pws[k].print("," + "Time_" + upmts.questionCodes.get(i) + "_" + upmts.currentCondition);
                            }
                        }
                    }
                    pws[k].println();
                }

            }

            //write the data to a file.
            start = 0;
            limit = 0;

            for (int n = 0; n < upmts.currentConditions.size(); n++) {
                start = n * upmts.sizeOfACondition;
                limit = start + upmts.sizeOfACondition;//write the data to a file.

                int j = 0;
                int taskSize = upmts.questionSizes.get(j);
                //k++;
                int cnt = 0;
                int totalTime = 0;
                boolean quanttaskBegin = false;
                boolean quanttaskExist = false;
                int quantNumber = 0;

                for (int k = start; k < limit; k++) {

                    // if (upmts.evalQuestions.get(k).hasCorrectAnswer()) {
                    // cnt++;
                    //}
                    if (k == start) {
                        taskSize = (Integer) upmts.questionSizes.get(j);
                        j++;
                        totalTime = 0;
                        cnt = 1;
                    }

                    if (upmts.evalQuestions.get(k).hasCorrectAnswer()) {

                        quanttaskExist = true;
                        quanttaskBegin = true;
                        totalTime += upmts.evalQuestions.get(k).getTimeInSeconds();
                        if (k > start) {
                            cnt++;
                        }
                    } else {
                        quanttaskExist = false;
                        quanttaskBegin = false;
                    }

                    if ((cnt == taskSize)) {

                        if (quanttaskBegin) {
                            if (quantNumber == 0) {
                                upmts.timeInfo = "" + (double) totalTime / taskSize;
                                pws[n].print((double) totalTime / taskSize);
                            } else {
                                upmts.timeInfo += "," + (double) totalTime / taskSize;
                                pws[n].print("," + (double) totalTime / taskSize);
                            }

                            quantNumber++;
                        }

                        if (j < (Integer) upmts.questionSizes.size()) {
                            taskSize = (Integer) upmts.questionSizes.get(j);
                            totalTime = 0;
                            cnt = 1;
                            j++;
                        }

                        if (upmts.evalQuestions.get(k).hasCorrectAnswer()) {
                            quanttaskBegin = true;
                        } else {
                            quanttaskBegin = false;
                        }

                    }

                }

                /* int size = upmts.evalQuestions.size();

                 if (j == 0 && quanttaskExist == true) { //only one question type
                 upmts.timeInfo = "" + (double) totalTime / taskSize;
                 pws[i].print((double) totalTime / taskSize);
                 } else if (j > 0
                 && upmts.evalQuestions.get(size - 1).hasCorrectAnswer()) {
                 upmts.timeInfo += "," + (double) totalTime / taskSize;
                 pws[i].print("," + (double) totalTime / taskSize);
                 }*/
                pws[n].println();
            }
            System.out.println("Results written to file successfully!");

            //close the printWriters
            for (int i = 0; i < pws.length; i++) {
                pws[i].close();
            }

            /**
             * *
             * Write the raw data to file
             */
            start = 0;
            int taskSize = 0;
            int cnt = 0;
            /**
             * First write the file headers *
             */
            for (int i = 0; i < upmts.currentConditions.size(); i++) {

                filename = upmts.utils.getConditionAccuracyBasicFileName(upmts.currentConditions.get(i));

                String studydataurl = "users" + File.separator + userid + File.separator
                        + "studies" + File.separator + upmts.studyname + File.separator + "data";

                //String studydataurl = "studies" + File.separator + upmts.studyname + File.separator + "data";
                files[i] = new File(getServletContext().getRealPath(studydataurl + File.separator + filename));

                boolean newFile = false;

                if (!files[i].exists()) {
                    files[i].createNewFile();
                    newFile = true;
                    //print the headers
                }

                bws[i] = new BufferedWriter(new FileWriter(files[i], true));
                pws[i] = new PrintWriter(bws[i]);

                if (newFile) {
                    //write the headers
                    //first line will be the actual name and the second line will be the question numbers
                    upmts.currentCondition = upmts.currentConditions.get(i);

                    System.out.println("***The current condition is " + upmts.currentCondition);

                    start = 0;
                    taskSize = 0;
                    cnt = 0;

                    for (int j = 0; j < upmts.questionSizes.size(); j++) {
                        start = taskSize;
                        taskSize = upmts.questionSizes.get(j);
                        String ttype = upmts.questionCodes.get(j);
                        limit = start + taskSize;
                        cnt = 0;

                        TaskDetails td = upmts.taskDetails.get(j);
                        if (td.hasCorrectAnswer()) {
                            for (int k = start; k < limit; k++) {
                                //ttype = taskTypes.get(m);
                                String name = "Acc_" + ttype + "_" + upmts.currentCondition;
                                if (k == 0 && start == 0) {
                                    pws[i].print(name);
                                } else if (cnt == 0) {
                                    pws[i].print(" :: " + name);
                                } else {
                                    pws[i].print(",");
                                }

                                cnt++;
                            }
                        }
                    }

                    pws[i].println();

                    //print the question headers
                    start = 0;
                    taskSize = 0;
                    cnt = 0;
                    for (int j = 0; j < upmts.questionSizes.size(); j++) {
                        start = taskSize;
                        taskSize = upmts.questionSizes.get(j);
                        limit = start + taskSize;
                        cnt = 0;

                        TaskDetails td = upmts.taskDetails.get(j);
                        if (td.hasCorrectAnswer()) {
                            //it is a quatitative
                            for (int k = start; k < limit; k++) {
                                String name = "Q" + (cnt + 1);
                                if (k == 0 && start == 0) {
                                    pws[i].print(name);
                                } else if (cnt == 0) {
                                    pws[i].print(" :: " + name);
                                } else {
                                    pws[i].print("," + name);
                                }
                                cnt++;
                            }
                        }
                    }
                    pws[i].println();

                }
            }

            /**
             * Now write the answers given for each question *
             */
            start = 0;
            limit = 0;
            // System.out.println("*** Size of a condition::: " + upmts.sizeOfACondition);
            for (int i = 0; i < upmts.currentConditions.size(); i++) {
                start = i * upmts.sizeOfACondition;
                limit = start + upmts.sizeOfACondition;

                int j = 0;
                taskSize = upmts.questionSizes.get(j);
                
                cnt = 0;

                //   System.out.println("The Start is:: " + start +"  The LIMIT IS :: " +limit);
                for (int k = start; k < limit; k++) {

                    //TaskDetails td = upmts.taskDetails.get(j);
                    if (upmts.evalQuestions.get(k).hasCorrectAnswer()) {
                        cnt++;
                        if (upmts.evalQuestions.get(k).getAnsType().equalsIgnoreCase("interface")) {
                            String givenAns = upmts.evalQuestions.get(k).getGivenAnswer();
                            if (j == 0 && cnt == 1) {
                                pws[i].print(givenAns);
                            } else if (cnt == 1) {
                                pws[i].print(" :: " + givenAns);
                            } else {
                                pws[i].print("," + givenAns);
                            }
                        } else {
                            //it is a widget
                            String ans = upmts.evalQuestions.get(k).getGivenAnswer();

                            if (j == 0 && cnt == 1) {
                                pws[i].print(ans);
                            } else if (cnt == 1) {
                                pws[i].print(" :: " + ans);
                            } else {
                                pws[i].print("," + ans);
                            }
                        }

                    }

                    if (cnt == taskSize) {

                        TaskDetails td = upmts.taskDetails.get(j);
                        if (td.hasCorrectAnswer()) {
                            j++;
                            if (j <  upmts.questionSizes.size())
                            taskSize = (Integer) upmts.questionSizes.get(j);
                            
                            cnt = 0;
                        }
                    }

                }
                pws[i].println();
            }

            //close the printWriters
            for (int i = 0; i < pws.length; i++) {
                pws[i].close();
            }
            /**
             * **************************************
             * Write the Raw Time data also to file
             * *****************************************
             */
            for (int i = 0; i < upmts.currentConditions.size(); i++) {
                filename = upmts.utils.getConditionTimeBasicFileName(upmts.currentConditions.get(i));

                String studydataurl = "users" + File.separator + userid + File.separator
                        + "studies" + File.separator + upmts.studyname + File.separator + "data";

                //String studydataurl = "studies" + File.separator + upmts.studyname + File.separator + "data";
                files[i] = new File(getServletContext().getRealPath(studydataurl + File.separator + filename));

                boolean newFile = false;

                if (!files[i].exists()) {
                    files[i].createNewFile();
                    newFile = true;
                }

                bws[i] = new BufferedWriter(new FileWriter(files[i], true));
                pws[i] = new PrintWriter(bws[i]);

                start = 0;
                taskSize = 0;
                cnt = 0;

                if (newFile) {
                    //write the headers
                    //first line will be the actual name and the second line will be the question numbers
                    upmts.currentCondition = upmts.currentConditions.get(i);

                    for (int j = 0; j < upmts.questionSizes.size(); j++) {
                        start = taskSize;
                        taskSize = upmts.questionSizes.get(j);
                        String ttype = upmts.questionCodes.get(j);
                        limit = start + taskSize;
                        cnt = 0;

                        TaskDetails td = upmts.taskDetails.get(j);
                        if (td.hasCorrectAnswer()) {
                            for (int k = start; k < limit; k++) {
                                String name = "Time_" + ttype + "_" + upmts.currentCondition;
                                if (k == 0 && start == 0) {
                                    pws[i].print(name);
                                } else if (cnt == 0) {
                                    pws[i].print(" :: " + name);
                                } else {
                                    pws[i].print(",");
                                }
                                cnt++;
                            }
                        }
                    }

                    pws[i].println();
                    //print the question headers
                    start = 0;
                    taskSize = 0;
                    cnt = 0;
                    for (int j = 0; j < upmts.questionSizes.size(); j++) {
                        start = taskSize;
                        taskSize = upmts.questionSizes.get(j);
                        limit = start + taskSize;
                        cnt = 0;

                        TaskDetails td = upmts.taskDetails.get(j);
                        if (td.hasCorrectAnswer()) {
                            for (int k = start; k < limit; k++) {
                                String name = "Q" + (cnt + 1);
                                if (k == 0 && start == 0) {
                                    pws[i].print(name);
                                } else if (cnt == 0) {
                                    pws[i].print(" :: " + name);
                                } else {
                                    pws[i].print("," + name);
                                }
                                cnt++;
                            }
                        }
                    }
                    pws[i].println();
                }
            }

            /**
             * Now write the time for each question *
             */
            start = 0;
            limit = 0;
            for (int i = 0; i < upmts.currentConditions.size(); i++) {
                start = i * upmts.sizeOfACondition;
                limit = start + upmts.sizeOfACondition;

                int j = 0;
                taskSize = upmts.questionSizes.get(j);
                cnt = 0;

                for (int k = start; k < limit; k++) {

                    if (upmts.evalQuestions.get(k).hasCorrectAnswer()) {

                        cnt++;
                        int time = upmts.evalQuestions.get(k).getTimeInSeconds();

                        if (j == 0 && cnt == 1) {
                            pws[i].print(time);
                        } else if (cnt == 1) {
                            pws[i].print(" :: " + time);
                        } else {
                            pws[i].print("," + time);
                        }

                        if (cnt == taskSize && (j+1) < upmts.questionSizes.size()) {
                            j++;                            
                            taskSize = (Integer) upmts.questionSizes.get(j);                            
                            cnt = 0;
                            
                        }
                    }
                }
                pws[i].println();
            }
            //close the printWriters
            for (int i = 0; i < pws.length; i++) {
                pws[i].close();
            }

            /**
             * **************************************
             * **************************************
             * *write the basic raw error data also to file
             * *****************************************
             * *****************************************
             * ********************************************
             */
            start = 0;
            taskSize = 0;
            cnt = 0;
            /**
             * First write the file headers *
             */
            for (int i = 0; i < upmts.currentConditions.size(); i++) {

                filename = upmts.utils.getConditionErrorBasicFileName(upmts.currentConditions.get(i));

                String studydataurl = "users" + File.separator + userid + File.separator
                        + "studies" + File.separator + upmts.studyname + File.separator + "data";

                // String studydataurl = "studies" + File.separator + upmts.studyname + File.separator + "data";
                files[i] = new File(getServletContext().getRealPath(studydataurl + File.separator + filename));

                boolean newFile = false;

                if (!files[i].exists()) {
                    files[i].createNewFile();
                    newFile = true;
                    //print the headers
                }

                bws[i] = new BufferedWriter(new FileWriter(files[i], true));
                pws[i] = new PrintWriter(bws[i]);

                if (newFile) {
                    //write the headers
                    //first line will be the actual name and the second line will be the question numbers
                    upmts.currentCondition = upmts.currentConditions.get(i);

                    start = 0;
                    taskSize = 0;
                    cnt = 0;

                    for (int j = 0; j < upmts.questionSizes.size(); j++) {
                        start = taskSize;
                        taskSize = upmts.questionSizes.get(j);
                        String ttype = upmts.questionCodes.get(j);
                        limit = start + taskSize;
                        cnt = 0;

                        for (int k = start; k < limit; k++) {
                            //ttype = taskTypes.get(m);
                            String name = "Error_" + ttype + "_" + upmts.currentCondition;
                            if (k == 0 && start == 0) {
                                pws[i].print(name);
                            } else if (cnt == 0) {
                                pws[i].print(" :: " + name);
                            } else {
                                pws[i].print(",");
                            }

                            cnt++;
                        }
                    }

                    pws[i].println();

                    //print the question headers
                    start = 0;
                    taskSize = 0;
                    cnt = 0;
                    for (int j = 0; j < upmts.questionSizes.size(); j++) {
                        start = taskSize;
                        taskSize = upmts.questionSizes.get(j);
                        limit = start + taskSize;
                        cnt = 0;
                        for (int k = start; k < limit; k++) {
                            String name = "Q" + (cnt + 1);
                            if (k == 0 && start == 0) {
                                pws[i].print(name);
                            } else if (cnt == 0) {
                                pws[i].print(" :: " + name);
                            } else {
                                pws[i].print("," + name);
                            }
                            cnt++;
                        }
                    }
                    pws[i].println();

                }
            }

            /**
             * Now write the answers given for each question *
             */
            start = 0;
            limit = 0;
            //   System.out.println("*** Size of a condition::: " + upmts.sizeOfACondition);
            for (int i = 0; i < upmts.currentConditions.size(); i++) {
                start = i * upmts.sizeOfACondition;
                limit = start + upmts.sizeOfACondition;

                int j = 0;
                taskSize = upmts.questionSizes.get(j);
                cnt = 0;

                //   System.out.println("The Start is:: " + start +"  The LIMIT IS :: " +limit);
                for (int k = start; k < limit; k++) {
                    cnt++;

                    {
                        double numOfErrors = upmts.evalQuestions.get(k).getNumberOfErrors();

                        if (j == 0 && cnt == 1) {
                            pws[i].print(numOfErrors);
                        } else if (cnt == 1) {
                            pws[i].print(" :: " + numOfErrors);
                        } else {
                            pws[i].print("," + numOfErrors);
                        }
                    }

                    if (cnt == taskSize && (j+1) < upmts.questionSizes.size()) {
                        j++;
                        taskSize = (Integer) upmts.questionSizes.get(j);
                        
                        cnt = 0;
                    }

                }
                pws[i].println();
            }

            //close the printWriters
            for (int i = 0; i < pws.length; i++) {
                pws[i].close();
            }

            /**
             * **************************************
             * **************************************
             * *write the basic raw missed data also to file
             * *****************************************
             * *****************************************
             * ********************************************
             */
            start = 0;
            taskSize = 0;
            cnt = 0;
            /**
             * First write the file headers *
             */
            for (int i = 0; i < upmts.currentConditions.size(); i++) {

                filename = upmts.utils.getConditionMissedBasicFileName(upmts.currentConditions.get(i));

                String studydataurl = "users" + File.separator + userid + File.separator
                        + "studies" + File.separator + upmts.studyname + File.separator + "data";
                //String studydataurl = "studies" + File.separator + upmts.studyname + File.separator + "data";
                files[i] = new File(getServletContext().getRealPath(studydataurl + File.separator + filename));

                boolean newFile = false;

                if (!files[i].exists()) {
                    files[i].createNewFile();
                    newFile = true;
                    //print the headers
                }

                bws[i] = new BufferedWriter(new FileWriter(files[i], true));
                pws[i] = new PrintWriter(bws[i]);

                if (newFile) {
                    //write the headers
                    //first line will be the actual name and the second line will be the question numbers
                    upmts.currentCondition = upmts.currentConditions.get(i);

                    start = 0;
                    taskSize = 0;
                    cnt = 0;

                    for (int j = 0; j < upmts.questionSizes.size(); j++) {
                        start = taskSize;
                        taskSize = upmts.questionSizes.get(j);
                        String ttype = upmts.questionCodes.get(j);
                        limit = start + taskSize;
                        cnt = 0;

                        for (int k = start; k < limit; k++) {
                            //ttype = taskTypes.get(m);
                            String name = "Missed_" + ttype + "_" + upmts.currentCondition;
                            if (k == 0 && start == 0) {
                                pws[i].print(name);
                            } else if (cnt == 0) {
                                pws[i].print(" :: " + name);
                            } else {
                                pws[i].print(",");
                            }

                            cnt++;
                        }
                    }

                    pws[i].println();

                    //print the question headers
                    start = 0;
                    taskSize = 0;
                    cnt = 0;
                    for (int j = 0; j < upmts.questionSizes.size(); j++) {
                        start = taskSize;
                        taskSize = upmts.questionSizes.get(j);
                        limit = start + taskSize;
                        cnt = 0;
                        for (int k = start; k < limit; k++) {
                            String name = "Q" + (cnt + 1);
                            if (k == 0 && start == 0) {
                                pws[i].print(name);
                            } else if (cnt == 0) {
                                pws[i].print(" :: " + name);
                            } else {
                                pws[i].print("," + name);
                            }
                            cnt++;
                        }
                    }
                    pws[i].println();

                }
            }

            /**
             * Now write the answers given for each question *
             */
            start = 0;
            limit = 0;
            //   System.out.println("*** Size of a condition::: " + upmts.sizeOfACondition);
            for (int i = 0; i < upmts.currentConditions.size(); i++) {
                start = i * upmts.sizeOfACondition;
                limit = start + upmts.sizeOfACondition;

                int j = 0;
                taskSize = upmts.questionSizes.get(j);
                cnt = 0;

                //   System.out.println("The Start is:: " + start +"  The LIMIT IS :: " +limit);
                for (int k = start; k < limit; k++) {
                    cnt++;

                    {
                        double numberMissed = upmts.evalQuestions.get(k).getNumberMissed();

                        if (j == 0 && cnt == 1) {
                            pws[i].print(numberMissed);
                        } else if (cnt == 1) {
                            pws[i].print(" :: " + numberMissed);
                        } else {
                            pws[i].print("," + numberMissed);
                        }
                    }

                    if (cnt == taskSize && (j+1) < upmts.questionSizes.size()) {
                        j++;
                        taskSize = (Integer) upmts.questionSizes.get(j);
                        
                        cnt = 0;
                    }

                }
                pws[i].println();
            }

            //close the printWriters
            for (int i = 0; i < pws.length; i++) {
                pws[i].close();
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void writeWithinStudyAnswersToFile(StudyParameters upmts, String userid) {
        try {
            int numOfConditions = upmts.orderOfConditionShortNames.size();
            //String filenames[] = new String[numOwriteWithinStudyAnswersToFilefConditions];
            File files[] = new File[numOfConditions];
            BufferedWriter bws[] = new BufferedWriter[numOfConditions];
            PrintWriter pws[] = new PrintWriter[numOfConditions];

            String filename;
            for (int i = 0; i < numOfConditions; i++) {
                filename = upmts.utils.getConditionAccuracyFileName(upmts.viewerConditionShortnames.get(i));
                String studydataurl = "users" + File.separator + userid + File.separator
                        + "studies" + File.separator + upmts.studyname + File.separator + "data";

                //String studydataurl = "studies" + File.separator + upmts.studyname + File.separator + "data";
                files[i] = new File(getServletContext().getRealPath(studydataurl + File.separator + filename));

                boolean newFile = false;

                if (!files[i].exists()) {
                    files[i].createNewFile();
                    newFile = true;
                }

                bws[i] = new BufferedWriter(new FileWriter(files[i], true));
                pws[i] = new PrintWriter(bws[i]);

                int quantCnt = 0;

                if (newFile) {
                    //write the headers
                    upmts.currentCondition = upmts.orderOfConditionShortNames.get(i);
                    for (int j = 0; j < upmts.questionCodes.size(); j++) {

                        TaskDetails td = upmts.taskDetails.get(j);
                        if (td.hasCorrectAnswer()) {
                            quantCnt++;
                            if (quantCnt == 1) {
                                pws[i].print("Acc_" + upmts.questionCodes.get(j) + "_" + upmts.currentCondition);
                            } else {
                                pws[i].print("," + "Acc_" + upmts.questionCodes.get(j) + "_" + upmts.currentCondition);
                            }
                        }
                    }
                    pws[i].println();
                }
            }

            //write the data to a file.
            int start = 0;
            int limit = 0;

            System.out.println("^^^ The number of conditions is " + numOfConditions);
            System.out.println("^^^ The size of questionSizes is " + upmts.questionSizes.size());
            System.out.println("^^^ The number of Eval questiosn is:  "+ upmts.evalQuestions.size());

            for (int i = 0; i < numOfConditions; i++) {
                start = i * upmts.sizeOfACondition;
                limit = start + upmts.sizeOfACondition;

                System.out.println("^^^ The size of a condition is " + upmts.sizeOfACondition);

                int j = 0;
                int taskSize = upmts.questionSizes.get(j);
                //j++;
                int cnt = 1;
                double numCorrect = 0;
                boolean quanttaskExist = false;
                boolean quantTaskBegin = false;
                int quantNumber = 0;
                for (int k = start; k < limit; k++) {
                    //if (upmts.evalQuestions.get(k).hasCorrectAnswer()) {                    //}
                    System.out.println("quantNumber: " + quantNumber + " :: cnt -- " + cnt
                            + " taskSize :" + taskSize);

                    System.out.println("j: " + j + "  start: " + start
                            + " limit: " + limit
                            + " curretn: " + k);

                    if (k == start) {
                        taskSize = (Integer) upmts.questionSizes.get(j);
                        j++;
                        numCorrect = 0;
                        cnt = 1;
                    }

                    //do this only if it is a quantitative task or part of a quantitative task.
                    if (upmts.evalQuestions.get(k).hasCorrectAnswer()) {
                        quantTaskBegin = true;
                        quanttaskExist = true;
                        numCorrect += upmts.evalQuestions.get(k).getIsGivenAnsCorrect();

                        if (k > start) {
                            cnt++;
                        }

                    } else {
                        quantTaskBegin = false;
                        quanttaskExist = false;
                    }

                    /* if (upmts.evalQuestions.get(k).hasCorrectAnswer()) {
                     quantTaskBegin = true;
                     } else {
                     quantTaskBegin = false;
                     }*/
                    if ((cnt == taskSize)) {
                        if (quantTaskBegin) {
                            System.out.println("in--here");

                            if (quantNumber == 0) {
                                upmts.accuracyInfo = "" + (double) numCorrect / taskSize;
                                pws[i].print((double) numCorrect / taskSize);
                            } else {
                                upmts.accuracyInfo += "," + (double) numCorrect / taskSize;
                                pws[i].print("," + (double) numCorrect / taskSize);
                            }
                            quantNumber++;
                        }

                        //check if there are still more tasks
                        if (j < (Integer) upmts.questionSizes.size()) {
                            taskSize = (Integer) upmts.questionSizes.get(j);
                            numCorrect = 0;
                            cnt = 1;
                            j++;
                        }

                        if (upmts.evalQuestions.get(k).hasCorrectAnswer()) {
                            quantTaskBegin = true;
                        } else {
                            quantTaskBegin = false;
                        }
                    }
                    //do this only if it is a quantitative task or part of a quantitative task.
                  /*  if (upmts.evalQuestions.get(k).hasCorrectAnswer()) {
                     quantTaskBegin = true;
                     quanttaskExist = true;
                     numCorrect += upmts.evalQuestions.get(k).getIsGivenAnsCorrect();
                     cnt++;

                     } else {
                     quantTaskBegin = false;
                     quanttaskExist = true;
                     }*/

                }
                /*
                 int size = upmts.evalQuestions.size();
                 if (j == 0 && quanttaskExist == true) { //only one question
                 upmts.accuracyInfo = "" + (double) numCorrect / taskSize;
                 pws[i].print((double) numCorrect / taskSize);
                 } else if (j > 0
                 && upmts.evalQuestions.get(size - 1).hasCorrectAnswer()) {

                 upmts.accuracyInfo += "," + (double) numCorrect / taskSize;
                 pws[i].print("," + (double) numCorrect / taskSize);
                 }  */
                pws[i].println();
            }
            System.out.println("Results written to file successfully!");

            //close the printWriters
            for (int i = 0; i < pws.length; i++) {
                pws[i].close();
                bws[i].close();
            }

            /**
             * *********************************************************
             * *********************************************************
             */
            /* Write the time also to file */
            for (int i = 0; i < numOfConditions; i++) {
                filename = upmts.utils.getConditionTimeFileName(upmts.orderOfConditionShortNames.get(i));

                String studydataurl = "users" + File.separator + userid + File.separator
                        + "studies" + File.separator + upmts.studyname + File.separator + "data";

                //String studydataurl = "studies" + File.separator + upmts.studyname + File.separator + "data";
                files[i] = new File(getServletContext().getRealPath(studydataurl + File.separator + filename));

                boolean newFile = false;

                if (!files[i].exists()) {
                    files[i].createNewFile();
                    newFile = true;
                }

                bws[i] = new BufferedWriter(new FileWriter(files[i], true));
                pws[i] = new PrintWriter(bws[i]);
                int quantCnt = 0;

                if (newFile) {
                    upmts.currentCondition = upmts.orderOfConditionShortNames.get(i);
                    //write the headers
                    for (int j = 0; j < upmts.questionCodes.size(); j++) {

                        TaskDetails td = upmts.taskDetails.get(j);
                        if (td.hasCorrectAnswer()) {
                            quantCnt++;

                            if (quantCnt == 1) {
                                pws[i].print("Time_" + upmts.questionCodes.get(j) + "_" + upmts.currentCondition);
                            } else {
                                pws[i].print("," + "Time_" + upmts.questionCodes.get(j) + "_" + upmts.currentCondition);
                            }
                        }

                    }
                    pws[i].println();
                }
            }

            //write the data to a file.
            start = 0;
            limit = 0;

            for (int i = 0; i < numOfConditions; i++) {
                start = i * upmts.sizeOfACondition;
                limit = start + upmts.sizeOfACondition;

                int j = 0;
                int taskSize = upmts.questionSizes.get(j);
                //j++;
                int cnt = 0;
                int totalTime = 0;
                boolean quanttaskExist = false;
                boolean quantTaskBegin = false;
                int quantNumber = 0;

                for (int k = start; k < limit; k++) {

                    // if (upmts.evalQuestions.get(k).hasCorrectAnswer()) {
                    // cnt++;
                    //}
                    if (k == start) {
                        taskSize = (Integer) upmts.questionSizes.get(j);
                        j++;
                        totalTime = 0;
                        cnt = 1;
                    }

                    if (upmts.evalQuestions.get(k).hasCorrectAnswer()) {

                        quanttaskExist = true;
                        quantTaskBegin = true;
                        totalTime += upmts.evalQuestions.get(k).getTimeInSeconds();
                        if (k > start) {
                            cnt++;
                        }
                    } else {
                        quanttaskExist = false;
                        quantTaskBegin = false;
                    }

                    if ((cnt == taskSize)) {

                        if (quantTaskBegin) {
                            if (quantNumber == 0) {
                                upmts.timeInfo = "" + (double) totalTime / taskSize;
                                pws[i].print((double) totalTime / taskSize);
                            } else {
                                upmts.timeInfo += "," + (double) totalTime / taskSize;
                                pws[i].print("," + (double) totalTime / taskSize);
                            }

                            quantNumber++;
                        }

                        if (j < (Integer) upmts.questionSizes.size()) {
                            taskSize = (Integer) upmts.questionSizes.get(j);
                            totalTime = 0;
                            cnt = 1;
                            j++;
                        }

                        if (upmts.evalQuestions.get(k).hasCorrectAnswer()) {
                            quantTaskBegin = true;
                        } else {
                            quantTaskBegin = false;
                        }

                    }

                }

                /* int size = upmts.evalQuestions.size();

                 if (j == 0 && quanttaskExist == true) { //only one question type
                 upmts.timeInfo = "" + (double) totalTime / taskSize;
                 pws[i].print((double) totalTime / taskSize);
                 } else if (j > 0
                 && upmts.evalQuestions.get(size - 1).hasCorrectAnswer()) {
                 upmts.timeInfo += "," + (double) totalTime / taskSize;
                 pws[i].print("," + (double) totalTime / taskSize);
                 }*/
                pws[i].println();
            }
            System.out.println("Results written to file successfully!");

            //close the printWriters
            for (int i = 0; i < pws.length; i++) {
                pws[i].close();
            }

            /**
             * **************************************
             * **************************************
             * *write the basic raw data also to file
             * *****************************************
             * *****************************************
             * ********************************************
             */
            start = 0;
            int taskSize = 0;
            int cnt = 0;
            /**
             * First write the file headers *
             */
            for (int i = 0; i < numOfConditions; i++) {

                filename = upmts.utils.getConditionAccuracyBasicFileName(upmts.orderOfConditionShortNames.get(i));

                String studydataurl = "users" + File.separator + userid + File.separator
                        + "studies" + File.separator + upmts.studyname + File.separator + "data";

                //String studydataurl = "studies" + File.separator + upmts.studyname + File.separator + "data";
                files[i] = new File(getServletContext().getRealPath(studydataurl + File.separator + filename));

                boolean newFile = false;

                if (!files[i].exists()) {
                    files[i].createNewFile();
                    newFile = true;
                    //print the headers
                }

                bws[i] = new BufferedWriter(new FileWriter(files[i], true));
                pws[i] = new PrintWriter(bws[i]);

                if (newFile) {
                    //write the headers
                    //first line will be the actual name and the second line will be the question numbers
                    upmts.currentCondition = upmts.orderOfConditionShortNames.get(i);

                    start = 0;
                    taskSize = 0;
                    cnt = 0;

                    for (int j = 0; j < upmts.questionSizes.size(); j++) {
                        start = taskSize;
                        taskSize = upmts.questionSizes.get(j);
                        String ttype = upmts.questionCodes.get(j);
                        limit = start + taskSize;
                        cnt = 0;

                        TaskDetails td = upmts.taskDetails.get(j);
                        if (td.hasCorrectAnswer()) {
                            for (int k = start; k < limit; k++) {
                                //ttype = taskTypes.get(m);
                                String name = "Acc_" + ttype + "_" + upmts.currentCondition;
                                if (k == 0 && start == 0) {
                                    pws[i].print(name);
                                } else if (cnt == 0) {
                                    pws[i].print(" :: " + name);
                                } else {
                                    pws[i].print(",");
                                }

                                cnt++;
                            }
                        }
                    }

                    pws[i].println();

                    //print the question headers
                    start = 0;
                    taskSize = 0;
                    cnt = 0;
                    for (int j = 0; j < upmts.questionSizes.size(); j++) {
                        start = taskSize;
                        taskSize = upmts.questionSizes.get(j);
                        limit = start + taskSize;
                        cnt = 0;

                        TaskDetails td = upmts.taskDetails.get(j);
                        if (td.hasCorrectAnswer()) {
                            //it is a quatitative
                            for (int k = start; k < limit; k++) {
                                String name = "Q" + (cnt + 1);
                                if (k == 0 && start == 0) {
                                    pws[i].print(name);
                                } else if (cnt == 0) {
                                    pws[i].print(" :: " + name);
                                } else {
                                    pws[i].print("," + name);
                                }
                                cnt++;
                            }
                        }
                    }
                    pws[i].println();

                }
            }

            /**
             * Now write the answers given for each question *
             */
            start = 0;
            limit = 0;
            System.out.println("*** Size of a condition::: " + upmts.sizeOfACondition);
            for (int i = 0; i < numOfConditions; i++) {
                start = i * upmts.sizeOfACondition;
                limit = start + upmts.sizeOfACondition;

                int j = 0;
                taskSize = upmts.questionSizes.get(j);
                cnt = 0;

                //   System.out.println("The Start is:: " + start +"  The LIMIT IS :: " +limit);
                for (int k = start; k < limit; k++) {

                    //TaskDetails td = upmts.taskDetails.get(j);
                    if (upmts.evalQuestions.get(k).hasCorrectAnswer()) {
                        cnt++;
                        
                        String ans = upmts.evalQuestions.get(k).getGivenAnswer();

                        if (j == 0 && cnt == 1) {
                            pws[i].print(ans);
                        } else if (cnt == 1) {
                            pws[i].print(" :: " + ans);
                        } else {
                            pws[i].print("," + ans);
                        }

                    }
                    
                     if (cnt == taskSize && (j+1) < upmts.questionSizes.size()) {
                            j++;
                            taskSize = (Integer) upmts.questionSizes.get(j);
                            
                            cnt = 0;
                    }


                }
                pws[i].println();
            }

            //close the printWriters
            for (int i = 0; i < pws.length; i++) {
                pws[i].close();
            }
            /**
             * **************************************
             * Write the Raw Time data also to file
             * *****************************************
             */
            for (int i = 0; i < numOfConditions; i++) {
                filename = upmts.utils.getConditionTimeBasicFileName(upmts.orderOfConditionShortNames.get(i));

                String studydataurl = "users" + File.separator + userid + File.separator
                        + "studies" + File.separator + upmts.studyname + File.separator + "data";

                //String studydataurl = "studies" + File.separator + upmts.studyname + File.separator + "data";
                files[i] = new File(getServletContext().getRealPath(studydataurl + File.separator + filename));

                boolean newFile = false;

                if (!files[i].exists()) {
                    files[i].createNewFile();
                    newFile = true;
                }

                bws[i] = new BufferedWriter(new FileWriter(files[i], true));
                pws[i] = new PrintWriter(bws[i]);

                start = 0;
                taskSize = 0;
                cnt = 0;

                if (newFile) {
                    //write the headers
                    //first line will be the actual name and the second line will be the question numbers
                    upmts.currentCondition = upmts.orderOfConditionShortNames.get(i);

                    for (int j = 0; j < upmts.questionSizes.size(); j++) {
                        start = taskSize;
                        taskSize = upmts.questionSizes.get(j);
                        String ttype = upmts.questionCodes.get(j);
                        limit = start + taskSize;
                        cnt = 0;

                        TaskDetails td = upmts.taskDetails.get(j);
                        if (td.hasCorrectAnswer()) {
                            for (int k = start; k < limit; k++) {
                                String name = "Time_" + ttype + "_" + upmts.currentCondition;
                                if (k == 0 && start == 0) {
                                    pws[i].print(name);
                                } else if (cnt == 0) {
                                    pws[i].print(" :: " + name);
                                } else {
                                    pws[i].print(",");
                                }
                                cnt++;
                            }
                        }
                    }

                    pws[i].println();
                    //print the question headers
                    start = 0;
                    taskSize = 0;
                    cnt = 0;
                    for (int j = 0; j < upmts.questionSizes.size(); j++) {
                        start = taskSize;
                        taskSize = upmts.questionSizes.get(j);
                        limit = start + taskSize;
                        cnt = 0;

                        TaskDetails td = upmts.taskDetails.get(j);
                        if (td.hasCorrectAnswer()) {
                            for (int k = start; k < limit; k++) {
                                String name = "Q" + (cnt + 1);
                                if (k == 0 && start == 0) {
                                    pws[i].print(name);
                                } else if (cnt == 0) {
                                    pws[i].print(" :: " + name);
                                } else {
                                    pws[i].print("," + name);
                                }
                                cnt++;
                            }
                        }
                    }
                    pws[i].println();
                }
            }

            /**
             * Now write the time for each question *
             */
            start = 0;
            limit = 0;
            for (int i = 0; i < numOfConditions; i++) {
                start = i * upmts.sizeOfACondition;
                limit = start + upmts.sizeOfACondition;

                int j = 0;
                taskSize = upmts.questionSizes.get(j);
                cnt = 0;

                for (int k = start; k < limit; k++) {

                    if (upmts.evalQuestions.get(k).hasCorrectAnswer()) {

                        cnt++;
                        int time = upmts.evalQuestions.get(k).getTimeInSeconds();

                        if (j == 0 && cnt == 1) {
                            pws[i].print(time);
                        } else if (cnt == 1) {
                            pws[i].print(" :: " + time);
                        } else {
                            pws[i].print("," + time);
                        }

                        if (cnt == taskSize && (j+1) < upmts.questionSizes.size()) {
                            j++;
                            taskSize = (Integer) upmts.questionSizes.get(j);
                            
                            cnt = 0;
                        }
                    }
                }
                pws[i].println();
            }
            //close the printWriters
            for (int i = 0; i < pws.length; i++) {
                pws[i].close();
            }

            /**
             * **************************************
             * **************************************
             * *write the basic raw error data also to file
             * *****************************************
             * *****************************************
             * ********************************************
             */
            start = 0;
            taskSize = 0;
            cnt = 0;
            /**
             * First write the file headers *
             */
            for (int i = 0; i < numOfConditions; i++) {

                filename = upmts.utils.getConditionErrorBasicFileName(upmts.orderOfConditionShortNames.get(i));

                String studydataurl = "users" + File.separator + userid + File.separator
                        + "studies" + File.separator + upmts.studyname + File.separator + "data";

                // String studydataurl = "studies" + File.separator + upmts.studyname + File.separator + "data";
                files[i] = new File(getServletContext().getRealPath(studydataurl + File.separator + filename));

                boolean newFile = false;

                if (!files[i].exists()) {
                    files[i].createNewFile();
                    newFile = true;
                    //print the headers
                }

                bws[i] = new BufferedWriter(new FileWriter(files[i], true));
                pws[i] = new PrintWriter(bws[i]);

                if (newFile) {
                    //write the headers
                    //first line will be the actual name and the second line will be the question numbers
                    upmts.currentCondition = upmts.orderOfConditionShortNames.get(i);

                    start = 0;
                    taskSize = 0;
                    cnt = 0;

                    for (int j = 0; j < upmts.questionSizes.size(); j++) {
                        start = taskSize;
                        taskSize = upmts.questionSizes.get(j);
                        String ttype = upmts.questionCodes.get(j);
                        limit = start + taskSize;
                        cnt = 0;

                        for (int k = start; k < limit; k++) {
                            //ttype = taskTypes.get(m);
                            String name = "Error_" + ttype + "_" + upmts.currentCondition;
                            if (k == 0 && start == 0) {
                                pws[i].print(name);
                            } else if (cnt == 0) {
                                pws[i].print(" :: " + name);
                            } else {
                                pws[i].print(",");
                            }

                            cnt++;
                        }
                    }

                    pws[i].println();

                    //print the question headers
                    start = 0;
                    taskSize = 0;
                    cnt = 0;
                    for (int j = 0; j < upmts.questionSizes.size(); j++) {
                        start = taskSize;
                        taskSize = upmts.questionSizes.get(j);
                        limit = start + taskSize;
                        cnt = 0;
                        for (int k = start; k < limit; k++) {
                            String name = "Q" + (cnt + 1);
                            if (k == 0 && start == 0) {
                                pws[i].print(name);
                            } else if (cnt == 0) {
                                pws[i].print(" :: " + name);
                            } else {
                                pws[i].print("," + name);
                            }
                            cnt++;
                        }
                    }
                    pws[i].println();

                }
            }

            /**
             * Now write the answers given for each question *
             */
            start = 0;
            limit = 0;
            //   System.out.println("*** Size of a condition::: " + upmts.sizeOfACondition);
            for (int i = 0; i < numOfConditions; i++) {
                start = i * upmts.sizeOfACondition;
                limit = start + upmts.sizeOfACondition;

                int j = 0;
                taskSize = upmts.questionSizes.get(j);
                cnt = 0;

                //   System.out.println("The Start is:: " + start +"  The LIMIT IS :: " +limit);
                for (int k = start; k < limit; k++) {
                    cnt++;

                    {
                        double numOfErrors = upmts.evalQuestions.get(k).getNumberOfErrors();

                        if (j == 0 && cnt == 1) {
                            pws[i].print(numOfErrors);
                        } else if (cnt == 1) {
                            pws[i].print(" :: " + numOfErrors);
                        } else {
                            pws[i].print("," + numOfErrors);
                        }
                    }

                    if (cnt == taskSize && (j+1) < upmts.questionSizes.size()) {
                        j++;
                        taskSize = (Integer) upmts.questionSizes.get(j);
                        
                        cnt = 0;
                    }

                }
                pws[i].println();
            }

            //close the printWriters
            for (int i = 0; i < pws.length; i++) {
                pws[i].close();
            }

            /**
             * **************************************
             * **************************************
             * *write the basic raw missed data also to file
             * *****************************************
             * *****************************************
             * ********************************************
             */
            start = 0;
            taskSize = 0;
            cnt = 0;
            /**
             * First write the file headers *
             */
            for (int i = 0; i < numOfConditions; i++) {

                filename = upmts.utils.getConditionMissedBasicFileName(upmts.orderOfConditionShortNames.get(i));

                String studydataurl = "users" + File.separator + userid + File.separator
                        + "studies" + File.separator + upmts.studyname + File.separator + "data";
                //String studydataurl = "studies" + File.separator + upmts.studyname + File.separator + "data";
                files[i] = new File(getServletContext().getRealPath(studydataurl + File.separator + filename));

                boolean newFile = false;

                if (!files[i].exists()) {
                    files[i].createNewFile();
                    newFile = true;
                    //print the headers
                }

                bws[i] = new BufferedWriter(new FileWriter(files[i], true));
                pws[i] = new PrintWriter(bws[i]);

                if (newFile) {
                    //write the headers
                    //first line will be the actual name and the second line will be the question numbers
                    upmts.currentCondition = upmts.orderOfConditionShortNames.get(i);

                    start = 0;
                    taskSize = 0;
                    cnt = 0;

                    for (int j = 0; j < upmts.questionSizes.size(); j++) {
                        start = taskSize;
                        taskSize = upmts.questionSizes.get(j);
                        String ttype = upmts.questionCodes.get(j);
                        limit = start + taskSize;
                        cnt = 0;

                        for (int k = start; k < limit; k++) {
                            //ttype = taskTypes.get(m);
                            String name = "Missed_" + ttype + "_" + upmts.currentCondition;
                            if (k == 0 && start == 0) {
                                pws[i].print(name);
                            } else if (cnt == 0) {
                                pws[i].print(" :: " + name);
                            } else {
                                pws[i].print(",");
                            }

                            cnt++;
                        }
                    }

                    pws[i].println();

                    //print the question headers
                    start = 0;
                    taskSize = 0;
                    cnt = 0;
                    for (int j = 0; j < upmts.questionSizes.size(); j++) {
                        start = taskSize;
                        taskSize = upmts.questionSizes.get(j);
                        limit = start + taskSize;
                        cnt = 0;
                        for (int k = start; k < limit; k++) {
                            String name = "Q" + (cnt + 1);
                            if (k == 0 && start == 0) {
                                pws[i].print(name);
                            } else if (cnt == 0) {
                                pws[i].print(" :: " + name);
                            } else {
                                pws[i].print("," + name);
                            }
                            cnt++;
                        }
                    }
                    pws[i].println();

                }
            }

            /**
             * Now write the answers given for each question *
             */
            start = 0;
            limit = 0;
            //   System.out.println("*** Size of a condition::: " + upmts.sizeOfACondition);
            for (int i = 0; i < numOfConditions; i++) {
                start = i * upmts.sizeOfACondition;
                limit = start + upmts.sizeOfACondition;

                int j = 0;
                taskSize = upmts.questionSizes.get(j);
                cnt = 0;

                //   System.out.println("The Start is:: " + start +"  The LIMIT IS :: " +limit);
                for (int k = start; k < limit; k++) {
                    cnt++;

                    {
                        double numberMissed = upmts.evalQuestions.get(k).getNumberMissed();

                        if (j == 0 && cnt == 1) {
                            pws[i].print(numberMissed);
                        } else if (cnt == 1) {
                            pws[i].print(" :: " + numberMissed);
                        } else {
                            pws[i].print("," + numberMissed);
                        }
                    }

                    if (cnt == taskSize && (j+1) < upmts.questionSizes.size()) {
                        j++;
                        taskSize = (Integer) upmts.questionSizes.get(j);
                        
                        cnt = 0;
                    }

                }
                pws[i].println();
            }

            //close the printWriters
            for (int i = 0; i < pws.length; i++) {
                pws[i].close();
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void writeFirstConditionToFile(StudyParameters upmts, String userid) {
        try {
            String filename = "firstConditions.txt";

            String studydataurl = "users" + File.separator + userid + File.separator
                    + "studies" + File.separator + upmts.studyname + File.separator + "data";

            //String studydataurl = "studies" + File.separator + upmts.studyname + File.separator + "data";
            File file = new File(getServletContext().getRealPath(studydataurl + File.separator + filename));
            // boolean newFile_acc = false;
            if (!file.exists()) {
                file.createNewFile();
            }
            FileWriter fw1 = new FileWriter(file, true);
            BufferedWriter bw1 = new BufferedWriter(fw1);
            PrintWriter pw1 = new PrintWriter(bw1);

            pw1.println(upmts.firstConditionShortName);

            pw1.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    /**
     * This function returns the url of the server from the request (*without
     * the name of this servlet*).
     *
     * @param request
     * @return
     */
    public String getServerUrl(HttpServletRequest request) {
        String uri = request.getScheme() + "://" + // "http" + "://
                request.getServerName() + // "myhost"
                ":" + // ":"
                request.getServerPort() + // "8080"
                request.getRequestURI();//+       // "/people"

        int lastbackslash = uri.lastIndexOf("/");
        return uri.substring(0, lastbackslash);
    }

}
