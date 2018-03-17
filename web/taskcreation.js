/**
 * This function makes a request to the setup servlet to display the demo page.
 * @returns {undefined}
 */
var sizeOfAnsOptions = 2; //starting default value
var numberOfInputs = 0; //default number of inputs

function answerOptionChanged(element) {
    //check if options where selected in which case you should allow options to be entered.
    if (element.value === "options-fixed" || element.value === "Color-options-fixed") {
        //show the options div
        document.getElementById("ansOptionsDiv").style.display = "block";
        document.getElementById("outputTypeDiv").style.display = "none";
    }
    else if (element.value === "Interface") {
        //show the output type
        // alert("selection selected");
        document.getElementById("outputTypeDiv").style.display = "block";
        document.getElementById("ansOptionsDiv").style.display = "none";
    }
    else {
        //hide the options div
        document.getElementById("ansOptionsDiv").style.display = "none";
        document.getElementById("outputTypeDiv").style.display = "none";
    }
}


function addMoreAnsOptions() {
    sizeOfAnsOptions++;
    var p = document.createElement("p");
    p.innerHTML = "Specify Option" + sizeOfAnsOptions;
    var inp = document.createElement("input");
    inp.setAttribute("type", "text");
    inp.setAttribute("class", "right");
    inp.setAttribute("name", "ansOptions" + sizeOfAnsOptions);
    inp.setAttribute("id", "ansOptions" + sizeOfAnsOptions);

    p.appendChild(inp);

    //append the this new paragraph to the options block
    var ob = document.getElementById("optionsBlock");
    ob.appendChild(p);
}

//when the type of task changes keep track of it.
function doesTaskHasCorrectAnswer(element) {

    if (element.value === "no") {
        document.getElementById("accuracyCheckingInterfaceDiv").style.display = "none";
    }
    else {
        document.getElementById("accuracyCheckingInterfaceDiv").style.display = "block";
    }


}

function checkShortNameAvailability() {
    //check with the server if the name already exists or not
    var taskshortname = document.getElementById("taskShortname").value;
    //get user id
    var userid = document.getElementById("userid").value;
    //alert(userid);


    var command = "checkTaskNameAvailability";
    var url = "TaskCreator?command=" + command
            + "&taskShortName=" + taskshortname
            + "&userid=" + userid;

    var xmlHttpRequest = getXMLHttpRequest();

    xmlHttpRequest.onreadystatechange = function()
    {
        if (xmlHttpRequest.readyState === 4 && xmlHttpRequest.status === 200)
        {                        //set the studyname variable

//alert(xmlHttpRequest.responseText);
            if (xmlHttpRequest.responseText === "exists") {
                //show a text saying that it is not available
                document.getElementById("nameAvailabilityChecking").style.display = "block";
                document.getElementById("nameUnavailable").style.display = "block";
                document.getElementById("nameAvailable").style.display = "none";
            }
            else if (taskshortname.trim() !== "") {
                //show a text showing that it does not exists.
                document.getElementById("nameAvailabilityChecking").style.display = "block";
                document.getElementById("nameAvailable").style.display = "block";
                document.getElementById("nameUnavailable").style.display = "none";
            }
        }
    };
    xmlHttpRequest.open("GET", url, true);
    xmlHttpRequest.send();
}

function removeDivChildren(div) {
    var last;
    while (last = div.lastChild)
        div.removeChild(last);
}

