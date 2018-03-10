/**
 * This function makes a request to the setup servlet to display the demo page.
 * @returns {undefined}
 */
var uploadedHtmls = [];
var answers = []; //an array to hold answers of task instances
var taskInstances = []; //
var taskAnswerType = "";
var taskInstancesCounter = 1;
var inputs = [];
var inputSize = 0;
var inputsCnt = 0;
var inputMedium = "";
var answerMedium = "";
var currentInput = "";
var dynamicOptionsArr = [];

var allOptionsArr = [];

var allOptions = "";
var optionsAnswer = "";

var doesTaskHaveCorrectAnswer = "";

function startTaskInstances() {

    //get the viewer directory name
    var viewerdir = document.getElementById("existingViewerDirName1").value;
    //get the viewer url selected by the users
    var viewerURL = document.getElementById("condition1").value;
//get the dataset
    var dataset = document.getElementById("dataset1").value;
    
    //get the datasetFormat
    var datasetFormat = document.getElementById("datasetFormat1").value;
    
    //alert(datasetFormat);
    
    //get the user id.
    var userid = document.getElementById("userid").value;
    //get the task also
    var task = document.getElementById("taskType1").value;
    //call task instances and submit these details.

    /*alert("viewer dir: "+viewerdir
     +"\nurl: "+viewerURL
     +"\ndataset: "+dataset
     +"\nuserid: "+userid
     +"\ntask: "+task);*/

    var command = "submitParametersToBegin";

    var url = "TaskInstancesCreator?command=" + command
            + "&userid=" + userid
            + "&dataset=" + dataset
            +"&datasetFormat=" + datasetFormat
            + "&viewerDirectory=" + viewerdir
            + "&viewerURL=" + viewerURL
            + "&task=" + task;

    var xmlHttpRequest = getXMLHttpRequest();
    xmlHttpRequest.onreadystatechange = function()
    {
        if (xmlHttpRequest.readyState === 4 && xmlHttpRequest.status === 200)
        {
            //  alert(xmlHttpRequest.responseText);



            var visPage = "TaskInstancesCreator?command=showVis";
            window.open(visPage);
        }
    };
    xmlHttpRequest.open("POST", url, false);
    xmlHttpRequest.send();


    /* var tempname = document.getElementById("tempname").value;
     var visPage = "TaskInstancesCreator?tempname=" + tempname
     + "&command=showVis";
     window.open(visPage);  */
}


/*function uploadFiles() {
 var tempname = document.getElementById("tempname").value;
 var viewer = document.getElementById("viewer").value;
 var dataset = document.getElementById("dataset").value;
 var task = document.getElementById("quantitativeTasks").value;
 var userid = document.getElementById("userid").value;
 
 var thefiles = document.getElementById("thefiles").files;
 var url = "TaskInstancesCreator?tempname=" + tempname
 + "&command=submitForm"
 + "&dataset=" + dataset
 + "&task=" + task
 + "&userid=" + userid
 + "&viewer=" + viewer;
 
 
 var formData = new FormData();
 for (var i = 0; i < thefiles.length; i++)
 formData.append("File", thefiles[i]);
 
 var xmlHttpRequest = getXMLHttpRequest();
 xmlHttpRequest.onreadystatechange = function()
 {
 if (xmlHttpRequest.readyState === 4 && xmlHttpRequest.status === 200)
 {
 var visPage = "TaskInstancesCreator?tempname=" + tempname
 + "&command=showVis";
 window.open(visPage);
 }
 };
 xmlHttpRequest.open("POST", url, false);
 xmlHttpRequest.send(formData);
 }*/

function getUploadedFileNames(thefiles) {
    var files = thefiles.files;
    //remove items in the uploadedHtml array;    
    while (uploadedHtmls.length > 0) {
        uploadedHtmls.pop();
    }

    for (var i = 0; i < files.length; i++) {
        //if the filename has an extension .html, .htm, xhtml, or .jsp we will add it to uploadedHtmls arrray       
        var name = files[i].name;
        if (name.indexOf(".html") > 0 || name.indexOf(".htm") > 0 || name.indexOf(".xhtml") > 0 || name.indexOf(".jsp") > 0) {
            uploadedHtmls.push(name);
        }
    }
    //there is only one viewer 
    var cond = document.getElementById("viewer");
    populateConditionOptions(cond);
}

