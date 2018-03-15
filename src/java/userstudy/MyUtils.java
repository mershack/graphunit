package userstudy;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import javax.servlet.http.HttpServletRequest;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author Mershack
 */
public class MyUtils {

    ArrayList<String> conditionNames = new ArrayList<String>();

    public MyUtils() {

    }

    public MyUtils(ArrayList condnames) {
        conditionNames = condnames; 
        
//        for(int i=0; i<conditionNames.size(); i++){
//            System.out.println("\t\t... "+conditionNames.get(i));
//        }
        
    }

    public String getConditionAccuracyFileName(String cname) {
        String filename = "";

        //this one returns the condition name, based on the condition code. 
        //Currently only 5 conditions but we will extend this code later.
        for (int i = 0; i < conditionNames.size(); i++) {
            if (conditionNames.get(i).equalsIgnoreCase(cname)) {
                filename = "AccuracyResults" + (i + 1) + ".txt";
            }
        }

        return filename;
    }


    public String getConditionQualitativeQnFileName(String cname) {
        String filename = "";

        for (int i = 0; i < conditionNames.size(); i++) {
            if (conditionNames.get(i).equalsIgnoreCase(cname)) {
                filename = "QualTask_PartOfActualTasks" + (i + 1) + ".txt";
                break;
            }
        }
        return filename;
    }

    public String getConditionTimeFileName(String cname) {
        String filename = "";

        for (int i = 0; i < conditionNames.size(); i++) {
            if (conditionNames.get(i).equalsIgnoreCase(cname)) {
                filename = "TimeResults" + (i + 1) + ".txt";
                break;
            }
        }

        /*if (cname.equalsIgnoreCase("cond1")) {
         filename = "TimeResults1.txt";
         } else if (cname.equalsIgnoreCase("cond2")) {
         filename = "TimeResults2.txt";
         } else if (cname.equalsIgnoreCase("cond3")) {
         filename = "TimeResults3.txt";
         } else if (cname.equalsIgnoreCase("cond4")) {
         filename = "TimeResults4.txt";
         } else if (cname.equalsIgnoreCase("cond5")) {
         filename = "TimeResults5.txt";
         } */
        return filename;
    }

    public String getConditionAccuracyBasicFileName(String cname) {
        String filename = "";
        //this one returns the condition name, based on the condition code. 
        //Currently only 5 conditions but we will extend this code later.

        for (int i = 0; i < conditionNames.size(); i++) {
            if (conditionNames.get(i).equalsIgnoreCase(cname)) {
                filename = "AccuracyResultsBasic" + (i + 1) + ".txt";
                break;
            }

        }

        /* if (cname.equalsIgnoreCase("cond1")) {
         filename = "AccuracyResultsBasic1.txt";
         } else if (cname.equalsIgnoreCase("cond2")) {
         filename = "AccuracyResultsBasic2.txt";
         } else if (cname.equalsIgnoreCase("cond3")) {
         filename = "AccuracyResultsBasic3.txt";
         } else if (cname.equalsIgnoreCase("cond4")) {
         filename = "AccuracyResultsBasic4.txt";
         } else if (cname.equalsIgnoreCase("cond5")) {
         filename = "AccuracyResultsBasic5.txt";
         }   */
        return filename;
    }

    public String getConditionErrorBasicFileName(String cname) {
        String filename = "";
        //this one returns the condition name, based on the condition code. 
        //Currently only 5 conditions but we will extend this code later.

        for (int i = 0; i < conditionNames.size(); i++) {
            if (conditionNames.get(i).equalsIgnoreCase(cname)) {
                filename = "ErrorResultsBasic" + (i + 1) + ".txt";
                break;
            }

        }

        /* if (cname.equalsIgnoreCase("cond1")) {
         filename = "AccuracyResultsBasic1.txt";
         } else if (cname.equalsIgnoreCase("cond2")) {
         filename = "AccuracyResultsBasic2.txt";
         } else if (cname.equalsIgnoreCase("cond3")) {
         filename = "AccuracyResultsBasic3.txt";
         } else if (cname.equalsIgnoreCase("cond4")) {
         filename = "AccuracyResultsBasic4.txt";
         } else if (cname.equalsIgnoreCase("cond5")) {
         filename = "AccuracyResultsBasic5.txt";
         }   */
        return filename;
    }

