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
var inputSize=0;
var inputsCnt = 0;

function uploadFiles() {
    var tempname = document.getElementById("tempname").value;
    var viewer = document.getElementById("viewer").value;
    var dataset = document.getElementById("dataset").value;
    var task = document.getElementById("quantitativeTasks").value;

    var thefiles = document.getElementById("thefiles").files;
    var url = "TaskInstancesCreator?tempname=" + tempname
            + "&command=submitForm"
            + "&dataset=" + dataset
            + "&task=" + task
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
}

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
    if (getAnswerGroup() === "interface") { //answer will be selected
        answer = getSelectedElements();
    }
    else {  //answer will be provided through a widget
        answer = document.getElementById("selectedAnswer").value;
    }


    if (answer === "") {
        alert("Please provide an answer for the current instance before continuing");
        return false;
    }

    answers.push(answer);
    //empty the selectedAnswer value
    document.getElementById("selectedAnswer").value = "";
    setUpAnswerControllers(taskAnswerType);
}

function saveInstance() {
    var inputStr = "";
    for (var i = 0; i < inputs.length; i++) {
        if (i === 0) {
            inputStr = inputs[i];
        }
        else {
            inputStr += "::" + inputs[i];
        }
    }
    //reset the inputs
    inputs = [];
    //alert(inputStr);
    //push the inputs
    taskInstances.push(inputStr);

    //get the instance answer
    getInstanceAnswer();

    incrementTaskInstanceCounter();
    //hide answersDivcontainer and show the inputs div    
    document.getElementById("answersDivContainer").style.display = "none";
    document.getElementById("inputsDiv").style.display = "block";

    //disable the next instance button
    document.getElementById("nextInstance").disabled = true;
}

function saveInputs() {
    var seElems = getSelectedElements();

    if (seElems !== "")
        inputs.push(seElems);
    
    inputsCnt++;
    //alert(inputsCnt + "--"+inputSize);
    if(inputsCnt === Number(inputSize)){
        //alert("done with inputs");
        inputsCnt = 0; //initialize the inputs counter.
        doneWithInputs();
    }
}

function saveAllTaskInstances() {    
    //first save the last instance
    saveInstance();
    
    //send the taskInstances and answers created to the server
    var tempname = document.getElementById("tempname").value;

    var instances = "";
    var ans = "";

    for (var i = 0; i < taskInstances.length; i++) {
        if (i === 0) {
            instances = taskInstances[i];
            ans = answers[i];
        }
        else {
            instances += "::::" + taskInstances[i];
            ans += "::::" + answers[i];
        }
    }
   /* alert("instances are: "+ instances
            + "  and answers: "+ans);  */
    var graphType = "";
    var iframe = document.getElementById("viewerFrame");
    //get the graphType assuming we are dealing with graph visualizations
    if (typeof iframe.contentWindow.getGraphType == "function") {
        graphType = iframe.contentWindow.getGraphType();
    }

    var url = "TaskInstancesCreator?tempname=" + tempname
            + "&command=saveAllTaskInstances"
            + "&taskInstances=" + instances
            + "&answers=" + ans
            + "&graphType=" + graphType;

    var xmlHttpRequest = getXMLHttpRequest();
    xmlHttpRequest.onreadystatechange = function()
    {
        if (xmlHttpRequest.readyState === 4 && xmlHttpRequest.status === 200)
        {

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
            taskAnswerType = xmlHttpRequest.responseText;
            setUpAnswerControllers(taskAnswerType);
         
          
         
            if (getAnswerGroup() === "interface") {
                //show the button that will be used to get the answers
                document.getElementById("answerSelectionDiv").style.display = "block";
            }
            
            
            getInputSize();
        }
    };
    xmlHttpRequest.open("GET", url, true);
    xmlHttpRequest.send();
}

function getInputSize(){    
     var command = "getInputSize";
    var url = "TaskInstancesCreator?command=" + command;
    var xmlHttpRequest = getXMLHttpRequest();

    xmlHttpRequest.onreadystatechange = function()
    {
        if (xmlHttpRequest.readyState === 4 && xmlHttpRequest.status === 200)
        {
           inputSize = xmlHttpRequest.responseText;
        }
    };
    xmlHttpRequest.open("GET", url, true);
    xmlHttpRequest.send();    
}




function incrementTaskInstanceCounter() {
    taskInstancesCounter++;

    var tnum = document.getElementById("taskInstanceNumber");
    tnum.innerHTML = taskInstancesCounter;
}

function doneWithInputs() {
    //save the current input
   // saveInputs();
    //enable the next button so that it can be clicked on after the inputs has been provided
    document.getElementById("nextInstance").disabled = false;

    //hide inputs div and show answersDivcontainer
    document.getElementById("inputsDiv").style.display = "none";
    document.getElementById("answersDivContainer").style.display = "block";
}