function populateConditionOptions(div) {
    removeDivChildren(div);
    var opt1 = document.createElement("option");
    opt1.setAttribute("value", "");
    opt1.innerHTML = "Select An Uploaded File";

    div.appendChild(opt1);

    for (var i = 0; i < uploadedHtmls.length; i++) {
        var opt = document.createElement("option");
        opt.setAttribute("value", uploadedHtmls[i]);
        opt.innerHTML = uploadedHtmls[i];

        div.appendChild(opt);
    }
}

function getSelectedElements() {
    var iframe = document.getElementById("viewerFrame");
    var seStr = "";
    //we will get the output by calling the interface method to get the output
    var iframe = document.getElementById("viewerFrame");
    var outInterfaceName = getOutputInterface();

    if (typeof iframe.contentWindow.window[outInterfaceName] == "function") {
        var selectedElements = iframe.contentWindow.window[outInterfaceName]();
        
        //now give command to unselect the selected items.
        //Ideally, we will use a user-given interface name, but for now we are assuming
        //the name of that method is unselectSelectedElements
        iframe.contentWindow.unSelectSelectedElements();

        //now give command to unselect the selected items.
        //Ideally, we will use a user-given interface name, but for now we are assuming
        //the name of that method is unselectSelectedElements
        iframe.contentWindow.unSelectSelectedElements();

        for (var i = 0; i < selectedElements.length; i++) {
            if (i === 0) {
                seStr = selectedElements[i];
            }
            else {
                seStr += ";;" + selectedElements[i];
            }
        }

        if (selectedElements === "") {
            alert("Please provide a valid answer before continuing");
            return false;
        }
    }
    else {
        alert("The output method that returns the output is not implemented.");
    }

    return seStr;
}

function getInstanceAnswer() {
    var answer = "";
    /*NB: if the answer medium is an interface, the answer can either come
     * from the visualization, or the user can choose to manually type the answers.
     */
    if (getAnswerGroup() === "interface") {

        //  var answermedium = document.getElementById("answerMedium").value;

        //alert("answermedium "+answermedium);
        getSelectedAnswer();

        var answerFromVis = document.getElementById("selectedAnswerFromVis").value;

        var answermedium = document.getElementById("answerMedium").value;

        var answerbytyping = "";
        if (answermedium === "by-typing" &&
                (document.getElementById("selectedAnswerFromTyping").value).trim() !== "")
            answerbytyping = document.getElementById("selectedAnswerFromTyping").value;


        document.getElementById("selectedAnswerFromVis").value = "";
        document.getElementById("selectedAnswerFromTyping").value = "";

        document.getElementById("selectedAnswerFromVis").style.display = "none";
        document.getElementById("selectedAnswerFromVis").style.display = "none";
        document.getElementById("answerMedium").value = "";


        if (answerbytyping !== "") {
            answer = answerbytyping;
        }
        else {
            answer = answerFromVis;
        }
    }
    else {
        // alert("+++");
        //check if its dynamic options
        if (getAnswerDataType().trim() === "options-dynamic") {
            saveOptionsAndAnswer();
        }
        //alert("*(*)*");
        answer = document.getElementById("selectedAnswer").value;


      

    }

    return answer;
}

function showAnswerByTyping() {
    //alert("here");
    document.getElementById("selectedAnswerFromTyping").style.display = "inline";
    document.getElementById("answerMedium").value = "by-typing";
}

