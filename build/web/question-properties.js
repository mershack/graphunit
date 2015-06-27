var quantTaskCounter = 1;
var qualTaskCounter = 1;
var viewerCondsCounter = 2;
var studystarted = false;
var trainingstarted = false;
var gettingTurkId = false;


function getAnswerControllers() {
    var studyid = document.getElementById("studyid").value;
    //  +"&studyid="+studyid;
    var url = "StudyManager?command=getAnswerControllers" + "&studyid=" + studyid;
    var xmlHttpRequest = getXMLHttpRequest();
    xmlHttpRequest.onreadystatechange = function()
    {
        if (xmlHttpRequest.readyState === 4 && xmlHttpRequest.status === 200)
        {
            //create the controls received from the servlet
            setUpAnswerControllers(xmlHttpRequest.responseText);
        }
    };
    xmlHttpRequest.open("GET", url, true);
    xmlHttpRequest.send(null);
}

function setUpAnswerControllers(controllersString) {
    //NB: the controllersString will be of the format:   dataType:::option1::option2

    var split = controllersString.split(":::");
    var dataType = split[0];
    //remove the previous answer controllers
    removeDivChildren(document.getElementById("answersDiv"));

    if (dataType === "options") {
        //get the options
        var split2 = split[1].split("::");


        for (var i = 0; i < split2.length; i++) {
            createAnswerOption(split2[i]);
        }
    }
    else if (dataType === "integer") {
        //create a numeric input 
        createIntegerInput();
    }
    else if (dataType === "float") {
        createFloatInput();
    }
    else if (dataType === "string") {
        createStringInput();
    }

    //show the answer div
    document.getElementById("answersDiv").style.display = "block";
}

function createAnswerOption(option) {
    //createRadio button
    var radio = document.createElement("input");
    radio.setAttribute("type", "radio");
    radio.setAttribute("name", "answer");
    radio.setAttribute("value", option);
    radio.setAttribute("onclick", "setSelectedAnswer(this)");
    //create label
    var label = document.createElement("label");
    label.innerHTML = option;

    //create a paragraph
    var paragraph = document.createElement("p");

    //append the radio and label to the paragraph

    paragraph.appendChild(radio);
    paragraph.appendChild(label);

    var answerDiv = document.getElementById("answersDiv");
    answerDiv.appendChild(paragraph);
    //answerDiv.appendChild(label);
}

function createIntegerInput() {
    // alert("I'm going to create the integer controller");
    //create a numeric input that accepts numeric numbers 
    var input = document.createElement("input");
    input.setAttribute("type", "number");
    input.setAttribute("name", "answer");
    input.setAttribute("min", "0");
    input.setAttribute("onKeyUp", "setSelectedAnswer(this)");
    input.setAttribute("oninput", "setSelectedAnswer(this)");  //this will be triggered when the spinner control is used


    //create label
    var label = document.createElement("label");
    label.innerHTML = "Your Answer : ";

    //create a paragraph
    var paragraph = document.createElement("p");
    //append the inputBox and label to the paragraph
    paragraph.appendChild(label);
    paragraph.appendChild(input);
    var form = document.createElement("form");
    form.setAttribute("onsubmit", "return false;");
    form.appendChild(paragraph);
    var answerDiv = document.getElementById("answersDiv");
    answerDiv.appendChild(form);

    input.focus();

}


function createQualRangeInput(li, ind, min, max) {
    var select = document.createElement("select");
    select.setAttribute("id", "qualAnswer" + ind);
    var opt = document.createElement("option");
    opt.setAttribute("value", "");
    opt.innerHTML = "Select One";

    select.appendChild(opt);
    min = parseInt(min);
    max = parseInt(max);
    for (var i = min; i <= max; i++) {
        var opt = document.createElement("option");
        opt.setAttribute("value", i);
        opt.innerHTML = i;
        select.appendChild(opt);
    }
    //var form = document.createElement("form");
    //form.setAttribute("onsubmit", "return false;");
    //form.appendChild(select);

    li.appendChild(select);
}

