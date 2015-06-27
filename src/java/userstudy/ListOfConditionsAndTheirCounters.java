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
public class ListOfConditionsAndTheirCounters {
    
     public ArrayList<String> conditionNames;
    public ArrayList<Integer> conditionCounters;
    
    public ListOfConditionsAndTheirCounters(ArrayList<String> condname){
        conditionNames = condname;
        
        conditionCounters= new ArrayList<Integer>();
        //initialize it with zeros
        for(int i=0; i<conditionNames.size(); i++){
            conditionCounters.add(0);
        }
    }
    
    public void incrementCondCounter(String condName){
         for(int i=0; i<conditionNames.size(); i++)        {
            if(conditionNames.get(i).equalsIgnoreCase(condName)){
                
                int counter = conditionCounters.get(i);
                
                conditionCounters.set(i, (counter+1));                
                break;
            }
        }
    }
    public void decrementCondCounter(String condName){
         for(int i=0; i<conditionNames.size(); i++)        {
            if(conditionNames.get(i).equalsIgnoreCase(condName)){
                
                int counter = conditionCounters.get(i);
                
                conditionCounters.set(i, (counter-1));                
                break;
            }
        }
    }
    
    public int getCondCounter(String condName){
        int counter = 0;
        for(int i=0; i<conditionNames.size(); i++)        {
            if(conditionNames.get(i).equalsIgnoreCase(condName)){
                counter = conditionCounters.get(i);
                break;
            }
        }
        
        return counter;
    }
}
