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
    ArrayList<String> nodes;
    private String correctAns;
    private String ansType;
    private ArrayList<String> ansOptions;
    private double averageCorrect;
    private String givenAnswer;
    private int timeInSeconds;
    private int maxTimeInSeconds;
    private String answerGroup;
    private String inputInterface;
    private String outputInterface;
    private int numberMissed;
    private int numberOfErrors;
    private String inputTypeStr;

    public EvaluationQuestion(String question, String correctAns, ArrayList<String> nodes,
            ArrayList<String> ansOptions, String ansType, String ansGroup, int maxTime, String inputInterface, String outputInterface, String inputTypeString) {
        this.question = question;
        this.correctAns = correctAns;
        this.nodes = nodes;
        this.ansOptions = ansOptions;
        this.ansType = ansType;
        this.answerGroup = ansGroup;
        this.inputInterface = inputInterface;
        this.outputInterface = outputInterface;
        this.averageCorrect = 0;
        this.maxTimeInSeconds = maxTime;
        numberMissed = 0;
        numberOfErrors = 0;
        inputTypeStr = inputTypeString; //by default
        
        //System.out.println("----"+inputTypeStr);
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public ArrayList<String> getNodes() {
        return nodes;
    }

    public void setNodes(ArrayList<String> nodes) {
        this.nodes = nodes;
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

    public String getAnswerGroup() {
        return answerGroup;
    }

    public void setAnswerGroup(String answerGroup) {
        this.answerGroup = answerGroup;
    }

    public String getInputInterface() {
        return inputInterface;
    }

    public void setInputInterface(String inputInterface) {
        this.inputInterface = inputInterface;
    }

    public String getOutputInterface() {
        return outputInterface;
    }

    public void setOutputInterface(String outputInterface) {
        this.outputInterface = outputInterface;
    }

    public String getNodesAsString() {
        String nodesString = "";
        if (nodes.size() > 0) {
            nodesString = nodes.get(0);
            for (int i = 1; i < nodes.size(); i++) {
                nodesString += "," + nodes.get(i);
            }
        }
        return nodesString;
    }

    public String getAnsOptionsAsString() {
        String ansOptionsString = answerGroup + ":::" + ansType + ":::";
        //include the answer options
        if (ansOptions.size() > 0) {
            ansOptionsString += ansOptions.get(0);
            for (int i = 1; i < ansOptions.size(); i++) {
                ansOptionsString += "::" + ansOptions.get(i);
            }
        } else {
            ansOptionsString += "none";
        }
        //now include the names the inputinterface and output interface names.
        ansOptionsString += ":::" + inputInterface + ":::" + outputInterface;

        return ansOptionsString;
    }

    public void setAverageCorrect(String ans) {

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
            
           // System.out.println("AverageCorrect is "+ averageCorrect);
            //calculate the number of missed items 
            //and the number of erroneous selections
            
            numberMissed = correctAnsArr.length - cnt;
            numberOfErrors = numOfErrors;
            
            //System.out.println("Given answer is :: "+ ans);
            //System.out.println("Correct answer is :: "+ correctAns);
            
         //   System.out.println("# of erroneous selection is "+ numberOfErrors);
           // System.out.println("# selections missed is "+ numberMissed);
            
           // System.out.println("#correct is "+ cnt);
        }

    }

    public double getIsGivenAnsCorrect() {
        return averageCorrect;
    }

    public double isIsGivenAnsCorrect() {
        return averageCorrect;
    } 

    /*public void setAverageCorrect(double isGivenAnsCorrect) {
        this.averageCorrect = isGivenAnsCorrect;
    } */

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

   public int getNumberMissed(){
       return numberMissed;
   }
   public int getNumberOfErrors(){
       return numberOfErrors;
   }

    public String getInputTypeStr() {
        return inputTypeStr;
    }

    public void setInputTypeStr(String inputTypeStr) {
        this.inputTypeStr = inputTypeStr;
    }
    
    
    public String getNodesAndInputTypesAsString(){
        
        //System.out.println(inputTypeStr);
        
        //System.out.println ("^^^^___^^^^"+ getNodesAsString() + "::"+inputTypeStr);
        
        return   getNodesAsString() + "::"+inputTypeStr;
    }
   
}