function createQualStringInput(li, ind) {
    var input1 = document.createElement("input");
   
    input1.setAttribute("type","text");
    input1.setAttribute("value", "");
    input1.setAttribute("onKeyUp", "setQualSelectedAnswer(this,"+ind+ ")");

    var input2= document.createElement("input");
    input2.setAttribute("type", "hidden");
    input2.setAttribute("id", "qualAnswer" + ind);
    //var form = document.createElement("form");
  //  form.setAttribute("onsubmit", "return false;");
    //form.appendChild(input);

    li.appendChild(input1);
    li.appendChild(input2);
}



function createQualMultipleChoiceInput(li, ind, choices) {
    //createRadio button
    var input = document.createElement("input");
    input.setAttribute("type", "hidden");
    input.setAttribute("value", "");
    input.setAttribute("id", "qualAnswer" + ind);

    var choicediv = document.createElement("div");
    var split = choices.split("::");
    for (var i = 0; i < split.length; i++) {
        var choice = split[i];
        var radio = document.createElement("input");
        radio.setAttribute("type", "radio");
        radio.setAttribute("name", "radio"+ind);
        radio.setAttribute("value", choice);
        radio.setAttribute("onclick", "setQualSelectedAnswer(this,"+ind+ ")");
        //create label
        var label = document.createElement("label");
        label.innerHTML = choice;
        //create a paragraph
        var paragraph = document.createElement("p");
        //append the radio and label to the paragraph
        paragraph.appendChild(radio);
        paragraph.appendChild(label);
        
        choicediv.appendChild(paragraph);
    }
   // var form = document.createElement("form");
   // form.setAttribute("onsubmit", "return false;");
   // form.appendChild(choicediv);
   // form.appendChild(input); //the inputbox that will be used to hold the selected option
    
    //li.appendChild(form);
    li.appendChild(input);
    li.appendChild(choicediv);
    
}


function createFloatInput() {
    //create a numeric input that accepts numeric numbers 
    var input = document.createElement("input");
    input.setAttribute("type", "number");
    input.setAttribute("name", "answer");
    input.setAttribute("min", "0");
    input.setAttribute("step", "any");
    input.setAttribute("onKeyUp", "setSelectedAnswer(this)");

    //create label
    var label = document.createElement("label");
    label.innerHTML = "Your Answer : ";

    //create a paragraph
    var paragraph = document.createElement("p");
    //append the inputBox and label to the paragraph
    paragraph.appendChild(label);
    paragraph.appendChild(input);
    var form = document.createElement("form");
    form.setAttribute("onsubmit", "return false;");

    form.appendChild(paragraph);
    var answerDiv = document.getElementById("answersDiv");
    answerDiv.appendChild(form);

    input.focus();
}

function createStringInput() {
    //create a numeric input that accepts numeric numbers 
    var input = document.createElement("input");
    input.setAttribute("type", "text");
    input.setAttribute("name", "answer");
    input.setAttribute("onKeyUp", "setSelectedAnswer(this)");

    //create label
    var label = document.createElement("label");
    label.innerHTML = "Your Answer : ";
    //create a paragraph
    var paragraph = document.createElement("p");
    //append the inputBox and label to the paragraph
    paragraph.appendChild(label);
    paragraph.appendChild(input);
    var answerDiv = document.getElementById("answersDiv");
    answerDiv.appendChild(paragraph);

    input.focus();
}

function removeDivChildren(div) {
    var last;
    while (last = div.lastChild)
        div.removeChild(last);
}


function showCheckAnswerButton() {
    document.getElementById("checkAnswer").style.display = "block";
}
function  hideCheckAnswerButton() {
    document.getElementById("checkAnswer").style.display = "none";
    //show the afterTrial controls
    showAfterTrialControls();
}

