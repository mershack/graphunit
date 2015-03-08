package userstudy;

import java.util.ArrayList;

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
    
    public MyUtils(){
        
    }
    public MyUtils(ArrayList condnames){
        conditionNames = condnames;
    }
    
     public String getConditionAccuracyFileName(String cname) {
        String filename = "";

        //this one returns the condition name, based on the condition code. 
        //Currently only 5 conditions but we will extend this code later.
         for(int i=0; i<conditionNames.size(); i++){
            if(conditionNames.get(i).equalsIgnoreCase(cname)){
                filename = "AccuracyResults" +(i+1) +".txt";
            }
            
         }
        
        
       /* if (cname.equalsIgnoreCase("cond1")) {
            filename = "AccuracyResults1.txt";
        } else if (cname.equalsIgnoreCase("cond2")) {
            filename = "AccuracyResults2.txt";
        } else if (cname.equalsIgnoreCase("cond3")) {
            filename = "AccuracyResults3.txt";
        } else if (cname.equalsIgnoreCase("cond4")) {
            filename = "AccuracyResults4.txt";
        } else if (cname.equalsIgnoreCase("cond5")) {
            filename = "AccuracyResults5.txt";
        }*/

        return filename;
    }

    public String getConditionTimeFileName(String cname) {
        String filename = "";

        
        for(int i=0; i<conditionNames.size(); i++){
            if(conditionNames.get(i).equalsIgnoreCase(cname)){
                 filename = "TimeResults" + (i+1) + ".txt";
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
        
        
        for(int i=0; i<conditionNames.size(); i++){
            if(conditionNames.get(i).equalsIgnoreCase(cname)){
                filename = "AccuracyResultsBasic" + (i+1) +".txt";
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

          for(int i=0; i<conditionNames.size(); i++){
            if(conditionNames.get(i).equalsIgnoreCase(cname)){
                 filename = "TimeResultsBasic" +(i+1) +".txt";
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
     * @param numOfConditions - The number of conditions we are dealing with
     * @return an ArrayList of the filenames.
     */
    public ArrayList<String> getAccuracyFileNames(int numOfConditions){
        
        ArrayList<String> filenames = new ArrayList<String>();
             
        for(int i=0; i<numOfConditions; i++){
            String filename = "AccuracyResults"+(i+1) + ".txt";
            String filenameBasic = "AccuracyResultsBasic" + (i+1) + ".txt";
         
            filenames.add(filename);
            filenames.add(filenameBasic);
            
            
        }
        return filenames;              
    }
    
    /**
     * Create a list o fall the time result Files
     * @param numOfConditions - the number of conditions we are dealing with
     * @return an ArrayList of the filenames
     */
    public ArrayList<String> getTimeFileNames(int numOfConditions){
        
        ArrayList<String> filenames = new ArrayList<String>();
   
        for(int i=0; i<numOfConditions; i++){
            String filename = "TimeResults"+(i+1) + ".txt";
            String filenameBasic = "TimeResultsBasic" + (i+1) + ".txt";
         
            filenames.add(filename);
            filenames.add(filenameBasic);
            
            
        }
        return filenames;              
    }
}