function saveInstance() {
    //first the answer
    var ianswer = getInstanceAnswer();


    //alert("saving the instances");
    
    //alert("yeaas");

    if (doesTaskHaveCorrectAnswer === "no") {

        //There are currently two conditions that will warrant task instances for this.
        //1. If there are inputs to this task.
        //2. if the answer type is dynamic options.

        //save inputs if this task required inputs
        if (Number(inputSize) > 0) {
            var inputmedium = getInputMediums()[inputsCnt];
            var currentInput = "";

            
            alert(inputmedium);

            if (inputmedium === "from-visualization") {
                getSelectedInput();
                currentInput = document.getElementById("selectedInput").value;
            }
            else if (inputmedium === "by-typing") {
                currentInput = document.getElementById("typedInput").value;
            }

            //save the inputs if there are unsaved inputs.
            if (currentInput !== "") {
                saveInputs();
            }
        }
    }

   

    if (ianswer.trim() === "" && doesTaskHaveCorrectAnswer === "yes") {
        //there should be an answer for such ones.
        alert("Please provide an answer for the current instance before continuing");
        return false;
    }
    else if (doesTaskHaveCorrectAnswer === "yes") {
        answers.push(ianswer);
    }

    if (getAnswerDataType().trim() === "options-dynamic") {
        // alert("about to save the dynamic answer types");
        allOptionsArr.push(allOptions);
    }
    
    //unselect the previous answers and inputs
    if (getAnswerGroup().trim() === "interface") {
        var omutatorMethod = getOutputMutatorMethod();
        var iframe = document.getElementById("viewerFrame");
        if (typeof iframe.contentWindow.window[omutatorMethod] == "function") {
            var input = iframe.contentWindow.window[omutatorMethod]("");
        }
        else {
            alert("Your visualization does not implement the mutator method --" + omutatorMethod + "() for this input");
        }
    }
    else {
        document.getElementById("selectedAnswer").value = "";
    }

    //get the instance answer
    incrementTaskInstanceCounter();

    //hide answersDivcontainer and show the inputs div    
    document.getElementById("answersDivContainer").style.display = "none";

    //disable the next instance button
    document.getElementById("nextInstance").disabled = true;

    //now let's create the input description for the next instance.


    // alert("I'm over here");

    setUpAnswerControllers(taskAnswerType);

    if (inputSize > 0) { //create the next input stringsif this task has an input
        document.getElementById("inputsDiv").style.display = "block";
        createInputDescriptions();
    }
    else {
        noInputs();
    }





    return true;
}

function saveInputs() {

    //check if there is an input value for the medium chosen
    //if there is an input, save it.

    currentInput = "";

    var inputmedium = getInputMediums()[inputsCnt];

    if (inputmedium === "from-visualization") {
        getSelectedInput();
        currentInput = document.getElementById("selectedInput").value;
        if (currentInput.trim() === "" && does) {
            alert("No inputs have been provided");
            return false;
        }
        document.getElementById("selectedInput").value = "";
        document.getElementById("selectedInput").style.display = "none";

        resetSelectedInput();
    }
    else if (inputmedium === "by-typing") {
        // alert("here we are---bytypeing");
        currentInput = document.getElementById("typedInput").value;
        if (currentInput.trim() === "") {
            alert("No inputs have been provided");
            return false;
        }
        document.getElementById("typedInput").value = "";
    }
    //  alert(currentInput);
    inputs.push(currentInput);

    inputsCnt++;



    //set up the field for the next inputs  

    if (inputsCnt < inputSize) {
        //   alert("a");
        createInputDescriptions();
    }
    else {
        //now get the inputs also
        var inputStr = "";


        for (var i = 0; i < inputs.length; i++) {
            //alert(inputs[i]);
            if (i === 0) {
                inputStr = inputs[i];
            }
            else {
                inputStr += ":::" + inputs[i];
            }
        }
        // alert("inputsStr "+inputStr);

        inputs = [];
        inputsCnt = 0;

        //push the inputs
        taskInstances.push(inputStr);
        //alert(taskInstances);
    }



    return true;

}


function noInputs() {
    //enable the next button so that it can be clicked on after the inputs has been provided
    document.getElementById("nextInstance").disabled = false;

    //hide inputs div and show answersDivcontainer
    document.getElementById("inputsDiv").style.display = "none";
    document.getElementById("answersDivContainer").style.display = "block";

    //alert("answers group is "+ getAnswerGroup());

    //alert("there are no inputs hooray!");

    //check the answer group
    if (!(getAnswerGroup().trim() === "widget")) {
        document.getElementById("answer-from-visualization").style.display = "block";
        document.getElementById("answer-by-typing").style.display = "none"
    }
    else if (getAnswerDataType().trim() === "options-dynamic") {
        //we will ask the user to specify the options, 
        //then select the correct option as answer
        document.getElementById("dynamic-optionsDiv").style.display = "block";
    }
    else {
        //
    }
}