function showAfterTrialControls() {
    //startStudy();

    //first hide the studyControls and show the afterTrial controls
    trainingstarted = false;
    gettingTurkId = true;
    document.getElementById("studyControls").style.display = "none";

    document.getElementById("afterTrialControls").style.display = "block";
    //   document.getElementById("turkId").focus();  
}

function checkAnswer() {
    var givenAnswer = document.getElementById("selectedAnswer").value;

    if (givenAnswer === "") {
        alert("Please provide a valid answer to check its correctness");
        return false;
    }
    var studyid = document.getElementById("studyid").value;
    //  +"&studyid="+studyid;
    var url = "StudyManager?command=checkAnswer&givenAnswer=" + givenAnswer + "&studyid=" + studyid;
    var xmlHttpRequest = getXMLHttpRequest();
    xmlHttpRequest.onreadystatechange = function()
    {
        if (xmlHttpRequest.readyState === 4 && xmlHttpRequest.status === 200)
        {
            //set the correctness of the answer label with the returned response
            document.getElementById("correctnessOfAnswer").innerHTML = xmlHttpRequest.responseText;
        }
    };
    xmlHttpRequest.open("GET", url, true);
    xmlHttpRequest.send(null);
}

function setSelectedAnswer(element) {
    document.getElementById("selectedAnswer").value = element.value;
}

function setQualSelectedAnswer(element,ind){
    //alert("hey");
    document.getElementById("qualAnswer"+ind).value = element.value;
}


function startStudy() {
    //check if the turkId has been provided
    /*var turkId = document.getElementById("turkId").value;
     if (turkId.trim() === "") {
     alert("Please Enter your TurkID before continuing");
     return false;
     } */
    //hide the after trial controls and show the study controls

    studystarted = true;
    trainingstarted = false;
    gettingTurkId = false;
    document.getElementById("afterTrialControls").style.display = "none";
    document.getElementById("studyControls").style.display = "block";
    //getQuestion();
    getNodes(); //get the nodes to be highlighted.
    startQuestionDurationCountDown();
}

function endOfQuantitative(turkcode) {
    //hide the study controls and show the TurkID, we may include qualitative questions laterr
    document.getElementById("studyControls").style.display = "none";
    document.getElementById("endOfQuantitative").style.display = "block";
    document.getElementById("turkcode").innerHTML = turkcode;
}

function showQualitativeQuestions(qnString) {
    document.getElementById("studyControls").style.display = "none";
    document.getElementById("qualitativeQuestions").style.display = "block";
    var qualqnDiv = document.getElementById("qualitativeQuestions");
    var split = qnString.split("::::");

    var input = document.createElement("input");
    input.setAttribute("type", "hidden");
    input.setAttribute("id", "numOfQualQns");
    input.setAttribute("value", split.length - 1);  //the number of questions 
    qualqnDiv.appendChild(input);

    var ol = document.createElement("ol");

    //keep note of the size of the qualitative questions

    //display the questions and their answer controls
    for (var i = 1; i < split.length; i++) {
        var li = document.createElement("li");
        li.setAttribute("id", "qualQn" + i);
        var split2 = split[i].split(":::");

        var p1 = document.createElement("p");
        p1.innerHTML = split2[0];
        li.appendChild(p1);

        if (split2[1] === "Range") {
            //create a rating input
            var split3 = split2[2].split("::");
            createQualRangeInput(li, i, split3[0], split3[1]);//append the answer controller too
        }
        else if (split2[1] === "String") {
            createQualStringInput(li, i);
        }
        else if (split2[1] === "MultipleChoice") {
            createQualMultipleChoiceInput(li, i, split2[2]);
        }
        ol.appendChild(li);
    }
    qualqnDiv.appendChild(ol);

    //now append a submit button to  the form.
    var button = document.createElement("button");
    button.setAttribute("onclick", "submitQualitativeAnswers()");
    button.innerHTML = "Submit";

    qualqnDiv.appendChild(document.createElement("br"));
    qualqnDiv.appendChild(button);
}

