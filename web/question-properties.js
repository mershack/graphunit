var quantTaskCounter = 1;
var qualTaskCounter = 1;
var viewerCondsCounter = 2;
var datasetsCounter = 1;
var studystarted = false;
var trainingstarted = false;
var gettingTurkId = false;

var answergroup = "";  //this can be widget or interface. 
var answerDataType; //if answer group is widget, this will be a primary data type such as integer, double etc. If answer group is interface, this will be a name of a method. 
var inputInterface = "";
var outputInterface = "";
var checkAnswerClicked = true;
var inputAnswerOptions = "";
var inputAnswersOptionsAlreadySet = false;

var inputAccessorMethods = [];
var inputMutatorMethods = [];
var outputAccessorMethod = "";
var outputMutatorMethod = "";
var outputDescription = "";

var inputTypes = [];
var inputDescriptions = [];
var inputMediums = [];

var interfaceForValidatingAnswers = "";
var correctAnswer = "";
var hasCorrectAnswer = "";

function getInputInformations() {

}


function setInputAnswerOptions(optionStr) {
    inputAnswerOptions = optionStr;
}
function getInputAnswersControllersAlreadySet() {
    return inputAnswersOptionsAlreadySet;
}
function getAnswerGroup() {
    return answergroup;
}
function getAnswerDataType() {
    return answerDataType;
}

function getInputInterface() {
    return inputInterface;
}

function getOutputInterface() {
    return outputInterface;
}

function getOutputAccessorMethod() {
    return outputAccessorMethod;
}

function getOutputMutatorMethod() {
    return outputMutatorMethod;
}

function getInputsAndAnswerControllers() {
    getInputControllers();
    getAnswerControllers();
    //alert("ok");
}


function getAnswerControllers() {
    var studyid = document.getElementById("studyid").value;
    //  +"&studyid="+studyid;    
    inputAnswersOptionsAlreadySet = false;
    inputAnswerOptions = ""
    var url = "StudyManager?command=getAnswerControllers" + "&studyid=" + studyid;
    var xmlHttpRequest = getXMLHttpRequest();
    xmlHttpRequest.onreadystatechange = function()
    {
        if (xmlHttpRequest.readyState === 4 && xmlHttpRequest.status === 200)
        {
            //create the controls received from the servlet
            retrieveHasCorrectAnswer();
            setUpAnswerControllers(xmlHttpRequest.responseText);

            retrieveCorrectAnswer();

            if (answergroup.trim() === "interface" && hasCorrectAnswer === "yes") {
                //i.e. do this if the task has a correct answer.
                retrieveInterfaceForValidatingAnswers();

            }
            //setup the inputtypes also 
            //get the input controllers also           
        }
    };
    xmlHttpRequest.open("GET", url, true);
    xmlHttpRequest.send(null);
}

function getInputControllers() {
    var studyid = document.getElementById("studyid").value;

    var url = "StudyManager?command=getInputTypes" + "&studyid=" + studyid;
    var xmlHttpRequest = getXMLHttpRequest();
    xmlHttpRequest.onreadystatechange = function()
    {
        if (xmlHttpRequest.readyState === 4 && xmlHttpRequest.status === 200)
        {
            // alert(xmlHttpRequest.responseText);
            setUpInputTypesAndDescriptions(xmlHttpRequest.responseText);
        }
    };
    xmlHttpRequest.open("GET", url, true);
    xmlHttpRequest.send(null);
}

function setUpInputTypesAndDescriptions(inputStr) {
    var inputs = inputStr.split(":::");

    inputAccessorMethods = [];
    inputMutatorMethods = [];
    inputTypes = [];
    inputDescriptions = [];

    //alert(inputStr);

    for (var i = 0; i < inputs.length; i++) {
        var type = inputs[i].split("::")[0].trim();
        var descp = inputs[i].split("::")[1].trim();
        //create accessor and mutator methods for the input types
        inputAccessorMethods.push("get" + capitalizeFirstLetter(type));
        inputMutatorMethods.push("set" + capitalizeFirstLetter(type));
        inputTypes.push(type);
        inputDescriptions.push(descp);

        // alert("about to set the input accessor method === "+inputAccessorMethods);



        //include the inputmedium if it is given
        if (inputs[i].split("::").length > 2) {
            var medium = inputs[i].split("::")[2].trim();
            inputMediums.push(medium);
        }
    }

}

function capitalizeFirstLetter(string) {
    return string.charAt(0).toUpperCase() + string.slice(1);
}

function setUpAnswerControllers(controllersString) {
    //NB: the controllersString will be of the format:   answerGroup:::dataType:::option1::option2:::inputInterface:::outputInterface
    //new format: answertype:::datatype::options::

    //The format is answertype:::outputtype.
    //Answertype can be interface, or the other widgets

    //alert


    var split = controllersString.split(":::");
    answergroup = split[0];

    var dataType = "";

    if (answergroup.trim() === "interface") {
        dataType = split[1].split("::")[0];
        outputAccessorMethod = "get" + capitalizeFirstLetter(dataType);
        outputMutatorMethod = "set" + capitalizeFirstLetter(dataType);
        //get interface for checking answer correctness
    }
    else {
        //it is some kind of widget
        answergroup = "widget";
        dataType = split[0].split("::")[0];
    }



    removeDivChildren(document.getElementById("answersDiv"));



    answerDataType = dataType;



    if (dataType === "options-fixed" || dataType === "options-dynamic") {

        //get the options
        var options = split[1].split("::");


        //and over here too.
        //var optionsArr = options.split(":");
        for (var i = 0; i < options.length; i++) {
            if (options[i].trim() !== "")
                createAnswerOption(options[i]);
        }
        //}



    }
    else if (dataType === "integer" || dataType === "Number") {
        //create a numeric input 
        createIntegerInput();
    }
    else if (dataType === "float") {
        createFloatInput();
    }
    else if (dataType === "string") {
        createStringInput();
    }
    else if (dataType === "Color-options-fixed") {
        var split2 = split[1].split("::");

        for (var i = 0; i < split2.length; i++)
            createColorOption(split2[i], i);
    }
    else if (dataType === "inputOptions") {
        //the inputOptions should already be set by now.
        setUpInputOptionsAnswerController();
    }
    /* else if (dataType === "options-dynamic") {
     
     }  */
    //show the answer div
    document.getElementById("answersDiv").style.display = "block";
}

