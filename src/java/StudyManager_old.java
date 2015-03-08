
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import userstudy.EvaluationQuestion;
import userstudy.MyUtils;

/**
 *
 * @author Mershack
 */
@WebServlet(urlPatterns = {"/StudyManager_old"})
public class StudyManager_old extends HttpServlet {

    private final String DATA_DIR = "data";

    private final String QUANT_QNS_FILENAME = "quantitativeQuestions.txt";
    private final String TASKS_NODES_FILENAME = "taskNodesIndexes.txt";//"tasksNodes.txt";
    String studyType = "Between";  //"Within"
    // String studyType = "Within";  //
    boolean firstTime = true;

    ArrayList<String> questions = new ArrayList<String>();
    ArrayList<Integer> questionSizes = new ArrayList<Integer>();
    ArrayList<String> questionCodes = new ArrayList<String>();
    ArrayList<Integer> questionMaxTimes = new ArrayList<Integer>();

    ArrayList<String> taskTypes = new ArrayList<String>();
    ArrayList<String> orderOfConditions = new ArrayList<String>();
    String currentCondition = "cond1";

    ArrayList<EvaluationQuestion> evalQuestions = new ArrayList<EvaluationQuestion>();
    ArrayList<EvaluationQuestion> tutorialQuestions = new ArrayList<EvaluationQuestion>();
    int testCounter = 0;
    int tutorialCounter = 0;
    boolean isTutorial = true;
    String turkCode = "AEJOUK1";
    boolean viewersChanged = false;
    int numberOfConditions = 2;
    int numberOfTasks = 2;
    int sizeOfACondition;
    public int tutorialSize = 2;
    MyUtils utils = new MyUtils();

//    /ArrayList<String> taskNodes = new ArrayList<String>();
    HashMap<Integer, ArrayList<String>> allTaskNodes = new HashMap<Integer, ArrayList<String>>(); //this hashmap will be used
    HashMap<Integer, String> allQuestions = new HashMap<Integer, String>();
    String instruction = "This is the instruction..and some other random strings i just added ania oianod oiadoioa doiaon odoanod donaoieoi dioaoneoa.";

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
            String msg = "Finished";
            String command = request.getParameter("command");

