/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package userstudy;

/*import com.amazon.mturk.requester.MTurkRequestsMgr;
 import graphEvaluation.GraphUserStudyViewer;
 import graphEvaluation.GraphUserStudyViewerFactory; */
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.net.URL;
import java.util.ArrayList;
import org.rosuda.JRI.REXP;
import org.rosuda.JRI.Rengine;

/**
 *
 * @author Mershack
 */
public class UserStudyResultsEvaluation {

    // Environment env;
    private String hostURL = "";
    private final String QLT_STUDY_RESULT_FILE = "qualitativeStudyResults.txt";
    private final String QUANT_CONTROLCONDITION_RESULT_FILE = "quantitativeControlConditionResults.txt";
    private final String QUANT_TESTCONDITION_RESULT_FILE = "quantitativeTestConditionResults.txt";
    private final String QUANT_TEXT_RESULT_FILE = "textResult.txt";
    private ArrayList<String> accuracyFilenames = new ArrayList<String>();
    private ArrayList<String> timeFilenames = new ArrayList<String>();

    int numOfConditions = 2;

    Rengine re;
    // Property<PText> p_textResult;

    public UserStudyResultsEvaluation() {
        String[] Rargs = {"--vanilla"};
        //This time, give the R engine a callback listener.
        re = new Rengine(Rargs, false, null);
        if (!re.waitForR()) {
            System.out.println("Cannot load R");
            //return null;
        }

        generateFilenames();

        generateAllResults();
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
        result += generateResultGraphs() + "\n---------------------\n\n";
        result += generateShapiroWilk() + "\n---------------------\n\n";
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
            for (int i = 0; i < numOfConditions; i++) {
                re.eval("accuracy" + (i + 1) + " = read.csv(\"data/" + accuracyFilenames.get(i) + "\")");
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

            re.eval("png(filename=\"" + "data/accResults.png" + "\")");
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
                re.eval("time" + (i + 1) + " = read.csv(\"data/" + timeFilenames.get(i) + "\")");
            }

            //re.eval("mydataCon = read.csv(\"studyResults/" + "testDurationControl.txt" + "\")");
            //re.eval("mydataTest = read.csv(\"studyResults/" + "testDurationTest.txt" + "\")");
            re.eval("timedata = data.frame(" + timeNameAll + ")");
            re.eval("timedata = timedata[,order(names(timedata))]");
            //re.eval("boxplot(mydataAll)");
            re.eval("png(filename=\"" + "data/timeResults.png" + "\")");
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

    public String generateShapiroWilk() {
        System.out.println("Began Shapiro Wilk");
        String textResultAccuracy = "";
        String textResultTime = "";
        int numberofColumns = 0;

        try {

            for (int i = 0; i < numOfConditions; i++) {
                re.eval("accuracy" + (i + 1) + " = read.csv(\"data/" + accuracyFilenames.get(i) + "\")");
            }

            String accNameAll = "accuracy1";
            String timeNameAll = "time1";

            File controlResultFile = new File("data/" + accuracyFilenames.get(0));

            BufferedReader br = new BufferedReader(new FileReader(controlResultFile));
            String line = "";
            while ((line = br.readLine()) != null) {
                String split[] = line.split(",");
                numberofColumns = split.length;
                break;
            }
            br.close();

            if ((numberofColumns == 0)) {
                textResultAccuracy = "Empty result file";
            } else {

                for (int i = 0; i < numOfConditions; i++) {
                    re.eval("accuracy" + (i + 1) + " = read.csv(\"data/" + accuracyFilenames.get(i) + "\")");
                }

                //  re.eval("mydataCon = read.csv(\"data/" + QUANT_CONTROLCONDITION_RESULT_FILE + "\")");
                //re.eval("mydataTest = read.csv(\"data/" + QUANT_TESTCONDITION_RESULT_FILE + "\")");

                /*  REXP rexpCon, rexpTest;
              
                 rexpCon = re.eval("names(mydataCon)");
                 rexpTest = re.eval("names(mydataTest)");
                
                 String controlColumnNames[] = rexpCon.asStringArray();
                 String testColumnNames[] = rexpTest.asStringArray();

                 String strArrCon[];
                 String strArrTest[];  */
                String strArr[][] = new String[numOfConditions][];
                for (int j = 0; j < numOfConditions; j++) {
                    String name = "accuracy" + (j + 1);
                    REXP rexp = re.eval("names(" + name + ")");

                    String columnNames[] = rexp.asStringArray();

                    for (int i = 0; i < columnNames.length; i++) {
                        re.eval("" + columnNames[i] + "= c(" + name + "[," + (i + 1) + "])");
                        //re.eval("" + testColumnNames[i] + "= c(mydataTest[," + (i + 1) + "])");
                        re.eval("results<-capture.output(shapiro.test(" + columnNames[i] + "))");
                        strArr[j] = re.eval("results").asStringArray();

                        for (int k = 0; k < numOfConditions; k++) {
                            textResultAccuracy += strArr[k] + "\n";
                        }

                    }

                }

                /* Do similar for the time as well */
                for (int i = 0; i < numOfConditions; i++) {
                    re.eval("time" + (i + 1) + " = read.csv(\"data/" + timeFilenames.get(i) + "\")");
                }

//------------------------------------------------
                // re.eval("mydataCon = read.csv(\"studyResults/" + "testDurationControl.txt" + "\")");
                //re.eval("mydataTest = read.csv(\"studyResults/" + "testDurationTest.txt" + "\")");
                //REXP rexpCon, rexpTest;
                strArr = new String[numOfConditions][];
                for (int j = 0; j < numOfConditions; j++) {
                    String name = "time" + (j + 1);
                    REXP rexp = re.eval("names(" + name + ")");

                    String columnNames[] = rexp.asStringArray();

                    for (int i = 0; i < columnNames.length; i++) {
                        re.eval("" + columnNames[i] + "= c("+name+"[," + (i + 1) + "])");
                        //re.eval("" + testColumnNames[i] + "= c(mydataTest[," + (i + 1) + "])");
                        re.eval("results<-capture.output(shapiro.test(" + columnNames[i] + "))");
                        strArr[j] = re.eval("results").asStringArray();

                        //re.eval("results<-capture.output(shapiro.test(" + testColumnNames[i] + "))");
                        //strArrTest = re.eval("results").asStringArray();

                        //  System.out.println("------");
                        for(int k = 0; k < strArr.length; k++) {
                            textResultTime += strArr[k] + "\n";
                        }
                    }
                }
            }

           
            System.out.println("------------shapiro-wilk generated successfully-----------");

            //writing to file
            FileWriter fileWriter = new FileWriter(new File("data/shapiro_accuracy.txt"));
            BufferedWriter bw = new BufferedWriter(fileWriter);
            PrintWriter pw = new PrintWriter(bw);
            pw.println(textResultAccuracy);

            bw.close();
            pw.close();

            //writing to file
            fileWriter = new FileWriter(new File("data/shapiro_time.txt"));
            bw = new BufferedWriter(fileWriter);
            pw = new PrintWriter(bw);
            pw.println(textResultTime);

            bw.close();
            pw.close();

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return textResultAccuracy;
    }

    public String generateTTest() {
        System.out.println("Began T-test");
        String textResult = "";
        String textResultTime = "";
        int numberofColumns = 0;
        try {
            File controlResultFile = new File("studyResults/" + QUANT_CONTROLCONDITION_RESULT_FILE);
            BufferedReader br = new BufferedReader(new FileReader(controlResultFile));
            String line = "";
            while ((line = br.readLine()) != null) {
                String split[] = line.split(",");
                numberofColumns = split.length;
                break;
            }
            br.close();
            if ((numberofColumns == 0)) {
                textResult = "Empty result file";
            } else {
                re.eval("mydataCon = read.csv(\"studyResults/" + QUANT_CONTROLCONDITION_RESULT_FILE + "\")");
                re.eval("mydataTest = read.csv(\"studyResults/" + QUANT_TESTCONDITION_RESULT_FILE + "\")");

                REXP rexpCon, rexpTest;
                rexpCon = re.eval("names(mydataCon)");
                rexpTest = re.eval("names(mydataTest)");
                String controlColumnNames[] = rexpCon.asStringArray();
                String testColumnNames[] = rexpTest.asStringArray();

                String strArr[];
                //  String strArrTest[];
                for (int i = 0; i < controlColumnNames.length; i++) {
                    re.eval("" + controlColumnNames[i] + "= c(mydataCon[," + (i + 1) + "])");
                    re.eval("" + testColumnNames[i] + "= c(mydataTest[," + (i + 1) + "])");
                    re.eval("results<-capture.output(t.test(" + controlColumnNames[i] + "," + testColumnNames[i] + ", paired=TRUE, alt=\"less\"))");
                    strArr = re.eval("results").asStringArray();

                    for (int j = 0; j < strArr.length; j++) {
                        textResult += strArr[j] + "\n";
                    }

                }
                //------------------------------------------------
                re.eval("mydataCon = read.csv(\"studyResults/" + "testDurationControl.txt" + "\")");
                re.eval("mydataTest = read.csv(\"studyResults/" + "testDurationTest.txt" + "\")");

                // REXP rexpCon, rexpTest;
                rexpCon = re.eval("names(mydataCon)");
                rexpTest = re.eval("names(mydataTest)");
                controlColumnNames = rexpCon.asStringArray();
                testColumnNames = rexpTest.asStringArray();

                //String strArr[];
                for (int i = 0; i < controlColumnNames.length; i++) {
                    re.eval("" + controlColumnNames[i] + "= c(mydataCon[," + (i + 1) + "])");
                    re.eval("" + testColumnNames[i] + "= c(mydataTest[," + (i + 1) + "])");
                    re.eval("results<-capture.output(t.test(" + controlColumnNames[i] + "," + testColumnNames[i] + ", paired=TRUE, alt=\"less\"))");
                    strArr = re.eval("results").asStringArray();

                    for (int j = 0; j < strArr.length; j++) {
                        textResultTime += strArr[j] + "\n";
                    }

                }

            }
            //re.end();
            System.out.println("------------T-test---generated successfully------------");

            //writing to file
            FileWriter fileWriter = new FileWriter(new File("studyResults/t_test_accuracy.txt"));
            BufferedWriter bw = new BufferedWriter(fileWriter);
            PrintWriter pw = new PrintWriter(bw);
            pw.println(textResult);

            bw.close();
            pw.close();

            fileWriter = new FileWriter(new File("studyResults/t_test_time.txt"));
            bw = new BufferedWriter(fileWriter);
            pw = new PrintWriter(bw);
            pw.println(textResultTime);

            bw.close();
            pw.close();

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return textResult;
    }
    
    
    
    public static void main(String[] args){
        UserStudyResultsEvaluation usrev = new UserStudyResultsEvaluation();
        
    }
    
}