function retrieveInterfaceForValidatingAnswers() {
    var studyid = document.getElementById("studyid").value;
    var url = "StudyManager?command=getInterfaceForValidatingAnswers" + "&studyid=" + studyid;
    var xmlHttpRequest = getXMLHttpRequest();
    xmlHttpRequest.onreadystatechange = function()
    {
        if (xmlHttpRequest.readyState === 4 && xmlHttpRequest.status === 200)
        {
            interfaceForValidatingAnswers = xmlHttpRequest.responseText;
        }
    };
    xmlHttpRequest.open("GET", url, true);
    xmlHttpRequest.send(null);
}

function getInterfaceForValidatingAnswers() {
    return interfaceForValidatingAnswers;
}

function retrieveCorrectAnswer() {
    var studyid = document.getElementById("studyid").value;
    var url = "StudyManager?command=getCorrectAnswerForInterfaceAnswerTypes" + "&studyid=" + studyid;
    var xmlHttpRequest = getXMLHttpRequest();
    xmlHttpRequest.onreadystatechange = function()
    {
        if (xmlHttpRequest.readyState === 4 && xmlHttpRequest.status === 200)
        {
            correctAnswer = xmlHttpRequest.responseText;
        }
    };
    xmlHttpRequest.open("GET", url, true);
    xmlHttpRequest.send(null);
}

function retrieveHasCorrectAnswer() {
    var studyid = document.getElementById("studyid").value;
    var url = "StudyManager?command=getHasCorrectAnswer" + "&studyid=" + studyid;
    var xmlHttpRequest = getXMLHttpRequest();
    xmlHttpRequest.onreadystatechange = function()
    {
        if (xmlHttpRequest.readyState === 4 && xmlHttpRequest.status === 200)
        {
            hasCorrectAnswer = (xmlHttpRequest.responseText).trim();
        }
    };
    xmlHttpRequest.open("GET", url, true);
    xmlHttpRequest.send(null);
}

function getHasCorrectAnswer() {
    return hasCorrectAnswer;
}



function getCorrectAnswer() {
    return correctAnswer;
}

function setUpInputOptionsAnswerController() {
//    //note: inputAnswerOptions has already been set by now

    var optionsArr = inputAnswerOptions.split(";;");
    //alert(optionsArr.length);
    if (optionsArr.length > 0 && optionsArr[0] !== "") {
        inputAnswersOptionsAlreadySet = true;

        // removeDivChildren(document.getElementById("answersDiv"));
        for (var i = 0; i < optionsArr.length; i++) {

            createAnswerOption(optionsArr[i]);
        }
    }
    inputAnswersOptionsAlreadySet = true;
    //   alert("here too " + inputAnswerOptions);

//alert("^^ of okay");


    // }
    // alert("end of okay");
}

function setUpInputOptionsAnswerControllerWithArg(optionStr) {

//alert("okay2");
    //if (inputAnswersOptionsAlreadySet === false) {
    removeDivChildren(document.getElementById("answersDiv"));
    if (optionStr && optionStr.split(";;").length > 0 && optionStr.split(";;")[0] !== "") {
        var optionsArr = optionStr.split(";;");
        for (var i = 0; i < optionsArr.length; i++) {
            createAnswerOption(optionsArr[i]);
        }
        //inputAnswerOptions = ""; //empty

        //        alert("*** okay2");
    }
    //     alert("--- okay2");
    // }
    // alert("end of okay2");
}

function createColorOption(option, i) {
    var color = "";
    if (i === 0) {
        color = "red";
    }
    else {
        color = "blue";
    }
    // alert("here")

    //createRadio button
    var radio = document.createElement("input");
    radio.setAttribute("type", "radio");
    radio.setAttribute("name", "answer");
    radio.setAttribute("value", color);
    radio.setAttribute("onclick", "setSelectedAnswer(this)");
    //create label
    var label = document.createElement("label");
    label.setAttribute("style", "background:" + color + "; color:" + color + ";");

    label.innerHTML = "color";

    //create a paragraph
    var paragraph = document.createElement("p");

    //append the radio and label to the paragraph

    paragraph.appendChild(radio);
    paragraph.appendChild(label);

    var answerDiv = document.getElementById("answersDiv");
    answerDiv.appendChild(paragraph);
    //answerDiv.appendChild(label);
}

