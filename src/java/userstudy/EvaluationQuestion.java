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
public class EvaluationQuestion {

    private String question;
    private ArrayList<String> inputs;
    private String correctAns;
    private String ansType;
    private ArrayList<String> ansOptions;
    private double averageCorrect;
    private String givenAnswer;
    private int timeInSeconds;
    private int maxTimeInSeconds;
    //private String answerGroup;
    private String inputTypes;
    private String outputType;
    private int numberMissed;
    private int numberOfErrors;
    private String inputTypeStr;
    private String interfaceForValidatingAnswers;
    private String taskOptions;
    private String hasCorrectAnswer;
    //private double accuracy;

    public EvaluationQuestion(String question, String correctAns, ArrayList<String> inputs,
            String ansType, int maxTime, String inputInterface, String outputInterface,
            String inputTypeString, String interfaceForValidatingAnswers
    , String hasCorrectAns) {
        this.question = question;
        this.correctAns = correctAns;
        this.inputs = inputs;
        this.ansOptions = ansOptions;
        this.ansType = ansType;
        //this.answerGroup = answ;
        this.inputTypes = inputInterface;
        this.outputType = outputInterface;
        this.averageCorrect = 0;
        this.maxTimeInSeconds = maxTime;
        numberMissed = 0;
        numberOfErrors = 0;
        inputTypeStr = inputTypeString; //by default
        this.interfaceForValidatingAnswers = interfaceForValidatingAnswers;
        this.hasCorrectAnswer  = hasCorrectAns;        
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public ArrayList<String> getInputs() {
        return inputs;
    }

    public void setInputs(ArrayList<String> nodes) {
        this.inputs = nodes;
    }

    public String getCorrectAns() {
        return correctAns;
    }

    public void setCorrectAns(String correctAns) {
        this.correctAns = correctAns;
    }

    public String getAnsType() {
        return ansType;
    }

    public void setAnsType(String ansType) {
        this.ansType = ansType;
    }

    public ArrayList<String> getAnsOptions() {
        return ansOptions;
    }

    public void setAnsOptions(ArrayList<String> ansOptions) {
        this.ansOptions = ansOptions;
    }

    public String getInputInterface() {
        return inputTypes;
    }

    public void setInputInterface(String inputInterface) {
        this.inputTypes = inputInterface;
    }

    public String getOutputInterface() {
        return outputType;
    }

    public void setOutputInterface(String outputInterface) {
        this.outputType = outputInterface;
    }

    public String getInputsAsString() {
        String inputsString = "";
        if (inputs.size() > 0) {
            inputsString = inputs.get(0);
            for (int i = 1; i < inputs.size(); i++) {
                //note we are separting it with this colons
                inputsString += ":::::" + inputs.get(i);
            }
        }
        return inputsString;
    }

    public String getAnswerTypeAndOutputType() {
        String ansAndoutputType = this.ansType + ":::" + outputType;

        return ansAndoutputType;
    }

    public void setAverageCorrect(double accuracy) {
        /**
         * The Average correct will represent the accuracy the user gave.
         */

        averageCorrect = accuracy;

        /*
         String correctAnsArr[] = correctAns.split(";;");
         String givenAnsArr[] = ans.split(";;");
       
        
         if (correctAnsArr.length < 2) {
         if (ans.equalsIgnoreCase(correctAns)) {
         averageCorrect = 1;
         } else {
         averageCorrect = 0;
         }
         }
         else{
         //check the percentage correct.
         int cnt = 0, numOfErrors=0;
         boolean exists =false;
         for(int i=0; i<givenAnsArr.length; i++){
         exists = false;
         for(int j=0; j<correctAnsArr.length; j++){
         if(givenAnsArr[i].trim().equalsIgnoreCase(correctAnsArr[j].trim())){
         cnt++;
         exists = true;
         break;
         }
         }
         //if it doesn't exist, then it is an erroneous selection
         if(!exists){
         numOfErrors++;
         }
                
                
         }
            
         averageCorrect = (double)cnt/correctAnsArr.length;
                        
         numberMissed = correctAnsArr.length - cnt;
         numberOfErrors = numOfErrors;            
         }
        
         */
    }

    public double getIsGivenAnsCorrect() {
        //if the answer type is an interface, return the averageCorrect value

        //otherwise, check what is wrong and return that one.
        if (ansType.trim().equalsIgnoreCase("interface")) {
            //we would know the answer by now.
            return averageCorrect;
        } else {
            
            System.out.println("givenAnswer: " + givenAnswer + "  correctAnswer: " + correctAns);
            //compute the answer now.
            if (givenAnswer.equalsIgnoreCase(correctAns)) {
                averageCorrect = 1;
            } else {
                averageCorrect = 0;
           }
            
            return averageCorrect;
            
        }

    }    
    public int getTimeInSeconds() {
        return timeInSeconds;
    }

    public void setTimeInSeconds(int timeInSeconds) {
        this.timeInSeconds = timeInSeconds;
    }

    public int getMaxTimeInSeconds() {
        return maxTimeInSeconds;
    }

    public void setMaxTimeInSeconds(int maxTimeInSeconds) {
        this.maxTimeInSeconds = maxTimeInSeconds;
    }

    public String getGivenAnswer() {
        return givenAnswer;
    }

    public void setGivenAnswer(String givenAnswer) {
        this.givenAnswer = givenAnswer;
    }

    public int getNumberMissed() {
        return numberMissed;
    }

    public int getNumberOfErrors() {
        return numberOfErrors;
    }

    public String getInputTypeStr() {
        return inputTypeStr;
    }

    public void setInputTypeStr(String inputTypes) {
        this.inputTypeStr = inputTypes;
    }

    public String getInputTypes() {
        return inputTypes;
    }

    public void setInputTypes(String inputTypeStr) {
        this.inputTypeStr = inputTypeStr;
    }

    /* public String getNodesAndInputTypesAsString(){
     return   getInputsAsString() + "::"+inputTypeStr;
     }*/
    public String getInterfaceForValidatingAnswers() {
        return interfaceForValidatingAnswers;
    }

    //set the interface for validating the answers
    public void setInterfaceForValidatingAnswers(String ifva) {
        interfaceForValidatingAnswers = ifva;
    }
    
    //setting the tasktype
     public void setHasCorrectAnswer(String ttype){
         hasCorrectAnswer = ttype;
     }

     //getting the tasktype
     public String getHasCorrectAnswer(){
         return hasCorrectAnswer;
     }
     
     public boolean hasCorrectAnswer(){
         return (hasCorrectAnswer.equalsIgnoreCase("yes"));
     }
}
