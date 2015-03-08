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
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.rosuda.JRI.REXP;
import org.rosuda.JRI.Rengine;

/**
 *
 * @author Mershack
 */
public class UserStudyResultsEvaluation2 extends HttpServlet {

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    private ArrayList<String> accuracyFilenames = new ArrayList<String>();
    private ArrayList<String> timeFilenames = new ArrayList<String>();
    private final String DATA_DIR = "data";
    ArrayList<String[][]> accuracyResults = new ArrayList<String[][]>();
    ArrayList<String[][]> timeResults = new ArrayList<String[][]>();

    ArrayList<String> accuracySummary_Label;
    ArrayList<Double> accuracySummary_Value;
    String accuracySummary_All;
    String timeSummary_All;

    int numOfConditions = 2;
    int numOfTasks = 2;
    
    int maxResultsRows = 0;

    Rengine re;

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        try {
            generateFilenames();

            generateShapiroWilk();

            generateTTest();

            getAccuracyResults();
            getSummarizedRawData();
            //getBasicRawData()

            //getAccuracyResults();
            //getTimeResults();
            //System.out.println("-------------");
        } catch (Exception ex) {
            ex.printStackTrace();

        } finally {
            out.close();
        }
    }

    public void getSummarizedRawData() {
        //TODO: //read the summarized - Results file and arrange them based on tasks
        ArrayList<String[][]> allAccResultsBasedOnTasks = new ArrayList<String[][]>();
        ArrayList<String[][]> allTimeResultsBasedOnTasks = new ArrayList<String[][]>();
        
        
        //initialize the arrays that will be used to hold the results per task 
        for(int i=0; i<numOfTasks; i++){
            allAccResultsBasedOnTasks.add(new String[maxResultsRows][numOfConditions]);
            allTimeResultsBasedOnTasks.add(new String[maxResultsRows][numOfConditions]);            
        }
                
        //rearrange the results of each task type      
            for (int i = 0; i < accuracyResults.size(); i++) {
                String[][] arr = (String[][]) accuracyResults.get(i);                
                for (int j = 0; j < numOfTasks; j++) {
                    for (int k = 0; k < arr.length; k++) {               
                        allAccResultsBasedOnTasks.get(j)[k][i] = arr[k][j];                    
                    }
                }
            }
            //do the same for the time
            for (int i = 0; i < timeResults.size(); i++) {
                String[][] arr = (String[][]) timeResults.get(i);                
                for (int j = 0; j < numOfTasks; j++) {
                    for (int k = 0; k < arr.length; k++) {               
                        allTimeResultsBasedOnTasks.get(j)[k][i] = arr[k][j];                    
                    }
                }
            }
            
            
            //now write to a file.
            
            //
            for(int i =0; i<allTimeResultsBasedOnTasks.size(); i++){
                //System.out.println("Hey*****");
                String arr[][] = allTimeResultsBasedOnTasks.get(i);
                
                for(int j=0; j<arr.length; j++){
                    for(int k=0; k<numOfConditions; k++){   
                        
                        if(k==0){
                            System.out.print(arr[j][k]);
                        }
                        else{
                            System.out.print( "," + arr[j][k]);
                        }
                        
                    }
                    
                    System.out.println();
                }
                System.out.println();
                System.out.println("----------------------------------------------");
                System.out.println();
            }
          
            
    }

    public void getBasicRawData() {
        //TODO: Make sure we will be saving the raw data (i.e. individual answers given by the user
        //We will be reading this basic raw data here and we will ensure we will group by task type side by side
    }

    public String getAccuracyResults() {
        //accuracyResults = new ArrayList<String[][]>();
        String allAccuracyData = "";
        try {

            for (int i = 0; i < numOfConditions; i++) { //Read the results from file
                String filename = "AccuracyResults" + (i + 1) + ".txt";

                System.out.println("This is the result for file:: " + filename);

                File file = new File(getServletContext().getRealPath(DATA_DIR + File.separator + filename));

                BufferedReader br = new BufferedReader(new FileReader(file));
                String line = "";
                ArrayList<String> taskAccuracy = new ArrayList<String>();
                while ((line = br.readLine()) != null) {
                    //taskAccuracy = new ArrayList<String>();
                    //String split[] = line.split(",");

                    taskAccuracy.add(line);

                   // System.out.println("------" + line);

                    //  for (int j = 0; j < split.length; j++) {
                    //  taskAccuracy.add(split[j]);
                    //}
                }
                String[][] taskAccuracy2 = new String[taskAccuracy.size()][numOfTasks];
                for (int j = 0; j < taskAccuracy.size(); j++) {

                    String split[] = taskAccuracy.get(j).split(",");
                    for (int k = 0; k < split.length; k++) {
                        // System.out.println("***** " +taskAccuracy.get(i));
                        taskAccuracy2[j][k] = split[k];
                    }

                }
                //record the maximum rows
                if(taskAccuracy.size() > maxResultsRows){
                    maxResultsRows = taskAccuracy.size();
                }

                accuracyResults.add(taskAccuracy2);
                br.close();

            }

            /**
             * *I'm going to find the average of the results this time
             */
            accuracySummary_Label = new ArrayList<String>();
            accuracySummary_Value = new ArrayList<Double>();

            for (int i = 0; i < accuracyResults.size(); i++) {

                String[][] arr = (String[][]) accuracyResults.get(i);

                double average = 0;
                double sum = 0;
                String label = "";
                int total = arr.length - 1;

                for (int j = 0; j < numOfTasks; j++) {

                    label = arr[0][j]; //get the label of the 
                    sum = 0;
                    for (int k = 1; k < arr.length; k++) {
                        sum += Double.parseDouble(arr[k][j]);
                    }
                    average = sum / total;

                    int index = 0;
                    boolean added = false;
                    for (int m = 0; m < accuracySummary_Label.size(); m++) {
                        if (accuracySummary_Label.get(m).compareTo(label) > 0) {
                            accuracySummary_Label.add(m, label);
                            accuracySummary_Value.add(m, average);
                            added = true;
                            break;
                        }
                    }

                    if (!added) {
                        accuracySummary_Label.add(label);
                        accuracySummary_Value.add(average);
                    }

                }
            }

            /**
             * * Store all the accuracy results based on tasks
             */
            ArrayList<String> accuracySummaryTemp = new ArrayList<String>();
            /*   for(int i=0; i<accuracySummary_Label.size(); i++){
             accuracySummaryTemp.add(accuracySummary_Label.get(i));
             } */

            int cnt = 0;

            String taskSeparator = "::::";
            String conditionSeparator = "::";
            for (int i = 0; i < accuracySummary_Label.size(); i++) {

                if (cnt == 0) {
                    allAccuracyData += accuracySummary_Label.get(i) + "," + accuracySummary_Value.get(i);
                } else {
                    allAccuracyData += conditionSeparator + accuracySummary_Label.get(i) + "," + accuracySummary_Value.get(i);
                }
                cnt++;

                if (cnt == numOfConditions) {
                    cnt = 0;
                    allAccuracyData += taskSeparator;
                }

            }

            //  return allAccuracyData;         
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return allAccuracyData;

    }

    public void getTimeResults() {
        try {

            for (int i = 0; i < numOfConditions; i++) { //Read the results from file
                String filename = "TimeResults" + (i + 1) + ".txt";

                System.out.println("This is the result for Time file:: " + filename);

                File file = new File(getServletContext().getRealPath(DATA_DIR + File.separator + filename));

                BufferedReader br = new BufferedReader(new FileReader(file));
                String line = "";
                while ((line = br.readLine()) != null) {
                    System.out.println(line);
                }
                br.close();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void generateFilenames() {
        accuracyFilenames = new ArrayList<String>();
        timeFilenames = new ArrayList<String>();

        String filename = "";
        for (int i = 0; i < numOfConditions; i++) {
            filename = "AccuracyResults" + (i + 1) + ".txt";
            accuracyFilenames.add(filename);
        }

        for (int i = 0; i < numOfConditions; i++) {
            filename = "TimeResults" + (i + 1) + ".txt";
            timeFilenames.add(filename);
        }

    }

    protected void generateAllResults() {
        /* Property<PString> p_hitTitle = new Property<PString>("User-Study.HIT title", new PString(""));
         this.addProperty(p_hitTitle); */
        String result = "";
        // result += downloadStudyResultsFile() + "\n---------------------\n\n";
        //  result += generateResultGraphs() + "\n---------------------\n\n";
        //     result += generateShapiroWilk() + "\n---------------------\n\n";
        //result += generateTTest() + "\n---------------------\n\n";

        System.out.println("The result after R-analysis is :::::: " + result);

    }

    public String downloadStudyResultsFile() {
        //download the results file into the studyResults directory

        /* String textResultAccuracy = "";
         if (!hostURL.isEmpty()) {

         String qltyFileStr = hostURL + "data/" + QLT_STUDY_RESULT_FILE;
         String quantControlFileStr = hostURL + "data/" + QUANT_CONTROLCONDITION_RESULT_FILE;
         String quantTestFileStr = hostURL + "data/" + QUANT_TESTCONDITION_RESULT_FILE;
         String timeConFileStr = hostURL + "data/" + "testDurationControl.txt";
         String timeTestFileStr = hostURL + "data/" + "testDurationTest.txt";

         try {
         org.apache.commons.io.FileUtils.copyURLToFile(new URL(qltyFileStr), new File("studyResults/" + QLT_STUDY_RESULT_FILE));
         org.apache.commons.io.FileUtils.copyURLToFile(new URL(quantControlFileStr), new File("studyResults/" + QUANT_CONTROLCONDITION_RESULT_FILE));
         org.apache.commons.io.FileUtils.copyURLToFile(new URL(quantTestFileStr), new File("studyResults/" + QUANT_TESTCONDITION_RESULT_FILE));
         org.apache.commons.io.FileUtils.copyURLToFile(new URL(timeConFileStr), new File("studyResults/" + "testDurationControl.txt"));
         org.apache.commons.io.FileUtils.copyURLToFile(new URL(timeTestFileStr), new File("studyResults/" + "testDurationTest.txt"));
                
         } catch (Exception ex) {
         textResultAccuracy = "an error occured when trying to download the file";
         ex.printStackTrace();
         }
         textResultAccuracy = "Result files downloaded to the directory  studyResults/";
         } else {
         textResultAccuracy = "unable to download the files";
         }

         return textResultAccuracy;  */
        return "";
    }

    public String generateResultGraphs() {
        String textResult = "";

        try {
            /*do for the accuracy */

            //File fileNameFile = new File(getServletContext().getRealPath( + QUANT_QNS_FILENAME));
            for (int i = 0; i < numOfConditions; i++) {
                re.eval("accuracy" + (i + 1) + " = read.csv(\"" + DATA_DIR + File.separator + accuracyFilenames.get(i) + "\")");
            }

            String accNameAll = "accuracy1";
            String timeNameAll = "time1";

            for (int i = 1; i < numOfConditions; i++) {
                accNameAll = ", accuracy" + (i + 1);
                timeNameAll = ", time" + (i + 1);

            }

            //re.eval("mydataCon = read.csv(\"studyResults/" + QUANT_CONTROLCONDITION_RESULT_FILE + "\")");
            //re.eval("mydataTest = read.csv(\"studyResults/" + QUANT_TESTCONDITION_RESULT_FILE + "\")");
            re.eval("accdata = data.frame(" + accNameAll + ")");
            re.eval("accdata = accdata[,order(names(accdata))]");

            re.eval("png(filename=\"" + DATA_DIR + File.separator + "accResults.png" + "\")");
            re.eval("boxplot(accdata, ylab=\"Accuracy\", par(cex.axis=1.5, cex.lab=1.5))");
            re.eval("dev.off()");

            //re.eval("mydataAll = data.frame(mydataCon, mydataTest)");
            //re.eval("mydataAll = mydataAll[,order(names(mydataAll))]");
            //re.eval("boxplot(mydataAll)");
            //re.eval("png(filename=\"" + "studyResults/quantitativeResultsImage.png" + "\")");
            //re.eval("boxplot(mydataAll, ylab=\"Accuracy\", par(cex.axis=1.5, cex.lab=1.5))");
            //re.eval("dev.off()");
            /*do similar for time*/
            for (int i = 0; i < numOfConditions; i++) {
                re.eval("time" + (i + 1) + " = read.csv(\"" + DATA_DIR + File.separator + timeFilenames.get(i) + "\")");
            }

            //re.eval("mydataCon = read.csv(\"studyResults/" + "testDurationControl.txt" + "\")");
            //re.eval("mydataTest = read.csv(\"studyResults/" + "testDurationTest.txt" + "\")");
            re.eval("timedata = data.frame(" + timeNameAll + ")");
            re.eval("timedata = timedata[,order(names(timedata))]");
            //re.eval("boxplot(mydataAll)");
            re.eval("png(filename=\"" + DATA_DIR + File.separator + "timeResults.png" + "\")");
            re.eval("boxplot(timedata, ylab=\"Time (sec)\", par(cex.axis=1.5, cex.lab=1.5))");
            re.eval("dev.off()");
            //  re.end();
        } catch (Exception ex) {
            textResult = "an error occurred when generating the graphs";
            ex.printStackTrace();
        }
        textResult = "Graphs have been generated in the directory studyResults";
        return textResult;

    }

    public void generateShapiroWilk() {

        try {

            //first get the column names of the files
            BufferedReader br;// = new BufferedReader(new FileReader(firstAccuracyFile));
            String line = "";
            int numOfColumns = numOfTasks;
            String[][] accColumnNames = new String[numOfConditions][numOfTasks];
            String[][] timeColumnNames = new String[numOfConditions][numOfTasks];
            for (int i = 0; i < numOfConditions; i++) {
                File AccuracyFile = new File(getServletContext().getRealPath(DATA_DIR + File.separator + accuracyFilenames.get(i)));
                br = new BufferedReader(new FileReader(AccuracyFile));
                while ((line = br.readLine()) != null) {
                    String split[] = line.split(",");

                    for (int j = 0; j < split.length; j++) {
                        accColumnNames[i][j] = split[j];
                    }
                    break;
                }
                br.close();
            }

            for (int i = 0; i < numOfConditions; i++) {
                File timeFile = new File(getServletContext().getRealPath(DATA_DIR + File.separator + timeFilenames.get(i)));
                br = new BufferedReader(new FileReader(timeFile));
                while ((line = br.readLine()) != null) {
                    String split[] = line.split(",");

                    for (int j = 0; j < split.length; j++) {
                        timeColumnNames[i][j] = split[j];
                    }
                    break;
                }
                br.close();
            }

            //Write the R-Script
            String scriptFilename = "rscript-shapiro.R";
            String scriptOutputFilename = "rscript-shapiro.Rout";
            String filePath = getServletContext().getRealPath(DATA_DIR + File.separator + scriptFilename);
            String dataPath = getServletContext().getRealPath(DATA_DIR);

            File scriptFile = new File(filePath);
            FileWriter fileWriter = new FileWriter(scriptFile);
            BufferedWriter bw = new BufferedWriter(fileWriter);
            PrintWriter pw = new PrintWriter(bw);

            if (dataPath.indexOf("\\") >= 0) {
                dataPath = dataPath.replaceAll("\\\\", "\\\\\\\\");
            }
            //first set the working directory
            pw.println("setwd(\"" + dataPath + "\")");

            //read the accuracy files
            for (int i = 0; i < numOfConditions; i++) {
                pw.println("accuracy" + (i + 1) + " = read.csv(\"" + accuracyFilenames.get(i) + "\")");
            }
            //write the accuracy analysis
            for (int i = 0; i < numOfConditions; i++) {

                String dataName = "accuracy" + (i + 1);

                for (int j = 0; j < accColumnNames[i].length; j++) {
                    pw.println("" + accColumnNames[i][j] + "= c(" + dataName + "[," + (j + 1) + "])");
                    pw.println("results<-capture.output(shapiro.test(" + accColumnNames[i][j] + "))");
                    pw.println("results");
                }
            }
            //read the time files
            for (int i = 0; i < numOfConditions; i++) {
                pw.println("time" + (i + 1) + " = read.csv(\"" + timeFilenames.get(i) + "\")");
            }
            //write the time analysis
            for (int i = 0; i < numOfConditions; i++) {
                String dataName = "time" + (i + 1);

                for (int j = 0; j < timeColumnNames[i].length; j++) {
                    pw.println("" + timeColumnNames[i][j] + "= c(" + dataName + "[," + (j + 1) + "])");
                    pw.println("results<-capture.output(shapiro.test(" + timeColumnNames[i][j] + "))");
                    pw.println("results");
                }

            }

            //close the printwriter
            pw.close();

            /**
             * Run the script *
             */
            Runtime r = Runtime.getRuntime();
            Process p = null;
            String load;
            String workdir = getServletContext().getRealPath(DATA_DIR);

            String cmd[] = new String[3];
            cmd[0] = "cmd.exe";
            cmd[1] = "/C";
            cmd[2] = "Rterm.exe --slave --no-restore --no-save <" + workdir + File.separator + scriptFilename + " > "
                    + workdir + File.separator + scriptOutputFilename + " 2>&1";

            //the working directory has to be the servlet and the datat direc
            //System.out.println("The Rterm is ::::: " + cmd[2]);
            //System.out.println("***The working directory is::: " + getServletContext().getRealPath(DATA_DIR));
            try {
                // Use instance of Runtime class to run the command as a java Process
                p = r.exec(cmd);

                // Need to be careful with Process input and output to prevent it seizing up
                // May not be needed (up to **) in most cases but included just in case
                InputStream pin = p.getInputStream();
                InputStreamReader cin = new InputStreamReader(pin);
                BufferedReader in = new BufferedReader(cin);
                try {
                    while ((load = in.readLine()) != null) {
                    }
                } catch (IOException e) {
                }

                InputStream epin = p.getErrorStream();
                InputStreamReader ecin = new InputStreamReader(epin);
                BufferedReader ein = new BufferedReader(ecin);
                try {
                    while ((load = ein.readLine()) != null) {
                    }
                } catch (IOException e) {
                }

                ein.close();
                in.close();
                // **
            } catch (IOException e) {
                System.out.println("Problems running: " + e.toString());
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    public String generateTTest() {

        try {

            //first get the column names of the files
            BufferedReader br;// = new BufferedReader(new FileReader(firstAccuracyFile));
            String line = "";
            int numOfColumns = numOfTasks;
            String[][] accColumnNames = new String[numOfConditions][numOfTasks];
            String[][] timeColumnNames = new String[numOfConditions][numOfTasks];
            for (int i = 0; i < numOfConditions; i++) {
                File AccuracyFile = new File(getServletContext().getRealPath(DATA_DIR + File.separator + accuracyFilenames.get(i)));
                br = new BufferedReader(new FileReader(AccuracyFile));
                while ((line = br.readLine()) != null) {
                    String split[] = line.split(",");

                    for (int j = 0; j < split.length; j++) {
                        accColumnNames[i][j] = split[j];
                    }
                    break;
                }
                br.close();
            }

            for (int i = 0; i < numOfConditions; i++) {
                File timeFile = new File(getServletContext().getRealPath(DATA_DIR + File.separator + timeFilenames.get(i)));
                br = new BufferedReader(new FileReader(timeFile));
                while ((line = br.readLine()) != null) {
                    String split[] = line.split(",");

                    for (int j = 0; j < split.length; j++) {
                        timeColumnNames[i][j] = split[j];
                    }
                    break;
                }
                br.close();
            }

            //Write the R-Script
            String scriptFilename = "rscript-ttest.R";
            String scriptOutputFilename = "rscript-ttest.Rout";
            String filePath = getServletContext().getRealPath(DATA_DIR + File.separator + scriptFilename);
            String dataPath = getServletContext().getRealPath(DATA_DIR);

            File scriptFile = new File(filePath);
            FileWriter fileWriter = new FileWriter(scriptFile);
            BufferedWriter bw = new BufferedWriter(fileWriter);
            PrintWriter pw = new PrintWriter(bw);

            if (dataPath.indexOf("\\") >= 0) {
                dataPath = dataPath.replaceAll("\\\\", "\\\\\\\\");
            }
            //first set the working directory
            pw.println("setwd(\"" + dataPath + "\")");

            //read the accuracy files
            for (int i = 0; i < numOfConditions; i++) {
                pw.println("accuracy" + (i + 1) + " = read.csv(\"" + accuracyFilenames.get(i) + "\")");
            }
            //write the accuracy analysis
            for (int i = 0; i < numOfConditions - 1; i++) {

                String dataName1 = "accuracy" + (i + 1);
                String dataName2 = "accuracy" + (i + 2);

                for (int j = 0; j < accColumnNames[i].length; j++) {
                    pw.println("" + accColumnNames[i][j] + "= c(" + dataName1 + "[," + (j + 1) + "])");
                    pw.println("" + accColumnNames[i + 1][j] + "= c(" + dataName1 + "[," + (j + 1) + "])");
                    pw.println("results<-capture.output(t.test(" + accColumnNames[i][j] + "," + accColumnNames[i + 1][j] + "))");
                    pw.println("results");
                }
            }

            //close the printwriter
            pw.close();

            /**
             * Run the script *
             */
            Runtime r = Runtime.getRuntime();
            Process p = null;
            String load;
            String workdir = getServletContext().getRealPath(DATA_DIR);

            String cmd[] = new String[3];
            cmd[0] = "cmd.exe";
            cmd[1] = "/C";
            cmd[2] = "Rterm.exe --slave --no-restore --no-save <" + workdir + File.separator + scriptFilename + " > "
                    + workdir + File.separator + scriptOutputFilename + " 2>&1";

            //the working directory has to be the servlet and the datat direc
            //System.out.println("The Rterm is ::::: " + cmd[2]);
            //System.out.println("***The working directory is::: " + getServletContext().getRealPath(DATA_DIR));
            try {
                // Use instance of Runtime class to run the command as a java Process
                p = r.exec(cmd);

                // Need to be careful with Process input and output to prevent it seizing up
                // May not be needed (up to **) in most cases but included just in case
                InputStream pin = p.getInputStream();
                InputStreamReader cin = new InputStreamReader(pin);
                BufferedReader in = new BufferedReader(cin);
                try {
                    while ((load = in.readLine()) != null) {
                    }
                } catch (IOException e) {
                }

                InputStream epin = p.getErrorStream();
                InputStreamReader ecin = new InputStreamReader(epin);
                BufferedReader ein = new BufferedReader(ecin);
                try {
                    while ((load = ein.readLine()) != null) {
                    }
                } catch (IOException e) {
                }

                ein.close();
                in.close();
                // **
            } catch (IOException e) {
                System.out.println("Problems running: " + e.toString());
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }        
        return "";
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
