package com.amazon.mturk.requester;

import java.io.FileInputStream;
import java.util.ArrayList;

/**
 *
 * @author Mershack
 */
public class MTurkRequestsMgr {

    //private final static String AWS_ACCESS_KEY_ID;
    //private final static String SECRET_KEY;
    private final static String QN_FILE_NAME = "C:\\Users\\Mershack\\Documents\\NetbeansProjects\\GitHub_projects\\d3graphevaluation\\build\\web\\data\\graphQuestionForm.xml";
    //private final static String QN_FILE_NAME = "graphQuestionForm1.xml";
    private String awsAccessKey = "AKIAIIWBS5IE72WKVEGQ";
    private String secretKey = "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";
    private String title = "graphTasks";
    private String description = "You will be shown a graph visualization with two or three nodes highlighted. You will be asked whether the "
            + "highlighted nodes are connected or whether there is a path between them"; // description
    private String questionFileName;
    private String question;
   // private String lifetimeInSeconds = "604800"; //expiration of HIT: 1 day 3 hours        
    private String lifetimeInSeconds = "1000"; //2 mins
    private String keywords = "graph visualization, graph tasks, visualization, interactive graphs"; //keywords
    private String reward = "0.00"; //reward : $0.01
    private int maxAssignments = 5;
    private int assignmentDurationInSeconds = 1800; //deadline : 30mins
    private int autoApprovalDelayInSeconds = 86400; //autoapproval : 1 day

    public MTurkRequestsMgr() {
        questionFileName = QN_FILE_NAME;
        question = getQuestionXML(questionFileName);
    }

     public MTurkRequestsMgr(String title, String AwsAccessKey, String secretKey, String reward, int maxAssignments, String questionFileName, String ss) {
        
        //this.title = title;
        //this.awsAccessKey = AwsAccessKey;
        //this.secretKey = secretKey;
        //this.reward = reward;
        //this.maxAssignments = maxAssignments;
        
         
       //new MTurkRequestsMgr();
        questionFileName = QN_FILE_NAME;
        question = getQuestionXML(questionFileName);
        
        if (questionFileName.equalsIgnoreCase(QN_FILE_NAME)){
            System.out.println("They are equal:::");
        }
        else{
            System.out.println("They are not equal ::: ");
            System.out.println(questionFileName);
            System.out.println("QN FILENAME:: " + QN_FILE_NAME);
        }
        if(AwsAccessKey.equals(this.awsAccessKey)){
            System.out.println("EQUAL AWS");
        }
        else{
            System.out.println("OOPS! UNEQUAL AWS");
        }
        if(secretKey.equals(this.secretKey)){
            System.out.println("EQUAL SK");
        }
        else{
            System.out.println("OOPS! UNEQUAL SK");
        }
        if(reward.equals(this.reward)){
            System.out.println("EQUAL REWARDS");
        }
        else{
            System.out.println("OOOPS! UNEQUAL REWARDS");
            System.out.println("reward:: "+reward + "::");
            System.out.println("reward:: "+this.reward + "::");
        }
        if(maxAssignments == this.maxAssignments){
            System.out.println("EQUAL MAX ASSIGNMENTS");
        }
        else{
            System.out.println("OOOPS! UNEQUAL MAX-ASSIGNMENTS");
            System.out.println("reward:: "+maxAssignments + "::");
            System.out.println("reward:: "+this.maxAssignments + "::");
        }        
    }
    
    
    
    public MTurkRequestsMgr(String title, String AwsAccessKey, String secretKey, String reward, int maxAssignments) {
        this.title = title;
        this.awsAccessKey = AwsAccessKey;
        this.secretKey = secretKey;
        this.reward = reward;
        this.maxAssignments = maxAssignments;
        
        questionFileName = QN_FILE_NAME;
        question = getQuestionXML(questionFileName);
    }
    
    public MTurkRequestsMgr(String title, String AwsAccessKey, String secretKey, String reward, int maxAssignments, String questionFileName) {
        this.title = title;
        this.awsAccessKey = AwsAccessKey;
        this.secretKey = secretKey;
        this.reward = ""+reward + "0";
        this.maxAssignments = maxAssignments;
        
        /*System.out.println("+++reward:::"+ reward +":::");
        System.out.println("+++this.reward::"+this.reward+"::");
        
        System.out.println("+++maxAssignments:::"+ maxAssignments +":::");
        System.out.println("+++this.maxAssignments::"+this.maxAssignments+"::");
        */
        
        
        this.questionFileName = questionFileName;
        question = getQuestionXML(questionFileName);
        
        
     
        
        
       // System.out.println("QUESTION:::: " + question);
        
        
        
    }

