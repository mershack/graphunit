/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package userstudy;

import java.util.ArrayList;

/**
 *
 * @author Mershack
 */
public class ResultParameters {
    public ArrayList<String> accuracyFilenames = new ArrayList<String>();
    public  ArrayList<String> timeFilenames = new ArrayList<String>();
    public  final String DATA_DIR = "data";
    public ArrayList<String[][]> accuracyResults = new ArrayList<String[][]>();
    public ArrayList<String[][]> accuracyResultsBasic = new ArrayList<String[][]>();

    public ArrayList<String[][]> timeResults = new ArrayList<String[][]>();
    public ArrayList<String[][]> timeResultsBasic = new ArrayList<String[][]>();

    public ArrayList<String> accuracySummary_Label;
    public ArrayList<Double> accuracySummary_Value;
    public ArrayList<Double> accuracyStandardError_Value;

    public ArrayList<String> timeSummary_Label;
    public ArrayList<Double> timeSummary_Value;
    public ArrayList<Double> timeStandardError_Value;

    public int numOfConditions = 2;
    public int numOfTasks = 2;
    public int maxResultsRows = 0;
    public int maxResultsRowsBasic = 0;
    public String studyType = "";
    public String studydataurl ="";
    public ArrayList<Integer> numOfCompletedStudiesPerCondition = new ArrayList<Integer>();
    public ArrayList<String> labelsForcompletedStudiesPerCondition= new ArrayList<String>();
    
    public ArrayList<String> viewerConditionShortnames = new ArrayList<String>();
    
    public MyUtils utils = new MyUtils();
}
