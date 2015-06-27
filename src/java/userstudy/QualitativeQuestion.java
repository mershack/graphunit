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
public class QualitativeQuestion {

    private String question;
    private String ansType;
    private int rangeMinimum;
    private int rangeMaximum;
    private String answer;
    private String positionOfTask;
    private ArrayList<String> mchoices;

    public QualitativeQuestion(String question, String ansType) {
        this.question = question;
        this.ansType = ansType;
        rangeMinimum =-1;
        rangeMaximum = -1;    
        mchoices = new ArrayList<String>();
    }
   
    public void setMChoices(ArrayList<String> mchoices){
        this.mchoices = mchoices;
    }
    public ArrayList<String> getMChoices(){
        return mchoices;
    }
    
    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getAnsType() {
        return ansType;
    }

    public void setAnsType(String ansType) {
        this.ansType = ansType;
    }

    public int getRangeMinimum() {
        return rangeMinimum;
    }

    public void setRangeMinimum(int ratingMinimum) {
        this.rangeMinimum = ratingMinimum;
    }

    public int getRangeMaximum() {
        return rangeMaximum;
    }

    public void setRangeMaximum(int ratingMaximum) {
        this.rangeMaximum = ratingMaximum;
    }

    public String getPositionOfTask() {
        return positionOfTask;
    }

    public void setPositionOfTask(String positionOfTask) {
        this.positionOfTask = positionOfTask;
    }
    
    
    
     public String getAnsDetailsAsString(){
        String ansDetailsString = ansType;//+ rangeMinimum + ":: "  + rangeMaximum;
        
        if(ansType.equalsIgnoreCase("Range")){
            ansDetailsString += ":::";
            ansDetailsString +=  rangeMinimum + ":: "  + rangeMaximum;
        }
        else if(ansType.equalsIgnoreCase("MultipleChoice")){
                ansDetailsString += ":::";
               for(int i=0; i<mchoices.size(); i++){
                  if(i==0){
                      ansDetailsString += mchoices.get(i);
                  }
                  else{
                      ansDetailsString += ":: "+mchoices.get(i);
                  }
            }
        }
       /* if(rangeMaximum>0 && rangeMinimum>=0){
            ansDetaislsString += rangeMinimum + ":: "  + rangeMaximum;
        } */
        return ansDetailsString;        
    }
}