    public String getConditionMissedBasicFileName(String cname) {
        String filename = "";
        //this one returns the condition name, based on the condition code. 
        //Currently only 5 conditions but we will extend this code later.

        for (int i = 0; i < conditionNames.size(); i++) {
            if (conditionNames.get(i).equalsIgnoreCase(cname)) {
                filename = "MissedResultsBasic" + (i + 1) + ".txt";
                break;
            }

        }

        /* if (cname.equalsIgnoreCase("cond1")) {
         filename = "AccuracyResultsBasic1.txt";
         } else if (cname.equalsIgnoreCase("cond2")) {
         filename = "AccuracyResultsBasic2.txt";
         } else if (cname.equalsIgnoreCase("cond3")) {
         filename = "AccuracyResultsBasic3.txt";
         } else if (cname.equalsIgnoreCase("cond4")) {
         filename = "AccuracyResultsBasic4.txt";
         } else if (cname.equalsIgnoreCase("cond5")) {
         filename = "AccuracyResultsBasic5.txt";
         }   */
        return filename;
    }

    public String getConditionTimeBasicFileName(String cname) {
        String filename = "";

        for (int i = 0; i < conditionNames.size(); i++) {
            if (conditionNames.get(i).equalsIgnoreCase(cname)) {
                filename = "TimeResultsBasic" + (i + 1) + ".txt";
            }
        }

        /*  if (cname.equalsIgnoreCase("cond1")) {
         filename = "TimeResultsBasic1.txt";
         } else if (cname.equalsIgnoreCase("cond2")) {
         filename = "TimeResultsBasic2.txt";
         } else if (cname.equalsIgnoreCase("cond3")) {
         filename = "TimeResultsBasic3.txt";
         } else if (cname.equalsIgnoreCase("cond4")) {
         filename = "TimeResultsBasic4.txt";
         } else if (cname.equalsIgnoreCase("cond5")) {
         filename = "TimeResultsBasic5.txt";
         } */
        return filename;
    }

    /**
     * create a list of all the accuracy result files
     *
     * @param numOfConditions - The number of conditions we are dealing with
     * @return an ArrayList of the filenames.
     */
    public ArrayList<String> getAccuracyFileNames(int numOfConditions) {

        ArrayList<String> filenames = new ArrayList<String>();

        for (int i = 0; i < numOfConditions; i++) {
            String filename = "AccuracyResults" + (i + 1) + ".txt";
            String filenameBasic = "AccuracyResultsBasic" + (i + 1) + ".txt";

            filenames.add(filename);
            filenames.add(filenameBasic);

        }
        return filenames;
    }

    /**
     * Create a list o fall the time result Files
     *
     * @param numOfConditions - the number of conditions we are dealing with
     * @return an ArrayList of the filenames
     */
    public ArrayList<String> getTimeFileNames(int numOfConditions) {

        ArrayList<String> filenames = new ArrayList<String>();

        for (int i = 0; i < numOfConditions; i++) {
            String filename = "TimeResults" + (i + 1) + ".txt";
            String filenameBasic = "TimeResultsBasic" + (i + 1) + ".txt";

            filenames.add(filename);
            filenames.add(filenameBasic);

        }
        return filenames;
    }
    
    public String getTaskCode(HttpServletRequest servlet,  String task, String userid) {
        
        String shortname = "";

        //check if we can find the task among the existing tasks, 
        //otherwise check the tasks in the user directory
        try {
            File qlFile = new File(servlet.getServletContext()
                    .getRealPath("quanttasks" + File.separator + "quanttasklist.txt"));

            BufferedReader br = new BufferedReader(new FileReader(qlFile));
            String line = "";
            String sn = "";
            
            while ((line = br.readLine()) != null) {
                sn = line.split(":::")[0].trim();
                String t = line.split(":::")[1].trim();
                if (t.equalsIgnoreCase(task.trim())) {
                    shortname = sn;
                    break;
                }
            }
            br.close();

            //now check if the shortname has already been found ampong the system's files
            if (shortname.trim().equalsIgnoreCase("")) {
                //get the shortname among the user's files
                qlFile = new File(servlet.getServletContext()
                        .getRealPath("users" + File.separator + userid + File.separator
                        + "quanttasks" + File.separator + "quanttasklist.txt"));

                br = new BufferedReader(new FileReader(qlFile));
                line = "";
                sn = "";
                while ((line = br.readLine()) != null) {
                    sn = line.split(":::")[0].trim();
                    String t = line.split(":::")[1].trim();
                    if (t.equalsIgnoreCase(task.trim())) {
                        shortname = sn;
                        break;
                    }
                }
                br.close();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        
        return shortname;
    }
    
    
    
    
}