function doneWithInputs() {

    //first save the last input provided
    if (saveInputs() === false) {
        return false;
    }

    // alert("taskInstances.length  -- "+taskInstances.length);
    //enable the next button so that it can be clicked on after the inputs has been provided
    document.getElementById("nextInstance").disabled = false;

    //hide inputs div and show answersDivcontainer
    document.getElementById("inputsDiv").style.display = "none";
    document.getElementById("answersDivContainer").style.display = "block";

    //check the answer group
    if (!(getAnswerGroup().trim() === "widget")) {
        document.getElementById("answer-from-visualization").style.display = "block";
        document.getElementById("answer-by-typing").style.display = "none"
    }
    else if (getAnswerDataType().trim() === "options-dynamic") {
        //we will ask the user to specify the options, 
        //then select the correct option as answer
        document.getElementById("dynamic-optionsDiv").style.display = "block";
    }
}

function provideInterfaceAnswerByTyping() {
    document.getElementById("answer-by-typing").style.display = "block"
    document.getElementById("answer-from-visualization").style.display = "none";
}

function answerMediumChanged() {
    var answermedium = document.getElementById("answerMedium").value;

    if (answermedium.trim() === "from-visualization") {
        //show the div for that   
        document.getElementById("answer-from-visualization").style.display = "block";
        document.getElementById("answer-by-typing").style.display = "none"
    }
    else {
        //show the div for providing answers by typing.
        document.getElementById("answer-by-typing").style.display = "block"
        document.getElementById("answer-from-visualization").style.display = "none";

    }
}





function chosenInputMedium(elem) {
    inputMedium = elem.value;
}

/*
 function chosenAnswerMedium(elem) {
 answerMedium = elem.value;
 }
 */


function saveAllTaskInstances() {


    //check if the current instance has not been saved, then save it.     
    var instanceSaved = true;

    if (answers.length < taskInstances.length) {
        //  alert("over here");
        //if this is really true save the last instance duh!
        instanceSaved = saveInstance();
    }
    else {
        // alert("ok");
        if (taskInstances.length === 0 && (getAnswerGroup() === "widget" || getAnswerGroup() === "interface")) {
            instanceSaved = saveInstance();
        }
    }


    if (instanceSaved === false) {
        alert("couldn't save all tasks");
        return false;
    }

    //now let's get the instances and the answers together and save it.
    //send the taskInstances and answers created to the server
    var tempname = document.getElementById("tempname").value;

    var instances = "";
    var ans = "";

    if (taskInstances.length > 0 && answers.length > 0) {
        for (var i = 0; i < taskInstances.length; i++) {
            //alert(taskInstances[i]);        
            if (i === 0) {
                instances = taskInstances[i];
                ans = answers[i];
            }
            else {
                instances += "::::" + taskInstances[i];
                ans += "::::" + answers[i];
            }
        }
    }
    else if (taskInstances.length > 0) {
        for (var i = 0; i < taskInstances.length; i++) {
            //alert(taskInstances[i]);        
            if (i === 0) {
                instances = taskInstances[i];
            }
            else {
                instances += "::::" + taskInstances[i];
            }
        }
    }
    else if (answers.length > 0) {
        for (var i = 0; i < taskInstances.length; i++) {
            //alert(taskInstances[i]);        
            if (i === 0) {
                ans = answers[i];
            }
            else {
                // instances += "::::" + taskInstances[i];
                ans += "::::" + answers[i];
            }
        }
    }

    var taskOptions = "";
    for (var i = 0; i < allOptionsArr.length; i++) {
        if (i === 0) {
            taskOptions = allOptionsArr[i];
        }
        else {
            taskOptions += "::::" + allOptionsArr[i];
        }

    }



    var userid = document.getElementById("userid").value;


    //alert("instances is "+instances);



    var url = "TaskInstancesCreator?tempname=" + tempname
            + "&command=saveAllTaskInstances"
            + "&taskInstances=" + instances
            + "&answers=" + ans
            + "&userid=" + userid
            + "&taskOptions=" + taskOptions;

    var xmlHttpRequest = getXMLHttpRequest();
    xmlHttpRequest.onreadystatechange = function()
    {
        if (xmlHttpRequest.readyState === 4 && xmlHttpRequest.status === 200)
        {
            //alert("tasks has been saved successfully - now your life can go on.");

            //redirect to the successful page.
            window.location = "task_instances_creation_success.html";


        }
    };
    xmlHttpRequest.open("POST", url, false);
    xmlHttpRequest.send();
}

