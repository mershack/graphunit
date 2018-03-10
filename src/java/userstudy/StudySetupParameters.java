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
public class StudySetupParameters {    
    ArrayList<introductionFileParameters> introductionFiles = new ArrayList<introductionFileParameters>();
    ArrayList<StandardTestParam> standardTests = new ArrayList<StandardTestParam>();
       
    ArrayList<String> viewerConditions = new ArrayList<String>();
    ArrayList<String> viewerConditionShortNames = new ArrayList<String>();
    ArrayList<String> quantitativeQuestions = new ArrayList<String>();
    ArrayList<String> preStudyQuestions = new ArrayList<String>();
    ArrayList<String> postStudyQuestions = new ArrayList<String>();
    
    
    ArrayList<String> quantQuestionSizes = new ArrayList<String>();
    ArrayList<String> quantQuestionTime = new ArrayList<String>();
    
    
    
    ArrayList<String> qualitativeQuestions = new ArrayList<String>();
    ArrayList<String> qualitativeQuestionsPositions = new ArrayList<String>();

    ArrayList<String> datasets = new ArrayList<String>();
    ArrayList<String> datasetFormats = new ArrayList<String>();
    ArrayList<String> datasetTypes = new ArrayList<String>();
    String datasetType;
    String expType_vis;
    String expType_ds;
    String studyname;
    String studydataurl;
    String viewerWidth ="";
    String viewerHeight ="";
    int trainingSize = 2;
    
    
    private String managementCommand = "new"; //the default command is new. Other alternatives are "edit", and "copy".
    
    
   
    
    
    public String getManagementCommand(){
        return managementCommand;
    }
    
    public void setManagementCommand(String mc){
        managementCommand = mc;
    }
    
    
}