function submitQualitativeAnswers() {
    //get all the qualitative answers, send them to the servlet, and then display the turkcode
    var numOfQualQns = document.getElementById("numOfQualQns").value;

    var allQualitativeAnswers = "";
    
    //check if all qualitative questions has been answered before coninuing
    for (var i = 1; i <= numOfQualQns; i++) {
        var answer = document.getElementById("qualAnswer" + i).value;

        if(answer==="")   {//if no answer, return false;
            alert ("Please provide answers to all the questions before you continue");
            return false;
        }

        if (i == 1) {
            allQualitativeAnswers = answer;
        }
        else {
            allQualitativeAnswers += "::::" + answer;
        }
    }
    //now send the answers to the server 
    var studyid = document.getElementById("studyid").value;
    var url = "StudyManager?command=setQualitativeAnswers" + "&qualitativeAnswers=" + allQualitativeAnswers
            + "&studyid=" + studyid;
    var xmlHttpRequest = getXMLHttpRequest();
    xmlHttpRequest.onreadystatechange = function()
    {
        if (xmlHttpRequest.readyState === 4 && xmlHttpRequest.status === 200)
        {
            //end of study give the turk code and 

            document.getElementById("qualitativeQuestions").style.display = "none";
            var split = (xmlHttpRequest.responseText).split("::");
            var turkcode = split[1];
            endOfQuantitative(turkcode);

        }
    };
    xmlHttpRequest.open("GET", url, true);
    xmlHttpRequest.send(null);
}

function showPreQualitativeQuestions(qnString) {

    document.getElementById("studyControls").style.display = "none";
    document.getElementById("preQualitativeQuestions").style.display = "block";


    var qualqnDiv = document.getElementById("preQualitativeQuestions");
    var split = qnString.split("::::");

    var input = document.createElement("input");
    input.setAttribute("type", "hidden");
    input.setAttribute("id", "numOfPreQualQns");
    input.setAttribute("value", split.length - 1);  //the number of questions 
    qualqnDiv.appendChild(input);

    var ol = document.createElement("ol");

    //keep note of the size of the qualitative questions
    //display the questions and their answer controls
    for (var i = 1; i < split.length; i++) {
        var li = document.createElement("li");
        li.setAttribute("id", "preQualQn" + i);
        var split2 = split[i].split(":::");

        var p1 = document.createElement("p");
        p1.innerHTML = split2[0];
        li.appendChild(p1);

        if (split2[1] === "Range") {
            //create a rating input
            var split3 = split2[2].split("::");
            createQualRangeInput(li, i, split3[0], split3[1]);//append the answer controller too
        }
        else if (split2[1] === "String") {
            createQualStringInput(li, i);
        }
        else if (split2[1] === "MultipleChoice") {
            createQualMultipleChoiceInput(li, i, split2[2]);
        }
        ol.appendChild(li);
    }
    qualqnDiv.appendChild(ol);

    //now append a submit button to  the form.
    var button = document.createElement("button");
    button.setAttribute("onclick", "submitPreQualitativeAnswers()");
    button.innerHTML = "Submit";

    qualqnDiv.appendChild(document.createElement("br"));
    qualqnDiv.appendChild(button);
}

