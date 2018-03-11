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
import java.util.HashMap;
import javax.servlet.RequestDispatcher;
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
public class StudyResults extends HttpServlet {

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    HashMap<String, ResultParameters> resultParameters = new HashMap<String, ResultParameters>();

    boolean windows = true;

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        try {
            String msg = "";
            HttpSession session = request.getSession();

            String studyname = request.getParameter("studyname");
            String studydataurl;

            System.out.println("***** " + studyname);
            //this is the first this url has been called
            if (request.getParameter("studyname") != null) {
                //get the studyname and userid and saved them in sessions to be accessed later.
                studyname = request.getParameter("studyname");
                String userid = request.getParameter("userid");

                session.setAttribute("studyname", studyname);
                session.setAttribute("userid", userid);
                RequestDispatcher view = request.getRequestDispatcher("result-graphs.html");
                view.forward(request, response);

            } else {
                String nameofstudy;
                //getStudyId
                String command = request.getParameter("command");

                //System.out.println("\t\tThe command is " + command);
                String userid = session.getAttribute("userid").toString();

                //this is the first command that will be executed.
                if (command.equalsIgnoreCase("getStudyId")) {
                    nameofstudy = session.getAttribute("studyname").toString();
                } else {
                    nameofstudy = request.getParameter("studyid").toString();
                }

                String studyId = session.getId() + "_" + userid + "_" + nameofstudy;

                //      studyname = session.getAttribute("studyname").toString();
                // System.out.println("studyname is " + studyname);;
                //  studydataurl = "studies" + File.separator + nameofstudy + File.separator + "data";
                studydataurl = "users" + File.separator + userid + File.separator
                        + "studies" + File.separator + nameofstudy + File.separator + "data";

                ResultParameters rpmts = (ResultParameters) resultParameters.get(studyId);

                if (rpmts == null) { // first time
                    // System.out.println("^^^^^ SESSION IS NULL");
                    rpmts = new ResultParameters();
                }

                rpmts.studydataurl = studydataurl;

                loadStudyDetails(request, rpmts);

                String url = request.getRequestURL().toString();
                //System.out.println("URL:: " + url);

                if (command != null) {
                    if (command.equalsIgnoreCase("getStudyId")) {
                        //return the studyname and the user id;.

                        msg = nameofstudy;
                    } else if (command.equalsIgnoreCase("getExperimentType")) {
                        //get experiment type and return it.
                        msg = rpmts.studyType;
                    } else if (command.equalsIgnoreCase("getNumberOfTasks")) {
                        msg = "" + rpmts.numOfTasks;
                    } else if (command.equalsIgnoreCase("getAccuracyAnalysis")) {
                        msg = getAccuracyResults(rpmts);
                    } else if (command.equalsIgnoreCase("getTimeAnalysis")) {
                        msg = getTimeResults(rpmts);
                    } else if (command.equalsIgnoreCase("getCompletionStats")) {
                        msg = getCompletionStats(rpmts);
                    } else if (command.equalsIgnoreCase("getRAnalysis")) {
                        generateFilenames(rpmts);
                //for now I will be getting both the shapiro-wilk and ttest.
                        //TODO: check the specific analysis(es) the user wants and do that.

                        //get the study type
                        // rpmts.studyType = getStudyType();
                        //System.out.println("STUDY TYPE IS :: " + rpmts.studyType);
                        ArrayList<String> normalTasks = new ArrayList<String>();
                        ArrayList<String> nonNormalTasks = new ArrayList<String>();

                        msg += generateShapiroWilk(normalTasks, nonNormalTasks, rpmts);

                        msg += "::" + generateMeanAndStandardDeviation(rpmts);

                        //pass the respective analysis based on the normality of the results
                        msg += "::";
                        msg += getTypeOfAnalysis(normalTasks, nonNormalTasks, rpmts);

                        //   msg += "::";
                        //  msg += generateTTest(rpmts.studyType, rpmts);
                        //   msg += "::";
                        //   generateANOVA(rpmts.studyType, rpmts);
                        //replace all nextLines with <br />
                        //   msg =  msg.replaceAll("\\r?\\n", "<br />");
                        //replace all tabs in the string with 3 &nbsp;
                        // msg = msg.replaceAll("\\\"", "&nbsp;");
                        // msg = msg.replaceAll("\\\\t","&nbsp;");
                        // msg = msg.replaceAll("\\[(.*?)\\]","&nbsp;");
                    } else if (command.equalsIgnoreCase("getCombinedRawDataFilename")) {
                        /**
                         * Returns a filename of a combined summarized data or
                         * basic data.
                         */
                        //compute the raw data and return the 
                        String type = request.getParameter("type");

                        if (type.equalsIgnoreCase("summarized")) {
                            getAccuracyResults(rpmts);
                            getTimeResults(rpmts);
                            msg = getSummarizedRawData(rpmts);

                        } else {
                            getAccuracyResultsBasic(rpmts);
                            getTimeResultsBasic(rpmts);
                            msg = getBasicRawData(rpmts);
                        }
                    } else if (command.equalsIgnoreCase("getAllRawDataFilenames")) {
                        /**
                         * Returns the individual filenames (time or accuracy)
                         * for either summarized or basic.
                         */

                        String type = request.getParameter("type");

                        String filenames = "";

                        if (type.equalsIgnoreCase("summarized")) {
                            for (int i = 0; i < rpmts.numOfConditions; i++) { //Read the results from file

                                String acc_filename = rpmts.studydataurl + File.separator + "AccuracyResults" + (i + 1) + ".txt";
                                String time_filename = rpmts.studydataurl + File.separator + "TimeResults" + (i + 1) + ".txt";;

                                if (i == 0) {
                                    //this is the first time    
                                    filenames = acc_filename
                                            + "::" + time_filename;
                                } else {

                                    filenames += "::::" + acc_filename
                                            + "::" + time_filename;
                                }

                            }

                        }

                        msg = filenames;
                    }
                }/*else if(command.equalsIgnoreCase("getRawData")){
                    
                 }*/


                resultParameters.put(studyId, rpmts);

                out = response.getWriter();
                out.write(msg);
                out.flush();
                out.close();

            }
//            else {
//                System.out.println("---It seems they are both NULL!!");
//                msg = "study name was not given; try again with the studyname";
//            }

        } finally {
            out.close();
        }
    }

    public void loadStudyDetails(HttpServletRequest request, ResultParameters rpmts) {

        //taskTypes = new ArrayList<String>();
        String datasetname = "";
        try {
            //read the xml file that contains the details about the quantitative questions    
            String filename = getServletContext().getRealPath(rpmts.studydataurl + File.separator + "quantitativeTasks.xml");
            File fXmlFile = new File(filename);
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(fXmlFile);

            //optional, but recommended
            //read this - http://stackoverflow.com/questions/13786607/normalization-in-dom-parsing-with-java-how-does-it-work
            doc.getDocumentElement().normalize();

            //System.out.println("Root element :" + doc.getDocumentElement().getNodeName());
            NodeList taskNode = doc.getElementsByTagName("task");
            NodeList datasetNode = doc.getElementsByTagName("dataset");
            NodeList experimentTypeNode = doc.getElementsByTagName("experimenttype");
            NodeList conditionNode = doc.getElementsByTagName("condition");
            NodeList studynameNode = doc.getElementsByTagName("studyname");

            //get the dataset
            datasetname = ((Element) datasetNode.item(0)).getTextContent();
            rpmts.studyType = ((Element) experimentTypeNode.item(0)).getTextContent();
            rpmts.numOfConditions = conditionNode.getLength();
            rpmts.numOfTasks = taskNode.getLength();

            //get the condition shortnames
            for (int i = 0; i < conditionNode.getLength(); i++) {
                Node nNode = conditionNode.item(i);

                if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element eElement = (Element) nNode;

                    String conditionshortname = eElement.getElementsByTagName("conditionshortname").item(0).getTextContent();

                    rpmts.viewerConditionShortnames.add(conditionshortname);
                }
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    public String getTypeOfAnalysis(ArrayList<String> normalTasks, ArrayList<String> nonNormalTasks, ResultParameters rpmts) {
        String analysis = "";  //for the type of analysis that will be done

        String posthoc = ""; // for the posthoc of the analysis, if there is any.
        String effectSize = "";

        //TODO: Find the specific effect-size calculator depending on the analsyis
        //cohen's d for a between study 
        //Formula using tstats:
        //         1. Cohen's d = 2*t / sqrt(df)  i.e. if the sample sizes are equal
        //         2. Cohen's d = t(n1+n2)/sqrt((n1+n2 -2)*(n1*n2))    i.e If sample sizes are not equal
        //Formula that does not use tstats
        //              d = M1 - M2 / SD_pooled
        //      where:
        //              SD_pooled = sqrt((SD_1^2  + SD_2^2)/2) 
        //              SD_pooled = sqrt(((n1- 1)*SD_1^2 + (n2-1)*SD_2^2)/(n1+n2-2)
        // I will include the effect size in the calculation of them
        //http://www.polyu.edu.hk/mm/effectsizefaqs/effect_size_equations2.html
        //-------(http://www.lifesci.sussex.ac.uk/home/Zoltan_Dienes/inference/Neyman%20Pearson.html)
        //cohen's dz for a within study  Formula(1. Cohen's  dz=t/sqrt(n)
        ///      or 2. = M_diff/(sqrt(sum(X_diff-M_diff)^2)/N-1)) ----(http://www.ncbi.nlm.nih.gov/pmc/articles/PMC3840331/)
        //For non-parametric analysis Effect sizes:
        //     Wilcox rank sum or Mann-Whitney:   r = z/sqrt(N) : where N is the total number of participants n1+n2
        //For Wilcox signed-rank:   r /sqrt(N) : where N is the total number of observations (N*2).
        //For kruskal-Wallis find the effect for pairs:  r/sqrt(N) : where N is the  total number of the pairs.
        //Friedman is similar to kruskal wallis.
        //t-test to get the t use results$statistic, then get 
        if (normalTasks.size() > 0) {
            // System.out.println("***-normal***" + normalTasks.get(0));
            //there are some normal data so we will use a normalized            
            if (rpmts.numOfConditions == 2) { //we can use a ttest(it requires comparison between the two) or anova
                analysis += generateTTest(rpmts.studyType, rpmts, normalTasks);
            } else {
                //do an anova
                analysis += generateANOVA(rpmts.studyType, rpmts, normalTasks);
                posthoc = "::" + generateANOVAPostHoc(rpmts, normalTasks);
                //System.out.println("postposthoc " + posthoc);
            }
        }
        if (nonNormalTasks.size() > 0) {//

            if (!analysis.isEmpty()) {
                analysis += "::";
            }
            if (!posthoc.isEmpty()) {
                posthoc += "::";
            }

            //We will use a non parametric analysis here. Kruskal Wallis for a between-group and Friedman for a within-group
            if (rpmts.studyType.equalsIgnoreCase("Within")) {
                if (rpmts.numOfConditions == 2) {
                    //Wilcoxon-signed-rank test.
                    analysis += generateWilcoxonSignedRank(rpmts, nonNormalTasks);
                } else {
                    // Friedman's test
                    analysis += generateFriedmanTest(rpmts, nonNormalTasks);
                    //change this too.
                    //posthoc += "::";
                    posthoc += generateFriedmanTestPosttHoc(rpmts, nonNormalTasks);
                }

            } else {

                if (rpmts.numOfConditions == 2) {
                    // Wilcoxon-rank-sum-test
                    analysis += generateWilcoxonRankSum(rpmts, nonNormalTasks);
                } else {
                    //kruskal wallis
                    analysis += generateKruskalWallis(rpmts);
                    //posthoc for kruskal wallis
                    posthoc += "::" + generateKruskalWallisPostHoc(rpmts);
                }

            }
            //  generateKruskalWallis();
        }

        //System.out.println("The CONTENT OF VALUE IS ::: -->" + analysis + "::" + posthoc);
        return analysis + "::" + posthoc;
    }

    /**
     * This is a method to generate Wilcoxon Signed Rank test //Within
     *
     * @param rpmts
     * @param nonNormalTasks
     * @return
     */
    public String generateWilcoxonSignedRank(ResultParameters rpmts, ArrayList<String> nonNormalTasks) {
        //Write the R-Script
        String scriptFilename = "rscript-wilcoxonSignedRank.R";
        String scriptOutputFilename = "rscript-wilcoxonSignedRank.Rout"; //this is for standard output
        String scriptOutputFilename_requiredResult = "wilcoxon-signed-rank-analysis.txt"; //this will be the outputfilename for only the required result

        try {
            //first get the column names of the files
            BufferedReader br;// = new BufferedReader(new FileReader(firstAccuracyFile));
            String line = "";
            int numOfColumns = rpmts.numOfTasks;
            String[][] accColumnNames = new String[rpmts.numOfConditions][rpmts.numOfTasks];
            String[][] timeColumnNames = new String[rpmts.numOfConditions][rpmts.numOfTasks];
            int numOfRows = 0;
            int cnt = 0;
            for (int i = 0; i < rpmts.numOfConditions; i++) {
                File AccuracyFile = new File(getServletContext().getRealPath(rpmts.studydataurl + File.separator + rpmts.accuracyFilenames.get(i)));
                br = new BufferedReader(new FileReader(AccuracyFile));

                cnt = 0;
                while ((line = br.readLine()) != null) {
                    String split[] = line.split(",");
                    if (cnt == 0) {
                        for (int j = 0; j < split.length; j++) {
                            accColumnNames[i][j] = split[j];
                        }
                    }
                    cnt++;
                    //break;
                }
                br.close();
            }
            numOfRows = cnt - 1;
            //  System.out.println("The number of lines is ::  "+ numOfRows);

            for (int i = 0; i < rpmts.numOfConditions; i++) {
                File timeFile = new File(getServletContext().getRealPath(rpmts.studydataurl + File.separator + rpmts.timeFilenames.get(i)));
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

            String filePath = getServletContext().getRealPath(rpmts.studydataurl + File.separator + scriptFilename);
            String dataPath = getServletContext().getRealPath(rpmts.studydataurl);

            File scriptFile = new File(filePath);
            FileWriter fileWriter = new FileWriter(scriptFile);
            BufferedWriter bw = new BufferedWriter(fileWriter);
            PrintWriter pw = new PrintWriter(bw);

            if (dataPath.indexOf("\\") >= 0) {
                dataPath = dataPath.replaceAll("\\\\", "\\\\\\\\");
            }
            //first set the working directory
            pw.println("setwd(\"" + dataPath + "\")");
            pw.println("sink(\"" + scriptOutputFilename_requiredResult + "\")");

            //read the accuracy files in r
            for (int i = 0; i < rpmts.numOfConditions; i++) {
                pw.println("accuracy" + (i + 1) + " = read.csv(\"" + rpmts.accuracyFilenames.get(i) + "\")");
            }
            //read the time files
            for (int i = 0; i < rpmts.numOfConditions; i++) {
                pw.println("time" + (i + 1) + " = read.csv(\"" + rpmts.timeFilenames.get(i) + "\")");
            }

            boolean nonNormalAccuracy = false;
            /**
             * ** For Accuracy ****
             */

            /**
             * first print the header
             */
            //  pw.println("cat(\"\\n--------------------------------------------------------------\")");
            pw.println("cat(paste(\"\\n" + "Accuracy-Taskname" + " ,\" ,\"" + "p-value" + ",\" , \"" + "Effect-Size(r=z/sqrt(n))\"))");
            pw.println("cat(\"\\n--------------------------------------------------------------\")");

            for (int i = 0; i < accColumnNames[0].length; i++) {
                //check if task is part of the non-normal tasks
                String tname = accColumnNames[0][i].substring(0,
                        accColumnNames[0][i].lastIndexOf("_")); //first name without the condition name

                for (String task : nonNormalTasks) {
                    System.out.println(task + "::" + tname);
                    if (task.trim().equalsIgnoreCase(tname.trim())) {
                        nonNormalAccuracy = true;
                        break;
                    }
                }

                //if(nonNormalAccuracy)
//
                if (nonNormalAccuracy) {

                    String dataName1 = "accuracy1";
                    String dataName2 = "accuracy2";

                    //get the task name 
                    String taskname = accColumnNames[0][i].substring(0, accColumnNames[0][i].lastIndexOf("_")); //first name without the condition name
                   /* pw.println("taskname=\"TaskName = " + taskname + "\"");
                     pw.println("cat(taskname)");  */

                    //       for (int j = 0; j < accColumnNames[i].length; j++) {
                    pw.println("" + accColumnNames[0][i] + "= c(" + dataName1 + "[," + (i + 1) + "])");
                    pw.println("" + accColumnNames[1][i] + "= c(" + dataName2 + "[," + (i + 1) + "])");

                    String cbindData = "combineddata =data.frame(" + accColumnNames[0][i] + ", " + accColumnNames[1][i] + ")";
                    pw.println(cbindData);
                    pw.println("combineddata = stack(combineddata)");
                    pw.println("result = wilcox.test(values~ind, combineddata, paired=TRUE)");

                    //calculate the effect size
                    //     Wilcox rank sum or Mann-Whitney:  
                    //     r = z/sqrt(N) : where N is the total number of participants n1+n2
                    pw.println("size1 = length(c(" + dataName1 + "[," + (i + 1) + "]))");
                    pw.println("size2 = length(c(" + dataName2 + "[," + (i + 1) + "]))");
                    //now calculating the z first: formula: z = (W - (n1*n2)/2 )/sqrt(n1*n2(n1+n2+n1)/12) 
                    pw.println("num = result$statistic - (size1*size2)/2");
                    pw.println("denom = sqrt(((size1*size2) * (size1+size2+1))/12)");
                    pw.println("es = num/denom");

                    pw.println("cat(paste(\"\\n" + taskname + "\", " + "\" , \"" + " , result$p.value, "
                            + "\" , " + "\"" + ", abs(es) , " + "\"\\n\" ))");
                    //pw.println("cat(paste(\"\\n" + taskname + "\", " + "\" , \"" + " , result$p.value, " + "\"\\n\" ))");
                }
            }

            /**
             * Now lets work on the time data too
             */
            /*First print the  header */
            pw.println("cat(paste(\"\\n\\n\\n" + "Time-Taskname" + " ,\" ,\"" + "p-value\"))");//get mean and standard deviation
            pw.println("cat(\"\\n--------------------------------------------------------------\")");

            for (int i = 0; i < timeColumnNames[0].length; i++) {
                //check if task is part of the non-normal tasks
                String tname = timeColumnNames[0][i].substring(0,
                        timeColumnNames[0][i].lastIndexOf("_")); //first name without the condition name

                boolean nonNormalTime = false;
                for (String task : nonNormalTasks) {
                    if (task.trim().equalsIgnoreCase(tname.trim())) {
                        nonNormalTime = true;
                        break;
                    }
                }

                if (nonNormalTime) {

                    String dataName1 = "time1"; // + (i + 1);
                    String dataName2 = "time2";// + (i + 2);
                    //print the taskname so that we can know which anova this belongs to 
                    String taskname = timeColumnNames[0][i].substring(0, timeColumnNames[0][i].lastIndexOf("_")); //first name without the condition name
                    //remember there are only 2 conditions so the indexes will be zero and 1.
                    //   for (int j = 0; j < timeColumnNames[0].length; j++) {
                    pw.println("" + timeColumnNames[0][i] + "= c(" + dataName1 + "[," + (i + 1) + "])");
                    pw.println("" + timeColumnNames[1][i] + "= c(" + dataName2 + "[," + (i + 1) + "])");

                    taskname = timeColumnNames[0][i].substring(0, timeColumnNames[0][i].lastIndexOf("_")); //first name without the condition name

                    String cbindData = "combineddata =data.frame(" + timeColumnNames[0][i] + ", " + timeColumnNames[1][i] + ")";
                    pw.println(cbindData);
                    pw.println("combineddata = stack(combineddata)");
                    pw.println("result = wilcox.test(values~ind, combineddata, paired=TRUE)");

                    //calculate the effect size
                    //Wilcox rank sum or Mann-Whitney:  
                    //r = z/sqrt(N) : where N is the total number of participants n1+n2
                    pw.println("size1 = length(c(" + dataName1 + "[," + (i + 1) + "]))");
                    pw.println("size2 = length(c(" + dataName2 + "[," + (i + 1) + "]))");
                    //now calculating the z first: formula: z = (W - (n1*n2)/2 )/sqrt(n1*n2(n1+n2+n1)/12) 
                    pw.println("num = result$statistic - (size1*size2)/2");
                    pw.println("denom = sqrt(((size1*size2) * (size1+size2+1))/12)");
                    pw.println("es = num/denom");

                    pw.println("cat(paste(\"\\n" + taskname + "\", " + "\" , \"" + " , result$p.value, "
                            + "\" , " + "\"" + ", abs(es) , " + "\"\\n\" ))");

                    //  pw.println("cat(paste(\"\\n" + taskname + "\", " + "\" , \"" + " , result$p.value, " + "\"\\n\" ))");
                }

            }
            pw.println("sink()");
            //close the printwriter
            pw.close();

            runOSCommandForR(scriptFilename, scriptOutputFilename, rpmts);

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        //read the output file as string and return it.
        String analysisOutput = "";

        analysisOutput = scriptOutputFilename_requiredResult;//fileContentAsString(scriptOutputFilename);

        analysisOutput = rpmts.studydataurl + File.separator + analysisOutput;
        if (File.separatorChar != '/') {
            analysisOutput = analysisOutput.replace(File.separatorChar, '/');
        }

        return analysisOutput;

    }

    /**
     * //Between
     *
     * @param rpmts
     * @param nonNormalTasks
     * @return
     */
    public String generateWilcoxonRankSum(ResultParameters rpmts, ArrayList<String> nonNormalTasks) {
        //Write the R-Script
        String scriptFilename = "rscript-wilcoxonRankSum.R";
        String scriptOutputFilename = "rscript-wilcoxonRankSum.Rout"; //this is for standard output
        String scriptOutputFilename_requiredResult = "wilcoxon-rank-sum-analysis.txt"; //this will be the outputfilename for only the required result

        try {
            //first get the column names of the files
            BufferedReader br;// = new BufferedReader(new FileReader(firstAccuracyFile));
            String line = "";
            int numOfColumns = rpmts.numOfTasks;
            String[][] accColumnNames = new String[rpmts.numOfConditions][rpmts.numOfTasks];
            String[][] timeColumnNames = new String[rpmts.numOfConditions][rpmts.numOfTasks];
            int numOfRows = 0;
            int cnt = 0;
            for (int i = 0; i < rpmts.numOfConditions; i++) {
                File AccuracyFile = new File(getServletContext().getRealPath(rpmts.studydataurl + File.separator + rpmts.accuracyFilenames.get(i)));
                br = new BufferedReader(new FileReader(AccuracyFile));

                cnt = 0;
                while ((line = br.readLine()) != null) {
                    String split[] = line.split(",");
                    if (cnt == 0) {
                        for (int j = 0; j < split.length; j++) {
                            accColumnNames[i][j] = split[j];
                        }
                    }
                    cnt++;
                    //break;
                }
                br.close();
            }
            numOfRows = cnt - 1;

            for (int i = 0; i < rpmts.numOfConditions; i++) {
                File timeFile = new File(getServletContext().getRealPath(rpmts.studydataurl + File.separator + rpmts.timeFilenames.get(i)));
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

            String filePath = getServletContext().getRealPath(rpmts.studydataurl + File.separator + scriptFilename);
            String dataPath = getServletContext().getRealPath(rpmts.studydataurl);

            File scriptFile = new File(filePath);
            FileWriter fileWriter = new FileWriter(scriptFile);
            BufferedWriter bw = new BufferedWriter(fileWriter);
            PrintWriter pw = new PrintWriter(bw);

            if (dataPath.indexOf("\\") >= 0) {
                dataPath = dataPath.replaceAll("\\\\", "\\\\\\\\");
            }
            //first set the working directory
            pw.println("setwd(\"" + dataPath + "\")");
            pw.println("sink(\"" + scriptOutputFilename_requiredResult + "\")");

            //read the accuracy files in r
            for (int i = 0; i < rpmts.numOfConditions; i++) {
                pw.println("accuracy" + (i + 1) + " = read.csv(\"" + rpmts.accuracyFilenames.get(i) + "\")");
            }
            //read the time files
            for (int i = 0; i < rpmts.numOfConditions; i++) {
                pw.println("time" + (i + 1) + " = read.csv(\""
                        + rpmts.timeFilenames.get(i) + "\")");
            }

            boolean nonNormalAccuracy = false;
            /**
             * ** For Accuracy ****
             */

            /**
             * first print the header
             */
            //  pw.println("cat(\"\\n--------------------------------------------------------------\")");
            pw.println("cat(paste(\"\\n" + "Accuracy-Taskname" + " ,\" ,\"" + "p-value" + ",\" , \"" + "Effect-Size (r=z/sqrt(n))\"))");//get mean and standard deviation//get mean and standard deviation
            pw.println("cat(\"\\n--------------------------------------------------------------\")");

            for (int i = 0; i < accColumnNames[0].length; i++) {
                //check if task is part of the non-normal tasks
                String tname = accColumnNames[0][i].substring(0,
                        accColumnNames[0][i].lastIndexOf("_")); //first name without the condition name

                for (String task : nonNormalTasks) {
                    System.out.println(task + "::" + tname);
                    if (task.trim().equalsIgnoreCase(tname.trim())) {

                        nonNormalAccuracy = true;
                        break;
                    }
                }

                //if(nonNormalAccuracy)
//
                if (nonNormalAccuracy) {

                    //   System.out.println("-----*****8----- " + tname);
                    String dataName1 = "accuracy1";
                    String dataName2 = "accuracy2";
                    //print the taskname so that we can know which anova this belongs to 
                    String taskname = accColumnNames[0][i].substring(0, accColumnNames[0][i].lastIndexOf("_")); //first name without the condition name
                   /* pw.println("taskname=\"TaskName = " + taskname + "\"");
                     pw.println("cat(taskname)");  */

                    //       for (int j = 0; j < accColumnNames[i].length; j++) {
                    pw.println("" + accColumnNames[0][i] + "= c(" + dataName1 + "[," + (i + 1) + "])");
                    pw.println("" + accColumnNames[1][i] + "= c(" + dataName2 + "[," + (i + 1) + "])");

                   // taskname = accColumnNames[0][i].substring(0, accColumnNames[0][i].lastIndexOf("_")); //first name without the condition name
                        /* String cbindData = "combineddata =data.frame(cbind(" + accColumnNames[i][j] + ", " + accColumnNames[i + 1][j] + "))";
                     pw.println(cbindData);
                     pw.println("combineddata = stack(combineddata)"); */
                    // pw.println("combineddata");
                    pw.println("result = wilcox.test(" + accColumnNames[0][i] + "," + accColumnNames[1][i] + ")");

                    //calculate the effect size
                    //     Wilcox rank sum or Mann-Whitney:  
                    //     r = z/sqrt(N) : where N is the total number of participants n1+n2
                    pw.println("size1 = length(c(" + dataName1 + "[," + (i + 1) + "]))");
                    pw.println("size2 = length(c(" + dataName2 + "[," + (i + 1) + "]))");
                    //now calculating the z first: formula: z = (W - (n1*n2)/2 )/sqrt(n1*n2(n1+n2+n1)/12) 
                    pw.println("num = result$statistic - (size1*size2)/2");
                    pw.println("num = num / sqrt(((size1*size2) * (size1+size2+1))/12)");
                    pw.println("denom = sqrt(size1 + size2)");
                    pw.println("es = num/denom");
                    
                    System.out.println("^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^");

                    pw.println("cat(paste(\"\\n" + taskname + "\", " + "\" , \"" + " , result$p.value, "
                            + "\" , " + "\"" + ", abs(es) , " + "\"\\n\" ))");
                }

            }

            /**
             * * Testing what the loop does
             */
            //write the accuracy analysis
         /*   for (int i = 0; i < rpmts.numOfConditions - 1; i++) {

             String dataName1 = "accuracy" + (i + 1);
             //String dataName2 = "accuracy" + (i + 2);

             for (int k = i; k < rpmts.numOfConditions - 1; k++) {
             String dataName2 = "accuracy" + (k + 2);
             pw.println("tasknames = NULL");
             pw.println("pvalues = NULL");

             int numberOfTasksDone = 0;
             for (int j = 0; j < accColumnNames[i].length; j++) {

             //check if the task is part of the non-normal before you do this
             String tname = accColumnNames[i][j].substring(0, accColumnNames[i][j].lastIndexOf("_")); //first name without the condition name

             boolean nonNormalAccuracy = false;

             for (String task : nonNormalTasks) {
             if (task.trim().equalsIgnoreCase(tname.trim())) {
             nonNormalAccuracy = true;
             break;
             }
             }
             //do this for the normal tasks
             if (nonNormalAccuracy) {
             numberOfTasksDone++;
             String taskname = accColumnNames[i][j] + "-" + accColumnNames[k + 1][j];
             pw.println("" + accColumnNames[i][j] + "= c(" + dataName1 + "[," + (j + 1) + "])");
             pw.println("" + accColumnNames[k + 1][j] + "= c(" + dataName2 + "[," + (j + 1) + "])");

             String cbindData = "combineddata =data.frame(" + accColumnNames[i][j] + ", " + accColumnNames[k + 1][j] + ")";
             pw.println(cbindData);
             pw.println("combineddata = stack(combineddata)");
             // pw.println("combineddata");
             pw.println("results = wilcox.test(values~ind, combineddata, paired=TRUE)");

             pw.println("tasknames[" + numberOfTasksDone + "] = \"" + taskname + "\"");
             pw.println("pvalues[" + numberOfTasksDone + "] = " + "results$p.value");
             }
             }

             if (numberOfTasksDone > 0) {
             //adjust the pvalues{
             pw.println("pvalues_adj = p.adjust(pvalues, \"bonferroni\")");
             //  pw.println("pvalues_adj");
             //now print the name and the adjusted pvalues to file
             for (int j = 1; j <= numberOfTasksDone; j++) {
             pw.println("paste(tasknames[" + j + "] , " + "\",\" , pvalues_adj[" + j + "])");//get the pvalue for each column name
             }
             }

             }

             }
                
             */
            /**
             * ** For Time **
             */
            /*First print the  header */
            //  pw.println("cat(\"\\n\\n\\n--------------------------------------------------------------\")");
            pw.println("cat(paste(\"\\n\\n\\n" + "Time-Taskname" + " ,\" ,\"" + "p-value" + ",\" , \"" + "Effect-Size (r=z/sqrt(n))\"))");//get mean and standard deviation
            pw.println("cat(\"\\n--------------------------------------------------------------\")");

            for (int i = 0; i < timeColumnNames[0].length; i++) {
                //check if task is part of the non-normal tasks
                String tname = timeColumnNames[0][i].substring(0,
                        timeColumnNames[0][i].lastIndexOf("_")); //first name without the condition name

                // System.out.println("I'm ---- here");
                //   System.out.println("---- # of conditions --- " + rpmts.numOfConditions);
                boolean nonNormalTime = false;
                for (String task : nonNormalTasks) {
                    if (task.trim().equalsIgnoreCase(tname.trim())) {
                        //   System.out.println("^^^___^^^ " + tname);

                        nonNormalTime = true;
                        break;
                    }
                }

                if (nonNormalTime) {

                    String dataName1 = "time1"; // + (i + 1);
                    String dataName2 = "time2";// + (i + 2);
                    //print the taskname so that we can know which anova this belongs to 
                    String taskname = timeColumnNames[0][i].substring(0, timeColumnNames[0][i].lastIndexOf("_")); //first name without the condition name
                    pw.println("taskname=\"TaskName = " + taskname + "\"");
                    // pw.println("cat(taskname)");

                    //remember there are only 2 conditions so the indexes will be zero and 1.
                    //   for (int j = 0; j < timeColumnNames[0].length; j++) {
                    pw.println("" + timeColumnNames[0][i] + "= c(" + dataName1 + "[," + (i + 1) + "])");
                    pw.println("" + timeColumnNames[1][i] + "= c(" + dataName2 + "[," + (i + 1) + "])");

                    taskname = timeColumnNames[0][i].substring(0, timeColumnNames[0][i].lastIndexOf("_")); //first name without the condition name

                    pw.println("result = wilcox.test(" + timeColumnNames[0][i] + "," + timeColumnNames[1][i] + ")");

                    //calculate the effect size
                    //     Wilcox rank sum or Mann-Whitney:  
                    //     r = z/sqrt(N) : where N is the total number of participants n1+n2
                    pw.println("size1 = length(c(" + dataName1 + "[," + (i + 1) + "]))");
                    pw.println("size2 = length(c(" + dataName2 + "[," + (i + 1) + "]))");
                    //now calculating the z first: formula: z = (W - (n1*n2)/2 )/sqrt(n1*n2(n1+n2+n1)/12) 
                    pw.println("num = result$statistic - (size1*size2)/2");
                    pw.println("denom = sqrt(((size1*size2) * (size1+size2+1))/12)");
                    pw.println("es = num/denom");

                    pw.println("cat(paste(\"\\n" + taskname + "\", " + "\" , \"" + " , result$p.value, "
                            + "\" , " + "\"" + ", abs(es) , " + "\"\\n\" ))");
                }

            }

            pw.println("sink()");
            //close the printwriter
            pw.close();

            runOSCommandForR(scriptFilename, scriptOutputFilename, rpmts);

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        //read the output file as string and return it.
        String analysisOutput = "";

        analysisOutput = scriptOutputFilename_requiredResult;//fileContentAsString(scriptOutputFilename);

        analysisOutput = rpmts.studydataurl + File.separator + analysisOutput;
        if (File.separatorChar != '/') {
            analysisOutput = analysisOutput.replace(File.separatorChar, '/');
        }

        return analysisOutput;
    }

    /**
     * This is kruskal-Wallis method
     *
     * @return
     */
    public String generateKruskalWallis(ResultParameters rpmts) {

        //data preparation is similar to the  between group anova        
        String scriptFilename = "rscript-kruskal.R";
        String scriptOutputFilename = "rscript-kruskal.Rout"; //this is for standard output
        String scriptOutputFilename_requiredResult = "kruskal-wallis-analysis.txt"; //this will be the outputfilename for only the required result
        try {
            //first get the column names of the files
            BufferedReader br;// = new BufferedReader(new FileReader(firstAccuracyFile));
            String line = "";
            int numOfColumns = rpmts.numOfTasks;
            String[][] accColumnNames = new String[rpmts.numOfConditions][rpmts.numOfTasks];
            String[][] timeColumnNames = new String[rpmts.numOfConditions][rpmts.numOfTasks];
            int numOfRows = 0;
            int cnt = 0;
            for (int i = 0; i < rpmts.numOfConditions; i++) {
                File AccuracyFile = new File(getServletContext().getRealPath(rpmts.studydataurl + File.separator + rpmts.accuracyFilenames.get(i)));
                br = new BufferedReader(new FileReader(AccuracyFile));

                cnt = 0;
                while ((line = br.readLine()) != null) {
                    String split[] = line.split(",");
                    if (cnt == 0) {
                        for (int j = 0; j < split.length; j++) {
                            accColumnNames[i][j] = split[j];
                        }
                    }
                    cnt++;
                    //break;
                }
                br.close();
            }
            numOfRows = cnt - 1;
            //  System.out.println("The number of lines is ::  "+ numOfRows);

            for (int i = 0; i < rpmts.numOfConditions; i++) {
                File timeFile = new File(getServletContext().getRealPath(rpmts.studydataurl + File.separator + rpmts.timeFilenames.get(i)));
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

            String filePath = getServletContext().getRealPath(rpmts.studydataurl + File.separator + scriptFilename);
            String dataPath = getServletContext().getRealPath(rpmts.studydataurl);

            File scriptFile = new File(filePath);
            FileWriter fileWriter = new FileWriter(scriptFile);
            BufferedWriter bw = new BufferedWriter(fileWriter);
            PrintWriter pw = new PrintWriter(bw);

            if (dataPath.indexOf("\\") >= 0) {
                dataPath = dataPath.replaceAll("\\\\", "\\\\\\\\");
            }
            //first set the working directory
            pw.println("setwd(\"" + dataPath + "\")");

            pw.println("sink(\"" + scriptOutputFilename_requiredResult + "\")");

            //read the accuracy files in r
            for (int i = 0; i < rpmts.numOfConditions; i++) {
                pw.println("accuracy" + (i + 1) + " = read.csv(\"" + rpmts.accuracyFilenames.get(i) + "\")");
            }
          //  System.out.println("**** The length of accColumnNames is  :: " + accColumnNames.length);

            //write the accuracy analysis
            for (int i = 0; i < rpmts.numOfTasks; i++) {

                ArrayList<String> colnames = new ArrayList<String>();
                for (int j = 0; j < rpmts.numOfConditions; j++) {
                    String dataName = "accuracy" + (j + 1);
                    pw.println("" + accColumnNames[j][i] + "= c(" + dataName + "[," + (i + 1) + "])");

                    colnames.add(accColumnNames[j][i]);
                }

                //print the taskname so that we can know which anova this belongs to 
                String taskname = accColumnNames[0][i].substring(0, accColumnNames[0][i].lastIndexOf("_")); //first name without the condition name
                pw.println("taskname=\"TaskName = " + taskname + "\"");
                pw.println("taskname");

                //do the r cbind
                //String cbindData = "combineddata =data.frame(cbind(";
                String colnameStr = "";
                for (int k = 0; k < colnames.size(); k++) {
                    //System.out.print("    " + coln);
                    if (k == 0) {
                        colnameStr += colnames.get(k);
                    } else {
                        colnameStr += "," + colnames.get(k);
                    }
                }
               // cbindData += "))";  //closing parentheses
                // pw.println(cbindData);

                //now do the stack before you do the kruskal
                // pw.println("combineddata = stack(combineddata)");
                pw.println("kruskalresult = kruskal.test(list(" + colnameStr + "))");

                pw.println("cat(paste(\"" + taskname + "\", " + "\" , \"" + " , kruskalresult$p.value, " + "\"\\n\" ))");
                pw.println("\"*********************\"");
                //pw.println("kruskalresult");
                //pw.println("cat(\"\\n\\n\")"); //leave a couple of lines to separate the 
            }
            /**
             * ******************************************************
             */
            /**
             * ********** Do the same for the time as well *********
             */
            //write the time analysis

            //read the accuracy files
            for (int i = 0; i < rpmts.numOfConditions; i++) {
                pw.println("time" + (i + 1) + " = read.csv(\"" + rpmts.timeFilenames.get(i) + "\")");
            }

            //write the time analysis
            for (int i = 0; i < rpmts.numOfTasks; i++) {
                ArrayList<String> colnames = new ArrayList<String>();
                for (int j = 0; j < rpmts.numOfConditions; j++) {
                    String dataName = "time" + (j + 1);
                    pw.println("" + timeColumnNames[j][i] + "= c(" + dataName + "[," + (i + 1) + "])");
                    colnames.add(timeColumnNames[j][i]);
                }

                //print the taskname so that we can know which anova this belongs to 
                String taskname = timeColumnNames[0][i].substring(0, timeColumnNames[0][i].lastIndexOf("_")); //first name without the condition name
                pw.println("taskname=\"TaskName = " + taskname + "\"");
                pw.println("taskname");

                String colnameStr = "";
                for (int k = 0; k < colnames.size(); k++) {
                    //System.out.print("    " + coln);
                    if (k == 0) {
                        colnameStr += colnames.get(k);
                    } else {
                        colnameStr += "," + colnames.get(k);
                    }
                }
               // cbindData += "))";  //closing parentheses
                // pw.println(cbindData);

                //now do the stack before you do the kruskal
                // pw.println("combineddata = stack(combineddata)");
                pw.println("kruskalresult = kruskal.test(list(" + colnameStr + "))");
                pw.println("cat(paste(\"" + taskname + "\", " + "\" , \"" + " , kruskalresult$p.value, " + "\"\\n\" ))");
                pw.println("\"*********************\"");
                //pw.println("kruskalresult");
                //pw.println("cat(\"\\n\\n\")"); //leave a couple of lines to separate the 
            }

            pw.println("sink()");
            //close the printwriter
            pw.close();

            runOSCommandForR(scriptFilename, scriptOutputFilename, rpmts);

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        //read the output file as string and return it.
        String analysisOutput = "";

        analysisOutput = scriptOutputFilename_requiredResult;//fileContentAsString(scriptOutputFilename);

        analysisOutput = rpmts.studydataurl + File.separator + analysisOutput;
        if (File.separatorChar != '/') {
            analysisOutput = analysisOutput.replace(File.separatorChar, '/');
        }

        return analysisOutput;

    }

    //posthoc for kruskal-wallis
    public String generateKruskalWallisPostHoc(ResultParameters rpmts) {
        //do Wilcoxon rank-sum test to compare pairs of the conditions followed by adjustment of the p-values with bonferroni correction.

        //Write the R-Script
        String scriptFilename = "rscript-kruskalPostHoc.R";
        String scriptOutputFilename = "rscript-kruskalPostHoc.Rout"; //this is for standard output
        String scriptOutputFilename_requiredResult = "kruskal-PostHoc-analysis.txt"; //this will be the outputfilename for only the required result

        try {
            //first get the column names of the files
            BufferedReader br;// = new BufferedReader(new FileReader(firstAccuracyFile));
            String line = "";
            int numOfColumns = rpmts.numOfTasks;
            String[][] accColumnNames = new String[rpmts.numOfConditions][rpmts.numOfTasks];
            String[][] timeColumnNames = new String[rpmts.numOfConditions][rpmts.numOfTasks];
            int numOfRows = 0;
            int cnt = 0;
            for (int i = 0; i < rpmts.numOfConditions; i++) {
                File AccuracyFile = new File(getServletContext().getRealPath(rpmts.studydataurl + File.separator + rpmts.accuracyFilenames.get(i)));
                br = new BufferedReader(new FileReader(AccuracyFile));

                cnt = 0;
                while ((line = br.readLine()) != null) {
                    String split[] = line.split(",");
                    if (cnt == 0) {
                        for (int j = 0; j < split.length; j++) {
                            accColumnNames[i][j] = split[j];
                        }
                    }
                    cnt++;
                    //break;
                }
                br.close();
            }
            numOfRows = cnt - 1;
            //  System.out.println("The number of lines is ::  "+ numOfRows);

            for (int i = 0; i < rpmts.numOfConditions; i++) {
                File timeFile = new File(getServletContext().getRealPath(rpmts.studydataurl + File.separator + rpmts.timeFilenames.get(i)));
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

            String filePath = getServletContext().getRealPath(rpmts.studydataurl + File.separator + scriptFilename);
            String dataPath = getServletContext().getRealPath(rpmts.studydataurl);

            File scriptFile = new File(filePath);
            FileWriter fileWriter = new FileWriter(scriptFile);
            BufferedWriter bw = new BufferedWriter(fileWriter);
            PrintWriter pw = new PrintWriter(bw);

            if (dataPath.indexOf("\\") >= 0) {
                dataPath = dataPath.replaceAll("\\\\", "\\\\\\\\");
            }
            //first set the working directory
            pw.println("setwd(\"" + dataPath + "\")");
            pw.println("sink(\"" + scriptOutputFilename_requiredResult + "\")");

            //read the accuracy files in r
            for (int i = 0; i < rpmts.numOfConditions; i++) {
                pw.println("accuracy" + (i + 1) + " = read.csv(\"" + rpmts.accuracyFilenames.get(i) + "\")");
            }
            //read the time files
            for (int i = 0; i < rpmts.numOfConditions; i++) {
                pw.println("time" + (i + 1) + " = read.csv(\"" + rpmts.timeFilenames.get(i) + "\")");
            }

            //do the wilcoxon rank-sum tests
            //write the accuracy analysis
            for (int i = 0; i < rpmts.numOfConditions - 1; i++) {
                String dataName1 = "accuracy" + (i + 1);
                //String dataName2 = "accuracy" + (i + 2);

                for (int k = i; k < rpmts.numOfConditions - 1; k++) {
                    String dataName2 = "accuracy" + (k + 2);
                    pw.println("tasknames = NULL");
                    pw.println("pvalues = NULL");
                    pw.println("ess = NULL");

                    for (int j = 0; j < accColumnNames[i].length; j++) {

                        String taskname = accColumnNames[i][j] + "-" + accColumnNames[k + 1][j];
                        pw.println("" + accColumnNames[i][j] + "= c(" + dataName1 + "[," + (j + 1) + "])");
                        pw.println("" + accColumnNames[k + 1][j] + "= c(" + dataName2 + "[," + (j + 1) + "])");

                        //calculate the effect sizes.. r = z/sqrt(n) -- where n is the number of 
                        pw.println("results = wilcox.test(" + accColumnNames[i][j] + "," + accColumnNames[k + 1][j] + ")");

                        //calculate the effect size
                        //     Wilcox effect size  
                        //     r = z/sqrt(N) : where N is the total number of participants n1+n2
                        pw.println("size1 = length(c(" + dataName1 + "[," + (i + 1) + "]))");
                        pw.println("size2 = length(c(" + dataName2 + "[," + (i + 1) + "]))");
                        //now calculating the z first: formula: z = (W - (n1*n2)/2 )/sqrt(n1*n2(n1+n2+n1)/12) 
                        pw.println("num = results$statistic - (size1*size2)/2");
                        pw.println("denom = sqrt(((size1*size2) * (size1+size2+1))/12)");
                        pw.println("ess[" + (j + 1) + "] = abs(num/denom)");

                        // pw.println("tasknames[" + numberOfTasksDone + "] = \"" + taskname + "\"");
                        //  pw.println("pvalues[" + numberOfTasksDone + "] = " + "results$p.value");
                        pw.println("tasknames[" + (j + 1) + "] = \"" + taskname + "\"");
                        pw.println("pvalues[" + (j + 1) + "] = " + "results$p.value");//                        
                    }
                    //adjust the pvalues{
                    pw.println("pvalues_adj = p.adjust(pvalues, \"bonferroni\")");
                    //now print the name and the adjusted pvalues to file
                    for (int j = 0; j < accColumnNames[i].length; j++) {

                        pw.println("cat(\"\\n\")");
                        //pw.println("cat(paste(tasknames[" + j + "] , " + "\",\" , pvalues_adj[" + j + "]))");//get the pvalue for each column name
                        pw.println("cat(paste(tasknames[" + (j + 1) + "] , " + "\",\" , pvalues_adj[" + (j + 1) + "], "
                                + "\",\" , ess[" + (j + 1) + "]))");//get the pvalue for each column name

                        //pw.println("paste(tasknames[" + (j + 1) + "] , " + "\",\" , pvalues_adj[" + (j + 1) + "])");//get the pvalue for each column name
                    }

                }

            }

            //time
            for (int i = 0; i < rpmts.numOfConditions - 1; i++) {
                String dataName1 = "time" + (i + 1);
                //  String dataName2 = "time" + (i + 2);

                for (int k = i; k < rpmts.numOfConditions - 1; k++) {
                    String dataName2 = "time" + (k + 2);
                    pw.println("tasknames = NULL");
                    pw.println("pvalues = NULL");
                    pw.println("ess = NULL");

                    for (int j = 0; j < timeColumnNames[i].length; j++) {
                        String taskname = timeColumnNames[i][j] + "-" + timeColumnNames[k + 1][j];
                        pw.println("" + timeColumnNames[i][j] + "= c(" + dataName1 + "[," + (j + 1) + "])");
                        pw.println("" + timeColumnNames[k + 1][j] + "= c(" + dataName2 + "[," + (j + 1) + "])");
                        String cbindData = "combineddata =data.frame(cbind(" + timeColumnNames[i][j] + ", " + timeColumnNames[k + 1][j] + "))";
                        pw.println(cbindData);
                        pw.println("combineddata = stack(combineddata)");
                        pw.println("results = wilcox.test(values~ind, combineddata)");

                        //calculate the effect size
                        //     Wilcox effect size  
                        //     r = z/sqrt(N) : where N is the total number of participants n1+n2
                        pw.println("size1 = length(c(" + dataName1 + "[," + (i + 1) + "]))");
                        pw.println("size2 = length(c(" + dataName2 + "[," + (i + 1) + "]))");
                        //now calculating the z first: formula: z = (W - (n1*n2)/2 )/sqrt(n1*n2(n1+n2+n1)/12) 
                        pw.println("num = results$statistic - (size1*size2)/2");
                        pw.println("denom = sqrt(((size1*size2) * (size1+size2+1))/12)");
                        pw.println("ess[" + (j + 1) + "] = abs(num/denom)");

                        pw.println("tasknames[" + (j + 1) + "] = \"" + taskname + "\"");
                        pw.println("pvalues[" + (j + 1) + "] = " + "results$p.value");
                    }
                    //adjust the pvalues{
                    pw.println("pvalues_adj = p.adjust(pvalues, \"bonferroni\")");
                    //now print the name and the adjusted pvalues to file
                    for (int j = 0; j < timeColumnNames[i].length; j++) {
                        pw.println("cat(\"\\n\")");
                        //pw.println("cat(paste(tasknames[" + j + "] , " + "\",\" , pvalues_adj[" + j + "]))");//get the pvalue for each column name
                        pw.println("cat(paste(tasknames[" + (j + 1) + "] , " + "\",\" , pvalues_adj[" + (j + 1) + "], "
                                + "\",\" , ess[" + (j + 1) + "]))");//get the pvalue for each column name                      

                        //pw.println("paste(tasknames[" + (j + 1) + "] , " + "\",\" , pvalues_adj[" + (j + 1) + "])");//get the pvalue for each column name
                    }

                }

            }
            pw.println("sink()");
            //close the printwriter
            pw.close();

            runOSCommandForR(scriptFilename, scriptOutputFilename, rpmts);

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        //read the output file as string and return it.
        String analysisOutput = "";

        analysisOutput = scriptOutputFilename_requiredResult;//fileContentAsString(scriptOutputFilename);

        analysisOutput = rpmts.studydataurl + File.separator + analysisOutput;
        if (File.separatorChar != '/') {
            analysisOutput = analysisOutput.replace(File.separatorChar, '/');
        }

        return analysisOutput;

    }

    //posthoc for friedmandtest
    public String generateFriedmanTestPosttHoc(ResultParameters rpmts, ArrayList<String> nonNormalTasks) {
        //do Wilcoxon signed-rank test to compare pairs of the conditions followed by adjustment of the p-values with bonferroni correction.
        //Write the R-Script
        String scriptFilename = "rscript-friedmanPostHoc.R";
        String scriptOutputFilename = "rscript-friedmanPostHoc.Rout"; //this is for standard output
        String scriptOutputFilename_requiredResult = "friedman-PostHoc-Analysis.txt"; //this will be the outputfilename for only the required result

        try {
            //first get the column names of the files
            BufferedReader br;// = new BufferedReader(new FileReader(firstAccuracyFile));
            String line = "";
            int numOfColumns = rpmts.numOfTasks;
            String[][] accColumnNames = new String[rpmts.numOfConditions][rpmts.numOfTasks];
            String[][] timeColumnNames = new String[rpmts.numOfConditions][rpmts.numOfTasks];
            int numOfRows = 0;
            int cnt = 0;
            for (int i = 0; i < rpmts.numOfConditions; i++) {
                File AccuracyFile = new File(getServletContext().getRealPath(rpmts.studydataurl + File.separator + rpmts.accuracyFilenames.get(i)));
                br = new BufferedReader(new FileReader(AccuracyFile));

                cnt = 0;
                while ((line = br.readLine()) != null) {
                    String split[] = line.split(",");
                    if (cnt == 0) {
                        for (int j = 0; j < split.length; j++) {
                            accColumnNames[i][j] = split[j];
                        }
                    }
                    cnt++;
                    //break;
                }
                br.close();
            }
            numOfRows = cnt - 1;
            //  System.out.println("The number of lines is ::  "+ numOfRows);

            for (int i = 0; i < rpmts.numOfConditions; i++) {
                File timeFile = new File(getServletContext().getRealPath(rpmts.studydataurl + File.separator + rpmts.timeFilenames.get(i)));
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

            String filePath = getServletContext().getRealPath(rpmts.studydataurl + File.separator + scriptFilename);
            String dataPath = getServletContext().getRealPath(rpmts.studydataurl);

            File scriptFile = new File(filePath);
            FileWriter fileWriter = new FileWriter(scriptFile);
            BufferedWriter bw = new BufferedWriter(fileWriter);
            PrintWriter pw = new PrintWriter(bw);

            if (dataPath.indexOf("\\") >= 0) {
                dataPath = dataPath.replaceAll("\\\\", "\\\\\\\\");
            }
            //first set the working directory
            pw.println("setwd(\"" + dataPath + "\")");
            pw.println("sink(\"" + scriptOutputFilename_requiredResult + "\")");

            //read the accuracy files in r
            for (int i = 0; i < rpmts.numOfConditions; i++) {
                pw.println("accuracy" + (i + 1) + " = read.csv(\"" + rpmts.accuracyFilenames.get(i) + "\")");
            }
            //read the time files
            for (int i = 0; i < rpmts.numOfConditions; i++) {
                pw.println("time" + (i + 1) + " = read.csv(\"" + rpmts.timeFilenames.get(i) + "\")");
            }

            /*    System.out.println("********************************************************");
             for (int i = 0; i < accColumnNames.length; i++) {
             for (int j = 0; j < accColumnNames[0].length; j++) {
             System.out.print(accColumnNames[i][j] + "\t");
             }
             System.out.println();
             }

             for (int i = 0; i < rpmts.numOfConditions - 1; i++) {

             String dataName1 = "accuracy" + (i + 1);

             for (int k = i; k < rpmts.numOfConditions - 1; k++) {
             String dataName2 = "accuracy" + (k + 2);
             for (int j = 0; j < accColumnNames[i].length; j++) {
             String taskname = accColumnNames[i][j] + "-" + accColumnNames[i + 1][j];
             System.out.println("" + accColumnNames[i][j] + "= c(" + dataName1 + "[," + (j + 1) + "])");
             System.out.println("" + accColumnNames[k + 1][j] + "= c(" + dataName2 + "[," + (j + 1) + "])");

             String cbindData = "combineddata =data.frame(" + accColumnNames[i][j] + ", " + accColumnNames[k + 1][j] + ")";
             System.out.println(cbindData);
             System.out.println("combineddata = stack(combineddata)");
             // pw.println("combineddata");
             System.out.println("results = wilcox.test(values~ind, combineddata, paired=TRUE)");

             System.out.println("tasknames[" + (j + 1) + "] = \"" + taskname + "\"");
             System.out.println("pvalues[" + (j + 1) + "] = " + "results$p.value");
             }
             System.out.println("pvalues_adj = p.adjust(pvalues, \"bonferroni\")");
             for (int j = 0; j < accColumnNames[i].length; j++) {
             System.out.println("paste(tasknames[" + (j + 1) + "] , " + "\",\" , pvalues_adj[" + (j + 1) + "])");//get the pvalue for each column name
             }
             }

             }
             System.out.println("********************************************************");

             */
            pw.println("cat(paste(\"\\n" + "Accuracy-Taskname" + " ,\" ,\"" + "p-value" + ",\" , \"" + "Effect-Size (r = z/sqrt(n))\"))");//get mean and standard deviation
            pw.println("cat(\"\\n--------------------------------------------------------------\")");

            // pw.println("\"task-name ,  pvalue\"");//get the pvalue for each column name
            //pw.println("\"------------------------------------\"");
            //do the wilcoxon rank-sum tests
            //write the accuracy analysis
            for (int i = 0; i < rpmts.numOfConditions - 1; i++) {

                String dataName1 = "accuracy" + (i + 1);
                //String dataName2 = "accuracy" + (i + 2);

                for (int k = i; k < rpmts.numOfConditions - 1; k++) {
                    String dataName2 = "accuracy" + (k + 2);
                    pw.println("tasknames = NULL");
                    pw.println("pvalues = NULL");
                    pw.println("ess = NULL");

                    int numberOfTasksDone = 0;
                    for (int j = 0; j < accColumnNames[i].length; j++) {

                        //check if the task is part of the non-normal before you do this
                        String tname = accColumnNames[i][j].substring(0, accColumnNames[i][j].lastIndexOf("_")); //first name without the condition name

                        boolean nonNormalAccuracy = false;

                        for (String task : nonNormalTasks) {
                            if (task.trim().equalsIgnoreCase(tname.trim())) {
                                nonNormalAccuracy = true;
                                break;
                            }
                        }
                        //do this for the normal tasks
                        if (nonNormalAccuracy) {
                            numberOfTasksDone++;
                            String taskname = accColumnNames[i][j] + "-" + accColumnNames[k + 1][j];
                            pw.println("" + accColumnNames[i][j] + "= c(" + dataName1 + "[," + (j + 1) + "])");
                            pw.println("" + accColumnNames[k + 1][j] + "= c(" + dataName2 + "[," + (j + 1) + "])");

                            String cbindData = "combineddata =data.frame(" + accColumnNames[i][j] + ", " + accColumnNames[k + 1][j] + ")";
                            pw.println(cbindData);
                            pw.println("combineddata = stack(combineddata)");
                            // pw.println("combineddata");
                            pw.println("results = wilcox.test(values~ind, combineddata, paired=TRUE)");

                            //calculate the effect size
                            //     Wilcox effect size  
                            //     r = z/sqrt(N) : where N is the total number of participants n1+n2
                            pw.println("size1 = length(c(" + dataName1 + "[," + (i + 1) + "]))");
                            pw.println("size2 = length(c(" + dataName2 + "[," + (i + 1) + "]))");
                            //now calculating the z first: formula: z = (W - (n1*n2)/2 )/sqrt(n1*n2(n1+n2+n1)/12) 
                            pw.println("num = results$statistic - (size1*size2)/2");
                            pw.println("denom = sqrt(((size1*size2) * (size1+size2+1))/12)");
                            pw.println("ess[" + numberOfTasksDone + "] = abs(num/denom)");

                            pw.println("tasknames[" + numberOfTasksDone + "] = \"" + taskname + "\"");
                            pw.println("pvalues[" + numberOfTasksDone + "] = " + "results$p.value");
                        }
                    }

                    if (numberOfTasksDone > 0) {
                        //adjust the pvalues{
                        pw.println("pvalues_adj = p.adjust(pvalues, \"bonferroni\")");
                        //  pw.println("pvalues_adj");
                        //now print the name and the adjusted pvalues to file
                        for (int j = 1; j <= numberOfTasksDone; j++) {
                            pw.println("cat(\"\\n\")");
                            //pw.println("cat(paste(tasknames[" + j + "] , " + "\",\" , pvalues_adj[" + j + "]))");//get the pvalue for each column name

                            pw.println("cat(paste(tasknames[" + j + "] , " + "\",\" , pvalues_adj[" + j + "], "
                                    + "\",\" , ess[" + j + "]))");//get the pvalue for each column name
                        }
                    }

                }

            }

            //time
            pw.println("cat(paste(\"\\n\\n\\n" + "Time-Taskname" + " ,\" ,\"" + "p-value" + ",\" , \"" + "Effect-Size (r = z/sqrt(n))\"))");//get mean and standard deviation
            pw.println("cat(\"\\n--------------------------------------------------------------\")");

            for (int i = 0; i < rpmts.numOfConditions - 1; i++) {
                String dataName1 = "time" + (i + 1);
                //  String dataName2 = "time" + (i + 2);

                for (int k = i; k < rpmts.numOfConditions - 1; k++) {
                    String dataName2 = "time" + (k + 2);
                    pw.println("tasknames = NULL");
                    pw.println("pvalues = NULL");
                    pw.println("ess = NULL");

                    int numberOfTasksDone = 0;
                    for (int j = 0; j < timeColumnNames[i].length; j++) {

                        //check if the task is part of the non-normal before you do this
                        String tname = timeColumnNames[i][j].substring(0, timeColumnNames[i][j].lastIndexOf("_")); //first name without the condition name

                        boolean nonNormalAccuracy = false;

                        for (String task : nonNormalTasks) {
                            if (task.trim().equalsIgnoreCase(tname.trim())) {
                                nonNormalAccuracy = true;
                                break;
                            }
                        }
                        //do this for the normal tasks
                        if (nonNormalAccuracy) {
                            numberOfTasksDone++;

                            String taskname = timeColumnNames[i][j] + "-" + timeColumnNames[k + 1][j];
                            pw.println("" + timeColumnNames[i][j] + "= c(" + dataName1 + "[," + (j + 1) + "])");
                            pw.println("" + timeColumnNames[k + 1][j] + "= c(" + dataName2 + "[," + (j + 1) + "])");
                            String cbindData = "combineddata =data.frame(" + timeColumnNames[i][j] + ", " + timeColumnNames[k + 1][j] + ")";
                            pw.println(cbindData);
                            pw.println("combineddata = stack(combineddata)");
                            // pw.println("combineddata");
                            pw.println("results = wilcox.test(values~ind, combineddata, paired=TRUE)");

                            //calculate the effect size
                            //     Wilcox effect size  
                            //     r = z/sqrt(N) : where N is the total number of participants n1+n2
                            pw.println("size1 = length(c(" + dataName1 + "[," + (i + 1) + "]))");
                            pw.println("size2 = length(c(" + dataName2 + "[," + (i + 1) + "]))");
                            //now calculating the z first: formula: z = (W - (n1*n2)/2 )/sqrt(n1*n2(n1+n2+n1)/12) 
                            pw.println("num = results$statistic - (size1*size2)/2");
                            pw.println("denom = sqrt(((size1*size2) * (size1+size2+1))/12)");
                            pw.println("ess[" + numberOfTasksDone + "] = abs(num/denom)");

                            pw.println("tasknames[" + numberOfTasksDone + "] = \"" + taskname + "\"");
                            pw.println("pvalues[" + numberOfTasksDone + "] = " + "results$p.value");

                        }
                    }

                    if (numberOfTasksDone > 0) {
                        pw.println("pvalues_adj = p.adjust(pvalues, \"bonferroni\")");
                        //now print the name and the adjusted pvalues to file
                        for (int j = 1; j <= numberOfTasksDone; j++) {
                            pw.println("cat(\"\\n\")");
                            pw.println("cat(paste(tasknames[" + j + "] , " + "\",\" , pvalues_adj[" + j + "], "
                                    + "\",\" , ess[" + j + "]))");//get the pvalue for each column name
                        }
                    }

                }

            }
            pw.println("sink()");
            //close the printwriter
            pw.close();

            runOSCommandForR(scriptFilename, scriptOutputFilename, rpmts);

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        //read the output file as string and return it.
        String analysisOutput = "";

        analysisOutput = scriptOutputFilename_requiredResult;//fileContentAsString(scriptOutputFilename);

        analysisOutput = rpmts.studydataurl + File.separator + analysisOutput;
        if (File.separatorChar != '/') {
            analysisOutput = analysisOutput.replace(File.separatorChar, '/');
        }

        return analysisOutput;
    }

    /**
     * This is a Friedman's test method
     *
     * @return
     */
    public String generateFriedmanTest(ResultParameters rpmts, ArrayList<String> nonNormalTasks) {
        //Write the R-Script
        String scriptFilename = "rscript-friedman.R";
        String scriptOutputFilename = "rscript-friedman.Rout"; //this is for standard output
        String scriptOutputFilename_requiredResult = "friedman-analysis.txt"; //this will be the outputfilename for only the required result
        try {
            //first get the column names of the files
            BufferedReader br;// = new BufferedReader(new FileReader(firstAccuracyFile));
            String line = "";
            int numOfColumns = rpmts.numOfTasks;
            String[][] accColumnNames = new String[rpmts.numOfConditions][rpmts.numOfTasks];
            String[][] timeColumnNames = new String[rpmts.numOfConditions][rpmts.numOfTasks];
            int numOfRows = 0;
            int cnt = 0;
            for (int i = 0; i < rpmts.numOfConditions; i++) {
                File AccuracyFile = new File(getServletContext().getRealPath(rpmts.studydataurl + File.separator + rpmts.accuracyFilenames.get(i)));
                br = new BufferedReader(new FileReader(AccuracyFile));

                cnt = 0;
                while ((line = br.readLine()) != null) {
                    String split[] = line.split(",");
                    if (cnt == 0) {
                        for (int j = 0; j < split.length; j++) {
                            accColumnNames[i][j] = split[j];
                        }
                    }
                    cnt++;
                    //break;
                }
                br.close();
            }
            numOfRows = cnt - 1;

            for (int i = 0; i < rpmts.numOfConditions; i++) {
                File timeFile = new File(getServletContext().getRealPath(rpmts.studydataurl + File.separator + rpmts.timeFilenames.get(i)));
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

            String filePath = getServletContext().getRealPath(rpmts.studydataurl + File.separator + scriptFilename);
            String dataPath = getServletContext().getRealPath(rpmts.studydataurl);

            File scriptFile = new File(filePath);
            FileWriter fileWriter = new FileWriter(scriptFile);
            BufferedWriter bw = new BufferedWriter(fileWriter);
            PrintWriter pw = new PrintWriter(bw);

            if (dataPath.indexOf("\\") >= 0) {
                dataPath = dataPath.replaceAll("\\\\", "\\\\\\\\");
            }
            //first set the working directory
            pw.println("setwd(\"" + dataPath + "\")");

            pw.println("sink(\"" + scriptOutputFilename_requiredResult + "\")");

            //read the accuracy files in r
            for (int i = 0; i < rpmts.numOfConditions; i++) {
                pw.println("accuracy" + (i + 1) + " = read.csv(\"" + rpmts.accuracyFilenames.get(i) + "\")");
            }
            //  System.out.println("**** The length of accColumnNames is  :: " + accColumnNames.length);
            pw.println("\"task-name ,  pvalue\"");//get the pvalue for each column name
            pw.println("\"------------------------------------\"");
            //write the accuracy analysis
            for (int i = 0; i < rpmts.numOfTasks; i++) {

                String tname = accColumnNames[0][i].substring(0, accColumnNames[0][i].lastIndexOf("_")); //first name without the condition name

                boolean nonNormalAccuracy = false;
                for (String task : nonNormalTasks) {
                    //  System.out.println("^^--"+task);
                    if (task.trim().equalsIgnoreCase(tname.trim())) {
                        nonNormalAccuracy = true;
                        break;
                    }
                }

                if (nonNormalAccuracy) {
                    ArrayList<String> colnames = new ArrayList<String>();
                    for (int j = 0; j < rpmts.numOfConditions; j++) {
                        String dataName = "accuracy" + (j + 1);
                        pw.println("" + accColumnNames[j][i] + "= c(" + dataName + "[," + (i + 1) + "])");

                        colnames.add(accColumnNames[j][i]);
                    }

                    //print the taskname so that we can know which anova this belongs to 
                    String taskname = accColumnNames[0][i].substring(0, accColumnNames[0][i].lastIndexOf("_")); //first name without the condition name
                    pw.println("taskname=\"TaskName = " + taskname + "\"");
                  //  pw.println("taskname");

                    //do the r cbind
                    String cbindData = "combineddata =data.frame(cbind(";
                    for (int k = 0; k < colnames.size(); k++) {
                        if (k == 0) {
                            cbindData += colnames.get(k);
                        } else {
                            cbindData += "," + colnames.get(k);
                        }
                    }
                    cbindData += "))";  //closing parentheses
                    pw.println(cbindData);

                    //now do the stack before you do the friedman
                    pw.println("combineddata = stack(combineddata)");
                    pw.println("numcases = " + numOfRows);  //the number of cases goes here
                    pw.println("numvariables =" + rpmts.numOfConditions);
                    pw.println("recall.df = data.frame(recall = combineddata,");
                    pw.println("subj=factor(rep(paste(\"subj\", 1:numcases, sep=\"\"), numvariables)))");
                    pw.println("friedmanresult = friedman.test(recall.values ~ recall.ind | subj, data=recall.df)");

                    pw.println("cat(paste(\"      " + taskname + "\", " + "\" , \"" + " , friedmanresult$p.value, " + "\"\\n\" ))");

                }
            }

            /**
             * ********** Do the same for the time as well *********
             */
            //write the time analysis
            //read the accuracy files
            for (int i = 0; i < rpmts.numOfConditions; i++) {
                pw.println("time" + (i + 1) + " = read.csv(\"" + rpmts.timeFilenames.get(i) + "\")");
            }

            //write the time analysis
            for (int i = 0; i < rpmts.numOfTasks; i++) {

                String tname = timeColumnNames[0][i].substring(0, timeColumnNames[0][i].lastIndexOf("_")); //first name without the condition name
                boolean nonNormalTime = false;
                for (String task : nonNormalTasks) {
                    //  System.out.println("^^--"+task);
                    if (task.trim().equalsIgnoreCase(tname.trim())) {
                        nonNormalTime = true;
                        break;
                    }
                }
                if (nonNormalTime) {
                    ArrayList<String> colnames = new ArrayList<String>();
                    for (int j = 0; j < rpmts.numOfConditions; j++) {
                        String dataName = "time" + (j + 1);
                        pw.println("" + timeColumnNames[j][i] + "= c(" + dataName + "[," + (i + 1) + "])");
                        colnames.add(timeColumnNames[j][i]);
                    }

                    //print the taskname so that we can know which anova this belongs to 
                    String taskname = timeColumnNames[0][i].substring(0, timeColumnNames[0][i].lastIndexOf("_")); //first name without the condition name
                    pw.println("taskname=\"TaskName = " + taskname + "\"");
                    //pw.println("taskname");

                    //do the r cbind
                    String cbindData = "combineddata =data.frame(cbind(";
                    for (int k = 0; k < colnames.size(); k++) {
                        //System.out.print("    " + coln);
                        if (k == 0) {
                            cbindData += colnames.get(k);
                        } else {
                            cbindData += "," + colnames.get(k);
                        }
                    }
                    cbindData += "))";  //closing parentheses
                    pw.println(cbindData);

                    //now do the stack before you do the friedman
                    pw.println("combineddata = stack(combineddata)");
                    pw.println("numcases = " + numOfRows);  //the number of cases goes here
                    pw.println("numvariables =" + rpmts.numOfConditions);
                    pw.println("recall.df = data.frame(recall = combineddata,");
                    pw.println("subj=factor(rep(paste(\"subj\", 1:numcases, sep=\"\"), numvariables)))");
                    pw.println("friedmanresult = friedman.test(recall.values ~ recall.ind | subj, data=recall.df)");

                    pw.println("cat(paste(\"      " + taskname + "\", " + "\" , \"" + " , friedmanresult$p.value, " + "\"\\n\" ))");
                    //pw.println("\"*********************\"");
                    //pw.println("friedmanresult");
                    //pw.println("cat(\"\\n\\n\")"); //leave a couple of lines to separate the 
                }
            }

            pw.println("sink()");
            //close the printwriter
            pw.close();

            runOSCommandForR(scriptFilename, scriptOutputFilename, rpmts);

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        //read the output file as string and return it.
        String analysisOutput = "";

        analysisOutput = scriptOutputFilename_requiredResult;//fileContentAsString(scriptOutputFilename);

        analysisOutput = rpmts.studydataurl + File.separator + analysisOutput;
        if (File.separatorChar != '/') {
            analysisOutput = analysisOutput.replace(File.separatorChar, '/');
        }

        return analysisOutput;
    }

    public String getStudyType(ResultParameters rpmts) {
        String studyType = "";
        try {
            //read the xml file that contains the details about the quantitative questions    
            String filename = getServletContext().getRealPath(rpmts.studydataurl + File.separator + "quantitativeTasks.xml");
            File fXmlFile = new File(filename);
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(fXmlFile);

            //optional, but recommended
            //read this - http://stackoverflow.com/questions/13786607/normalization-in-dom-parsing-with-java-how-does-it-work
            doc.getDocumentElement().normalize();

            NodeList experimentTypeNode = doc.getElementsByTagName("experimenttype");
            studyType = ((Element) experimentTypeNode.item(0)).getTextContent();

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return studyType;
    }

    public String getAccuracyResults(ResultParameters rpmts) {
        rpmts.accuracyResults = new ArrayList<String[][]>();
        String allAccuracyData = "";
        try {
            for (int i = 0; i < rpmts.numOfConditions; i++) { //Read the results from file
                String filename = "AccuracyResults" + (i + 1) + ".txt";
                //  System.out.println("This is the result for file:: " + filename);
                File file = new File(getServletContext().getRealPath(rpmts.studydataurl + File.separator + filename));
                if (file.exists()) {
                    BufferedReader br = new BufferedReader(new FileReader(file));
                    String line = "";
                    ArrayList<String> taskAccuracy = new ArrayList<String>();
                    //  System.out.println("-----------------*___*------------------------");
                    while ((line = br.readLine()) != null) {
                        //System.out.println("--- " + line);
                        taskAccuracy.add(line);
                        //System.out.println("------" + line);
                    }
                    String[][] taskAccuracy2 = new String[taskAccuracy.size()][rpmts.numOfTasks];

                    //  System.out.println("accSize:: " + taskAccuracy.size() + "--- numoftasks:: " + rpmts.numOfTasks);
                    for (int j = 0; j < taskAccuracy.size(); j++) {

                        //  System.out.println("***--- " + taskAccuracy);
                        String split[] = taskAccuracy.get(j).split(",");
                        // System.out.println("^^ " + split.toString());
                        for (int k = 0; k < split.length; k++) {
                            //  System.out.println("+++***** " + split[k]);
                            //  System.out.println("j: " + j + " k: " + k);
                            taskAccuracy2[j][k] = split[k];
                        }
                    }
                    //record the maximum rows
                    if (taskAccuracy.size() > rpmts.maxResultsRows) {
                        rpmts.maxResultsRows = taskAccuracy.size();
                    }

                    rpmts.accuracyResults.add(taskAccuracy2);
                    br.close();
                }

            }

            /**
             * *I'm going to find the average of the results this time
             */
            rpmts.accuracySummary_Label = new ArrayList<String>();
            rpmts.accuracySummary_Value = new ArrayList<Double>();
            rpmts.accuracyStandardError_Value = new ArrayList<Double>();

            for (int i = 0; i < rpmts.accuracyResults.size(); i++) {

                String[][] arr = (String[][]) rpmts.accuracyResults.get(i);
                /*  for (int j = 0; j < arr[0].length; j++) {
                 System.out.println("*** " + arr[j]);
                 }*/

                double average = 0;
                double sum = 0;
                double standardError = 0;
                String label = "";
                int total = arr.length - 1;

                for (int j = 0; j < rpmts.numOfTasks; j++) {
                    label = arr[0][j]; //get the label of the 
                    sum = 0;
                    for (int k = 1; k < arr.length; k++) {
                        sum += Double.parseDouble(arr[k][j]);
                    }

                    //average
                    average = sum / total;

                    //find the standard deviation
                    double deviation = 0, delta = 0;
                    for (int m = 1; m < arr.length; m++) {
                        delta = Double.parseDouble(arr[m][j]) - average;
                        deviation += delta * delta;
                    }
                    deviation = Math.sqrt(deviation / total);
                    standardError = deviation / Math.sqrt(total);

                    // int index = 0;
                    boolean added = false;
                    //trying to do some sorting of the values here
                    for (int m = 0; m < rpmts.accuracySummary_Label.size(); m++) {
                        if (rpmts.accuracySummary_Label.get(m).compareTo(label) > 0) {
                            rpmts.accuracySummary_Label.add(m, label);
                            rpmts.accuracySummary_Value.add(m, average);
                            rpmts.accuracyStandardError_Value.add(m, standardError);
                            added = true;
                            break;
                        }
                    }
                    if (!added) {
                        rpmts.accuracySummary_Label.add(label);
                        rpmts.accuracySummary_Value.add(average);
                        rpmts.accuracyStandardError_Value.add(standardError);
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
            for (int i = 0; i < rpmts.accuracySummary_Label.size(); i++) {

                if (cnt == 0) {
                    allAccuracyData += rpmts.accuracySummary_Label.get(i) + "," + rpmts.accuracySummary_Value.get(i)
                            + "," + rpmts.accuracyStandardError_Value.get(i);
                } else {
                    allAccuracyData += conditionSeparator + rpmts.accuracySummary_Label.get(i) + "," + rpmts.accuracySummary_Value.get(i)
                            + "," + rpmts.accuracyStandardError_Value.get(i);
                }
                cnt++;

                if (cnt == rpmts.numOfConditions && (i + 1 != rpmts.accuracySummary_Label.size())) {
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

    public String getAccuracyResultsBasic(ResultParameters rpmts) {

        //TODO: read the basic files and rearrange them.
        rpmts.accuracyResultsBasic = new ArrayList<String[][]>();

        String allAccuracyData = "";
        try {

            for (int i = 0; i < rpmts.numOfConditions; i++) { //Read the results from file
                String filename2 = "AccuracyResultsBasic" + (i + 1) + ".txt";

                //  System.out.println("This is the result for file:: " + filename);
                File file = new File(getServletContext().getRealPath(rpmts.studydataurl + File.separator + filename2));

                BufferedReader br = new BufferedReader(new FileReader(file));
                String line = "";
                ArrayList<String> taskAccuracy = new ArrayList<String>();
                while ((line = br.readLine()) != null) {
                    taskAccuracy.add(line);
                }
                String[][] taskAccuracy2 = new String[taskAccuracy.size()][rpmts.numOfTasks];
                for (int j = 0; j < taskAccuracy.size(); j++) {

                    String split[] = taskAccuracy.get(j).split("::");
                    for (int k = 0; k < split.length; k++) {
                        // System.out.println("***** " +taskAccuracy.get(i));
                        taskAccuracy2[j][k] = split[k];
                    }

                }

                //record the maximum rows
                if (taskAccuracy.size() > rpmts.maxResultsRowsBasic) {
                    rpmts.maxResultsRowsBasic = taskAccuracy.size();
                }

                rpmts.accuracyResultsBasic.add(taskAccuracy2);
                br.close();
            }

            int cnt = 0;

            String taskSeparator = "::::";
            String conditionSeparator = "::";
            for (int i = 0; i < rpmts.accuracyResultsBasic.size(); i++) {

                if (cnt == 0) {
                    allAccuracyData += rpmts.accuracyResultsBasic.get(i);
                } else {
                    allAccuracyData += conditionSeparator + rpmts.accuracyResultsBasic.get(i);
                }
                cnt++;

                if (cnt == rpmts.numOfConditions && (i + 1 != rpmts.accuracySummary_Label.size())) {
                    cnt = 0;
                    allAccuracyData += taskSeparator;
                }

            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return allAccuracyData;
    }

    public String getTimeResults(ResultParameters rpmts) {

        rpmts.timeResults = new ArrayList<String[][]>();
        String allTimeData = "";
        try {

            for (int i = 0; i < rpmts.numOfConditions; i++) { //Read the results from file
                String filename = "TimeResults" + (i + 1) + ".txt";

                //System.out.println("This is the result for file:: " + filename);
                File file = new File(getServletContext().getRealPath(rpmts.studydataurl + File.separator + filename));
                if (file.exists()) {
                    BufferedReader br = new BufferedReader(new FileReader(file));
                    String line = "";
                    ArrayList<String> taskTime = new ArrayList<String>();
                    while ((line = br.readLine()) != null) {
                        taskTime.add(line);
                    }
                    String[][] taskTime2 = new String[taskTime.size()][rpmts.numOfTasks];
                    for (int j = 0; j < taskTime.size(); j++) {

                        String split[] = taskTime.get(j).split(",");
                        for (int k = 0; k < split.length; k++) {
                            // System.out.println("***** " +taskAccuracy.get(i));
                            taskTime2[j][k] = split[k];
                        }

                    }

                    rpmts.timeResults.add(taskTime2);
                    br.close();
                }
            }
            /**
             * *I'm going to find the average of the results this time
             */
            rpmts.timeSummary_Label = new ArrayList<String>();
            rpmts.timeSummary_Value = new ArrayList<Double>();
            rpmts.timeStandardError_Value = new ArrayList<Double>();

            for (int i = 0; i < rpmts.timeResults.size(); i++) {

                String[][] arr = (String[][]) rpmts.timeResults.get(i);

                double average = 0;
                double standardError = 0;
                double sum = 0;
                String label = "";
                int total = arr.length - 1;

                for (int j = 0; j < rpmts.numOfTasks; j++) {
                    label = arr[0][j]; //get the label of the 
                    sum = 0;
                    for (int k = 1; k < arr.length; k++) {
                        sum += Double.parseDouble(arr[k][j]);
                    }
                    //average
                    average = sum / total;
                    //find the standard deviation
                    double deviation = 0, delta = 0;
                    for (int m = 1; m < arr.length; m++) {
                        delta = Double.parseDouble(arr[m][j]) - average;
                        deviation += delta * delta;
                    }
                    deviation = Math.sqrt(deviation / total);
                    standardError = deviation / Math.sqrt(total);

                    boolean added = false;
                    //trying to do some sorting of the values here
                    for (int m = 0; m < rpmts.timeSummary_Label.size(); m++) {
                        if (rpmts.timeSummary_Label.get(m).compareTo(label) > 0) {
                            rpmts.timeSummary_Label.add(m, label);
                            rpmts.timeSummary_Value.add(m, average);
                            rpmts.timeStandardError_Value.add(m, standardError);
                            added = true;
                            break;
                        }
                    }
                    if (!added) {
                        rpmts.timeSummary_Label.add(label);
                        rpmts.timeSummary_Value.add(average);
                        rpmts.timeStandardError_Value.add(standardError);
                    }

                }
            }

            /**
             * * Store all the time results based on tasks
             */
            ArrayList<String> accuracySummaryTemp = new ArrayList<String>();
            /*   for(int i=0; i<accuracySummary_Label.size(); i++){
             accuracySummaryTemp.add(accuracySummary_Label.get(i));
             } */

            int cnt = 0;

            String taskSeparator = "::::";
            String conditionSeparator = "::";
            for (int i = 0; i < rpmts.timeSummary_Label.size(); i++) {

                if (cnt == 0) {
                    allTimeData += rpmts.timeSummary_Label.get(i) + "," + rpmts.timeSummary_Value.get(i) + "," + rpmts.timeStandardError_Value.get(i);
                } else {
                    allTimeData += conditionSeparator + rpmts.timeSummary_Label.get(i) + "," + rpmts.timeSummary_Value.get(i) + "," + rpmts.timeStandardError_Value.get(i);
                }
                cnt++;

                if (cnt == rpmts.numOfConditions && (i + 1 != rpmts.accuracySummary_Label.size())) {
                    cnt = 0;
                    allTimeData += taskSeparator;
                }

            }

            //  return allAccuracyData;         
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return allTimeData;

        /* try {

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
         }*/
    }

    public String getTimeResultsBasic(ResultParameters rpmts) {
        //TODO: read the basic files and rearrange them.
        rpmts.timeResultsBasic = new ArrayList<String[][]>();

        String allTimeData = "";
        try {

            for (int i = 0; i < rpmts.numOfConditions; i++) { //Read the results from file
                String filename2 = "TimeResultsBasic" + (i + 1) + ".txt";

                //  System.out.println("This is the result for file:: " + filename);
                File file = new File(getServletContext().getRealPath(rpmts.studydataurl + File.separator + filename2));

                if (file.exists()) {
                    BufferedReader br = new BufferedReader(new FileReader(file));
                    String line = "";
                    ArrayList<String> taskTime = new ArrayList<String>();
                    while ((line = br.readLine()) != null) {
                        taskTime.add(line);
                    }
                    String[][] taskTime2 = new String[taskTime.size()][rpmts.numOfTasks];
                    for (int j = 0; j < taskTime.size(); j++) {

                        String split[] = taskTime.get(j).split("::");
                        for (int k = 0; k < split.length; k++) {
                            // System.out.println("***** " +taskAccuracy.get(i));
                            taskTime2[j][k] = split[k];
                        }

                    }

                    //record the maximum rows
                    if (taskTime.size() > rpmts.maxResultsRowsBasic) {
                        rpmts.maxResultsRowsBasic = taskTime.size();
                    }

                    rpmts.timeResultsBasic.add(taskTime2);
                    br.close();
                }

            }

            int cnt = 0;

            String taskSeparator = "::::";
            String conditionSeparator = "::";
            for (int i = 0; i < rpmts.timeResultsBasic.size(); i++) {

                if (cnt == 0) {
                    allTimeData += rpmts.timeResultsBasic.get(i);
                } else {
                    allTimeData += conditionSeparator + rpmts.accuracyResultsBasic.get(i);
                }
                cnt++;

                if (cnt == rpmts.numOfConditions && (i + 1 != rpmts.accuracySummary_Label.size())) {
                    cnt = 0;
                    allTimeData += taskSeparator;
                }

            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return allTimeData;
    }

    public String getCompletionStats(ResultParameters rpmts) {
        String completionStats = "";
        String conditionSeparator = "::";
        try {

            //first read the accuracy files to know the number of rows of completed studies
            rpmts.numOfCompletedStudiesPerCondition = new ArrayList<Integer>();

            for (int i = 0; i < rpmts.numOfConditions; i++) { //Read the results from file
                String filename = "AccuracyResults" + (i + 1) + ".txt";

                File file = new File(getServletContext().getRealPath(rpmts.studydataurl + File.separator + filename));

                if (file.exists()) {
                    BufferedReader br = new BufferedReader(new FileReader(file));
                    String line = "";

                    int counter = 0;
                    while ((line = br.readLine()) != null) {
                        counter++;
                    }
                    //record the maximum rows
                    counter = counter - 1; //subtract the header.
                    rpmts.numOfCompletedStudiesPerCondition.add(counter);

                    br.close();
                }

            }

            for (int i = 0; i < rpmts.numOfConditions; i++) {
                if (i == 0) {
                    completionStats = rpmts.viewerConditionShortnames.get(i) + "," + rpmts.numOfCompletedStudiesPerCondition.get(i);
                } else {
                    completionStats += conditionSeparator + rpmts.viewerConditionShortnames.get(i) + "," + rpmts.numOfCompletedStudiesPerCondition.get(i);
                }

            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return completionStats;
    }

    public void generateFilenames(ResultParameters rpmts) {
        rpmts.accuracyFilenames = new ArrayList<String>();
        rpmts.timeFilenames = new ArrayList<String>();

        String filename = "";
        for (int i = 0; i < rpmts.numOfConditions; i++) {
            filename = "AccuracyResults" + (i + 1) + ".txt";
            rpmts.accuracyFilenames.add(filename);
        }

        for (int i = 0; i < rpmts.numOfConditions; i++) {
            filename = "TimeResults" + (i + 1) + ".txt";
            rpmts.timeFilenames.add(filename);
        }

    }

    public String generateShapiroWilk(ArrayList<String> normalTasks, ArrayList<String> nonNormalTasks, ResultParameters rpmts) {
        String scriptFilename = "rscript-shapiro.R";
        String scriptOutputFilename = "rscript-shapiro.Rout";
        String scriptOutputFilename2 = "shapiro-wilk-Analysis.txt";
        try {
            //first get the column names of the files
            BufferedReader br;// = new BufferedReader(new FileReader(firstAccuracyFile));
            String line = "";
            int numOfColumns = rpmts.numOfTasks;
            String[][] accColumnNames = new String[rpmts.numOfConditions][rpmts.numOfTasks];
            String[][] timeColumnNames = new String[rpmts.numOfConditions][rpmts.numOfTasks];
            for (int i = 0; i < rpmts.numOfConditions; i++) {
                File AccuracyFile = new File(getServletContext().getRealPath(rpmts.studydataurl + File.separator + rpmts.accuracyFilenames.get(i)));
                if (AccuracyFile.exists()) {
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

            }

            for (int i = 0; i < rpmts.numOfConditions; i++) {
                File timeFile = new File(getServletContext().getRealPath(rpmts.studydataurl + File.separator + rpmts.timeFilenames.get(i)));

                if (timeFile.exists()) {
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
            }

            //Write the R-Script           
            String filePath = getServletContext().getRealPath(rpmts.studydataurl + File.separator + scriptFilename);
            String dataPath = getServletContext().getRealPath(rpmts.studydataurl);

            File scriptFile = new File(filePath);
            FileWriter fileWriter = new FileWriter(scriptFile);
            BufferedWriter bw = new BufferedWriter(fileWriter);
            PrintWriter pw = new PrintWriter(bw);

            if (dataPath.indexOf("\\") >= 0) {
                dataPath = dataPath.replaceAll("\\\\", "\\\\\\\\");
            }

            //first set the working directory
            pw.println("setwd(\"" + dataPath + "\")");

            pw.println("sink(\"" + scriptOutputFilename2 + "\")");

            //read the accuracy files
            for (int i = 0; i < rpmts.numOfConditions; i++) {
                pw.println("accuracy" + (i + 1) + " = read.csv(\"" + rpmts.accuracyFilenames.get(i) + "\")");
            }

//          System.out.println(accColumnNames.length + "((((");
//          System.out.println(accColumnNames[0].length+ "))))");
//          for(int i=0; i<accColumnNames.length; i++){
//              for(int j=0; j<accColumnNames[i].length; j++){
//                  System.out.print("accColumn "+ accColumnNames[i][j]);
//              }
//              System.out.println("****************");
//          }
            //write the accuracy shapiro
            for (int i = 0; i < rpmts.numOfTasks; i++) {

                ArrayList<String> colnames = new ArrayList<String>();
                for (int j = 0; j < rpmts.numOfConditions; j++) {
                    String dataName = "accuracy" + (j + 1);
                    pw.println("" + accColumnNames[j][i] + "= c(" + dataName + "[," + (i + 1) + "])");
                    pw.println("" + "shapiro_" + accColumnNames[j][i] + "= shapiro.test(" + accColumnNames[j][i] + ")");
                    colnames.add(accColumnNames[j][i]);
                }

                //print the taskname so that we can know what task this shapiro belongs to .
                String taskname = accColumnNames[0][i].substring(0, accColumnNames[0][i].lastIndexOf("_")); //first name without the condition name
                pw.println("taskname=\"TaskName = " + taskname + "\"");
                pw.println("cat(taskname)");

                //do the r cbind
               /* String cbindData = "combineddata =data.frame(cbind(";
                 for (int k = 0; k < colnames.size(); k++) {
                 //System.out.print("    " + coln);
                 if (k == 0) {
                 cbindData += colnames.get(k);
                 } else {
                 cbindData += "," + colnames.get(k);
                 }
                 }
                 cbindData += "))";  //closing parentheses
                 pw.println(cbindData);

                 pw.println("result=lapply(combineddata, shapiro.test)");
                 */
                pw.println("cat(\"\\ncondition  ,  pvalue\")");//get the pvalue for each column name
                pw.println("cat(\"\\n------------------------------------\")");
                //print pvalues for the column names
                for (int n = 0; n < colnames.size(); n++) {
                    pw.println("cat(paste(\"\\n" + colnames.get(n) + " ,\" , shapiro_" + colnames.get(n) + "$p.value))");//get the pvalue for each column name
                }
                pw.println("cat(\"\\n**********************************************************\")");
                pw.println("cat(\"\\n\\n\\n\")");
                //pw.println("\"                                                          \"");

            }

            /**
             * ***************For time **********************
             */
            //read the accuracy files
            for (int i = 0; i < rpmts.numOfConditions; i++) {
                pw.println("time" + (i + 1) + " = read.csv(\"" + rpmts.timeFilenames.get(i) + "\")");
            }

            //write the time shapiro too
            for (int i = 0; i < rpmts.numOfTasks; i++) {

                ArrayList<String> colnames = new ArrayList<String>();
                for (int j = 0; j < rpmts.numOfConditions; j++) {
                    String dataName = "time" + (j + 1);
                    pw.println("" + timeColumnNames[j][i] + "= c(" + dataName + "[," + (i + 1) + "])");
                    pw.println("" + "shapiro_" + timeColumnNames[j][i] + "= shapiro.test(" + timeColumnNames[j][i] + ")");
                    colnames.add(timeColumnNames[j][i]);
                }

                //print the taskname so that we can know which shapiro this belongs to 
                String taskname = timeColumnNames[0][i].substring(0, timeColumnNames[0][i].lastIndexOf("_")); //first name without the condition name
                pw.println("taskname=\"TaskName = " + taskname + "\"");
                pw.println("cat(taskname)");

                //do the r cbind
               /* String cbindData = "combineddata =data.frame(cbind(";
                 for (int k = 0; k < colnames.size(); k++) {
                 //System.out.print("    " + coln);
                 if (k == 0) {
                 cbindData += colnames.get(k);
                 } else {
                 cbindData += "," + colnames.get(k);
                 }
                 }
                 cbindData += "))";  //closing parentheses
                 pw.println(cbindData);

                 pw.println("result=lapply(combineddata, shapiro.test)");
                
                 */
                //print the header
                pw.println("cat(\"\\ncondition  ,  pvalue\")");//get the pvalue for each column name
                pw.println("cat(\"\\n------------------------------------\")");
                //print pvalues for the column names
                for (int n = 0; n < colnames.size(); n++) {
                    //  pw.println("paste(\"" + colnames.get(n) + " ,\" , result$" + colnames.get(n) + "$p.value)");//get the pvalue for each column name
                    pw.println("cat(paste(\"\\n" + colnames.get(n) + " ,\" , shapiro_" + colnames.get(n) + "$p.value))");//get the pvalue for each column name
                }
                pw.println("cat(\"\\n**********************************************************\")");
                pw.println("cat(\"\\n\\n\\n\")");
                //pw.println("\"                                                          \"");

            }

            pw.println("sink()");

            //close the printwriter
            pw.close();
            runOSCommandForR(scriptFilename, scriptOutputFilename, rpmts);

            //Now read the file and populate the normal and non-normal tasks
            File file = new File(getServletContext().getRealPath(rpmts.studydataurl + File.separator + scriptOutputFilename2));
            BufferedReader br2 = new BufferedReader(new FileReader(file));
            line = "";
            int cnt = 0;
            int taskcnt = 0;
            boolean normal = true;
            String curtask = "";
            while ((line = br2.readLine()) != null) {
                //NB we are looking for the string within the quotation marks
                // System.out.println("Line::: " + line);

                if (line.indexOf("******") >= 0) {
                    //write the previous task
                    if (normal) {
                        //System.out.println("*** this task:: "+ curtask + "  is normal");
                        //add the task to normal tasks
                        normalTasks.add(curtask);
                    } else {
                        //add the task to non-normal tasks
                        nonNormalTasks.add(curtask);
                    }
                    //read the 2 empty lines added.
                    line = br2.readLine();
                    line = br2.readLine();

                    //reset these values
                    cnt = 0;
                    normal = true;
                    continue;
                }
                if (cnt == 0) {//get the taskname
                    //e.g.   [1] "TaskName = Acc_neighbor_one_step"
                    String line2[] = line.split("=");
                  //  System.out.println("The taskname is " + line2[1]);                

                    //line2 = Acc_neighbor_one_step"
                    // curtask = line2[1].substring(0, line2[1].length() - 1); //leave out the last quotation
                    curtask = line2[1].substring(0, line2[1].length()); //leave out the last quotation

                } else if (cnt >= 3) {
                    //"Acc_neighbor_cond1 , 0.00647000075166546"
                    int ind1 = line.indexOf("\"") + 1;
                    int ind2 = line.lastIndexOf("\"");
                    // line = line.substring(ind1, ind2);

                    double pvalue = Double.valueOf(line.split(",")[1]).doubleValue();

//                   / System.out.println("PVALUE IS ++ " + pvalue);
                    //double pvalue = Double.parseDouble(line.split(",")[1]);
                    /*NB: if at one point one of the p_values is less than 0.05, then the data for the task is not normal*/
                    if (pvalue < 0.05) {
                        normal = false;
                    }
                    //System.out.println("***pvalue :: " + pvalue);
                }

                cnt++;
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        //read the output file as string and return it.
        String analysisOutput = "";

        analysisOutput = scriptOutputFilename2;//fileContentAsString(scriptOutputFilename);

        //   analysisOutput = scriptOutputFilename_requiredResult;//fileContentAsString(scriptOutputFilename);
        analysisOutput = rpmts.studydataurl + File.separator + analysisOutput;
        if (File.separatorChar != '/') {
            analysisOutput = analysisOutput.replace(File.separatorChar, '/');
        }

        return analysisOutput;

    }

    public String generateMeanAndStandardDeviation(ResultParameters rpmts) {

        String scriptFilename = "rscript-meanAndSD.R";
        String scriptOutputFilename = "rscript-meanAndSD.Rout";
        String scriptOutputFilename2 = "means-and-Standard-deviations.txt";
        try {
            //first get the column names of the files
            BufferedReader br;// = new BufferedReader(new FileReader(firstAccuracyFile));
            String line = "";
            int numOfColumns = rpmts.numOfTasks;
            String[][] accColumnNames = new String[rpmts.numOfConditions][rpmts.numOfTasks];
            String[][] timeColumnNames = new String[rpmts.numOfConditions][rpmts.numOfTasks];
            for (int i = 0; i < rpmts.numOfConditions; i++) {
                File AccuracyFile = new File(getServletContext().getRealPath(rpmts.studydataurl + File.separator + rpmts.accuracyFilenames.get(i)));

                if (AccuracyFile.exists()) {
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

            }

            for (int i = 0; i < rpmts.numOfConditions; i++) {
                File timeFile = new File(getServletContext().getRealPath(rpmts.studydataurl + File.separator + rpmts.timeFilenames.get(i)));
                if (timeFile.exists()) {
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

            }

            //Write the R-Script           
            String filePath = getServletContext().getRealPath(rpmts.studydataurl + File.separator + scriptFilename);
            String dataPath = getServletContext().getRealPath(rpmts.studydataurl);

            File scriptFile = new File(filePath);
            FileWriter fileWriter = new FileWriter(scriptFile);
            BufferedWriter bw = new BufferedWriter(fileWriter);
            PrintWriter pw = new PrintWriter(bw);

            if (dataPath.indexOf("\\") >= 0) {
                dataPath = dataPath.replaceAll("\\\\", "\\\\\\\\");
            }

            //first set the working directory
            pw.println("setwd(\"" + dataPath + "\")");

            pw.println("sink(\"" + scriptOutputFilename2 + "\")");

            //read the accuracy files
            for (int i = 0; i < rpmts.numOfConditions; i++) {
                pw.println("accuracy" + (i + 1) + " = read.csv(\"" + rpmts.accuracyFilenames.get(i) + "\")");
            }

            //write the accuracy mean and SD
            for (int i = 0; i < rpmts.numOfTasks; i++) {

                ArrayList<String> colnames = new ArrayList<String>();
                for (int j = 0; j < rpmts.numOfConditions; j++) {
                    String dataName = "accuracy" + (j + 1);
                    pw.println("" + accColumnNames[j][i] + "= c(" + dataName + "[," + (i + 1) + "])");

                    pw.println("" + "mean_" + accColumnNames[j][i] + "= mean(" + accColumnNames[j][i] + ")");
                    pw.println("" + "sd_" + accColumnNames[j][i] + "= sd(" + accColumnNames[j][i] + ")");

                    colnames.add(accColumnNames[j][i]);
                }

                //print the taskname so that we can know which anova this belongs to 
                String taskname = accColumnNames[0][i].substring(0, accColumnNames[0][i].lastIndexOf("_")); //first name without the condition name
                pw.println("taskname=\"TaskName = " + taskname + "\"");
                pw.println("cat(taskname)");

                //print the headers 
                pw.println("cat(paste(\"\\n" + "condition" + " ,\" ,\"" + "Mean" + " ,\" , \"" + "Standard-Deviation\"))");//get mean and standard deviation
                pw.println("cat(\"\\n------------------------------------------------------\")");
                for (int n = 0; n < colnames.size(); n++) {
                    pw.println("cat(paste(\"\\n" + colnames.get(n) + " ,\" , mean_" + colnames.get(n) + " , \",\"" + " , sd_" + colnames.get(n) + "))");//get mean and standard deviation
                }
                pw.println("cat(\"\\n******************************************************************\")");
                pw.println("cat(\"\\n\\n\")");
                //pw.println("\"                                                       \"");

            }

            /**
             * ***************For time **********************
             */
            //read the time files
            for (int i = 0; i < rpmts.numOfConditions; i++) {
                pw.println("time" + (i + 1) + " = read.csv(\"" + rpmts.timeFilenames.get(i) + "\")");
            }

            //write the time shapiro too
            for (int i = 0; i < rpmts.numOfTasks; i++) {

                ArrayList<String> colnames = new ArrayList<String>();
                for (int j = 0; j < rpmts.numOfConditions; j++) {
                    String dataName = "time" + (j + 1);
                    pw.println("" + timeColumnNames[j][i] + "= c(" + dataName + "[," + (i + 1) + "])");

                    pw.println("" + "mean_" + timeColumnNames[j][i] + "= mean(" + timeColumnNames[j][i] + ")");
                    pw.println("" + "sd_" + timeColumnNames[j][i] + "= sd(" + timeColumnNames[j][i] + ")");

                    colnames.add(timeColumnNames[j][i]);
                }

                //print the taskname so that we can know which shapiro this belongs to 
                String taskname = timeColumnNames[0][i].substring(0, timeColumnNames[0][i].lastIndexOf("_")); //first name without the condition name
                pw.println("taskname=\"TaskName = " + taskname + "\"");
                pw.println("cat(taskname)");

                //print the headers 
                pw.println("cat(paste(\"\\n" + "condition" + " ,\" ,\"" + "Mean" + " ,\" , \"" + "Standard-Deviation\"))");//get mean and standard deviation
                pw.println("cat(\"\\n------------------------------------------------------\")");
                for (int n = 0; n < colnames.size(); n++) {
                    pw.println("cat(paste(\"\\n" + colnames.get(n) + " ,\" , mean_" + colnames.get(n) + " , \",\"" + " , sd_" + colnames.get(n) + "))");//get mean and standard deviation
                }
                pw.println("cat(\"\\n******************************************************************\")");
                pw.println("cat(\"\\n\\n\")");
                //pw.println("\"                                                       \"");
            }

            pw.println("sink()");

            //close the printwriter
            pw.close();
            runOSCommandForR(scriptFilename, scriptOutputFilename, rpmts);

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        //read the output file as string and return it.
        String analysisOutput = "";

        analysisOutput = scriptOutputFilename2;//fileContentAsString(scriptOutputFilename);

        //   analysisOutput = scriptOutputFilename_requiredResult;//fileContentAsString(scriptOutputFilename);
        analysisOutput = rpmts.studydataurl + File.separator + analysisOutput;
        if (File.separatorChar != '/') {
            analysisOutput = analysisOutput.replace(File.separatorChar, '/');
        }

        return analysisOutput;

    }

    public void runOSCommandForR(String scriptFilename, String scriptOutputFilename, ResultParameters rpmts) {
        String os = System.getProperty("os.name").toLowerCase();

        /**
         * Run the script *
         */
        Runtime r = Runtime.getRuntime();
        Process p = null;
        String load;
        String workdir = getServletContext().getRealPath(rpmts.studydataurl);

        try {
            // Use instance of Runtime class to run the command as a java Process

            if ((os.indexOf("windows") > -1)) {
                String cmd[] = new String[3];
                cmd[0] = "cmd.exe";
                cmd[1] = "/C";
                cmd[2] = "Rterm.exe --slave --no-restore --no-save <" + workdir + File.separator + scriptFilename + " > "
                        + workdir + File.separator + scriptOutputFilename + " 2>&1";
                p = r.exec(cmd);
            } else {
                String cmdu = "Rscript " + workdir + File.separator + scriptFilename;
                p = r.exec(cmdu);
            }

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
        } catch (IOException e) {
            System.out.println("Problems running: " + e.toString());
        }

    }

    public String fileContentAsString(String filename, ResultParameters rpmts) {
        String fileContent = "";

        try {
            BufferedReader br;
            String line = "\n\n\n";
            File file = new File(getServletContext().getRealPath(rpmts.studydataurl + File.separator + filename));
            if (file.exists()) {
                br = new BufferedReader(new FileReader(file));
                while ((line = br.readLine()) != null) {
                    fileContent += line + "\n";
                }
                br.close();
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return fileContent;
    }

    public String generateANOVAPostHoc(ResultParameters rpmts, ArrayList<String> normalTasks) {
        //System.out.println("******----ANOVAPOSTHOC----***");

        //Write the R-Script
        String scriptFilename = "rscript-anovaPostHoc.R";
        String scriptOutputFilename = "rscript-anovaPostHoc.Rout"; //this is for standard output
        String scriptOutputFilename_requiredResult = "anova-PostHoc-Analysis.txt"; //this will be the outputfilename for only the required result

        try {
            //first get the column names of the files
            BufferedReader br;// = new BufferedReader(new FileReader(firstAccuracyFile));
            String line = "";
            int numOfColumns = rpmts.numOfTasks;
            String[][] accColumnNames = new String[rpmts.numOfConditions][rpmts.numOfTasks];
            String[][] timeColumnNames = new String[rpmts.numOfConditions][rpmts.numOfTasks];
            int numOfRows = 0;
            int cnt = 0;
            for (int i = 0; i < rpmts.numOfConditions; i++) {
                File AccuracyFile = new File(getServletContext().getRealPath(rpmts.studydataurl + File.separator + rpmts.accuracyFilenames.get(i)));
                br = new BufferedReader(new FileReader(AccuracyFile));

                cnt = 0;
                while ((line = br.readLine()) != null) {
                    String split[] = line.split(",");
                    if (cnt == 0) {
                        for (int j = 0; j < split.length; j++) {
                            accColumnNames[i][j] = split[j];
                        }
                    }
                    cnt++;
                    //break;
                }
                br.close();
            }
            numOfRows = cnt - 1;

            for (int i = 0; i < rpmts.numOfConditions; i++) {
                File timeFile = new File(getServletContext().getRealPath(rpmts.studydataurl + File.separator + rpmts.timeFilenames.get(i)));
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

            String filePath = getServletContext().getRealPath(rpmts.studydataurl + File.separator + scriptFilename);
            String dataPath = getServletContext().getRealPath(rpmts.studydataurl);

            File scriptFile = new File(filePath);
            FileWriter fileWriter = new FileWriter(scriptFile);
            BufferedWriter bw = new BufferedWriter(fileWriter);
            PrintWriter pw = new PrintWriter(bw);

            if (dataPath.indexOf("\\") >= 0) {
                dataPath = dataPath.replaceAll("\\\\", "\\\\\\\\");
            }
            //first set the working directory
            pw.println("setwd(\"" + dataPath + "\")");

            pw.println("sink(\"" + scriptOutputFilename_requiredResult + "\")");

            //read the accuracy files in r
            for (int i = 0; i < rpmts.numOfConditions; i++) {
                pw.println("accuracy" + (i + 1) + " = read.csv(\"" + rpmts.accuracyFilenames.get(i) + "\")");
            }
            //read the time files
            for (int i = 0; i < rpmts.numOfConditions; i++) {
                pw.println("time" + (i + 1) + " = read.csv(\"" + rpmts.timeFilenames.get(i) + "\")");
            }

            if (!rpmts.studyType.equalsIgnoreCase("Within")) {
                //accuracy
                for (int i = 0; i < rpmts.numOfTasks; i++) {

                    String tname = accColumnNames[0][i].substring(0, accColumnNames[0][i].lastIndexOf("_")); //first name without the condition name
                    //tname = tname.trim();
                    boolean normalAccuracy = false;
                    for (String task : normalTasks) {
                        //  System.out.println("^^--"+task);
                        if (task.trim().equalsIgnoreCase(tname.trim())) {
                            normalAccuracy = true;
                            break;
                        }
                    }

                    if (normalAccuracy) {
                        ArrayList<String> colnames = new ArrayList<String>();
                        for (int j = 0; j < rpmts.numOfConditions; j++) {
                            String dataName = "accuracy" + (j + 1);
                            pw.println("" + accColumnNames[j][i] + "= c(" + dataName + "[," + (i + 1) + "])");

                            colnames.add(accColumnNames[j][i]);
                        }

                        //print the taskname so that we can know which anova this belongs to 
                        String taskname = accColumnNames[0][i].substring(0, accColumnNames[0][i].lastIndexOf("_")); //first name without the condition name
                        pw.println("taskname=\"TaskName = " + taskname + "\"");
                        pw.println("taskname");

                        //do the r cbind
                        String cbindData = "combineddata =data.frame(cbind(";
                        for (int k = 0; k < colnames.size(); k++) {
                            //System.out.print("    " + coln);
                            if (k == 0) {
                                cbindData += colnames.get(k);
                            } else {
                                cbindData += "," + colnames.get(k);
                            }
                        }
                        cbindData += "))";  //closing parentheses
                        pw.println(cbindData);

                        //now do the stack before you do the anova
                        pw.println("combineddata = stack(combineddata)");
                        // pw.println("anovaresult = aov(lm(values ~ ind, combineddata))");

                        pw.println("anovaresult = aov(values ~ ind, combineddata)");
                        pw.println("anovaresult");
                        pw.println("tukeyresult = TukeyHSD(anovaresultm group=TRUE)");
                        pw.println("\"***************\"");
                        pw.println("tukeyresult");

                        //concatenate the name and the pvalue of each comparison. Note the rowname can be get with rownames
                        //and the pvalue can be found in the column 4 of the result
                        pw.println("rownames = rownames(tukeyresult$ind)");
                        pw.println("for(i in 1:length(rownames)) { ");
                        pw.println("cat(paste(rownames[i] , " + "\" , \"" + " , tukeyresult$ind[i, 4], " + "\"\\n\" )) }");

                        pw.println("cat(\"******************\n\")"); //leave a couple of lines to separate the 
                    }

                }

                //time
                for (int i = 0; i < rpmts.numOfTasks; i++) {
                    String tname = timeColumnNames[0][i].substring(0, timeColumnNames[0][i].lastIndexOf("_")); //first name without the condition name

                    //tname = tname.trim();
                    boolean normalAccuracy = false;
                    for (String task : normalTasks) {
                        if (task.trim().equalsIgnoreCase(tname.trim())) {
                            normalAccuracy = true;
                            break;
                        }
                    }

                    if (normalAccuracy) {

                        ArrayList<String> colnames = new ArrayList<String>();
                        for (int j = 0; j < rpmts.numOfConditions; j++) {
                            String dataName = "time" + (j + 1);
                            pw.println("" + timeColumnNames[j][i] + "= c(" + dataName + "[," + (i + 1) + "])");
                            colnames.add(timeColumnNames[j][i]);
                        }

                        //print the taskname so that we can know which anova this belongs to 
                        String taskname = timeColumnNames[0][i].substring(0, timeColumnNames[0][i].lastIndexOf("_")); //first name without the condition name
                        pw.println("taskname=\"TaskName = " + taskname + "\"");
                        pw.println("taskname");

                        //do the r cbind
                        String cbindData = "combineddata =data.frame(cbind(";
                        for (int k = 0; k < colnames.size(); k++) {
                            //System.out.print("    " + coln);
                            if (k == 0) {
                                cbindData += colnames.get(k);
                            } else {
                                cbindData += "," + colnames.get(k);
                            }
                        }
                        cbindData += "))";  //closing parentheses
                        pw.println(cbindData);

                        //now do the stack before you do the anova
                        pw.println("combineddata = stack(combineddata)");
                        // pw.println("anovaresult = aov(lm(values ~ ind, combineddata))");

                        pw.println("anovaresult = aov(values ~ ind, combineddata)");
                        pw.println("tukeyresult = TukeyHSD(anovaresult)");

                        //concatenate the name and the pvalue of each comparison. Note the rowname can be get with rownames
                        //and the pvalue can be found in the column 4 of the result
                        pw.println("rownames = rownames(tukeyresult$ind)");
                        pw.println("for(i in 1:length(rownames)) { ");
                        pw.println("cat(paste(rownames[i] , " + "\" , \"" + " , tukeyresult$ind[i, 4], " + "\"\\n\" )) }");

                        pw.println("cat(\"******************\n\")"); //leave a couple of lines to separate the 
                    }
                }

            } else {

                //System.out.println("&&&& NOT WITHIN && " + rpmts.studyType);
                //do the paired tests
                //write the accuracy analysis
                for (int i = 0; i < rpmts.numOfConditions - 1; i++) {
                    String dataName1 = "accuracy" + (i + 1);

                    for (int k = i; k < rpmts.numOfConditions - 1; k++) {
                        String dataName2 = "accuracy" + (k + 2);
                        pw.println("tasknames = NULL");
                        pw.println("pvalues = NULL");
                        int numberOfTasksDone = 0;
                        for (int j = 0; j < accColumnNames[i].length; j++) {
                            //check if the task is normal before you do this
                            String tname = accColumnNames[i][j].substring(0, accColumnNames[i][j].lastIndexOf("_")); //first name without the condition name

                            boolean normalAccuracy = false;

                            for (String task : normalTasks) {
                                if (task.trim().equalsIgnoreCase(tname.trim())) {
                                    normalAccuracy = true;
                                    break;
                                }
                            }
                            //do this for the normal tasks
                            if (normalAccuracy) {
                                numberOfTasksDone++;
                                String taskname = accColumnNames[i][j] + "-" + accColumnNames[k + 1][j];
                                pw.println("" + accColumnNames[i][j] + "= c(" + dataName1 + "[," + (j + 1) + "])");
                                pw.println("" + accColumnNames[k + 1][j] + "= c(" + dataName2 + "[," + (j + 1) + "])");
                                pw.println("results = t.test(" + accColumnNames[i][j] + "," + accColumnNames[k + 1][j] + ", paired=TRUE)");

                                pw.println("tasknames[" + numberOfTasksDone + "] = \"" + taskname + "\"");
                                pw.println("pvalues[" + numberOfTasksDone + "] = " + "results$p.value");
                            }

                        }
                        if (numberOfTasksDone > 0) {
                            //adjust the pvalues{
                            pw.println("pvalues_adj = p.adjust(pvalues, \"bonferroni\")");
                            //now print the name and the adjusted pvalues to file
                            for (int j = 1; j <= numberOfTasksDone; j++) {
                                pw.println("paste(tasknames[" + j + "] , " + "\",\" , pvalues_adj[" + j + "])");//get the pvalue for each column name                               
                            }
                        }

                    }

                }

                //time
                for (int i = 0; i < rpmts.numOfConditions - 1; i++) {
                    String dataName1 = "time" + (i + 1);

                    for (int k = i; k < rpmts.numOfConditions - 1; k++) {
                        String dataName2 = "time" + (k + 2);
                        pw.println("tasknames = NULL");
                        pw.println("pvalues = NULL");
                        int numberOfTasksDone = 0;
                        for (int j = 0; j < timeColumnNames[i].length; j++) {
                            //check if the task is normal before you do this
                            String tname = timeColumnNames[i][j].substring(0, timeColumnNames[i][j].lastIndexOf("_")); //first name without the condition name

                            boolean normalAccuracy = false;

                            for (String task : normalTasks) {
                                if (task.trim().equalsIgnoreCase(tname.trim())) {
                                    normalAccuracy = true;
                                    break;
                                }
                            }
                            //do this for the normal tasks
                            if (normalAccuracy) {
                                numberOfTasksDone++;
                                String taskname = timeColumnNames[i][j] + "-" + timeColumnNames[k + 1][j];
                                pw.println("" + timeColumnNames[i][j] + "= c(" + dataName1 + "[," + (j + 1) + "])");
                                pw.println("" + timeColumnNames[k + 1][j] + "= c(" + dataName2 + "[," + (j + 1) + "])");
                                pw.println("results = t.test(" + timeColumnNames[i][j] + "," + timeColumnNames[k + 1][j] + ", paired=TRUE)");

                                pw.println("tasknames[" + numberOfTasksDone + "] = \"" + taskname + "\"");
                                pw.println("pvalues[" + numberOfTasksDone + "] = " + "results$p.value");
                            }

                        }
                        if (numberOfTasksDone > 0) {
                            //adjust the pvalues{
                            pw.println("pvalues_adj = p.adjust(pvalues, \"bonferroni\")");
                            //now print the name and the adjusted pvalues to file
                            for (int j = 1; j <= numberOfTasksDone; j++) {
                                pw.println("paste(tasknames[" + j + "] , " + "\",\" , pvalues_adj[" + j + "])");//get the pvalue for each column name
                                //pw.println("");
                            }
                        }

                    }

                }

            }
            pw.println("sink()");
            //close the printwriter
            pw.close();

            // System.out.println("---ANOVAPOSTHOCEND---");
            runOSCommandForR(scriptFilename, scriptOutputFilename, rpmts);

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        //read the output file as string and return it.
        String analysisOutput = "";

        analysisOutput = scriptOutputFilename_requiredResult;//fileContentAsString(scriptOutputFilename);

        analysisOutput = rpmts.studydataurl + File.separator + analysisOutput;
        if (File.separatorChar != '/') {
            analysisOutput = analysisOutput.replace(File.separatorChar, '/');
        }
        // System.out.println("POSTHOC ANALYSIS IS --- " + analysisOutput);
        return analysisOutput;

    }

    public String generateTTest(String studyType, ResultParameters rpmts, ArrayList<String> normalTasks) {
        //Write the R-Script
        String scriptFilename = "rscript-ttest.R";
        String scriptOutputFilename = "rscript-ttest.Rout"; //this is for standard output
        String scriptOutputFilename_requiredResult = "ttest-analysis.txt"; //this will be the outputfilename for only the required result

        try {

            //first get the column names of the files
            BufferedReader br;// = new BufferedReader(new FileReader(firstAccuracyFile));
            String line = "";
            int numOfColumns = rpmts.numOfTasks;
            String[][] accColumnNames = new String[rpmts.numOfConditions][rpmts.numOfTasks];
            String[][] timeColumnNames = new String[rpmts.numOfConditions][rpmts.numOfTasks];
            for (int i = 0; i < rpmts.numOfConditions; i++) {
                File AccuracyFile = new File(getServletContext().getRealPath(rpmts.studydataurl + File.separator + rpmts.accuracyFilenames.get(i)));
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

            for (int i = 0; i < rpmts.numOfConditions; i++) {
                File timeFile = new File(getServletContext().getRealPath(rpmts.studydataurl + File.separator + rpmts.timeFilenames.get(i)));
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

            String filePath = getServletContext().getRealPath(rpmts.studydataurl + File.separator + scriptFilename);
            String dataPath = getServletContext().getRealPath(rpmts.studydataurl);

            File scriptFile = new File(filePath);
            FileWriter fileWriter = new FileWriter(scriptFile);
            BufferedWriter bw = new BufferedWriter(fileWriter);
            PrintWriter pw = new PrintWriter(bw);

            if (dataPath.indexOf("\\") >= 0) {
                dataPath = dataPath.replaceAll("\\\\", "\\\\\\\\");
            }
            //first set the working directory
            pw.println("setwd(\"" + dataPath + "\")");

            pw.println("sink(\"" + scriptOutputFilename_requiredResult + "\")");

            //read the accuracy files
            for (int i = 0; i < rpmts.numOfConditions; i++) {
                pw.println("accuracy" + (i + 1) + " = read.csv(\"" + rpmts.accuracyFilenames.get(i) + "\")");
            }

            //TODO: print the phrase of paired t-test as part of the header
            //if it is a within group study,
            //otherwise print the phrase the independent t-test as part of the header
            /**
             * First print the header
             */
            if (studyType.equalsIgnoreCase("Within")) {
                //print the header that includes the paired t-test phrase.
                pw.println("cat(paste(\"\\n" + "Paired T-Test Accuracy-Taskname" + " ,\" ,\"" + "p-value" + ",\" , \"" + "Effect-Size (Cohen's d_z)\"))");//get mean and standard deviation
                pw.println("cat(\"\\n--------------------------------------------------------------\")");

            } else {
                //Formula for finding effect sizes using tstats:
                //1. Cohen's d = 2*t / sqrt(df)  i.e. if the sample sizes are equal
                //2. Cohen's d = t(n1+n2)/sqrt((n1+n2 -2)*(n1*n2))    i.e If sample sizes are not equal

                //print the header that includes the independent t-test phrase.
                pw.println("cat(paste(\"\\n" + "Independent T-Test Accuracy-Taskname" + " ,\" ,\"" + "p-value" + ",\" , \"" + "Effect-Size (Cohen's d)\"))");//get mean and standard deviation
                pw.println("cat(\"\\n--------------------------------------------------------------\")");

            }

            //write the accuracy analysis
            for (int i = 0; i < rpmts.numOfTasks; i++) {

                //check if the task name is part of the normal tasks                
                String tname = accColumnNames[0][i].substring(0, accColumnNames[0][i].lastIndexOf("_")); //first name without the condition name
                // System.out.println("^^^^^ "+tname);

                //tname = tname.trim();
                boolean normalAccuracy = false;
                for (String task : normalTasks) {
                    //  System.out.println("^^--"+task);
                    if (task.trim().equalsIgnoreCase(tname.trim())) {
                        normalAccuracy = true;
                        break;
                    }
                }

                if (normalAccuracy) {
                    //do the analysis for only this task

                    //   System.out.println("-----*****8----- " + tname);
                    String dataName1 = "accuracy1";
                    String dataName2 = "accuracy2";
                    //print the taskname so that we can know which anova this belongs to 
                    String taskname = accColumnNames[0][i].substring(0, accColumnNames[0][i].lastIndexOf("_")); //first name without the condition name

                    //remember we only have two condiotions so we can use the indexes 0 and 1.
                    pw.println("" + accColumnNames[0][i] + "= c(" + dataName1 + "[," + (i + 1) + "])");
                    pw.println("" + accColumnNames[1][i] + "= c(" + dataName2 + "[," + (i + 1) + "])");

                    if (studyType.equalsIgnoreCase("Within")) {
                        pw.println("results<-(t.test(" + accColumnNames[0][i] + "," + accColumnNames[1][i] + ", paired=TRUE))");
                        //calculate the cohen's dz for this one
                        //Cohen's  dz=t/sqrt(n) formula

                        //get the size (n) of one of the vectors                       
                        pw.println("size = length(c(" + dataName1 + "[," + (i + 1) + "]))");
                        pw.println("es = (results$statistic)/sqrt(size)");
                        pw.println("cat(paste(\"\\n" + taskname + "\", " + "\" , \"" + " , results$p.value, "
                                + "\" , " + "\"" + ", abs(es) , " + "\"\\n\" ))");

                    } else {
                        pw.println("results<-(t.test(" + accColumnNames[0][i] + "," + accColumnNames[1][i] + "))");

                        //get the sizes of the 2 vectors.                        
                        pw.println("size1 = length(c(" + dataName1 + "[," + (i + 1) + "]))");
                        pw.println("size2 = length(c(" + dataName2 + "[," + (i + 1) + "]))");
                        pw.println("es = (results$statistic * (size1+size2))/sqrt((results$parameter)  * (size1*size2))");

                        pw.println("cat(paste(\"\\n" + taskname + "\", " + "\" , \"" + " , results$p.value, "
                                + "\" , " + "\"" + ", abs(es) , " + "\"\\n\" ))");

                    }

                    // pw.println("cat(results, sep=\"\\n\")");
                }
            }

            /**
             * Now let's do this for the time also
             */
            //write the accuracy analysis
         /*   for (int i = 0; i < rpmts.numOfConditions - 1; i++) {

             String dataName1 = "accuracy" + (i + 1);
             String dataName2 = "accuracy" + (i + 2);

             for (int j = 0; j < accColumnNames[i].length; j++) {
             pw.println("" + accColumnNames[i][j] + "= c(" + dataName1 + "[," + (j + 1) + "])");
             pw.println("" + accColumnNames[i + 1][j] + "= c(" + dataName2 + "[," + (j + 1) + "])");
             if (studyType.equalsIgnoreCase("Within")) {
             pw.println("results<-capture.output(t.test(" + accColumnNames[i][j] + "," + accColumnNames[i + 1][j] + ", paired=TRUE))");
             } else {
             pw.println("results<-capture.output(t.test(" + accColumnNames[i][j] + "," + accColumnNames[i + 1][j] + "))");
             }

             pw.println("cat(results, sep=\"\\n\")");
             //pw.println("results");
             }
             }  */
            //do the same for time
            for (int i = 0; i < rpmts.numOfConditions; i++) {
                pw.println("time" + (i + 1) + " = read.csv(\"" + rpmts.timeFilenames.get(i) + "\")");
            }

            if (studyType.equalsIgnoreCase("Within")) {
                //print the header that includes the paired t-test phrase.
                pw.println("cat(paste(\"\\n\\n\\n\\n" + "Paired T-Test Time-Taskname" + " ,\" ,\"" + "p-value" + "  ,\" , \"" + "Effect-Size (Cohen's d_z)\"))");//get mean and standard deviation
                pw.println("cat(\"\\n--------------------------------------------------------------\")");

            } else {
                //print the header that includes the independent t-test phrase.
                pw.println("cat(paste(\"\\n\\n\\n\\n" + "Independent T-Test Time-Taskname" + " ,\" ,\"" + "p-value" + " ,\" , \"" + "Effect-Size (Cohen's d)\"))");//get mean and standard deviation
                pw.println("cat(\"\\n--------------------------------------------------------------\\n\")");

            }

            //write the time analysis now.
            for (int i = 0; i < rpmts.numOfTasks; i++) {

                //check if the task name is part of the normal tasks                
                String tname = timeColumnNames[0][i].substring(0, timeColumnNames[0][i].lastIndexOf("_")); //first name without the condition name
                // System.out.println("^^^^^ "+tname);

                //tname = tname.trim();
                boolean normalTime = false;
                for (String task : normalTasks) {
                    //  System.out.println("^^--"+task);
                    if (task.trim().equalsIgnoreCase(tname.trim())) {
                        normalTime = true;
                        break;
                    }
                }

                if (normalTime) {
                    //do the analysis for only this task
                    String dataName1 = "time1";
                    String dataName2 = "time2";
                    //print the taskname so that we can know which anova this belongs to 
                    String taskname = timeColumnNames[0][i].substring(0, timeColumnNames[0][i].lastIndexOf("_")); //first name without the condition name

                    //remember we only have two condiotions so we can use the indexes 0 and 1.
                    pw.println("" + timeColumnNames[0][i] + "= c(" + dataName1 + "[," + (i + 1) + "])");
                    pw.println("" + timeColumnNames[1][i] + "= c(" + dataName2 + "[," + (i + 1) + "])");

                    if (studyType.equalsIgnoreCase("Within")) {
                        pw.println("results<-(t.test(" + timeColumnNames[0][i] + "," + timeColumnNames[1][i] + ", paired=TRUE))");

                        //calculate the cohen's dz for this one
                        //Cohen's  dz=t/sqrt(n) formula                        
                        //get the size (n) of one of the vectors                       
                        pw.println("size = length(c(" + dataName1 + "[," + (i + 1) + "]))");
                        pw.println("es = (results$statistic)/sqrt(size)");
                        pw.println("cat(paste(\"\\n" + taskname + "\", " + "\" , \"" + " , results$p.value, "
                                + "\" , " + "\"" + ", abs(es) , " + "\"\\n\" ))");

                        // pw.println("cat(paste(\"\\n" + taskname + "\", " + "\" , \"" + " , results$p.value, " + "\"\\n\" ))");
                    } else {

                        //Formula for finding effect sizes using tstats:
                        //1. Cohen's d = 2*t / sqrt(df)  i.e. if the sample sizes are equal
                        //2. Cohen's d = t(n1+n2)/sqrt((df)*(n1*n2))    i.e If sample sizes are not equal
                        pw.println("results<-(t.test(" + timeColumnNames[0][i] + "," + timeColumnNames[1][i] + "))");

                        //get the sizes of the 2 vectors.                        
                        pw.println("size1 = length(c(" + dataName1 + "[," + (i + 1) + "]))");
                        pw.println("size2 = length(c(" + dataName2 + "[," + (i + 1) + "]))");
                        pw.println("es = (results$statistic * (size1+size2))/sqrt((results$parameter)  * (size1*size2))");

                        pw.println("cat(paste(\"\\n" + taskname + "\", " + "\" , \"" + " , results$p.value, "
                                + "\" , " + "\"" + ", abs(es) , " + "\"\\n\" ))");
                    }

                    //pw.println("cat(results, sep=\"\\n\")");
                }
            }

            //write the accuracy analysis
           /* for (int i = 0; i < rpmts.numOfConditions - 1; i++) {

             String dataName1 = "time" + (i + 1);
             String dataName2 = "time" + (i + 2);

             for (int j = 0; j < timeColumnNames[i].length; j++) {
             pw.println("" + timeColumnNames[i][j] + "= c(" + dataName1 + "[," + (j + 1) + "])");
             pw.println("" + timeColumnNames[i + 1][j] + "= c(" + dataName2 + "[," + (j + 1) + "])");
             if (studyType.equalsIgnoreCase("Within")) {
             pw.println("results<-capture.output(t.test(" + timeColumnNames[i][j] + "," + timeColumnNames[i + 1][j] + ", paired=TRUE))");
             } else {
             pw.println("results<-capture.output(t.test(" + timeColumnNames[i][j] + "," + timeColumnNames[i + 1][j] + "))");
             }

             pw.println("cat(results, sep=\"\\n\")");
             //pw.println("results");
             }
             }*/
            pw.println("sink()");
            //close the printwriter
            pw.close();

            runOSCommandForR(scriptFilename, scriptOutputFilename, rpmts);

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        //read the output file as string and return it.
        String analysisOutput = "";

        analysisOutput = scriptOutputFilename_requiredResult;//fileContentAsString(scriptOutputFilename);
        analysisOutput = rpmts.studydataurl + File.separator + analysisOutput;
        if (File.separatorChar != '/') {
            analysisOutput = analysisOutput.replace(File.separatorChar, '/');
        }

        return analysisOutput;

    }

    public String generateANOVA(String studyType, ResultParameters rpmts, ArrayList<String> normalTasks) {

        //Write the R-Script
        String scriptFilename = "rscript-anova.R";
        String scriptOutputFilename = "rscript-anova.Rout"; //this is for standard output
        String scriptOutputFilename_requiredResult = "anova-analysis.txt"; //this will be the outputfilename for only the required result

        try {
            //first get the column names of the files
            BufferedReader br;// = new BufferedReader(new FileReader(firstAccuracyFile));
            String line = "";
            int numOfColumns = rpmts.numOfTasks;
            String[][] accColumnNames = new String[rpmts.numOfConditions][rpmts.numOfTasks];
            String[][] timeColumnNames = new String[rpmts.numOfConditions][rpmts.numOfTasks];
            int numOfRows = 0;
            int cnt = 0;
            for (int i = 0; i < rpmts.numOfConditions; i++) {
                File AccuracyFile = new File(getServletContext().getRealPath(rpmts.studydataurl + File.separator + rpmts.accuracyFilenames.get(i)));
                br = new BufferedReader(new FileReader(AccuracyFile));

                cnt = 0;
                while ((line = br.readLine()) != null) {
                    String split[] = line.split(",");
                    if (cnt == 0) {
                        for (int j = 0; j < split.length; j++) {
                            accColumnNames[i][j] = split[j];
                        }
                    }
                    cnt++;
                    //break;
                }
                br.close();
            }
            numOfRows = cnt - 1;
            //  System.out.println("The number of lines is ::  "+ numOfRows);

            for (int i = 0; i < rpmts.numOfConditions; i++) {
                File timeFile = new File(getServletContext().getRealPath(rpmts.studydataurl + File.separator + rpmts.timeFilenames.get(i)));
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

            String filePath = getServletContext().getRealPath(rpmts.studydataurl + File.separator + scriptFilename);
            String dataPath = getServletContext().getRealPath(rpmts.studydataurl);

            File scriptFile = new File(filePath);
            FileWriter fileWriter = new FileWriter(scriptFile);
            BufferedWriter bw = new BufferedWriter(fileWriter);
            PrintWriter pw = new PrintWriter(bw);

            if (dataPath.indexOf("\\") >= 0) {
                dataPath = dataPath.replaceAll("\\\\", "\\\\\\\\");
            }
            //first set the working directory
            pw.println("setwd(\"" + dataPath + "\")");

            pw.println("sink(\"" + scriptOutputFilename_requiredResult + "\")");

            //read the accuracy files in r
            for (int i = 0; i < rpmts.numOfConditions; i++) {
                pw.println("accuracy" + (i + 1) + " = read.csv(\"" + rpmts.accuracyFilenames.get(i) + "\")");
            }
          //  System.out.println("**** The length of accColumnNames is  :: " + accColumnNames.length);

            //write the accuracy analysis
            for (int i = 0; i < rpmts.numOfTasks; i++) {

                //check if the task name is part of the normal tasks                
                String tname = accColumnNames[0][i].substring(0, accColumnNames[0][i].lastIndexOf("_")); //first name without the condition name
                // System.out.println("^^^^^ "+tname);

                //tname = tname.trim();
                boolean normalAccuracy = false;
                for (String task : normalTasks) {
                    //  System.out.println("^^--"+task);
                    if (task.trim().equalsIgnoreCase(tname.trim())) {
                        normalAccuracy = true;
                        break;
                    }
                }

                if (normalAccuracy) {
                    ArrayList<String> colnames = new ArrayList<String>();
                    for (int j = 0; j < rpmts.numOfConditions; j++) {
                        String dataName = "accuracy" + (j + 1);
                        pw.println("" + accColumnNames[j][i] + "= c(" + dataName + "[," + (i + 1) + "])");

                        colnames.add(accColumnNames[j][i]);
                    }

                    //print the taskname so that we can know which anova this belongs to 
                    String taskname = accColumnNames[0][i].substring(0, accColumnNames[0][i].lastIndexOf("_")); //first name without the condition name
                    pw.println("taskname=\"TaskName = " + taskname + "\"");
                    pw.println("cat(taskname)");

                    //do the r cbind
                    String cbindData = "combineddata =data.frame(cbind(";
                    for (int k = 0; k < colnames.size(); k++) {
                        //System.out.print("    " + coln);
                        if (k == 0) {
                            cbindData += colnames.get(k);
                        } else {
                            cbindData += "," + colnames.get(k);
                        }
                    }
                    cbindData += "))";  //closing parentheses
                    pw.println(cbindData);

                    //now do the stack before you do the anova
                    pw.println("combineddata = stack(combineddata)");
                    // pw.println("anovaresult = aov(lm(values ~ ind, combineddata))");

                    if (!studyType.equalsIgnoreCase("Within")) {
                        pw.println("anovaresult = aov(lm(values ~ ind, combineddata))");
                    } else {//do this if it is a within study. //i.e. a repeated measure anova
                        //TODO: Effect-size  for anova 

                        pw.println("numcases = " + numOfRows);  //the number of cases goes here
                        pw.println("numvariables =" + rpmts.numOfConditions);
                        pw.println("recall.df = data.frame(recall = combineddata,");
                        pw.println("subj=factor(rep(paste(\"subj\", 1:numcases, sep=\"\"), numvariables)))");
                        pw.println("anovaresult = aov(recall.values ~ recall.ind + Error(subj/recall.ind), data=recall.df)");
                    }

                    pw.println("summary(anovaresult)");
                    pw.println("cat(\"\\n\")"); //leave a couple of lines to separate the 
                    pw.println("cat(\"****************************************************************\")");
                    pw.println("cat(\"\\n\\n\")"); //leave a couple of lines to separate the 
                }
            }

            /**
             * ******************************************************
             */
            /**
             * ********** Do the same for the time as well *********
             */
            //write the time analysis
            //read the accuracy files
            for (int i = 0; i < rpmts.numOfConditions; i++) {
                pw.println("time" + (i + 1) + " = read.csv(\"" + rpmts.timeFilenames.get(i) + "\")");
            }

            //write the time analysis
            for (int i = 0; i < rpmts.numOfTasks; i++) {

                //print the taskname so that we can know which anova this belongs to 
                String tname = timeColumnNames[0][i].substring(0, timeColumnNames[0][i].lastIndexOf("_")); //first name without the condition name
                //tname = tname.trim();
                //  System.out.println("^^^^^"+tname);
                boolean normalTime = false;
                for (String task : normalTasks) {
                    //  System.out.println(task + "---" + tname);
                    if (task.trim().equalsIgnoreCase(tname.trim())) {
                        normalTime = true;
                        //   System.out.println("Yay!");
                        break;
                    }
                }

                if (normalTime) {
                    ArrayList<String> colnames = new ArrayList<String>();
                    for (int j = 0; j < rpmts.numOfConditions; j++) {
                        String dataName = "time" + (j + 1);
                        pw.println("" + timeColumnNames[j][i] + "= c(" + dataName + "[," + (i + 1) + "])");
                        colnames.add(timeColumnNames[j][i]);
                    }

                    //print the taskname so that we can know which anova this belongs to 
                    String taskname = timeColumnNames[0][i].substring(0, timeColumnNames[0][i].lastIndexOf("_")); //first name without the condition name
                    pw.println("taskname=\"TaskName = " + taskname + "\"");
                    pw.println("cat(taskname)");
                    pw.println("cat(\"\\n------------------------------------------------\")");

                    //do the r cbind
                    String cbindData = "combineddata =data.frame(cbind(";
                    for (int k = 0; k < colnames.size(); k++) {
                        //System.out.print("    " + coln);
                        if (k == 0) {
                            cbindData += colnames.get(k);
                        } else {
                            cbindData += "," + colnames.get(k);
                        }
                    }
                    cbindData += "))";  //closing parentheses
                    pw.println(cbindData);

                    //now do the stack before you do the anova
                    pw.println("combineddata = stack(combineddata)");

                    if (!studyType.equalsIgnoreCase("Within")) {
                        pw.println("anovaresult = aov(lm(values ~ ind, combineddata))");
                    } else {//do this if it is a within study. //i.e. a repeated measure anova
                        pw.println("numcases = " + numOfRows);  //the number of cases goes here
                        pw.println("numvariables=" + rpmts.numOfConditions);
                        pw.println("recall.df=data.frame(recall=combineddata,");
                        pw.println("subj=factor(rep(paste(\"subj\", 1:numcases, sep=\"\"), numvariables)))");
                        pw.println("anovaresult = aov(recall.values ~ recall.ind + Error(subj/recall.ind), data=recall.df)");
                    }

                    pw.println("summary(anovaresult)");
                    pw.println("cat(\"\\n\")"); //leave a couple of lines to separate the 
                    pw.println("cat(\"********************************************************************\")");
                    pw.println("cat(\"\\n\\n\")"); //leave a couple of lines to separate the 

                }

            }

            pw.println("sink()");
            //close the printwriter
            pw.close();

            runOSCommandForR(scriptFilename, scriptOutputFilename, rpmts);

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        //read the output file as string and return it.
        String analysisOutput = "";

        analysisOutput = scriptOutputFilename_requiredResult;//fileContentAsString(scriptOutputFilename);

        analysisOutput = rpmts.studydataurl + File.separator + analysisOutput;
        if (File.separatorChar != '/') {
            analysisOutput = analysisOutput.replace(File.separatorChar, '/');
        }

        return analysisOutput;

    }

    public String getSummarizedRawData(ResultParameters rpmts) {
        String filename = "summarizedRawData.txt";
        //TODO: //read the summarized - Results file and arrange them based on tasks
        ArrayList<String[][]> allAccResultsBasedOnTasks = new ArrayList<String[][]>();
        ArrayList<String[][]> allTimeResultsBasedOnTasks = new ArrayList<String[][]>();

        //initialize the arrays that will be used to hold the results per task 
        for (int i = 0; i < rpmts.numOfTasks; i++) {
            allAccResultsBasedOnTasks.add(new String[rpmts.maxResultsRows][rpmts.numOfConditions]);
            allTimeResultsBasedOnTasks.add(new String[rpmts.maxResultsRows][rpmts.numOfConditions]);
        }

        //rearrange the results of each task type      
        for (int i = 0; i < rpmts.accuracyResults.size(); i++) {
            String[][] arr = (String[][]) rpmts.accuracyResults.get(i);
            for (int j = 0; j < rpmts.numOfTasks; j++) {
                for (int k = 0; k < arr.length; k++) {
                    allAccResultsBasedOnTasks.get(j)[k][i] = arr[k][j];
                }
            }
        }
        //do the same for the time
        for (int i = 0; i < rpmts.timeResults.size(); i++) {
            String[][] arr = (String[][]) rpmts.timeResults.get(i);
            for (int j = 0; j < rpmts.numOfTasks; j++) {
                for (int k = 0; k < arr.length; k++) {
                    //  System.out.print( arr[k][j] + ",");
                    allTimeResultsBasedOnTasks.get(j)[k][i] = arr[k][j];
                }
            }
        }

        try {
            filename = "summarizedRawData.txt";
            File file = new File(getServletContext().getRealPath(rpmts.studydataurl + File.separator + filename));
            FileWriter fileWriter = new FileWriter(file);

            BufferedWriter bw = new BufferedWriter(fileWriter);
            PrintWriter pw = new PrintWriter(bw);

            //accuracy
            for (int i = 0; i < allAccResultsBasedOnTasks.size(); i++) {
                String arr[][] = allAccResultsBasedOnTasks.get(i);
                for (int j = 0; j < arr.length; j++) {
                    for (int k = 0; k < rpmts.numOfConditions; k++) {
                        if (k == 0) {
                            pw.print(arr[j][k]);
                        } else {
                            pw.print("," + arr[j][k]);
                        }
                    }
                    pw.println();
                }
                pw.println();

                pw.println();
            }

            //time
            for (int i = 0; i < allTimeResultsBasedOnTasks.size(); i++) {
                String arr[][] = allTimeResultsBasedOnTasks.get(i);
                for (int j = 0; j < arr.length; j++) {
                    for (int k = 0; k < rpmts.numOfConditions; k++) {
                        if (k == 0) {
                            pw.print(arr[j][k]);
                        } else {
                            pw.print("," + arr[j][k]);
                        }
                    }
                    pw.println();
                }
                pw.println();

                pw.println();
            }

            pw.close();

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        filename = rpmts.studydataurl + File.separator + filename;
        if (File.separatorChar != '/') {
            filename = filename.replace(File.separatorChar, '/');
        }
        return filename;

        //now write to a file.
    }

    public String getBasicRawData(ResultParameters rpmts) {
        String filename = "basicRawData.txt";
        //TODO: //read the summarized - Results file and arrange them based on tasks
        ArrayList<String[][]> allAccResultsBasedOnTasks = new ArrayList<String[][]>();
        ArrayList<String[][]> allTimeResultsBasedOnTasks = new ArrayList<String[][]>();

        //initialize the arrays that will be used to hold the results per task 
        for (int i = 0; i < rpmts.numOfTasks; i++) {
            allAccResultsBasedOnTasks.add(new String[rpmts.maxResultsRowsBasic][rpmts.numOfConditions]);
            allTimeResultsBasedOnTasks.add(new String[rpmts.maxResultsRowsBasic][rpmts.numOfConditions]);
        }

        //rearrange the results of each task type      
        for (int i = 0; i < rpmts.accuracyResultsBasic.size(); i++) {
            String[][] arr = (String[][]) rpmts.accuracyResultsBasic.get(i);
            for (int j = 0; j < rpmts.numOfTasks; j++) {
                for (int k = 0; k < arr.length; k++) {
                    allAccResultsBasedOnTasks.get(j)[k][i] = arr[k][j];
                }
            }
        }
        //do the same for the time
        for (int i = 0; i < rpmts.timeResultsBasic.size(); i++) {
            String[][] arr = (String[][]) rpmts.timeResultsBasic.get(i);
            for (int j = 0; j < rpmts.numOfTasks; j++) {
                for (int k = 0; k < arr.length; k++) {
                    //  System.out.print( arr[k][j] + ",");
                    allTimeResultsBasedOnTasks.get(j)[k][i] = arr[k][j];
                }
            }
        }

        try {
            filename = "basicRawData.txt";
            File file = new File(getServletContext().getRealPath(rpmts.studydataurl + File.separator + filename));
            FileWriter fileWriter = new FileWriter(file);

            BufferedWriter bw = new BufferedWriter(fileWriter);
            PrintWriter pw = new PrintWriter(bw);

            //accuracy
            for (int i = 0; i < allAccResultsBasedOnTasks.size(); i++) {
                String arr[][] = allAccResultsBasedOnTasks.get(i);
                for (int j = 0; j < arr.length; j++) {
                    for (int k = 0; k < rpmts.numOfConditions; k++) {
                        if (k == 0) {
                            pw.print(arr[j][k]);
                        } else {
                            pw.print(", ," + arr[j][k]);
                        }
                    }
                    pw.println();
                }
                pw.println();
                pw.println();
            }

            //time         
            for (int i = 0; i < allTimeResultsBasedOnTasks.size(); i++) {
                String arr[][] = allTimeResultsBasedOnTasks.get(i);
                for (int j = 0; j < arr.length; j++) {
                    for (int k = 0; k < rpmts.numOfConditions; k++) {
                        if (k == 0) {
                            pw.print(arr[j][k]);
                        } else {
                            pw.print(", ," + arr[j][k]);
                        }
                    }
                    pw.println();
                }
                pw.println();
                pw.println();
            }
            pw.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        filename = rpmts.studydataurl + File.separator + filename;
        if (File.separatorChar != '/') {
            filename = filename.replace(File.separatorChar, '/');
        }

        return filename;
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
