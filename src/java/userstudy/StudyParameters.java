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
public class StudyParameters {

    public String studyType = "Between";  //"Within"
    // String studyType = "Within";  //
    //  boolean firstTime = true;

    public ArrayList<String> questions = new ArrayList<String>();
    public ArrayList<Integer> questionSizes = new ArrayList<Integer>();
    public ArrayList<String> questionCodes = new ArrayList<String>();
    public ArrayList<Integer> questionMaxTimes = new ArrayList<Integer>();
    public ArrayList<String> viewerConditionUrls = new ArrayList<String>();
    public ArrayList<String> viewerConditionShortnames = new ArrayList<String>();
    public ArrayList<String> qualQuestionsAfter = new ArrayList<String>();
    public ArrayList<String> qualQuestionCodesAfter = new ArrayList<String>();
    public ArrayList<String> qualQuestionsBefore = new ArrayList<String>();
    public ArrayList<String> qualQuestionCodesBefore = new ArrayList<String>();    
    
    
    
    public String firstConditionShortName = "";
    public String firstConditionUrl = "";
    public String studyname;
    public String dataseturl;
    public String nodePositions;
    public String datasetname;

    //ArrayList<String> taskTypes = new ArrayList<String>();
    public ArrayList<String> orderOfConditionShortNames = new ArrayList<String>();
    public ArrayList<String> orderOfConditionUrls = new ArrayList<String>();

    public String currentCondition = "cond1";
    public String currentConditions;

    public ArrayList<EvaluationQuestion> evalQuestions = new ArrayList<EvaluationQuestion>();
    public ArrayList<EvaluationQuestion> tutorialQuestions = new ArrayList<EvaluationQuestion>();
    public ArrayList<QualitativeQuestion> qualEvalQuestionAfter = new ArrayList<QualitativeQuestion>();
    public ArrayList<QualitativeQuestion> qualEvalQuestionBefore = new ArrayList<QualitativeQuestion>();

    public int testCounter = 0;
    public int tutorialCounter = 0;
    public int viewerConditionCounter = 0;
    public boolean isTutorial = true;
    public String turkCode = "AEJOUK1";
    public boolean viewersChanged = false;
    public int numberOfConditions = 2;
    public int numberOfTasks = 2;
    public int sizeOfACondition;
    public int tutorialSize = 2;
    public boolean studyDetailsLoaded = false;
    public String instruction = "";
    public MyUtils utils = new MyUtils();
    
    public String viewerWidth = "";
    public String viewerHeight = "";
    
    public int totalNumOfQuestions = 0;

}