function submitPreQualitativeAnswers() {
    //get all the qualitative answers, send them to the servlet, and then display the turkcode
    var numOfQualQns = document.getElementById("numOfPreQualQns").value;

    var allQualitativeAnswers = "";
    //check if all answers has been provided.
    for (var i = 1; i <= numOfQualQns; i++) {
        var answer = document.getElementById("qualAnswer" + i).value;

         if(answer==="")   {//if no answer, return false;
            alert ("Please provide answers to all the questions before you continue");
            return false;
        }



        if (i === 1) {
            allQualitativeAnswers = answer;
        }
        else {
            allQualitativeAnswers += "::::" + answer;
        }
    }
    //-alert(allQualitativeAnswers);
    //now send the answers to the server 
    var studyid = document.getElementById("studyid").value;
    var url = "StudyManager?command=setPreQualitativeAnswers" + "&preQualitativeAnswers=" + allQualitativeAnswers
            + "&studyid=" + studyid;
    var xmlHttpRequest = getXMLHttpRequest();
    xmlHttpRequest.onreadystatechange = function()
    {
        if (xmlHttpRequest.readyState === 4 && xmlHttpRequest.status === 200)
        {
            //Hide the preQualitative qn control, and show the studyControls div
            document.getElementById("preQualitativeQuestions").style.display = "none";
            document.getElementById("studyControls").style.display = "block";
        }
    };
    xmlHttpRequest.open("GET", url, true);
    xmlHttpRequest.send(null);
}


function newQuantitativeTaskDetails() {
    //  alert("hey");
    quantTaskCounter++;

    var div = document.createElement("div");

    if (quantTaskCounter % 2 === 0) {
        div.setAttribute("class", "questionNumberEven");
    }
    else {
        div.setAttribute("class", "questionNumberOdd");
    }


    var quantTaskDiv = document.getElementById("quantitativeTasksDiv");
    quantTaskDiv.appendChild(document.createElement("br"));

    var parag1 = document.createElement("p");
    parag1.innerHTML = "Task Type " + (quantTaskCounter);

    var select = document.createElement("select");
    select.setAttribute("class", "right");
    select.setAttribute("name", "quantitativeTasks");

    var opt1 = document.createElement("option");
    opt1.setAttribute("value", "select a task");
    opt1.innerHTML = "Select a Task";

    select.appendChild(opt1);

    var optgroup1 = document.createElement("optgroup");
    optgroup1.setAttribute("label", "Topology-Based Tasks");

    var opt2 = document.createElement("option");
    opt2.setAttribute("value", "Are the two highlighted nodes directly connected?");
    opt2.innerHTML = "Are the two highlighted nodes directly connected?";

    var opt2b = document.createElement("option");
    opt2b.setAttribute("value", "Can you get from one of the highlighted nodes to the other with exactly 2 steps?");
    opt2b.innerHTML = "Can you get from one of the highlighted nodes to the other with exactly 2 steps?";


    var opt3 = document.createElement("option");
    opt3.setAttribute("value", "Are the three highlighted nodes directly connected?");
    opt3.innerHTML = "Are the three highlighted nodes directly connected?";

    var opt4 = document.createElement("option");
    opt4.setAttribute("value", "Can you get from one of the highlighted nodes to the other in at most 3 steps?");
    opt4.innerHTML = "Can you get from one of the highlighted nodes to the other in at most 3 steps?";

    var opt5 = document.createElement("option");
    opt5.setAttribute("value", "How many nodes can be reached in one step from the highlighted node?");
    opt5.innerHTML = "How many nodes can be reached in one step from the highlighted node?";


    var opt6 = document.createElement("option");
    opt6.setAttribute("value", "What is the maximum number of nodes connected to one of the two highlighted nodes?");
    opt6.innerHTML = "What is the maximum number of nodes connected to one of the two highlighted nodes?";

    optgroup1.appendChild(opt2);
    optgroup1.appendChild(opt2b);
    optgroup1.appendChild(opt3);
    optgroup1.appendChild(opt4);
    optgroup1.appendChild(opt5);
    optgroup1.appendChild(opt6);

    select.appendChild(optgroup1);

    var optgroup2 = document.createElement("optgroup");
    optgroup2.setAttribute("label", "Attribute-Based Tasks");

    var opt7 = document.createElement("option");
    opt7.setAttribute("value", "Is there an adjacent node that contains the letter B?");
    opt7.innerHTML = "Is there an adjacent node that contains the letter B?";

    var opt8 = document.createElement("option");
    opt8.setAttribute("value", "How many adjacent nodes contain the letter B?");
    opt8.innerHTML = "How many adjacent nodes contain the letter B?";

    optgroup2.appendChild(opt7);
    optgroup2.appendChild(opt8);
    select.appendChild(optgroup2);

    var optgroup3 = document.createElement("optgroup");
    optgroup3.setAttribute("label", "Browsing-Based Tasks");

    var opt9 = document.createElement("option");
    opt9.setAttribute("value", "Find the number of nodes on a given path starting with a letter");
    opt9.innerHTML = "Find the number of nodes on a given path starting with a letter";

    var opt10 = document.createElement("option");
    opt10.setAttribute("value", "Find the number of nodes starting with a letter on all paths between 2 nodes");
    opt10.innerHTML = "Find the number of nodes starting with a letter on all paths between 2 nodes";

    optgroup3.appendChild(opt9);
    optgroup3.appendChild(opt10);
    select.appendChild(optgroup3);

    parag1.appendChild(select);

    //quantTaskDiv.appendChild(parag1);
    div.appendChild(parag1);
    var parag2 = document.createElement("p");
    parag2.innerHTML = "Number of Task Type " + quantTaskCounter + " Questions";

    var taskSizeInput = document.createElement("input");
    taskSizeInput.setAttribute("type", "text");
    taskSizeInput.setAttribute("class", "right");
    taskSizeInput.setAttribute("name", "quantitativeTaskSize");

    parag2.appendChild(taskSizeInput);
    // quantTaskDiv.appendChild(parag2);
    div.appendChild(parag2);

    var parag3 = document.createElement("p");
    parag3.innerHTML = "Time In Seconds for Task Type " + quantTaskCounter + " (Enter 0 for unlimited)";

    var timeInput = document.createElement("input");
    timeInput.setAttribute("type", "text");
    timeInput.setAttribute("class", "right");
    timeInput.setAttribute("name", "quantitativeTaskTime");

    parag3.appendChild(timeInput);
    //quantTaskDiv.appendChild(parag3);
    div.appendChild(parag3);

    quantTaskDiv.appendChild(div);
}