function createAnswerOption(option) {
    // alert(option);
    if (option !== "") { //not empty
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
    select.setAttribute("id", "qualAnsWidget" + ind);
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

    input1.setAttribute("type", "text");
    input1.setAttribute("value", "");
    input1.setAttribute("id", "qualAnsWidget" + ind);
    input1.setAttribute("onKeyUp", "setQualSelectedAnswer(this," + ind + ")");

    var input2 = document.createElement("input");
    input2.setAttribute("type", "hidden");
    input2.setAttribute("id", "qualAnswer" + ind);
    //var form = document.createElement("form");
    //  form.setAttribute("onsubmit", "return false;");
    //form.appendChild(input);

    li.appendChild(input1);
    li.appendChild(input2);
}

function createQualIntegerInput(li, ind) {
    //alert("here now");
    var input1 = document.createElement("input");

    input1.setAttribute("type", "number");
    input1.setAttribute("value", "");
    input1.setAttribute("id", "qualAnsWidget" + ind);
    input1.setAttribute("onKeyUp", "setQualSelectedAnswer(this," + ind + ")");

    var input2 = document.createElement("input");
    input2.setAttribute("type", "hidden");
    input2.setAttribute("id", "qualAnswer" + ind);

    li.appendChild(input1);
    li.appendChild(input2);

    // alert("here2");

}



function createQualMediumSizeStringInput(li, ind) {

    // alert("here");
    var input1 = document.createElement("textarea");

    input1.setAttribute("rows", "4");
    input1.setAttribute("cols", "30");
    input1.setAttribute("id", "qualAnsWidget" + ind);
    input1.setAttribute("onKeyUp", "setQualSelectedAnswer(this," + ind + ")");

    var input2 = document.createElement("input");
    input2.setAttribute("type", "hidden");
    input2.setAttribute("id", "qualAnswer" + ind);
    //var form = document.createElement("form");
    //  form.setAttribute("onsubmit", "return false;");
    //form.appendChild(input);

    li.appendChild(input1);
    li.appendChild(input2);
}

function removeQualAnswerWidgetsIfExists(indx) {
    var widget = document.getElementById("qualAnsWidget" + indx);

    if (widget) {
        widget.parentNode.removeChild(widget);
    }

    var ans = document.getElementById("qualAnswer" + indx);
    if (ans) {
        ans.parentNode.removeChild(ans);
    }
}

function createQualMultipleChoiceInput(li, ind, choices) {

    //removeQualAnswerWidgetsIfExists();
    //createRadio button
    var input = document.createElement("input");
    input.setAttribute("type", "hidden");
    input.setAttribute("value", "");
    input.setAttribute("id", "qualAnsWidget" + ind);


    var input2 = document.createElement("input");
    input2.setAttribute("type", "hidden");
    input2.setAttribute("value", "");
    input2.setAttribute("id", "qualAnswer" + ind);


    var choicediv = document.createElement("div");
    var split = choices.split("::");
    for (var i = 0; i < split.length; i++) {
        var choice = split[i];
        var radio = document.createElement("input");
        radio.setAttribute("type", "radio");
        radio.setAttribute("name", "radio" + ind);
        radio.setAttribute("value", choice);
        radio.setAttribute("onclick", "setMultipleChoiceQualSelectedAnswer(this," + ind + ")");
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
    li.appendChild(input2);
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

    checkAnswerClicked = true;

    var givenAnswer = "";
    if (getAnswerGroup() === "widget") {
        givenAnswer = document.getElementById("selectedAnswer").value;

        if (givenAnswer === "") {
            alert("Please provide a valid answer to check its correctness");
            return false;
        }
    }
    else if (getAnswerGroup() === "interface") {
        var iframe = document.getElementById("viewerFrame");
        var outInterfaceName = getOutputInterface();

        //alert(outInterfaceName);

        if (typeof iframe.contentWindow.window[outInterfaceName] == "function") {
            var output = iframe.contentWindow.window[outInterfaceName]();
            if (output === "") {
                alert("Please provide a valid answer to check its correctness");
                return false;
            }

            var outputStr = "";
            for (var i = 0; i < output.length; i++) {
                if (i === 0) {
                    outputStr = output[i];
                }
                else {
                    outputStr += ";;" + output[i];
                }
            }
            //alert(outputStr);
            document.getElementById("selectedAnswer").value = outputStr;
            givenAnswer = document.getElementById("selectedAnswer").value;
        }
        else {
            alert("The output method that returns the output is not implemented.");
        }
    }
    
    if(givenAnswer.trim() === correctAnswer.trim()){
        document.getElementById("correctnessOfAnswer").innerHTML = "Correct!";
    }
    else{
        document.getElementById("correctnessOfAnswer").innerHTML = "Wrong.";
    }
    
/*
    var studyid = document.getElementById("studyid").value;
    //  +"&studyid="+studyid;
    var url = "StudyManager?command=checkAnswer&givenAnswer=" + givenAnswer + "&studyid=" + studyid;
    var xmlHttpRequest = getXMLHttpRequest();
    xmlHttpRequest.onreadystatechange = function()
    {
        if (xmlHttpRequest.readyState === 4 && xmlHttpRequest.status === 200)
        {
            //alert(xmlHttpRequest.responseText);
            //set the correctness of the answer label with the returned response
            document.getElementById("correctnessOfAnswer").innerHTML = xmlHttpRequest.responseText;
        }
    };
    xmlHttpRequest.open("GET", url, true);
    xmlHttpRequest.send(null); */
}

function setSelectedAnswer(element) {
    //alert(element.value);
    document.getElementById("selectedAnswer").value = element.value;
    //   alert(document.getElementById("selectedAnswer").value);
}

function setQualSelectedAnswer(element, ind) {
    //alert("hey");
    document.getElementById("qualAnswer" + ind).value = element.value;
}

function setMultipleChoiceQualSelectedAnswer(element, ind) {
    document.getElementById("qualAnsWidget" + ind).value = element.value;
    document.getElementById("qualAnswer" + ind).value = element.value;
}


function startStudy() {
    studystarted = true;
    trainingstarted = false;
    gettingTurkId = false;
    document.getElementById("afterTrialControls").style.display = "none";
    document.getElementById("studyControls").style.display = "block";
    getQuestion();
   // getNodes(); //get the nodes to be highlighted.
    startQuestionDurationCountDown();
}

function endOfQuantitative(turkcode) {
    //hide the study controls and show the TurkID, we may include qualitative questions laterr
    document.getElementById("studyControls").style.display = "none";
    document.getElementById("endOfQuantitative").style.display = "block";
    document.getElementById("turkcode").innerHTML = turkcode;
}

function showPostStudyQualitativeQuestions(qnString) {
    //alert("omg" + qnString);
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


        var dataType = split2[1];
        if (dataType === "options-fixed" || dataType === "options-dynamic") {
            createQualMultipleChoiceInput(li, i, split2[2]);
        }
        else if (dataType === "integer" || dataType === "Number") {
            //create a numeric input 
            createQualIntegerInput(li, i);
        }
        else if (dataType === "float") {
            createQualFloatInput(li, i);
        }
        else if (dataType === "string") {
            createQualStringInput(li, i);
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

    qualqnDiv.appendChild(document.createElement("br"));
    qualqnDiv.appendChild(document.createElement("br"));

    /*
     var note = document.createElement("p");
     note.innerHTML = "<b>Please Note:</b> "
     +" In case you submit and you did not get the turk-code, click on this button";
     var button2 = document.createElement("button");
     button2.setAttribute("onclick", "resolveTurkCodeIssue()");
     button2.innerHTML = "Resolve Turk Code issue";
     qualqnDiv.appendChild(note);
     qualqnDiv.appendChild(button2);
     */
}

function resolveTurkCodeIssue() {
    var studyid = document.getElementById("studyid").value;

    var qualqnDiv = document.getElementById("qualitativeQuestions");
    var p = document.createElement("p");


    var dt = new Date();

    var n = dt.getTime();


    if (studyid && n)
        p.innerHTML = "Please use the following turk Code: <b> 4ST-" + studyid + "-" + n + "</b>";
    else if (n) {
        p.innerHTML = "Please use the following turk Code: <b>1STUCODYD7E" + "</b>";
    }
    else if (studyid) {
        p.innerHTML = "Please use the following turk Code: <b>1STUCODY" + studyid + "</b>";
    }
    else {
        p.innerHTML = "Please use the following turk Code:<b> 1STUCODYD7E" + "</b>";
    }
    qualqnDiv.appendChild(p);


    var p2 = document.createElement("p");
    p2.innerHTML = "Please send a quick email with the text answers you provided above"
            + " to meshhome16@gmail.com.  Thank you and Sorry for the incovenience!"

    qualqnDiv.appendChild(p2);

}


function submitQualitativeAnswers() {
    //get all the qualitative answers, send them to the servlet, and then display the turkcode
    var numOfQualQns = document.getElementById("numOfQualQns").value;

    var allQualitativeAnswers = "";

    //check if all qualitative questions has been answered before continuing
    for (var i = 1; i <= numOfQualQns; i++) {
        var answer1 = document.getElementById("qualAnswer" + i).value;

        var answer2 = document.getElementById("qualAnsWidget" + i).value;

        var answer = "";
        if (answer1.trim() !== "") {
            answer = answer1;
        }
        else {
            answer = answer2;
        }

        if (answer === "") {//if no answer, return false;
            alert("Please provide answers to all the questions before you continue");
            return false;
        }

        if (i === 1) {
            allQualitativeAnswers = answer;
        }
        else {
            allQualitativeAnswers += "::::" + answer;
        }
    }

    //alert("here now");
    //now send the answers to the server 
    var studyid = document.getElementById("studyid").value;
    var url = "StudyManager?command=setQualitativeAnswers" + "&qualitativeAnswers=" + allQualitativeAnswers
            + "&studyid=" + studyid;
    //alert(url);

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



        var dataType = split2[1];


        if (dataType === "options-fixed" || dataType === "options-dynamic") {
            createQualMultipleChoiceInput(li, i, split2[2]);
        }
        else if (dataType === "integer" || dataType === "Number") {
            //create a numeric input 
            createQualIntegerInput(li, i);
        }
        else if (dataType === "float") {
            createQualFloatInput();
        }
        else if (dataType === "string") {
            createQualStringInput(li, i);
        }






        /*
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
         */



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
        var answer1 = document.getElementById("qualAnswer" + i).value;

        var answer2 = document.getElementById("qualAnsWidget" + i).value;

        var answer = "";
        if (answer1 !== "") {
            answer = answer1;
        }
        else {
            answer = answer2;
        }


        if (answer === "") {//if no answer, return false;
            alert("Please provide answers to all the questions before you continue");
            return false;
        }



        if (i === 1) {
            allQualitativeAnswers = answer;
        }
        else {
            allQualitativeAnswers += "::::" + answer;
        }
    }
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
    var numberOfTasks = document.getElementById("numberOfTasks").value;

    quantTaskCounter = Number(numberOfTasks) + 1;

    // var quantTaskDiv = document.getElementById("quantitativeTasksDiv");
    //quantTaskDiv.appendChild(document.createElement("br"));

    // var div = document.createElement("div");


    var qnDiv = document.createElement("div");
    qnDiv.setAttribute("id", "qnDiv" + quantTaskCounter);



    var table = document.getElementById("quantitativeTasksTable");
    var tr = document.createElement("tr");

    if (quantTaskCounter % 2 === 0) {
        tr.setAttribute("class", "questionNumberEven");
    }
    else {
        tr.setAttribute("class", "questionNumberOdd");
    }

    tr.setAttribute("id", "task" + quantTaskCounter);




    var td1 = document.createElement("td");
    td1.setAttribute("id", "qnDiv" + quantTaskCounter + "TD");
    var td2 = document.createElement("td");
    td2.setAttribute("id", "taskSize" + quantTaskCounter + "TD");

    var td3 = document.createElement("td");
    td3.setAttribute("id", "taskTime" + quantTaskCounter + "TD");




    td1.appendChild(qnDiv);

    var parag2 = document.createElement("div");
    //parag2.innerHTML = "Number of Task Type " + quantTaskCounter + " Questions";

    var taskSizeInput = document.createElement("input");
    taskSizeInput.setAttribute("type", "number");
    taskSizeInput.setAttribute("class", "taskTxtbox");
    taskSizeInput.setAttribute("id", "taskSize" + quantTaskCounter);
    taskSizeInput.setAttribute("name", "quantitativeTaskSize");

    parag2.appendChild(taskSizeInput);

    td2.appendChild(parag2);


    // tr.appendChild(td2)

    // quantTaskDiv.appendChild(parag2);
    // div.appendChild(parag2);

    var parag3 = document.createElement("div");
    //parag3.innerHTML = "Time In Seconds for Task Type " + quantTaskCounter + " (Enter 0 for unlimited)";

    var timeInput = document.createElement("input");
    timeInput.setAttribute("type", "number");
    timeInput.setAttribute("class", "taskTxtbox");
    timeInput.setAttribute("id", "taskTime" + quantTaskCounter);
    timeInput.setAttribute("name", "quantitativeTaskTime");

    parag3.appendChild(timeInput);

    td3.appendChild(parag3);


    var td4 = document.createElement("td");
    //td4.setAttribute("id", "taskPosition" + quantTaskCounter);


    var parag4 = document.createElement("p");
    parag4.setAttribute("id", "taskPosition" + quantTaskCounter);

    parag4.innerHTML = quantTaskCounter + ".";
    td4.appendChild(parag4);



    /* For the arrows */
    var td5 = document.createElement("td");
    td5.setAttribute("class", "sortingArrowsTD");
    var upImg = document.createElement("img");
    upImg.setAttribute("src", "images/up-arrow-icon.png");
    upImg.setAttribute("class", "sortingArrows");
    upImg.setAttribute("alt", "move task up");
    upImg.setAttribute("title", "Move Task Up");
    upImg.setAttribute("onclick", "moveTaskUp('" + quantTaskCounter + "');");

    var downImg = document.createElement("img");
    downImg.setAttribute("src", "images/down-arrow-icon.png");
    downImg.setAttribute("class", "sortingArrows");
    downImg.setAttribute("alt", "move task down");
    downImg.setAttribute("title", "Move Task Down");
    downImg.setAttribute("onclick", "moveTaskDown('" + quantTaskCounter + "');");

    td5.appendChild(upImg);
    td5.appendChild(document.createElement("br"));
    td5.appendChild(downImg);




    var td6 = document.createElement("td");
    td6.setAttribute("class", "deleteIconTD");
    var deleteIcon = document.createElement("img");
    deleteIcon.setAttribute("src", "images/delete-icon.jpg");
    deleteIcon.setAttribute("class", "deleteIcon");
    deleteIcon.setAttribute("alt", "delete task");
    deleteIcon.setAttribute("title", "Delete Task");
    deleteIcon.setAttribute("id", "deleteIcon" + quantTaskCounter);

    var id = "task" + quantTaskCounter;

    deleteIcon.setAttribute("onclick", "deleteTaskIconClicked(\"" + quantTaskCounter + "\")");

    td6.appendChild(deleteIcon);


    //div.appendChild(parag3);
    tr.appendChild(td5);
    tr.appendChild(td4);
    tr.appendChild(td1);
    tr.appendChild(td2);
    tr.appendChild(td3);
    tr.appendChild(td6);

    table.appendChild(tr);


    //now load the tasks into the tasks placeholder
    loadTasks(document.getElementById("qnDiv" + quantTaskCounter), "Task Type "
            + quantTaskCounter, quantTaskCounter, "taskType");

    //now increment the number of tasks variable.    
    var numberOfTasks = document.getElementById("numberOfTasks").value;
    document.getElementById("numberOfTasks").value = Number(numberOfTasks) + 1;


    //show the delete icon of the first task.
    document.getElementById("deleteIcon1").style.display = "block";

}


function moveTaskUp(value) {

    var prevValue = value - 1;

    var prevTaskRow = document.getElementById("task" + prevValue);

    var taskrow = document.getElementById("task" + value);

    if (prevTaskRow) {
        //size
        var prevTaskSizeTD = document.getElementById("taskSize" + prevValue + "TD");
        var prevTaskSize = document.getElementById("taskSize" + prevValue);
        var currentTaskSizeTD = document.getElementById("taskSize" + value + "TD");
        var currentTaskSize = document.getElementById("taskSize" + value);

        var cln1 = prevTaskSize.cloneNode(true);
        var cln2 = currentTaskSize.cloneNode(true);
        //cln1.setAttribute("value", currentTaskSize.value);
        //cln1.setAttribute("id", "taskSize" + value);
        cln1.value = currentTaskSize.value;
        cln2.value = prevTaskSize.value;

        //cln2.setAttribute("id", "taskSize" + prevValue);
        prevTaskSize.parentNode.removeChild(prevTaskSize);

        currentTaskSize.parentNode.removeChild(currentTaskSize);
        prevTaskSizeTD.appendChild(cln1);

        currentTaskSizeTD.appendChild(cln2);




        // currentTaskSizeTD.appendChild(cln2);
        //time
        var prevTaskTimeTD = document.getElementById("taskTime" + prevValue + "TD");
        var prevTaskTime = document.getElementById("taskTime" + prevValue);
        var currentTaskTimeTD = document.getElementById("taskTime" + value + "TD");
        var currentTaskTime = document.getElementById("taskTime" + value);
        var clnT1 = prevTaskTime.cloneNode(true);
        var clnT2 = currentTaskTime.cloneNode(true);


        clnT1.value = currentTaskTime.value;
        //clnT1.setAttribute("id", "taskTime"+prevValue)
        clnT2.value = prevTaskTime.value;

        prevTaskTime.parentNode.removeChild(prevTaskTime);
        currentTaskTime.parentNode.removeChild(currentTaskTime);

        prevTaskTimeTD.appendChild(clnT1);
        currentTaskTimeTD.appendChild(clnT2);

        //qn
        var prevQnDiv = document.getElementById("qnDiv" + prevValue);
        var prevTaskType = document.getElementById("taskType" + prevValue);
        var currentQnDiv = document.getElementById("qnDiv" + value);
        var currentTaskType = document.getElementById("taskType" + value);
        var clnQ1 = prevTaskType.cloneNode(true);
        var clnQ2 = currentTaskType.cloneNode(true);

        clnQ1.selectedIndex = currentTaskType.selectedIndex;
        clnQ1.setAttribute("id", "taskType" + prevValue);
        clnQ2.selectedIndex = prevTaskType.selectedIndex;
        clnQ2.setAttribute("id", "taskType" + value);
        prevTaskType.parentNode.removeChild(prevTaskType);
        prevQnDiv.appendChild(clnQ1);
        currentTaskType.parentNode.removeChild(currentTaskType);
        currentQnDiv.appendChild(clnQ2);

        // prevQnDiv.parentNode.removeChild(prevQnDiv);
        // prevQnDivTD.appendChild(clnQ1);        
        // currentQnDiv.parentNode.removeChild(currentQnDiv);
        // currentQnDivTD.appendChild(clnQ2);
    }


}

function moveTaskDown(value) {

    var nextValue = Number(value) + 1;

    // alert("task"+nextValue);

    var nextTaskRow = document.getElementById("task" + nextValue);

    var taskrow = document.getElementById("task" + value);

    //alert(" --hheere");
    if (nextTaskRow) {
        //("hey");
        var nextTaskSizeTD = document.getElementById("taskSize" + nextValue + "TD");
        var nextTaskSize = document.getElementById("taskSize" + nextValue);
        var currentTaskSizeTD = document.getElementById("taskSize" + value + "TD");
        var currentTaskSize = document.getElementById("taskSize" + value);
        var cln1 = nextTaskSize.cloneNode(true);
        var cln2 = currentTaskSize.cloneNode(true);



        cln1.value = currentTaskSize.value;
        //cln1.setAttribute("id", "taskSize" + value);

        cln2.value = nextTaskSize.value;
        //cln2.setAttribute("id", "taskSize" + nextValue);

        nextTaskSize.parentNode.removeChild(nextTaskSize);
        nextTaskSizeTD.appendChild(cln1);
        currentTaskSize.parentNode.removeChild(currentTaskSize);
        currentTaskSizeTD.appendChild(cln2);


        var nextTaskTimeTD = document.getElementById("taskTime" + nextValue + "TD");
        var nextTaskTime = document.getElementById("taskTime" + nextValue);
        var currentTaskTimeTD = document.getElementById("taskTime" + value + "TD");
        var currentTaskTime = document.getElementById("taskTime" + value);
        var clnT1 = nextTaskTime.cloneNode(true);
        var clnT2 = currentTaskTime.cloneNode(true);


        clnT1.value = currentTaskTime.value;
        //clnT1.setAttribute("id", "taskTime" + value);
        clnT2.value = nextTaskTime.value;

        //clnT2.setAttribute("id", "taskTime" + nextValue);

        nextTaskTime.parentNode.removeChild(nextTaskTime);
        nextTaskTimeTD.appendChild(clnT1);
        currentTaskTime.parentNode.removeChild(currentTaskTime);
        currentTaskTimeTD.appendChild(clnT2);



        //qn
        var nextQnDiv = document.getElementById("qnDiv" + nextValue);
        var prevTaskType = document.getElementById("taskType" + nextValue);
        var currentQnDiv = document.getElementById("qnDiv" + value);
        var currentTaskType = document.getElementById("taskType" + value);
        var clnQ1 = prevTaskType.cloneNode(true);
        var clnQ2 = currentTaskType.cloneNode(true);

        clnQ1.selectedIndex = currentTaskType.selectedIndex;
        clnQ1.setAttribute("id", "taskType" + nextValue);
        clnQ2.selectedIndex = prevTaskType.selectedIndex;
        clnQ2.setAttribute("id", "taskType" + value);
        prevTaskType.parentNode.removeChild(prevTaskType);
        nextQnDiv.appendChild(clnQ1);
        currentTaskType.parentNode.removeChild(currentTaskType);
        currentQnDiv.appendChild(clnQ2);
    }
}

function deleteTaskIconClicked(value) {

    var id = "task" + value;
    var taskrow = document.getElementById(id);

    taskrow.parentNode.removeChild(taskrow);
    var numberOfTasks = document.getElementById("numberOfTasks").value;

    value = Number(value);
    for (var i = value + 1; i <= numberOfTasks; i++) {
        var nextTaskPosition_parag = document.getElementById("taskPosition" + (i));

        if (nextTaskPosition_parag) {
            nextTaskPosition_parag.innerHTML = (i - 1) + ".";
            nextTaskPosition_parag.setAttribute("id", "taskPosition" + (i - 1));

            var nextTask = document.getElementById("task" + i);
            nextTask.setAttribute("id", "task" + (i - 1));

            var nextQnDivTD = document.getElementById("qnDiv" + i + "TD");
            nextQnDivTD.setAttribute("id", "qnDiv" + (i - 1) + "TD");

            var nextQnDiv = document.getElementById("qnDiv" + i);
            nextQnDiv.setAttribute("id", "qnDiv" + (i - 1));

            var nextTaskSizeTD = document.getElementById("taskSize" + i + "TD");
            nextTaskSizeTD.setAttribute("id", "taskSize" + (i - 1) + "TD")

            var nextTaskSize = document.getElementById("taskSize" + i);
            nextTaskSize.setAttribute("id", "taskSize" + (i - 1));

            var nextTaskTimeTD = document.getElementById("taskTime" + i + "TD");
            nextTaskTimeTD.setAttribute("id", "taskTime" + (i - 1) + "TD");

            var nextTaskTime = document.getElementById("taskTime" + i);
            nextTaskTime.setAttribute("id", "taskTime" + (i - 1));

            var nextDeleteIcon = document.getElementById("deleteIcon" + i);
            nextDeleteIcon.setAttribute("id", "deleteIcon" + (i - 1));
            nextDeleteIcon.setAttribute("onclick", "deleteTaskIconClicked(\"" + (i - 1) + "\")");
        }
    }

    //decrement the number of tasks by 1
    var nt = document.getElementById("numberOfTasks").value;
    document.getElementById("numberOfTasks").value = Number(nt) - 1;
    quantTaskCounter = Number(nt) - 1;

    //disable the delete icon if this is the last task.
    if ((Number(nt) - 1) == 1) {
        document.getElementById("deleteIcon1").style.display = "none";
    }
}

function deleteCondition(value) {

    //delete this condition
    var conditionrow = document.getElementById("conditionRow" + value);
    conditionrow.parentNode.removeChild(conditionrow);

    //now reorder the condition list again.

    value = Number(value);

    for (var i = value + 1; i <= viewerCondsCounter; i++) {

        var td = document.getElementById("conditionPosition" + i);
        td.innerHTML = " " + (i - 1) + ". ";
        td.setAttribute("id", "conditionPosition" + (i - 1));

        //change the id of the  image that will be used to delete this task.
        //and change the value that is passed the method.
        var img = document.getElementById("deleteCondition" + (i));
        img.setAttribute("id", "deleteCondition" + (i - 1));
        img.setAttribute("onclick", "deleteCondition('" + (i - 1) + "')");

        //change the viewerDirectories select too.
        var select1 = document.getElementById("existingViewerDirName" + i);
        select1.setAttribute("id", "existingViewerDirName" + (i - 1));
        select1.setAttribute("onchange", "viewerDirectoryChanged(this, '" + (i - 1) + "')");

        //change the viewer url select widget id
        var select2 = document.getElementById("condition" + i);
        select2.setAttribute("id", "condition" + (i - 1));

        //change the task short name widget id too
        var sn = document.getElementById("conditionShortName" + i);
        sn.setAttribute("id", "conditionShortName" + (i - 1));
        sn.setAttribute("value", "cond" + (i - 1));

        //now change the id of the row also 
        var tr = document.getElementById("conditionRow" + i);
        tr.setAttribute("id", "conditionRow" + (i - 1));
    }

    //reduce the viewer counters by 1
    viewerCondsCounter = viewerCondsCounter - 1;

    //make sure the last two conditions cannot be deleted
    if (viewerCondsCounter == 2) {

        document.getElementById("deleteCondition1").style.display = "none";
        document.getElementById("deleteCondition2").style.display = "none";
    }



}



function deleteDatasetIconClicked(value) {

    //delete this condition
    var datasetrow = document.getElementById("datasetRow" + value);
    datasetrow.parentNode.removeChild(datasetrow);

    //now reorder the condition list again.

    value = Number(value);
    for (var i = value + 1; i <= datasetsCounter; i++) {
        var tr = document.getElementById("datasetRow" + i);
        tr.setAttribute("id", "datasetRow" + (i - 1));

        var td = document.getElementById("datasetPosition" + i);
        td.innerHTML = " " + (i - 1) + ". ";
        td.setAttribute("id", "datasetPosition" + (i - 1));

        //change the id of the  image that will be used to delete this task.
        //and change the value that is passed the method.
        var img = document.getElementById("deleteDatasetIcon" + (i));
        img.setAttribute("id", "deleteDatasetIcon" + (i - 1));
        img.setAttribute("onclick", "deleteDatasetIconClicked('" + (i - 1) + "')");

        //change the viewerDirectories select too.
        var select1 = document.getElementById("dataset" + i);
        select1.setAttribute("id", "dataset" + (i - 1));
        select1.setAttribute("onchange", "datasetChanged(this, '" + (i - 1) + "')");

        //change the viewer url select widget id
        var select2 = document.getElementById("datasetFormat" + i);
        select2.setAttribute("id", "datasetFormat" + (i - 1));

    }

    //reduce the viewer counters by 1
    datasetsCounter = datasetsCounter - 1;

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

function getInputAccessorMethods() {
    return inputAccessorMethods;
}
function getInputMutatorMethods() {
    return inputMutatorMethods;
}
function getInputDescriptions() {
    return inputDescriptions;
}
function getInputTypes() {
    return inputTypes;
}
function getInputMediums() {
    return inputMediums;
}


function addAnotherCondition() {
    viewerCondsCounter++;
    var table = document.getElementById("conditionsTable");
    var tr = document.createElement("tr");
    tr.setAttribute("id", "conditionRow" + viewerCondsCounter);

    if (viewerCondsCounter % 2 === 0) {
        tr.setAttribute("class", "conditionNumberEven");
    }
    else {
        tr.setAttribute("class", "conditionNumberOdd");
    }

    var td0 = document.createElement("td");
    td0.setAttribute("id", "conditionPosition" + viewerCondsCounter);
    td0.innerHTML = viewerCondsCounter + ". ";
    tr.appendChild(td0);
    //table.appendChild(tr);

    //for the viewer directory.
    var td1 = document.createElement("td");
    var select1 = document.createElement("select");
    select1.setAttribute("id", "existingViewerDirName" + viewerCondsCounter);
    select1.setAttribute("onchange", "viewerDirectoryChanged(this, '" + viewerCondsCounter + "')");

    populateViewerDirectoryOptions2(select1);

    td1.appendChild(select1);
    tr.appendChild(td1);

    var td2 = document.createElement("td");

    var p2 = document.createElement("p");
    var select = document.createElement("select");
    select.setAttribute("id", "condition" + viewerCondsCounter);
    select.setAttribute("name", "conditions");
    select.setAttribute("style", "min-width:100%;");


    var option1 = document.createElement("option");
    option1.setAttribute("value", "");
    option1.innerHTML = "Select An Uploaded Web-page";
    select.appendChild(option1);

    p2.appendChild(select);
    p2.appendChild(document.createElement("br"));

    td2.appendChild(p2);
    tr.appendChild(td2);

    var td3 = document.createElement("td");
    var p3 = document.createElement("p");
    var input = document.createElement("input");
    input.setAttribute("name", "condition-shortnames");
    input.setAttribute("id", "conditionShortName" + viewerCondsCounter);
    input.setAttribute("type", "text");
    input.setAttribute("style", "min-width:49%;");
    input.setAttribute("value", "cond" + viewerCondsCounter);
    p3.appendChild(input);
    p3.appendChild(document.createElement("br"));

    td3.appendChild(p3);
    tr.appendChild(td3);

    var td4 = document.createElement("td");
    td4.setAttribute("class", "deleteIconTD");
    var img = document.createElement("img");
    img.setAttribute("src", "images/delete-icon.jpg");
    img.setAttribute("class", "deleteIcon");
    img.setAttribute("alt", "delete condition");
    img.setAttribute("title", "Delete Condition");
    img.setAttribute("id", "deleteCondition" + viewerCondsCounter);
    img.setAttribute("onclick", "deleteCondition('" + viewerCondsCounter + "')");

    td4.appendChild(img);
    tr.appendChild(td4);
    table.appendChild(tr);

    //show the delete icon for the first two conditions
    document.getElementById("deleteCondition1").style.display = "block";
    document.getElementById("deleteCondition2").style.display = "block";

    //now populate the select widgets
    //populateConditionOptions(document.getElementById("condition" + viewerCondsCounter));
}


function addAnotherDataset() {
    datasetsCounter++;
    var table = document.getElementById("datasetsTable");
    var tr = document.createElement("tr");

    tr.setAttribute("id", "datasetRow" + datasetsCounter);

    if (datasetsCounter % 2 === 0) {
        tr.setAttribute("class", "conditionNumberEven");
    }
    else {
        tr.setAttribute("class", "conditionNumberOdd");
    }


    var td0 = document.createElement("td");
    td0.setAttribute("id", "datasetPosition" + datasetsCounter);
    td0.innerHTML = datasetsCounter + ". ";
    tr.appendChild(td0);



    //create the combobox td
    var td1 = document.createElement("td");

    var parag = document.createElement("p");
    var select1 = document.createElement("select");

    select1.setAttribute("id", "dataset" + datasetsCounter);
    select1.setAttribute("onchange", "datasetChanged(this);");

    parag.appendChild(select1);
    td1.appendChild(parag);

    var td2 = document.createElement("td");

    var parag2 = document.createElement("p");

    var select2 = document.createElement("select");
    select2.setAttribute("id", "datasetFormat" + datasetsCounter);
    select2.setAttribute("style", "min-width: 100px");

    parag2.appendChild(select2);
    td2.appendChild(parag2);


    var td3 = document.createElement("td");
    td3.setAttribute("class", "deleteIconTD");

    var del_img = document.createElement("img");

    del_img.setAttribute("src", "images/delete-icon.jpg");
    del_img.setAttribute("class", "deleteIcon");
    del_img.setAttribute("title", "Delete Dataset");
    del_img.setAttribute("id", "deleteDatasetIcon" + datasetsCounter);
    del_img.setAttribute("onclick", "deleteDatasetIconClicked('" + datasetsCounter + "')");


    td3.appendChild(del_img);

    tr.appendChild(td1);
    tr.appendChild(td2);
    tr.appendChild(td3);

    table.appendChild(tr);

    populateDatasetOptions("" + datasetsCounter + "");


}