function saveNewTask() {

    //first check if the form has been 
//checkForInputParameters();

    var userid = document.getElementById("userid").value;

    var taskQuestion = document.getElementById("taskQuestion").value;
    var taskDescription = document.getElementById("taskDescription").value;
    var taskShortName = document.getElementById("taskShortname").value;
    var answerType = document.getElementById("answerType").value;
    //var inputinterface = document.getElementById("inputInterface").value;
    var outputType = document.getElementById("outputType").value;


    var numberOfInputs = document.getElementById("inputCnt").value;

    //alert(numberOfInputs);


    var outputTypeDescription = document.getElementById("outputTypeDescription").value;
    var accuracyCheckingInterface = document.getElementById("accuracyCheckingInterface").value;
    var emptyFields = false;
    var hasCorrectAns = document.getElementById("hasCorrectAnswer").value;

    //check if any of these is empty
    if (taskQuestion.trim() === "" || taskShortName.trim() === ""
            || answerType === "") {
        // alert("Some form fields are not filled");   

        emptyFields = true;
    }
    if (answerType.trim() === "Interface" && (outputType.trim() === ""
            || outputTypeDescription === "" ||
            (hasCorrectAns === "yes" && accuracyCheckingInterface === ""))) {
        //check if the ouput type and output type descriptions have been given
        emptyFields = true;
    }

    var ansOptions = "";

    if (answerType === "options-fixed") {//get the options 
        for (var i = 1; i <= sizeOfAnsOptions; i++) {
            if (i === 1) {
                ansOptions = document.getElementById("ansOptions" + i).value;
            }
            else {
                ansOptions += "::" + document.getElementById("ansOptions" + i).value;
            }
        }
        if (ansOptions.trim() === "") {
            emptyFields = true;
        }
    }
    var inputTypeShortNames = "";
    var inputTypeDescriptions = "";
    var inputMediums = "";


    //if the user intentionally left them empty, then the number of inputs is zero.
    if ((inputMediums.trim() === "" && inputTypeShortNames.trim() === "" && inputTypeDescriptions.trim() === "")) {
        numberOfInputs = 0;
    }
    
    //get the input type shortnames.
    for (var i = 1; i <= numberOfInputs; i++) {
        var sn = document.getElementById("inputShortName" + i).value;

        if (sn.trim() === "") {
            emptyFields = true;
            break;
        }

        if (i === 1) {

            inputTypeShortNames = document.getElementById("inputShortName" + i).value;
        }
        else {
            inputTypeShortNames += ":::" + document.getElementById("inputShortName" + i).value;
        }
    }



    for (var i = 1; i <= numberOfInputs; i++) {
        var inputmedium = document.getElementById("inputMedium" + i).value;

        if (inputmedium.trim === "") {
            emptyFields = true;
            break;
        }

        if (i === 1) {
            inputMediums = inputmedium;
        }
        else {
            inputMediums += ":::" + inputmedium;
        }
    }



    for (var i = 1; i <= numberOfInputs; i++) {
        var sd = document.getElementById("inputShortDescription" + i).value;
        if (sd.trim() === "") {
            emptyFields = true;
            break;
        }

        if (i === 1) {

            inputTypeDescriptions = (document.getElementById("inputShortDescription" + i).value).trim();

        }
        else {

            inputTypeDescriptions += ":::" + (document.getElementById("inputShortDescription" + i).value).trim();

        }
    }

    //check if there is empty input fields
    if (numberOfInputs > 0 && (inputTypeShortNames.trim() === "" || inputTypeDescriptions.trim() === "")) {
        emptyFields = true;
    }




    //now check if there was some empty fields    
    if (emptyFields === true) {
        //show the empty fields warning and return false;
        document.getElementById("emptyFieldsNotice").style.display = "block";
        document.getElementById("emptyFieldsNotice2").style.display = "block";

        return false;
    }


    //now check if the question text has placeholders for inputs.
    var inputPlaceHolders = [];
    for (var i = 0; i < taskQuestion.length; i++) {
        if (taskQuestion.charAt(i) === "$" && taskQuestion.charAt(i + 1) !== " ") {

            var regex = new RegExp('\\d');



            if (regex.test(taskQuestion.charAt(i + 1)) === true) { //if it is an integer
                var beginIndex = i + 1;

                var j = i + 1;

                for (j = i + 1; j < taskQuestion.length; j++) {

                    //stop the moment you find a non number value
                    if (regex.test(taskQuestion.charAt(j)) === false) {
                        break;
                    }
                }

                i = j;
                //alert("hey");
                //  alert("beginIndex: "+ beginIndex +" endIndex: "+(j-1));
                //alert("*" +taskQuestion.substring(beginIndex, (j))+ "*");

                //var str = "$1? snfosnoris $2? ad";

                //alert("hey-- "+str.substring(16, 17));

                inputPlaceHolders.push(Number(taskQuestion.substring(beginIndex, j)));

            }
        }
    }

    var numberOfInputs = document.getElementById("inputCnt").value;
    // alert(numberOfInputs);
    inputMediums = [];

    for (var i = 1; i <= numberOfInputs; i++) {
        var inputmedium = document.getElementById("inputMedium" + i).value;

        if (inputmedium.trim === "") {
            emptyFields = true;
            break;
        }

        if (i === 1) {
            inputMediums = inputmedium;
        }
        else {
            inputMediums += ":::" + inputmedium;
        }
    }

//get the input type shortnames.
    for (var i = 1; i <= numberOfInputs; i++) {
        var sn = document.getElementById("inputShortName" + i).value;

        if (sn.trim() === "") {
            emptyFields = true;
            break;
        }

        if (i === 1) {

            inputTypeShortNames = document.getElementById("inputShortName" + i).value;
        }
        else {
            inputTypeShortNames += ":::" + document.getElementById("inputShortName" + i).value;
        }
    }



    for (var i = 1; i <= numberOfInputs; i++) {
        var inputmedium = document.getElementById("inputMedium" + i).value;

        if (inputmedium.trim === "") {
            emptyFields = true;
            break;
        }

        if (i === 1) {
            inputMediums = inputmedium;
        }
        else {
            inputMediums += ":::" + inputmedium;
        }
    }



    for (var i = 1; i <= numberOfInputs; i++) {
        var sd = document.getElementById("inputShortDescription" + i).value;
        if (sd.trim() === "") {
            emptyFields = true;
            break;
        }

        if (i === 1) {

            inputTypeDescriptions = (document.getElementById("inputShortDescription" + i).value).trim();

        }
        else {

            inputTypeDescriptions += ":::" + (document.getElementById("inputShortDescription" + i).value).trim();

        }
    }



    //look for the maximum value among the positions of the placeholders, 
    //and check if it is less than the number of inputs.
    var maxph = 0;
    for (var i = 0; i < inputPlaceHolders.length; i++) {
        if (parseInt(inputPlaceHolders[i]) > maxph) {
            maxph = parseInt(inputPlaceHolders[i]);
        }
    }

    if (maxph > numberOfInputs) {
        alert("You expect to use input #" + maxph +
                " in the question text,  but the number of inputs you've provided for this task"
                + " is less than " + maxph
                + " This should be fixed before the task can be created.");

        return false;
    }

    //check if the input mediums for the placeholder are typing and not visualization.
    for (var i = 0; i < inputPlaceHolders.length; i++) {
        //alert(inputPlaceHolders[i]);

        var holderPosition = inputPlaceHolders[i];

        var inputMedium;
        //alert("hey--A");
        //alert(inputMediums);
        //alert("hey--B");
        if (inputMediums.split(":::").length > 1) {
            inputMedium = inputMediums.split(":::")[holderPosition - 1];
        }
        else {
            inputMedium = inputMediums;
        }


        if (inputMedium.trim() !== "by-typing") {
            alert("The input medium for inputs that are part of a question's text"
                    + " are supposed to be \"by typing\"");


            return false;
        }




    }


    var url = "TaskCreator?"
            + "command=saveNewTask"
            + "&taskQuestion=" + taskQuestion
            + "&taskDescription=" + taskDescription
            + "&taskShortName=" + taskShortName
            + "&answerType=" + answerType
            + "&answerOptions=" + ansOptions
            //  + "&inputInterface=" + inputinterface
            + "&outputType=" + outputType
            + "&outputTypeDescription=" + outputTypeDescription
            + "&accuracyCheckingInterface=" + accuracyCheckingInterface
            + "&numberOfInputs=" + numberOfInputs
            + "&inputTypeShortNames=" + inputTypeShortNames
            + "&inputTypeDescriptions=" + inputTypeDescriptions
            + "&inputMediums=" + inputMediums
            + "&userid=" + userid
            + "&hasCorrectAnswer=" + hasCorrectAns;




//alert ("Im here before pos");
    var xmlHttpRequest = getXMLHttpRequest();
    xmlHttpRequest.onreadystatechange = function()
    {
        if (xmlHttpRequest.readyState === 4 && xmlHttpRequest.status === 200)
        {
            window.location = "taskcreationsuccess.html";
        }
    };
    xmlHttpRequest.open("POST", url, false);
    xmlHttpRequest.send();


    //  alert ("Im here last pos");
}


