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

    public String expType_vis = "Between";  //"Within"
    public String expType_ds = "Within";
    // String studyType = "Within";  //
    //  boolean firstTime = true;

    public ArrayList<String> questions = new ArrayList<String>();
    public ArrayList<Integer> questionSizes = new ArrayList<Integer>();
    public ArrayList<String> inputTypeList = new ArrayList<String>();
    public ArrayList<String> questionCodes = new ArrayList<String>();
    public ArrayList<Integer> questionMaxTimes = new ArrayList<Integer>();
    public ArrayList<TaskDetails> taskDetails = new ArrayList<TaskDetails>();
    
    //for pre-study
    public ArrayList<String> preStudyQuestionCodes = new ArrayList<String>();
    public ArrayList<String> preStudyQuestions = new ArrayList<String>();
    public ArrayList<TaskDetails> preStudyTaskDetails = new ArrayList<TaskDetails>();
    
    //for post study
    public ArrayList<String> postStudyQuestionCodes = new ArrayList<String>();
    public ArrayList<String> postStudyQuestions = new ArrayList<String>();  
    public ArrayList<TaskDetails> postStudyTaskDetails = new ArrayList<TaskDetails>();
    
    
    
    public ArrayList<String> viewerConditionUrls = new ArrayList<String>();
    public ArrayList<String> viewerConditionShortnames = new ArrayList<String>();
    public ArrayList<String> tutorialViewerShortnames = new ArrayList<String>();
    public ArrayList<String> tutorialViewerUrls = new ArrayList<String>();
        
    public ArrayList<String> qualQuestionsAfter = new ArrayList<String>();
    public ArrayList<String> qualQuestionCodesAfter = new ArrayList<String>();
    public ArrayList<String> qualQuestionsBefore = new ArrayList<String>();
    public ArrayList<String> qualQuestionCodesBefore = new ArrayList<String>();

    public String firstConditionShortName = "";
    public String firstConditionUrl = "";

   //
    public String studyname;
    public String dataseturl;
    public String nodePositions;
    public String datasetname;

    //ArrayList<String> taskTypes = new ArrayList<String>();
    public ArrayList<String> orderOfConditionShortNames = new ArrayList<String>();
    public ArrayList<String> orderOfConditionUrls = new ArrayList<String>();

    public String currentCondition = "cond1";
    public ArrayList<String> currentConditions = new ArrayList<String>();

    public ArrayList<EvaluationQuestion> evalQuestions = new ArrayList<EvaluationQuestion>();
    public ArrayList<EvaluationQuestion> tutorialQuestions = new ArrayList<EvaluationQuestion>();
  
    public ArrayList<EvaluationQuestion> preStudyEvalQuestions = new ArrayList<EvaluationQuestion>();
    public ArrayList<EvaluationQuestion> postStudyEvalQuestions = new ArrayList<EvaluationQuestion>();
        
    public ArrayList<QualitativeQuestion> qualEvalQuestionAfter = new ArrayList<QualitativeQuestion>();
    public ArrayList<QualitativeQuestion> qualEvalQuestionBefore = new ArrayList<QualitativeQuestion>();
    
    private ArrayList<Introduction> introductions = new ArrayList<Introduction>();

    public int testCounter = 0;
    public int tutorialCounter = 0;
    public int viewerConditionCounter = 0;
    public int datasetConditionCounter = 0;
    public boolean isTutorial = true;
    public String turkCode = "AEJOUK1";
    public boolean viewersChanged = false;
    public int numberOfConditions = 2;
    public int numberOfTasks = 2;
    public int sizeOfACondition;
    public int trainingSize = 2;
    public boolean studyDetailsLoaded = false;
    public String instruction = "";
    public MyUtils utils = new MyUtils();

    public String viewerWidth = "";
    public String viewerHeight = "";

    public int totalNumOfQuestions = 0;

    public String userViewerWidth = "";
    public String userViewerHeight = "";
    public String offFocusTime = "";
    public String dateAndTime = "";
    public String colorBlindnessTestAnswers = "";

    public String accuracyInfo = "";
    public String timeInfo = "";
    public boolean miscellaneousInfoSaved = false;
    public boolean quantitativeAnswersSaved = false;
    public boolean qualitativeQnsSent = false;
    public int datasetSetSizeBeforeAdjustment = 0;
    public int viewerSizeBeforeAdjustment = 0;
    public int tutorialViewerConditionCounter = 0;
    public boolean tutorialViewerchanged = false;
    public int sizeOfATrainingCondition = 0;
    
    public int sizeOfADatasetCondition = 0;
    
    private int questionGroupCounter = 0;
    
    

    private ArrayList<String> datasetConditionNames = new ArrayList<String>();
    private ArrayList<String> datasetFormats = new ArrayList<String>();
    private ArrayList<String> datasetConditionUrls = new ArrayList<String>();
    private ArrayList<String> datasetTypes = new ArrayList<String>();

    private int questionsWithTraining = 0;

    public void setDatasetConditionNames(ArrayList<String> dcn) {
        datasetConditionNames = dcn;
    }

    public ArrayList<String> getDatasetConditionNames() {
        return datasetConditionNames;
    }

    public void setDatasetTypes(ArrayList<String> dt) {
        datasetTypes = dt;
    }

    public ArrayList<String> getDatasetTypes() {
        return datasetTypes;
    }

   /* public void setDatasetFormats(ArrayList<String> df) {
        datasetFormats = df;
    }

    public ArrayList<String> getDatasetFormats() {
        return datasetFormats;
    }*/

    public void setDatasetConditionUrls(ArrayList<String> dcu) {
        datasetConditionUrls = dcu;
    }

    public ArrayList<String> getDatasetConditionUrls() {
        return datasetConditionUrls;
    }

    public int getQuestionsWithTraining() {
        return questionsWithTraining;
    }

    public void setQuestionsWithTraining(int questionsWithTraining) {
        this.questionsWithTraining = questionsWithTraining;
    }

    public void incrementQuestionsWithTraining() {
        questionsWithTraining++;
    }

    public ArrayList<Introduction> getIntroductions() {
        return introductions;
    }

    public void setIntroductions(ArrayList<Introduction> introductions) {
        this.introductions = introductions;
    }
    
    public void addIntroduction(Introduction introduction) {
        this.introductions.add(introduction);
    }
    
    public String getIntroduction(String condition) {
        String intro = "";
        for (int i = 0; i < this.introductions.size(); i++) {
            if (this.introductions.get(i).getCondition().equalsIgnoreCase(condition.trim())) {
                intro = this.introductions.get(i).getUrl();
            }
        }
        
        return intro;
    }

    public int getQuestionGroupCounter() {
        return questionGroupCounter;
    }
    
    public void setQuestionGroupCounter(int qgc) {
        this.questionGroupCounter = qgc;
    }


    public void incrementQuestionGroupCounter() {
        this.questionGroupCounter++;
    }
}
