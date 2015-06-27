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
    
    ArrayList<String> viewerConditions = new ArrayList<String>();
    ArrayList<String> viewerConditionShortNames = new ArrayList<String>();
    ArrayList<String> quantitativeQuestions = new ArrayList<String>();
    ArrayList<String> quantQuestionSizes = new ArrayList<String>();
    ArrayList<String> quantQuestionTime = new ArrayList<String>();
    ArrayList<String> qualitativeQuestions = new ArrayList<String>();
    ArrayList<String> qualitativeQuestionsPositions = new ArrayList<String>();

    String dataset;
    String datasetType;
    String expType;
    String studyname;
    String studydataurl;
    String viewerWidth ="";
    String viewerHeight ="";
    int trainingSize = 2;
    
    
}
