package userstudy;

/**
 *
 * @author Mershack
 */
public class TaskDetails {
    private String taskname;
    private String taskQuestion;
    private String accuracyCheckingInterface;
    private String outputType;
    private String outputTypeDescription;
    private String inputTypes;
    private String taskDescription;
    private String answerType;
    private String inputSize;
    private String hasCorrectAnswer;
    private String questionSize;
    
    private String time;
      
    public TaskDetails(String tname, String question, String size, String time ){
        taskname = tname;
        taskQuestion = question;
        questionSize = size;
        this.time =time;
        
    }
    
    public void setQuestionSize(String qs){
        questionSize = qs;
    }
    public String getQuestionSize(){
        return questionSize;
    }
    
    public void setTime(String t){
        time = t;
    }
    public String getTime(){
        return time;
    }
    
    public TaskDetails(String taskname, String taskQuestion, String accuracyCheckingInterface,
            String outputType, String outputTypeDescription, String inputTypes,
            String taskDescription, String answerType, String inputSize, String hasCorrectAnswer){
        
        this.taskname = taskname;
        this. taskQuestion = taskQuestion;
        this.accuracyCheckingInterface = accuracyCheckingInterface;
        this.outputType = outputType;
        this.outputTypeDescription = outputTypeDescription;
        this.inputTypes = inputTypes;
        this.taskDescription = taskDescription;
        this.answerType = answerType;
        this.inputSize = inputSize;
        this.hasCorrectAnswer = hasCorrectAnswer;        
    }

    public String getTaskname() {
        return taskname;
    }

    public void setTaskname(String taskname) {
        this.taskname = taskname;
    }

    public String getTaskQuestion() {
        return taskQuestion;
    }

    public void setTaskQuestion(String taskQuestion) {
        this.taskQuestion = taskQuestion;
    }

    public String getAccuracyCheckingInterface() {
        return accuracyCheckingInterface;
    }

    public void setAccuracyCheckingInterface(String accuracyCheckingInterface) {
        this.accuracyCheckingInterface = accuracyCheckingInterface;
    }

    public String getOutputType() {
        return outputType;
    }

    public void setOutputType(String outputType) {
        this.outputType = outputType;
    }

    public String getOutputTypeDescription() {
        return outputTypeDescription;
    }

    public void setOutputTypeDescription(String outputTypeDescription) {
        this.outputTypeDescription = outputTypeDescription;
    }

    public String getInputTypes() {
        return inputTypes;
    }

    public void setInputTypes(String inputTypes) {
        this.inputTypes = inputTypes;
    }

    /**
     * getting the task description
     * @return the task description value
     */
    public String getTaskDescription() {
        return taskDescription;
    }

    /**
     * setting the task Description of the file
     * @param taskDescription the description
     */
    public void setTaskDescription(String taskDescription) {
        this.taskDescription = taskDescription;
    }

    //getting the answertype
    public String getAnswerType() {
        return answerType;
    }

    //setting the answer type
    public void setAnswerType(String answerType) {
        this.answerType = answerType;
    }

    //getting the inputsize
    public String getInputSize() {
        return inputSize;
    }

    //setting the input size
    public void setInputSize(String inputSize) {
        this.inputSize = inputSize;
    }
    //setting the task type
    public void setHasCorrectAnswer(String ttype){
        hasCorrectAnswer = ttype;
    }
    
    //getting the task type
    public String getHasCorrectAnswer(){
        return hasCorrectAnswer;
    }
    
    public boolean hasCorrectAnswer(){        
        return hasCorrectAnswer.equalsIgnoreCase("yes");        
    }    
}