    public void createHITRequest() {

        try {
            //Construct the request
            AWSMechanicalTurkRequester turk = new AWSMechanicalTurkRequester(
                    awsAccessKey, secretKey);
            //call web service to create a new HIT
            RESTResponse res = turk
                    .createHIT(
                            title, // title
                            description, // description
                            keywords, //keywords
                            reward, //reward : $0.01
                            maxAssignments, //maximum number of assignments
                            assignmentDurationInSeconds, //deadline : 
                            autoApprovalDelayInSeconds, //autoapproval : 1 hour
                            question, //question
                            lifetimeInSeconds); //expiration of HIT: 1 day 3 hours

            System.out.println("----------------*Begin*createHit-responseXML------------------------------------------");
            //print the xml (just for debugging)
            res.printXMLResponse();

            System.out.println("-----------------*End of* createHit-responseXML---------------------------------------");

                       //run xpath queries in DOM, so that you can get whatever you like from the response
            //read the REST XML response
            Object ohitid = res.getXPathValue("//HITId");
            String message = "";

            //return the response
            if (ohitid != null && ohitid != "") {
                message = "Hit Created : HITID = " + ohitid.toString();

                //test getHIT
                RESTResponse rest2 = turk.getHIT(ohitid.toString());
                rest2.printXMLResponse();
            } else {
                message = "Error :"
                        + res.getXPathValue("//Errors/Error/Message")
                        + "\n Try Again..";
            }

            System.out.println(message);

            RESTResponse res0 = turk.getReviewableHITs();
            ArrayList hitids = res0.getXPathValues("//HITId");
            if (hitids.size() <= 0) {
                System.out.println("No Reviewable HITs");
            }
            for (int i = 0; i < hitids.size(); i++) {
                String hitid = hitids.get(i).toString();
                System.out.println("HITID = " + hitid);
                RESTResponse res1 = turk.getAssignmentsForHIT(hitid);

                ArrayList assids = res1.getXPathValues("//AssignmentId");
                if (assids.size() <= 0) {
                    System.out.println("|-----No Assignments for this HIT");
                }
                for (int j = 0; j < assids.size(); j++) {
                    String assid = assids.get(j).toString();
                    System.out.println("|-----AID = " + assid);
                }
            }

        } catch (Exception ex) {
            System.out.println(ex.toString());
        }

    }

    
    private static String getQuestionXML(String filepath) {
        String result = "";
        try {
            FileInputStream file = new FileInputStream(filepath);
            byte[] b = new byte[file.available()];
            file.read(b);
            file.close();
            result = new String(b);

        } catch (Exception ex) {
            System.out.println(ex.toString());
        }
        return result;
    }

    public String getAwsAccessKey() {
        return awsAccessKey;
    }

    public void setAwsAccessKey(String awsAccessKey) {
        this.awsAccessKey = awsAccessKey;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getQuestionFileName() {
        return questionFileName;
    }

    public void setQuestionFileName(String questionFileName) {
        this.questionFileName = questionFileName;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getLifetimeInSeconds() {
        return lifetimeInSeconds;
    }

    public void setLifetimeInSeconds(String lifetimeInSeconds) {
        this.lifetimeInSeconds = lifetimeInSeconds;
    }

    public String getKeywords() {
        return keywords;
    }

    public void setKeywords(String keywords) {
        this.keywords = keywords;
    }

    public String getReward() {
        return reward;
    }

    public void setReward(String reward) {
        this.reward = reward;
    }

    public int getMaxAssignments() {
        return maxAssignments;
    }

    public void setMaxAssignments(int maxAssignments) {
        this.maxAssignments = maxAssignments;
    }

    public int getAssignmentDurationInSeconds() {
        return assignmentDurationInSeconds;
    }

    public void setAssignmentDurationInSeconds(int assignmentDurationInSeconds) {
        this.assignmentDurationInSeconds = assignmentDurationInSeconds;
    }

    public int getAutoApprovalDelayInSeconds() {
        return autoApprovalDelayInSeconds;
    }

    public void setAutoApprovalDelayInSeconds(int autoApprovalDelayInSeconds) {
        this.autoApprovalDelayInSeconds = autoApprovalDelayInSeconds;
    }
    
    
    public static void main(String args[]){
        MTurkRequestsMgr mgr = new MTurkRequestsMgr();
        mgr.createHITRequest();
    } 

}