function newQualitativeQuestion() {
    qualTaskCounter++;

    var div = document.createElement("div");
    if (qualTaskCounter % 2 === 0) {
        div.setAttribute("class", "questionNumberEven");
    }
    else {
        div.setAttribute("class", "questionNumberOdd");
    }

    var qualTaskDiv = document.getElementById("qualitativeTasksDiv");
    var p = document.createElement("p");
    qualTaskDiv.appendChild(p);

    var parag = document.createElement("p");
    parag.innerHTML = "Task Type " + (qualTaskCounter);

    var select = document.createElement("select");
    select.setAttribute("class", "right");
    select.setAttribute("name", "qualitativeTasks");

    var opt1 = document.createElement("option");
    opt1.setAttribute("value", "Select One");
    opt1.innerHTML = "Select One";

    var opt2 = document.createElement("option");
    opt2.setAttribute("value", "Rate the easiness of the visualization tasks  from 1-Not easy to 5-Very Easy");
    opt2.innerHTML = "Rate the easiness of the visualization tasks  from 1-Not easy to 5-Very Easy";

    var opt3 = document.createElement("option");
    opt3.setAttribute("value", "What problem did you have with the visualization?");
    opt3.innerHTML = "What problem did you have with the visualization?";

    var opt4 = document.createElement("option");
    opt4.setAttribute("value", "Do you have any comments about the visualization?");
    opt4.innerHTML = "Do you have any comments about the visualization?";


    var opt5 = document.createElement("option");
    opt5.setAttribute("value", "Rate easiness of using the interactive techniques");
    opt5.innerHTML = "Rate easiness of using the interactive techniques";

    var opt6 = document.createElement("option");
    opt6.setAttribute("value", "Rate helpfulness of the interactive techniques to tasks");
    opt6.innerHTML = "Rate helpfulness of the interactive techniques to tasks";

    var opt7 = document.createElement("option");
    opt7.setAttribute("value", "Have you worked with this type of visualization before?");
    opt7.innerHTML = "Have you worked with this type of visualization before?";

    var opt8 = document.createElement("option");
    opt8.setAttribute("value", "How will you rate your familiarity with this type of visualization prior to this study?");
    opt8.innerHTML = "How will you rate your familiarity with this type of visualization prior to this study?";

    var opt9 = document.createElement("option");
    opt9.setAttribute("value", "Please enter your Mechanical Turk ID");
    opt9.innerHTML = "Please enter your Mechanical Turk ID";

    select.appendChild(opt1);
    select.appendChild(opt2);
    select.appendChild(opt3);
    select.appendChild(opt4);
    select.appendChild(opt5);
    select.appendChild(opt6);
    select.appendChild(opt7);
    select.appendChild(opt8);
    select.appendChild(opt9);


    parag.appendChild(select);
    //qualTaskDiv.appendChild(parag);
    div.appendChild(parag);


    //now also create a select for the position of the quantitative question
    //var select2 = document.createElement("select");  
    var parag2 = document.createElement("p");
    parag2.innerHTML = "When should " + "Task Type " + (qualTaskCounter) + " be asked?";

    var select2 = document.createElement("select");
    select2.setAttribute("class", "right");
    select2.setAttribute("name", "qualitativeTasksPositions");

    var optt1 = document.createElement("option");
    optt1.setAttribute("value", "Select One");
    optt1.innerHTML = "Select One";

    var optt2 = document.createElement("option");
    optt2.setAttribute("value", "before");
    optt2.innerHTML = "Before quantitative tasks";

    var optt3 = document.createElement("option");
    optt3.setAttribute("value", "after");
    optt3.innerHTML = "After  quantitative tasks";



    select2.appendChild(optt1);
    select2.appendChild(optt2);
    select2.appendChild(optt3);
    parag2.appendChild(select2);

    div.appendChild(document.createElement("br"));
    div.appendChild(parag2);


    //qualTaskDiv.appendChild(parag2);
    qualTaskDiv.appendChild(div);



}