function getAnswerType() {
    var command = "getAnswerType";
    var url = "TaskInstancesCreator?command=" + command;

    var xmlHttpRequest = getXMLHttpRequest();

    xmlHttpRequest.onreadystatechange = function()
    {
        if (xmlHttpRequest.readyState === 4 && xmlHttpRequest.status === 200)
        {
            //  alert("hey--hey");

            taskAnswerType = xmlHttpRequest.responseText;
            setUpAnswerControllers(taskAnswerType);

            if (getAnswerGroup() === "interface") {
                //show the button that will be used to get the answers
                // document.getElementById("answerSelectionDiv").style.display = "block";
                //answerMedium has to be from the visualization
                document.getElementById("answerMedium").value = "from-visualization";
            }
            getInputSize();
        }
    };
    xmlHttpRequest.open("GET", url, false);
    xmlHttpRequest.send();
}


function getInputTypesAndDescriptions() {
    var url = "TaskInstancesCreator?command=" + "getInputTypesAndDescriptions";
    var xmlHttpRequest = getXMLHttpRequest();
    //alert("here");
    xmlHttpRequest.onreadystatechange = function()
    {
        if (xmlHttpRequest.readyState === 4 && xmlHttpRequest.status === 200)
        {
            //if there is inputs then do the following
            var inputStr = xmlHttpRequest.responseText;

            if (inputStr.trim() !== "") {
                setUpInputTypesAndDescriptions(xmlHttpRequest.responseText);
                createInputDescriptions();
            }
            else {
                //set up the answer controllers
                // setUpAnswerControllers(taskAnswerType);


                setUpAnswerControllers(taskAnswerType);
                noInputs();
            }



        }
    };

    xmlHttpRequest.open("GET", url, false);
    xmlHttpRequest.send();

}


function getTaskHasCorrectAnswerValue() {

    var url = "TaskInstancesCreator?command=" + "getTaskHasCorrectAnswer";
    var xmlHttpRequest = getXMLHttpRequest();
    //alert("here");
    xmlHttpRequest.onreadystatechange = function()
    {
        if (xmlHttpRequest.readyState === 4 && xmlHttpRequest.status === 200)
        {
            //if there is inputs then do the following
            //alert("see what was returned === " + xmlHttpRequest.responseText);

            doesTaskHaveCorrectAnswer = xmlHttpRequest.responseText;



            if ((doesTaskHaveCorrectAnswer.trim() === "no")
                    && (getInputTypes().length === 0) && (getAnswerDataType() !== "options-dynamic")) {

                //notify the evaluator there is no need to create task instances of this task type.

                //hide the div for input and answers.

                document.getElementById("inputsAndAnswers").style.display = "none";



                //show the div for notification
                document.getElementById("noNeedForTaskInstances").style.display = "block";

            }
        }
    };

    xmlHttpRequest.open("GET", url, false);
    xmlHttpRequest.send();

}



function createInputDescriptions() {

    if (getInputTypes().length > 0) {
        //TODO: this does not have to be 0. note that 
        document.getElementById("inputType").innerHTML = getInputTypes()[inputsCnt];
        document.getElementById("inputDescription").innerHTML = getInputDescriptions()[inputsCnt];

        var inputmedium = getInputMediums()[inputsCnt];


        //document.getElementById("inputMedium").value = inputmedium;


        //NB: currently the input medium can be of two types (i.e. "from-visualization"  and "by-typing".

        //depending on the input type, provide the appropriate answering method.
        if (inputmedium.trim() === "from-visualization") {
            //show the input from visualization
            document.getElementById("input-from-visualization").style.display = "block"
            document.getElementById("input-from-typing").style.display = "none";
        }
        else {
            // alert("kiddo -- ");
            //show the input by typing
            document.getElementById("input-from-visualization").style.display = "none"
            document.getElementById("input-from-typing").style.display = "block";
        }

       // alert(inputsCnt + "___" + (Number(inputSize) - 1))
        if (inputsCnt < (Number(inputSize))) {
            if (getInputTypes().length === 1 || inputsCnt === (Number(inputSize) - 1)) {
                //show the button that will lead to providing answer
                document.getElementById("next-input-button").style.display = "none";


                //show this button only if this task requires an answer.
                if (doesTaskHaveCorrectAnswer === "yes") {
                    document.getElementById("provide-answer-button").style.display = "block";
                }
                else {
                    document.getElementById("nextInstance").disabled = false;
                }

            }
            else {
                //we will show the next input method
                document.getElementById("next-input-button").style.display = "block";
                document.getElementById("provide-answer-button").style.display = "none";
            }
        }
    }
}