            if (command.equalsIgnoreCase("instruction")) {
                loadStudyDetails();  //load the study details
                viewersChanged = false;
                testCounter = 0;
                tutorialCounter = 0;
                msg = getInstruction(); //get the instruction
            } else if (command.equalsIgnoreCase("getQuestion")) {

                if (tutorialCounter < tutorialQuestions.size()) {
                    isTutorial = true;
                    tutorialCounter++;
                    msg = "Trial Question (" + tutorialCounter + "/" + tutorialQuestions.size() + ")"
                            + ":: " + tutorialQuestions.get(tutorialCounter - 1).getQuestion();
                    msg += "::" + tutorialQuestions.get(tutorialCounter - 1).getMaxTimeInSeconds();  //add the time also
                } else if (studyType.equalsIgnoreCase("Within") && (testCounter > 0 && testCounter < evalQuestions.size())
                        && ((testCounter % sizeOfACondition) == 0) && !viewersChanged) {
                    msg = "ChangeViewers:: ";
                    viewersChanged = true;
                } else if (testCounter < evalQuestions.size()) {
                    if (testCounter > 0) {//get the previousAnswer
                        String prevAnswer = request.getParameter("previousAnswer");
                        String prevTime = request.getParameter("previousTime");

                        System.out.println("Previous Time is ::: " + prevTime);

                        int previousTime = Integer.parseInt(prevTime);

                        evalQuestions.get(testCounter - 1).setIsGivenAnsCorrect(prevAnswer.trim());
                        evalQuestions.get(testCounter - 1).setTimeInSeconds(previousTime);

                        //System.out.println("==>Previous Answer is :::: " + prevAnswer);
                    }
                    isTutorial = false;
                    testCounter++;
                    viewersChanged = false;
                    msg = "Study Question (" + testCounter + "/" + evalQuestions.size() + ")";
                    msg += "::  " + evalQuestions.get(testCounter - 1).getQuestion(); //the question
                    msg += "::" + evalQuestions.get(testCounter - 1).getMaxTimeInSeconds();  //add the time also
                } else {
                    String prevAnswer = request.getParameter("previousAnswer");
                    String prevTime = request.getParameter("previousTime");

                    System.out.println("Previous Time is ::: " + prevTime);
                    int previousTime = Integer.parseInt(prevTime);

                    evalQuestions.get(testCounter - 1).setIsGivenAnsCorrect(prevAnswer.trim());
                    evalQuestions.get(testCounter - 1).setTimeInSeconds(previousTime);

                    msg = "Finished::" + turkCode;
                    //print the answers to see if the answers are correct.
                    // printEvaluationAnswers();

                    System.out.println("Study Finished");
                    writeAnswersToFile();
                }

            } else if (command.equalsIgnoreCase("getNodes")) {
                //get the nodes for that question as string.      
                if (isTutorial) {
                    //System.out.println("**** "+ (tutorialCounter - 1));
                    msg = tutorialQuestions.get(tutorialCounter - 1).getNodesAsString();
                } else {
                    msg = evalQuestions.get(testCounter - 1).getNodesAsString();
                }
            } else if (command.equalsIgnoreCase("getAnswerControllers")) {
                if (isTutorial) {
                    //System.out.println("**** "+ (tutorialCounter - 1));
                    msg = tutorialQuestions.get(tutorialCounter - 1).getAnsOptionsAsString();
                } else {
                    msg = evalQuestions.get(testCounter - 1).getAnsOptionsAsString();
                }
            } else if (command.equalsIgnoreCase("checkIsTutorial")) {
                if (tutorialCounter < tutorialQuestions.size()) {
                    msg = "true";
                } else {
                    msg = "false";
                }
            } else if (command.equalsIgnoreCase("checkAnswer")) {
                //check if the given answer is right, return "Correct" if right or "Wrong" if wrong
                String givenAns = request.getParameter("givenAnswer").trim();
                String correctAns = tutorialQuestions.get(tutorialCounter - 1).getCorrectAns(); //NB: check answer is only for tutorials

                if (givenAns.equalsIgnoreCase(correctAns)) {
                    msg = "Correct!";
                } else {
                    msg = "Wrong";
                }

                // System.out.println("The correctness message is ::: "+ msg);
            }