function checkForInputParameters() {
    //this will find the parameters and return them e.g. if it finds "$1,  $2" it will return [1,2]

    var question = document.getElementById("taskQuestion").value;


    var params = [];
    var regex = new RegExp('\\d');

    for (var i = 0; i < question.length; i++) {

        if (question.charAt(i) === "$") {

            //find out if what follows is a number or not
            var beginIndex = "";

            //check what follows if it is a number or not
            var regex = new RegExp('\\d');

            if (regex.test(question.charAt(i + 1)) === true) {

                beginIndex = (i + 1);
                var j = i + 1;
                for (j = i + 1; j < question.length; j++) {
                    //stop the moment you find a non number value
                    if (regex.test(question.charAt(j)) === false) {
                        break;
                    }
                }

                i = j;

                params.push(Number(question.substring(beginIndex, j)));

            }
        }
    }

    //alert("the parameters are ++ " + params);
    return params;

}

function addAnotherInputDetail() {
    var inputCnt = document.getElementById("inputCnt").value;

    // var inputsDetailsDiv = document.getElementById("inputsDetails");

    inputCnt++;  //we will be incrementing the count of the #of inputs

    var tr = document.createElement("tr");
    tr.setAttribute("id", "inputRow" + inputCnt);

    var td0 = document.createElement("td");

    td0.innerHTML = inputCnt + ". ";
    tr.appendChild(td0);

    var td1 = document.createElement("td");

    var td2 = document.createElement("td");

    var td3 = document.createElement("td");





    //first paragraph
    var parag1 = document.createElement("p");
    parag1.setAttribute("id", "parag_" + "inputShortName" + inputCnt);
    //parag1.innerHTML = "Type a shortname for input type #" + inputCnt + "(e.g. node)";

    var inputtype = document.createElement("input");
    inputtype.setAttribute("type", "text");
    inputtype.setAttribute("id", "inputShortName" + inputCnt);

    inputtype.setAttribute("size", "24");

    parag1.appendChild(inputtype);
    td1.appendChild(parag1);


    //inputsDetailsDiv.appendChild(parag1);

//second paragraph
    var parag2 = document.createElement("p");
    parag2.setAttribute("id", "parag_" + "inputMedium" + inputCnt);
    //parag2.innerHTML = "How will this input be provided during task instance creation?";


    var select = document.createElement("select");
    select.setAttribute("id", "inputMedium" + inputCnt);
    select.setAttribute("class", "inputMedium");

    var option1 = document.createElement("option");
    option1.setAttribute("value", "");
    option1.innerHTML = "Select One";
    var option2 = document.createElement("option");
    option2.setAttribute("value", "from-visualization");
    option2.innerHTML = "From Visualization";

    var option3 = document.createElement("option");
    option3.setAttribute("value", "by-typing");
    option3.innerHTML = "By Typing";
    //append options to the select.
    select.appendChild(option1);
    select.appendChild(option2);
    select.appendChild(option3);
    parag2.appendChild(select);

    td2.appendChild(parag2);


    //third paragraph
    var parag3 = document.createElement("p");
    parag3.setAttribute("id", "parag_" + "inputShortDescription" + inputCnt);
    // parag3.innerHTML = "Write a short description for input type #" + inputCnt;

    var inputtypedesc = document.createElement("textarea");
    inputtypedesc.setAttribute("rows", "2");
    inputtypedesc.setAttribute("cols", "38");
    inputtypedesc.setAttribute("id", "inputShortDescription" + inputCnt);

    parag3.appendChild(inputtypedesc);

    //add two new breaks
    /* parag3.appendChild(document.createElement("br"));
     parag3.appendChild(document.createElement("br"));
     parag3.appendChild(document.createElement("br"));  
     inputsDetailsDiv.appendChild(parag2);
     inputsDetailsDiv.appendChild(parag3);*/
    td3.appendChild(parag3);


    tr.appendChild(td1);
    tr.appendChild(td2);
    tr.appendChild(td3);

    var inputsTable = document.getElementById("inputsTable");

    inputsTable.appendChild(tr);

    document.getElementById("inputsTable").style.display = "block";

    //update the inputCount
    document.getElementById("inputCnt").value = inputCnt;
}