function getInputSize() {
    var command = "getInputSize";
    var url = "TaskInstancesCreator?command=" + command;
    var xmlHttpRequest = getXMLHttpRequest();

    xmlHttpRequest.onreadystatechange = function()
    {
        if (xmlHttpRequest.readyState === 4 && xmlHttpRequest.status === 200)
        {
            //alert("input size is " + xmlHttpRequest.responseText);
            inputSize = xmlHttpRequest.responseText;
            
            //alert("input-size is :: " +inputSize);
            
             document.getElementById("next-input-button").style.display = "none";

            // alert("something--here");
             
             //alert(doesTaskHaveCorrectAnswer);

                //show this button only if this task requires an answer.
               /* if (doesTaskHaveCorrectAnswer === "yes") {
                    document.getElementById("provide-answer-button").style.display = "block";
                }
                else {
                    document.getElementById("nextInstance").disabled = false;
                }*/
        }
    };
    xmlHttpRequest.open("GET", url, false);
    xmlHttpRequest.send();
}

function incrementTaskInstanceCounter() {
    taskInstancesCounter++;
    var tnum = document.getElementById("taskInstanceNumber");
    tnum.innerHTML = taskInstancesCounter;
}

function getAndShowSelectedInput() {
    getSelectedInput();
    document.getElementById("selectedInputSpan").style.display = "inline";
}

function getSelectedInput() {

    var inputAccessorName = getInputAccessorMethods() [inputsCnt];

    var iframe = document.getElementById("viewerFrame");
    if (typeof iframe.contentWindow.window[inputAccessorName] == "function") {
        var input = iframe.contentWindow.window[inputAccessorName]();
        document.getElementById("selectedInput").value = input;
    }
    else {
        alert("You visualization does not implement the accessor method --" + inputAccessorName + "() for this input");
    }
}

function resetSelectedInput() {


    var inputMutatorName = getInputMutatorMethods() [inputsCnt];

    var iframe = document.getElementById("viewerFrame");
    if (typeof iframe.contentWindow.window[inputMutatorName] == "function") {
        var input = iframe.contentWindow.window[inputMutatorName]("");
    }
    else {
        alert("Your visualization does not implement the necessary mutator method --" + inputMutatorName + "() for this input");
    }
}

function setDynamicOptionAnswer(element) {
    var value = element.value;
    document.getElementById("selectedAnswer").value = document.getElementById("option" + value).value;
    document.getElementById("selectedOption").value = value;
}