function addAnotherCondition() {
    var viewerCondDiv = document.getElementById("viewerConditionsDiv");

    viewerCondsCounter++;

    var div = document.createElement("div");
    if (viewerCondsCounter % 2 === 0) {
        div.setAttribute("class", "conditionNumberEven");
    }
    else {
        div.setAttribute("class", "conditionNumberOdd");
    }

    var p = document.createElement("p");
    p.innerHTML = "Condition " + viewerCondsCounter;

    var select = document.createElement("select");
    select.setAttribute("class", "right");
    select.setAttribute("name", "conditions");
    select.setAttribute("id", "condition" + viewerCondsCounter + "");
    select.setAttribute("style", "min-width:50%;");

    var opt = document.createElement("option");
    opt.setAttribute("value", "");
    opt.innerHTML = "Select An Uploaded File";

    select.appendChild(opt);
    p.appendChild(select);

    var inputshortname = document.createElement("input");
    inputshortname.setAttribute("type", "text");
    inputshortname.setAttribute("class", "right");
    inputshortname.setAttribute("name", "condition-shortnames");
    // inputshortname.setAttribute("size", "36");
    inputshortname.setAttribute("style", "min-width:49%;");

    var condname = "cond" + viewerCondsCounter + "";
    inputshortname.setAttribute("value", condname);

    var p2 = document.createElement("p");
    p2.innerHTML = "Condition " + viewerCondsCounter + " Short Name";

    p2.appendChild(inputshortname);

    //viewerCondDiv.appendChild(p);
    //viewerCondDiv.appendChild(p2);


    div.appendChild(p);
    div.appendChild(p2);

    viewerCondDiv.appendChild(document.createElement("br"));
    viewerCondDiv.appendChild(div);

    populateConditionOptions(select);

}