function removeLastInputDetail() {
    var inputCnt = document.getElementById("inputCnt").value;

    if (inputCnt > 0) {//remove if only there is an input

        var inputsTable = document.getElementById("inputsTable");



        inputsTable.removeChild(document.getElementById("inputRow" + inputCnt));


        /* var inputsDetailsDiv = document.getElementById("inputsDetails");
         inputsDetailsDiv.removeChild(document.getElementById("parag_" + "inputShortName" + inputCnt));
         inputsDetailsDiv.removeChild(document.getElementById("parag_" + "inputShortDescription" + inputCnt));
         inputsDetailsDiv.removeChild(document.getElementById("parag_" + "inputMedium" + inputCnt)); 
         */

    }
    //reduce the inputCnt
    inputCnt--;

    if (inputCnt === 0) {
        document.getElementById("inputsTable").style.display = "none";
    }
    document.getElementById("inputCnt").value = inputCnt;
}



function getNumberOfInputs(elem) {
    var value = elem.value;
    if (value.trim() !== "" && value !== "0") {
        numberOfInputs = value;
        //now create widgets for the answer type and type description
        var inputInfoDiv = document.getElementById("inputInfo");

        removeDivChildren(inputInfoDiv);
        //show or hide the input notice if the number is 0 or otherwise
        if (value === 0) {
            document.getElementById("inputNotice").style.display = "none";
        }
        else {
            document.getElementById("inputNotice").style.display = "block   ";
        }

        for (var i = 1; i <= value; i++) {
            //create the inputtype
            var parag1 = document.createElement("p");
            parag1.innerHTML = "Type a shortname for input type #" + i + "(e.g. node)";

            var inputtype = document.createElement("input");
            inputtype.setAttribute("type", "text");
            inputtype.setAttribute("id", "inputShortName" + i);
            inputtype.setAttribute("class", "right");
            inputtype.setAttribute("size", "24");

            parag1.appendChild(inputtype);

            var parag2 = document.createElement("p");
            parag2.innerHTML = "Write a short description for input type #" + i;

            var inputtypedesc = document.createElement("textarea");
            inputtypedesc.setAttribute("rows", "2");
            inputtypedesc.setAttribute("cols", "38");
            inputtypedesc.setAttribute("id", "inputShortDescription" + i);
            inputtypedesc.setAttribute("class", "right");
            parag2.appendChild(inputtypedesc);



            var parag4 = document.createElement("p");
            parag4.innerHTML = "How will this input be provided during task instance creation?";


            var select = document.createElement("select");
            select.setAttribute("id", "inputMedium" + i);
            select.setAttribute("class", "right");

            var option1 = document.createElement("option");
            option1.setAttribute("value", "");
            option1.innerHTML = "Select One";
            var option2 = document.createElement("option");
            option2.setAttribute("value", "from-visualization");
            option2.innerHTML = "From Visualization";

            var option3 = document.createElement("option");
            option3.setAttribute("value", "by-typing");
            option3.innerHTML = "By Typing";

            //append options to the select.
            select.appendChild(option1);
            select.appendChild(option2);
            select.appendChild(option3);

            parag4.appendChild(select);



            /* var parag3 = document.createElement("p");
             
             parag3.innerHTML = "<br/><em>(NB: You have to implement interfaces (i.e. accessor, and mutator methods) for this input type."
             + " For example, if the shortname for the output type is"
             + "\"node\", you need to implement the methods \"getNodes()\" and \"setNode()\"). </em>    ";
             
             */


            inputInfoDiv.appendChild(parag1);
            //inputInfoDiv.appendChild(parag1b);
            inputInfoDiv.appendChild(parag4);
            //inputInfoDiv.appendChild(document.createElement("br"));
            inputInfoDiv.appendChild(parag2);


            //<!-- inputInfoDiv.appendChild(parag3);   

            inputInfoDiv.appendChild(document.createElement("br"));
            inputInfoDiv.appendChild(document.createElement("br"));
        }


        inputInfoDiv.style.display = "block";



    }
    else {
        numberOfInputs = 0;
        //hide the inputInfo Div
        inputInfoDiv.style.display = "none";

    }
}