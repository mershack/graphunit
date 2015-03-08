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
    private boolean isGivenAnsCorrect;
    private int timeInSeconds;
    private int maxTimeInSeconds;

    public EvaluationQuestion(String question, String correctAns, ArrayList<String> nodes, ArrayList<String> ansOptions, String ansType, int maxTime) {
        this.question = question;
        this.correctAns = correctAns;
        this.nodes = nodes;
        this.ansOptions = ansOptions;
        this.ansType = ansType;
        isGivenAnsCorrect = false;
        maxTimeInSeconds = maxTime;
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

    public String getNodesAsString() {
        String nodesString = "";
        if (nodes.size() > 0) {
            nodesString = nodes.get(0);
            for (int i = 1; i < nodes.size(); i++) {
                nodesString += "," +nodes.get(i);
            }
        }
        return nodesString;
    }
    
    public String getAnsOptionsAsString(){
        String ansOptionsString = ansType +":::";
        
        if(ansOptions.size()>0){
            ansOptionsString += ansOptions.get(0);
            for(int i=1; i<ansOptions.size(); i++){
                ansOptionsString += "::" +ansOptions.get(i);
            }            
        }        
        return ansOptionsString;        
    }
    
    public void setIsGivenAnsCorrect(String ans){
        if(ans.equalsIgnoreCase(correctAns)){
            isGivenAnsCorrect = true;
        }
        else{
            isGivenAnsCorrect = false;
        }
    }
    
    public boolean getIsGivenAnsCorrect(){
        return isGivenAnsCorrect;
    }

    public boolean isIsGivenAnsCorrect() {
        return isGivenAnsCorrect;
    }

    public void setIsGivenAnsCorrect(boolean isGivenAnsCorrect) {
        this.isGivenAnsCorrect = isGivenAnsCorrect;
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

    
    
}