function saveOptionsAndAnswer() {
    //get the size of the options that were provided
    var optionsSize = document.getElementById("numberOfOptions").value;

    allOptions = "";
    optionsAnswer = "";


    var answer = document.getElementById("selectedAnswer").value;
    var selectedOption = document.getElementById("selectedOption").value;
    var selectedOptionValue = "";


    //if selected option has not been selected return false

    if (selectedOption.trim() === "" && doesTaskHaveCorrectAnswer === "yes") {
        return false;
    }
    else if (doesTaskHaveCorrectAnswer === "yes") {
        selectedOptionValue = document.getElementById("option" + selectedOption).value;
    }

    if ((answer.trim() === "" || selectedOptionValue.trim() === "") && doesTaskHaveCorrectAnswer === "yes") {
        //alert("Provide the correct option before proceeding");
        return false;
    }

    optionsAnswer = answer;

    var cnt = 0;

    //alert("optionSize is "+ optionsSize);

    for (var i = 0; i < optionsSize; i++) {
        var option = document.getElementById("option" + (i + 1)).value;

        if (option.trim() !== "") {
            cnt++;

            if (cnt === 1) {
                allOptions = option;
            }
            else {
                allOptions += " :: " + option;
            }
        }
    }


    //reset all the other variables we used here
    //try to remove an additional child if any

    if (optionsSize > 4) {
        var optionsDiv = document.getElementById("optionsDiv").value;

        for (var i = 5; i < optionsSize; i++) {
            optionsDiv.removeChild(document.getElementById("option" + i));
            optionsDiv.removeChild(document.getElementById("optionRadio" + i));
        }
        //reduce the inputCnt

    }
    //reset the values for the options
    for (var i = 0; i < 4; i++) {

        var theOption = document.getElementById("option" + (i + 1));

        //theOption.removeAttribute("checked");
        theOption.value = "";

    }

    //remove the selected paragraph and recreate it. 
    //this is because we couldn't uncheck the radio buttons for some reason

    // alert("parag_option"+selectedOption);


    //do this if an answer was given
    if (doesTaskHaveCorrectAnswer === "yes") {

        var optionDiv = document.getElementById("div_option" + selectedOption);
        removeDivChildren(optionDiv);

        //optionParag.removeChild("option"+selectedOption);
        //optionParag.removeChild("optionRadio"+selectedOption);

        //now lets re-add the radio button and the option text box.
        var parag = document.createElement("p");
        var radioBtn = document.createElement("input");
        radioBtn.setAttribute("type", "radio");
        radioBtn.setAttribute("id", "optionRadio" + selectedOption);
        radioBtn.setAttribute("name", "answerOption")
        radioBtn.setAttribute("value", selectedOption);
        radioBtn.setAttribute("onclick", "setDynamicOptionAnswer(this)");

        var input = document.createElement("input");
        input.setAttribute("type", "text");
        input.setAttribute("id", "option" + selectedOption);
        input.setAttribute("size", "15");


        parag.appendChild(radioBtn);
        parag.appendChild(input);
        optionDiv.appendChild(parag);

        document.getElementById("numberOfOptions").value = 4;
        document.getElementById("selectedOption").value = "";
    }

    return true;
}


/*
 function doneWithDynamicOptions() {
 document.getElementById("dynamic-option-answerDiv").style.display = "block";
 }
 */

function addMoreDynamicOptions() {
    //first get the highest number of boxes,
    //add a new box, and increment the highest number of boxes.
    var optionsSize = parseInt(document.getElementById("numberOfOptions").value);
    optionsSize++;

    //now append it to it.
    //var optionsDiv = document.getElementById("optionsDiv");


    var optionsDiv = document.getElementById("div_option" + optionsSize);

    //now lets re-add the radio button and the option text box.
    var parag = document.createElement("p");
    var radioBtn = document.createElement("input");
    radioBtn.setAttribute("type", "radio");
    radioBtn.setAttribute("id", "optionRadio" + optionsSize);
    radioBtn.setAttribute("name", "answerOption")
    radioBtn.setAttribute("value", optionsSize);
    radioBtn.setAttribute("onclick", "setDynamicOptionAnswer(this)");

    var input = document.createElement("input");
    input.setAttribute("type", "text");
    input.setAttribute("id", "option" + optionsSize);
    input.setAttribute("size", "15");


    parag.appendChild(radioBtn);
    parag.appendChild(input);

    optionsDiv.appendChild(parag);



    /* var input = document.createElement("input");
     input.setAttribute("type", "text");
     input.setAttribute("id", "option" + optionsSize);
     input.setAttribute("size", "15");  */

    //optionsDiv.appendChild(input);

    document.getElementById("numberOfOptions").value = optionsSize;
}

function getAndShowSelectedAnswer() {
    getSelectedAnswer();
    document.getElementById("selectedAnswerFromVis").style.display = "inline";
}


function getSelectedAnswer() {
    //get the answer from the visualization and set it 
    //first get the output interface name, and use it to call and get the answer
    var outputAccessorMethod = getOutputAccessorMethod();

    //now we will actuallly get the answer from the visualization
    var iframe = document.getElementById("viewerFrame");
    if (typeof iframe.contentWindow.window[outputAccessorMethod] == "function") {
        var selectedAnswer = iframe.contentWindow.window[outputAccessorMethod]();

        document.getElementById("selectedAnswerFromVis").value = selectedAnswer;
    }
    else {
        alert("The output method that returns the output is not implemented.");
    }
}

function showSteps() {
    document.getElementById("steps").style.display = "block";
    document.getElementById("showStepsButton").style.display = "none";
    document.getElementById("hideStepsButton").style.display = "block";

}
function hideSteps() {
    document.getElementById("steps").style.display = "none";
    document.getElementById("showStepsButton").style.display = "block";
    document.getElementById("hideStepsButton").style.display = "none";
}