            out = response.getWriter();
            out.write(msg);
            out.flush();
            out.close();
        } finally {
            out.close();
        }
    }

    public void printEvaluationAnswers() {

        System.out.println("Evaluation Answers");
        for (int i = 0; i < evalQuestions.size(); i++) {
            System.out.println(":::" + evalQuestions.get(i).getIsGivenAnsCorrect());
        }
    }

    public String getInstruction() {

        String instruction = "Instruction::In this study there are " + taskTypes.size() + " types of questions.\n\n"
                + "You will be given a simple trial  involving " + tutorialSize + " questions of each type. "
                + "You can check whether your chosen answer is correct or not during the trial session.\n\n"
                + "There are " + evalQuestions.size() + " questions in total for the  main study";

        return instruction;
    }

    public void loadStudyDetails() {
        //load study type file
        //load quantitative questions file        
        questionCodes = new ArrayList<String>();
        questions = new ArrayList<String>();
        questionSizes = new ArrayList<Integer>();
        questionMaxTimes = new ArrayList<Integer>();

        evalQuestions = new ArrayList<EvaluationQuestion>();
        tutorialQuestions = new ArrayList<EvaluationQuestion>();
        taskTypes = new ArrayList<String>();

        try {
            //load information about the quantitative questions
            File fileNameFile = new File(getServletContext().getRealPath(DATA_DIR + File.separator + QUANT_QNS_FILENAME));

            if (fileNameFile.exists()) { //read the task details 
                BufferedReader br = new BufferedReader(new FileReader(fileNameFile));
                String line = "";
                while ((line = br.readLine()) != null) {
                    String[] split = line.split("::");  //an example of a line is of the format:  are the highlighted nodes connected?? :: 3                    
                    questionCodes.add(split[0]);   //the question code
                    questions.add(split[1]);   // the question
                    questionSizes.add(Integer.parseInt(split[2].trim())); // the size of the question
                    questionMaxTimes.add(Integer.parseInt(split[3].trim()));
                }
                br.close();
            } else {
                System.out.println("FILE FOR QUANTITATIVE QUESTIONS NOT FOUND");
            }

            //Load Quantitative questions file
            File quantsFile = new File(getServletContext().getRealPath(DATA_DIR + File.separator + TASKS_NODES_FILENAME));
            BufferedReader br = new BufferedReader(new FileReader(quantsFile));
            String line = " ";
            String split[];

            int cnt = 0;

            String taskcode = "";

            ArrayList<String> lines = new ArrayList<String>();
            boolean firstTaskType = true;
            while ((line = br.readLine()) != null) {
                split = line.split(":");
                if (split[0].startsWith("#task")) {
                    if (!firstTaskType) {
                        setTasksForTaskType(taskcode, lines);
                    }
                    lines = new ArrayList<String>();
                    taskcode = split[1].trim();
                    firstTaskType = false;
                    continue;
                }
                lines.add(line);
            }
            setTasksForTaskType(taskcode, lines);
            br.close();

            //get the studytype from File
            String studyTypeFilename = "expType.txt";

            File studyTypeFile = new File(getServletContext().getRealPath(DATA_DIR + File.separator + studyTypeFilename));
            BufferedReader br2 = new BufferedReader(new FileReader(studyTypeFile));
            String line2 = "";
            while ((line2 = br2.readLine()) != null) {
                if (!line2.isEmpty()) {
                    studyType = line2.trim();
                }
            }

            br2.close();

            //adjust the tasks for a within user-study
            if (studyType.equalsIgnoreCase("Within")) {
                adjustTasksForWithinStudy();
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void adjustTasksForWithinStudy() {
        //we will double/triple/etc the testtasks, i.e. add the same tasks to the end of the list for each of the conditions.
        int length = evalQuestions.size();

        sizeOfACondition = length;

        //we will be having the same question for all the conditions
        for (int k = 0; k < numberOfConditions - 1; k++) {
            for (int i = 0; i < length; i++) {
                EvaluationQuestion evq = evalQuestions.get(i);
                EvaluationQuestion evalQ = new EvaluationQuestion(evq.getQuestion(), evq.getCorrectAns(), evq.getNodes(),
                        evq.getAnsOptions(), evq.getAnsType(), evq.getMaxTimeInSeconds());

                evalQuestions.add(evalQ);
            }
        }
    }

    public boolean isPartOfCurrentTasks(String qcode) {
        boolean ispart = false;

        for (int i = 0; i < questionCodes.size(); i++) {
            if (questionCodes.get(i).equalsIgnoreCase(qcode)) {
                //System.out.println(qcode);
                ispart = true;
                break;
            }
        }
        return ispart;
    }

    public void setTasksForTaskType(String taskType, ArrayList lines) {
        if (isPartOfCurrentTasks(taskType)) {
            taskTypes.add(taskType);

            if (taskType.equalsIgnoreCase("neighbor")) {
                setNeighborTasks(lines);
            } else if (taskType.equalsIgnoreCase("path_three_nodes")) {
                setPathThreeNodesTasks(lines);
            }
        }

        // printAllNodesInvolved();
    }

    public void printAllNodesInvolved() {
        for (EvaluationQuestion eq : evalQuestions) {
            ArrayList<String> nodes = eq.getNodes();

            for (String str : nodes) {
                System.out.print(str + ", ");
            }
            System.out.println(eq.getCorrectAns());
        }
    }

    public void setNeighborTasks(ArrayList<String> lines) {
        // System.out.println(lines);        
        String taskCode = "neighbor";
        String ansType = "options";
        ArrayList<String> ansOptions = new ArrayList<String>();
        ansOptions.add("Yes");
        ansOptions.add("No");

        //get the taskSize, and question
        int size = 0;
        String question = "";
        int maxTime = 0;
        for (int i = 0; i < questionCodes.size(); i++) {
            if (questionCodes.get(i).equalsIgnoreCase(taskCode)) {
                size = questionSizes.get(i);
                question = questions.get(i);
                maxTime = questionMaxTimes.get(i);
                break;
            }
        }
        int count = 0;
        int testCount = 0;

        ArrayList<String> nodes = new ArrayList<String>();
        String correctAns = "";

        for (int i = 0; i < lines.size(); i++) {
            String split[] = lines.get(i).split("\t");
            nodes = new ArrayList<String>();
            nodes.add(split[0]);
            nodes.add(split[1]);
            correctAns = split[2].trim();

            EvaluationQuestion evalQn = new EvaluationQuestion(question, correctAns, nodes, ansOptions, ansType, maxTime);

            count++;
            if (count <= tutorialSize) {  //set the tutorial questions
                tutorialQuestions.add(evalQn);
            } else { //set the test questions
                testCount++;
                evalQuestions.add(evalQn);
            }

            if (testCount == size) { //break when you get to the size for the tasktype
                break;
            }
        }
    }

    public void setPathThreeNodesTasks(ArrayList<String> lines) {
        String taskCode = "path_three_nodes";
        String ansType = "options";
        ArrayList<String> ansOptions = new ArrayList<String>();
        ansOptions.add("Yes");
        ansOptions.add("No");

        //get the taskSize, and question
        int size = 0;
        String question = "";
        int maxTime = 0;
        for (int i = 0; i < questionCodes.size(); i++) {
            if (questionCodes.get(i).equalsIgnoreCase(taskCode)) {
                size = questionSizes.get(i);
                question = questions.get(i);
                maxTime = questionMaxTimes.get(i);

                break;
            }
        }
        int count = 0;
        int testCount = 0;

        ArrayList<String> nodes = new ArrayList<String>();
        String correctAns = "";

        for (int i = 0; i < lines.size(); i++) {
            String split[] = lines.get(i).split("\t");
            nodes = new ArrayList<String>();
            //get the three nodes and the correct ans in the order
            nodes.add(split[0]);
            nodes.add(split[1]);
            nodes.add(split[2]);
            correctAns = split[3].trim();

            EvaluationQuestion evalQn = new EvaluationQuestion(question, correctAns, nodes, ansOptions, ansType, maxTime);

            count++;
            if (count <= tutorialSize) {  //set the tutorial questions
                tutorialQuestions.add(evalQn);
            } else { //set the test questions
                testCount++;
                evalQuestions.add(evalQn);
            }

            if (testCount == size) { //break when you get to the size for the tasktype
                break;
            }

        }

    }

    public void setQuantitativeQuestions() {
        //first load the questions
        for (int i = 0; i < questions.size(); i++) {

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

    public void writeAnswersToFile() {
        try {
            //String cond1_resultsFile = "condition1_results.txt";
            //String cond2_resultsFile = "condition2_results.txt";

            System.out.println("Writing Results to File");

            orderOfConditions = new ArrayList<String>();

            orderOfConditions.add("cond1");
            orderOfConditions.add("cond2");

            String filename = "";

            System.out.println("The StudyType is ***" + studyType + "*");

            if (studyType.equalsIgnoreCase("Between")) {
                currentCondition = "cond1";
                writeBetweenStudyAnwsersToFile();
            } else if (studyType.equalsIgnoreCase("Within")) {
                writeWithinStudyAnswersToFile();
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void writeBetweenStudyAnwsersToFile() {
        //write a between study results to file
        try {
            System.out.println("Writing Between Study Results");

            String filename_accuracy = utils.getConditionAccuracyFileName(currentCondition);

            File resultFile_accuracy = new File(getServletContext().getRealPath(DATA_DIR + File.separator + filename_accuracy));
            boolean newFile_acc = false;
            if (!resultFile_accuracy.exists()) {
                resultFile_accuracy.createNewFile();
                newFile_acc = true;
            }
            //do the actual writings of the results to the file
            FileWriter fw1 = new FileWriter(resultFile_accuracy, true);
            BufferedWriter bw1 = new BufferedWriter(fw1);

            PrintWriter pw1 = new PrintWriter(bw1);

            if (newFile_acc) {
                //write the headers
                pw1.print("Acc_" + taskTypes.get(0) + "_" + currentCondition);

                for (int i = 1; i < taskTypes.size(); i++) {
                    pw1.print("," + "Acc_" + taskTypes.get(i) + "_" + currentCondition);
                }
                pw1.println();
            }
            int j = 0;
            int taskSize = questionSizes.get(j);
            int cnt = 0;
            int numCorrect = 0;

            for (int i = 0; i < evalQuestions.size(); i++) {
                cnt++;
                if (!(cnt <= taskSize)) {
                    // taskCorrectness.add(cnt);
                    if (j == 0) {
                        pw1.print((double) numCorrect / taskSize);
                    } else {
                        pw1.print("," + (double) numCorrect / taskSize);
                    }
                    taskSize = (Integer) questionSizes.get(j);
                    j++;
                    cnt = 0;
                    numCorrect = 0;
                }
                if (evalQuestions.get(i).getIsGivenAnsCorrect()) {
                    numCorrect++;
                }
            }
            pw1.print("," + (double) numCorrect / taskSize);
            pw1.println();
            pw1.close();

            /*Write time to file */
            String filename_time = utils.getConditionTimeFileName(currentCondition);

            File resultFile_time = new File(getServletContext().getRealPath(DATA_DIR + File.separator + filename_time));
            boolean newFile_time = false;
            if (!resultFile_time.exists()) {
                resultFile_time.createNewFile();
                newFile_time = true;
            }

            FileWriter fw2 = new FileWriter(resultFile_time, true);
            BufferedWriter bw2 = new BufferedWriter(fw2);

            PrintWriter pw2 = new PrintWriter(bw2);

            if (newFile_time) {
                //write the headers
                pw2.print("Time_" + taskTypes.get(0) + "_" + currentCondition);
                System.out.print("Time_" + taskTypes.get(0) + "_" + currentCondition);

                for (int i = 1; i < taskTypes.size(); i++) {
                    pw2.print("," + "Time_" + taskTypes.get(i) + "_" + currentCondition);
                    System.out.print("," + "Time_" + taskTypes.get(i) + "_" + currentCondition);
                }
                pw2.println();
            }
            int k = 0;
            int taskSize_time = questionSizes.get(k);
            int cnt2 = 0;
            int totalTime = 0;

            for (int i = 0; i < evalQuestions.size(); i++) {
                cnt2++;
                if (!(cnt2 <= taskSize_time)) {
                    if (k == 0) {
                        pw2.print((double) totalTime / taskSize_time);

                        System.out.print((double) totalTime / taskSize_time);
                    } else {
                        pw2.print("," + (double) totalTime / taskSize_time);
                        System.out.print("," + (double) totalTime / taskSize_time);
                    }
                    taskSize_time = (Integer) questionSizes.get(k);
                    k++;
                    cnt2 = 0;
                    totalTime = 0;
                }

                totalTime += evalQuestions.get(i).getTimeInSeconds();
            }
            pw2.print("," + (double) totalTime / taskSize_time);
            System.out.print("," + (double) totalTime / taskSize_time);
            pw2.println();
            pw2.close();

            /**
             * * Write the basic raw data also to file  Starting with the Accuracy
             *
             */
            int start = 0;
            taskSize = 0;
            /**
             * First write the file headers *
             */
            String filename_bacc = utils.getConditionAccuracyBasicFileName(currentCondition);
            File file_bacc = new File(getServletContext().getRealPath(DATA_DIR + File.separator + filename_bacc));

            boolean newFile = false;

            if (!file_bacc.exists()) {
                file_bacc.createNewFile();
                newFile = true;
                //print the headers
            }

            BufferedWriter bw_bacc = new BufferedWriter(new FileWriter(file_bacc, true));
            PrintWriter pw_bacc = new PrintWriter(bw_bacc);

            if (newFile) {
                start = 0;
                taskSize = 0;
                for (int m = 0; m < taskTypes.size(); m++) {
                    start = taskSize;
                    taskSize = questionSizes.get(m);
                    int limit = start + taskSize;
                    cnt = 0;
                    
                    for (int n = start; n < limit; n++) {
                        
                        String name = "Acc_" + taskTypes.get(m) + "_" + currentCondition;
                        
                        if (n == 0 && start == 0) {
                            pw_bacc.print(name);
                        } else if (cnt == 0) {
                            pw_bacc.print(" :: " + name);
                        } else {
                            pw_bacc.print(",");
                        }
                        cnt++;
                    }
                }
                pw_bacc.println();
                
                /**
                 * *************************************************
                 */
                //The question headers i.e. Q1, Q2, etc.
                start = 0;
                taskSize = 0;
                for (int m = 0; m < taskTypes.size(); m++) {
                    start = taskSize;
                    taskSize = questionSizes.get(m);
                    int limit = start + taskSize;
                    cnt = 0;
                    for (int n = start; n < limit; n++) {
                        String name = "Q" + (cnt + 1);
                        if (n == 0 && start == 0) {
                            pw_bacc.print(name);
                        } else if (cnt == 0) {
                            pw_bacc.print(" :: " + name);
                        } else {
                            pw_bacc.print("," + name);
                        }
                        cnt++;
                    }
                }
                pw_bacc.println();
          }
            
            /**
             * Now write the answers given for each question *
             */           
                j = 0;                
                cnt = 0;
                taskSize = questionSizes.get(j);

                for (int m = 0; m < evalQuestions.size(); m++) {
                    cnt++;
                    boolean ans = evalQuestions.get(m).getIsGivenAnsCorrect();

                    if (j == 0 && cnt == 1) {
                        pw_bacc.print(ans);
                    } else if (cnt == 1) {
                        pw_bacc.print(" :: " + ans);
                    } else {
                        pw_bacc.print("," + ans);
                    }
                    if (cnt == taskSize) {
                        taskSize = (Integer) questionSizes.get(j);
                        j++;
                        cnt = 0;
                    }

                }
                pw_bacc.println();
                    
             //close the printWriters
              pw_bacc.close();
              
              
              
              /***
               * Write the raw time data also to file. It follows the same format as the accuracy
               */
              
            String filename_btime = utils.getConditionTimeBasicFileName(currentCondition);
            File file_btime = new File(getServletContext().getRealPath(DATA_DIR + File.separator + filename_btime));

            newFile = false;

            if (!file_btime.exists()) {
                file_btime.createNewFile();
                newFile = true;
                //print the headers
            }

            BufferedWriter bw_btime = new BufferedWriter(new FileWriter(file_btime, true));
            PrintWriter pw_btime = new PrintWriter(bw_btime);

            if (newFile) {
                start = 0;
                taskSize = 0;
                for (int m = 0; m < taskTypes.size(); m++) {
                    start = taskSize;
                    taskSize = questionSizes.get(m);
                    int limit = start + taskSize;
                    cnt = 0;
                    
                    for (int n = start; n < limit; n++) {                        
                        String name = "Time_" + taskTypes.get(m) + "_" + currentCondition;
                        
                        if (n == 0 && start == 0) {
                            pw_btime.print(name);
                        } else if (cnt == 0) {
                            pw_btime.print(" :: " + name);
                        } else {
                            pw_btime.print(",");
                        }
                        cnt++;
                    }
                }
                pw_btime.println();
                
                /**
                 * *************************************************
                 */
                //The question headers i.e. Q1, Q2, etc.
                start = 0;
                taskSize = 0;
                for (int m = 0; m < taskTypes.size(); m++) {
                    start = taskSize;
                    taskSize = questionSizes.get(m);
                    int limit = start + taskSize;
                    cnt = 0;
                    for (int n = start; n < limit; n++) {
                        String name = "Q" + (cnt + 1);
                        if (n == 0 && start == 0) {
                            pw_btime.print(name);
                        } else if (cnt == 0) {
                            pw_btime.print(" :: " + name);
                        } else {
                            pw_btime.print("," + name);
                        }
                        cnt++;
                    }
                }
                pw_btime.println();
          }
            
            /**
             * Now write the answers given for each question *
             */           
                j = 0;                
                cnt = 0;
                taskSize = questionSizes.get(j);

                for (int m = 0; m < evalQuestions.size(); m++) {
                    cnt++;
                    int time = evalQuestions.get(m).getTimeInSeconds();

                    if (j == 0 && cnt == 1) {
                        pw_btime.print(time);
                    } else if (cnt == 1) {
                        pw_btime.print(" :: " + time);
                    } else {
                        pw_btime.print("," + time);
                    }
                    if (cnt == taskSize) {
                        taskSize = (Integer) questionSizes.get(j);
                        j++;
                        cnt = 0;
                    }

                }
                pw_btime.println();
                    
             //close the printWriter
              pw_btime.close();
            
            

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void writeWithinStudyAnswersToFile() {

        try {
            int numOfConditions = orderOfConditions.size();
            //String filenames[] = new String[numOwriteWithinStudyAnswersToFilefConditions];
            File files[] = new File[numOfConditions];
            BufferedWriter bws[] = new BufferedWriter[numOfConditions];
            PrintWriter pws[] = new PrintWriter[numOfConditions];

            String filename;
            for (int i = 0; i < numOfConditions; i++) {
                filename = utils.getConditionAccuracyFileName(orderOfConditions.get(i));
                files[i] = new File(getServletContext().getRealPath(DATA_DIR + File.separator + filename));

                boolean newFile = false;

                if (!files[i].exists()) {
                    files[i].createNewFile();
                    newFile = true;
                    
                }

                bws[i] = new BufferedWriter(new FileWriter(files[i], true));
                pws[i] = new PrintWriter(bws[i]);

                if (newFile) {
                    //write the headers
                    currentCondition = orderOfConditions.get(i);
                    pws[i].print("Acc_" + taskTypes.get(0) + "_" + currentCondition);

                    for (int j = 1; j < taskTypes.size(); j++) {
                        pws[i].print("," + "Acc_" + taskTypes.get(j) + "_" + currentCondition);
                    }
                    pws[i].println();
                }
            }

            //write the data to a file.
            int start = 0;
            int limit = 0;
            for (int i = 0; i < numOfConditions; i++) {
                start = i * sizeOfACondition;
                limit = start + sizeOfACondition;

                int j = 0;
                int taskSize = questionSizes.get(j);
                int cnt = 0;
                int numCorrect = 0;

                for (int k = start; k < limit; k++) {
                    cnt++;
                    if (!(cnt <= taskSize)) {

                        if (j == 0) {
                            pws[i].print((double) numCorrect / taskSize);
                        } else {
                            pws[i].print("," + (double) numCorrect / taskSize);
                        }
                        taskSize = (Integer) questionSizes.get(j);
                        j++;
                        cnt = 0;
                        numCorrect = 0;
                    }
                    if (evalQuestions.get(k).getIsGivenAnsCorrect()) {
                        numCorrect++;
                    }
                }
                pws[i].print("," + (double) numCorrect / taskSize);
                pws[i].println();
            }
            System.out.println("Results written to file successfully!");

            //close the printWriters
            for (int i = 0; i < pws.length; i++) {
                pws[i].close();
            }

            /**
             * *********************************************************
             * *********************************************************
             */
            /* Write the time also to file */
            for (int i = 0; i < numOfConditions; i++) {
                filename = utils.getConditionTimeFileName(orderOfConditions.get(i));
                files[i] = new File(getServletContext().getRealPath(DATA_DIR + File.separator + filename));

                boolean newFile = false;

                if (!files[i].exists()) {
                    files[i].createNewFile();
                    newFile = true;
                }

                bws[i] = new BufferedWriter(new FileWriter(files[i], true));
                pws[i] = new PrintWriter(bws[i]);

                if (newFile) {
                    //write the headers
                    currentCondition = orderOfConditions.get(i);
                    pws[i].print("Time_" + taskTypes.get(0) + "_" + currentCondition);

                    for (int j = 1; j < taskTypes.size(); j++) {
                        pws[i].print("," + "Time_" + taskTypes.get(j) + "_" + currentCondition);
                    }
                    pws[i].println();
                }
            }

            //write the data to a file.
            start = 0;
            limit = 0;
            for (int i = 0; i < numOfConditions; i++) {
                start = i * sizeOfACondition;
                limit = start + sizeOfACondition;

                int j = 0;
                int taskSize = questionSizes.get(j);
                int cnt = 0;
                int totalTime = 0;

                for (int k = start; k < limit; k++) {
                    cnt++;
                    if (!(cnt <= taskSize)) {
                        if (j == 0) {
                            pws[i].print((double) totalTime / taskSize);
                        } else {
                            pws[i].print("," + (double) totalTime / taskSize);
                        }
                        taskSize = (Integer) questionSizes.get(j);
                        j++;
                        cnt = 0;
                        totalTime = 0;
                    }

                    totalTime += evalQuestions.get(k).getTimeInSeconds();
                }
                pws[i].print("," + (double) totalTime / taskSize);
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

                filename = utils.getConditionAccuracyBasicFileName(orderOfConditions.get(i));
                files[i] = new File(getServletContext().getRealPath(DATA_DIR + File.separator + filename));

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
                    currentCondition = orderOfConditions.get(i);

                    start = 0;
                    taskSize = 0;
                    cnt = 0;

                    for (int j = 0; j < numberOfTasks; j++) {
                        start = taskSize;
                        taskSize = questionSizes.get(j);
                        String ttype = taskTypes.get(j);
                        limit = start + taskSize;
                        cnt = 0;

                        for (int k = start; k < limit; k++) {
                            //ttype = taskTypes.get(m);
                            String name = "Acc_" + ttype + "_" + currentCondition;
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
                    for (int j = 0; j < numberOfTasks; j++) {
                        start = taskSize;
                        taskSize = questionSizes.get(j);
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
            for (int i = 0; i < numOfConditions; i++) {
                start = i * sizeOfACondition;
                limit = start + sizeOfACondition;

                int j = 0;
                taskSize = questionSizes.get(j);
                cnt = 0;

                //   System.out.println("The Start is:: " + start +"  The LIMIT IS :: " +limit);
                for (int k = start; k < limit; k++) {
                    cnt++;
                    boolean ans = evalQuestions.get(k).getIsGivenAnsCorrect();

                    if (j == 0 && cnt == 1) {
                        pws[i].print(ans);
                    } else if (cnt == 1) {
                        pws[i].print(" :: " + ans);
                    } else {
                        pws[i].print("," + ans);
                    }

                    if (cnt == taskSize) {
                        taskSize = (Integer) questionSizes.get(j);
                        j++;
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
                filename = utils.getConditionTimeBasicFileName(orderOfConditions.get(i));
                files[i] = new File(getServletContext().getRealPath(DATA_DIR + File.separator + filename));

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
                    currentCondition = orderOfConditions.get(i);

                    for (int j = 0; j < numberOfTasks; j++) {
                        start = taskSize;
                        taskSize = questionSizes.get(j);
                        String ttype = taskTypes.get(j);
                        limit = start + taskSize;
                        cnt = 0;

                        for (int k = start; k < limit; k++) {
                            String name = "Time_" + ttype + "_" + currentCondition;
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
                    for (int j = 0; j < numberOfTasks; j++) {
                        start = taskSize;
                        taskSize = questionSizes.get(j);
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
             * Now write the time for each question *
             */
            start = 0;
            limit = 0;
            for (int i = 0; i < numOfConditions; i++) {
                start = i * sizeOfACondition;
                limit = start + sizeOfACondition;

                int j = 0;
                taskSize = questionSizes.get(j);
                cnt = 0;

                for (int k = start; k < limit; k++) {
                    cnt++;
                    int time = evalQuestions.get(k).getTimeInSeconds();

                    if (j == 0 && cnt == 1) {
                        pws[i].print(time);
                    } else if (cnt == 1) {
                        pws[i].print(" :: " + time);
                    } else {
                        pws[i].print("," + time);
                    }

                    if (cnt == taskSize) {
                        taskSize = (Integer) questionSizes.get(j);
                        j++;
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

}
