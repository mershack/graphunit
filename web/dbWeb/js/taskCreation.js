/**
 * This function makes a request to the setup servlet to display the demo page.
 * @returns {undefined}
 */
var sizeOfAnsOptions = 2; //starting default value
var numberOfInputs = 0; //default number of inputs

function answerOptionChanged(element) {
    //check if options where selected in which case you should allow options to be entered.
    if (element.value === "Options") {
        //show the options div
        document.getElementById("ansOptionsDiv").style.display = "block";
    }
    else {
        //hide the options div
        document.getElementById("ansOptionsDiv").style.display = "none";
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

function checkShortNameAvailability() {
    //check with the server if the name already exists or not
    var taskshortname = document.getElementById("taskshortname").value;

    var command = "checkTaskNameAvailability";
    var url = "TaskCreator?command=" + command
            + "&taskShortName=" + taskshortname;
    var urlCustom = "http://" + location.host + ":8080/graphunit/" + url;

    console.log(urlCustom);
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
            else {
                //show a text showing that it does not exists.
                document.getElementById("nameAvailabilityChecking").style.display = "block";
                document.getElementById("nameAvailable").style.display = "block";
                document.getElementById("nameUnavailable").style.display = "none";
            }
        }
    };
    xmlHttpRequest.open("GET", urlCustom, true);
    xmlHttpRequest.send();
}

function removeDivChildren(div) {
    var last;
    while (last = div.lastChild)
        div.removeChild(last);
}

function saveNewTask() {
    var taskQuestion = document.getElementById("taskQuestion").value;
    var taskShortName = document.getElementById("taskshortname").value;
    var answerType = document.getElementById("answertype").value;
    var inputinterface = document.getElementById("inputInterface").value;
    var outputInterface = document.getElementById("outputInterface").value;
    var numberOfInputs = document.getElementById("numberOfInputs").value;

    var ansOptions = "";

    if (answerType === "Options") //get the options 
        for (var i = 1; i <= sizeOfAnsOptions; i++) {
            if (i === 1) {
                ansOptions = document.getElementById("ansOptions" + i).value;
            }
            else {
                ansOptions += "::" + document.getElementById("ansOptions" + i).value;
            }
        }



    var inputTypeShortNames = "";
    var inputTypeDescriptions = "";

    //get the input type shortnames.
    for (var i = 1; i <= numberOfInputs; i++) {
        if (i === 1) {
            inputTypeShortNames = document.getElementById("inputShortName" + i).value;
        }
        else {
            inputTypeShortNames += ":::" + document.getElementById("inputShortName" + i).value;
        }
    }

    //get the input type descriptions
    for (var i = 1; i <= numberOfInputs; i++) {
        if (i === 1) {
            inputTypeDescriptions = document.getElementById("inputShortDescription" + i).value;
        }
        else {
            inputTypeDescriptions += ":::" + document.getElementById("inputShortDescription" + i).value;
        }
    }


//    alert("inputtypeshortnames :---  " + inputTypeShortNames);
//    alert("inputtypedescriptions : --- " + inputTypeDescriptions);



    var url = "TaskCreator?"
            + "command=saveNewTask"
            + "&taskQuestion=" + taskQuestion
            + "&taskShortName=" + taskShortName
            + "&answerType=" + answerType
            + "&answerOptions=" + ansOptions
            + "&inputInterface=" + inputinterface
            + "&outputInterface=" + outputInterface
            + "&numberOfInputs=" + numberOfInputs
            + "&inputTypeShortNames=" + inputTypeShortNames
            + "&inputTypeDescriptions=" + inputTypeDescriptions;


    var urlCustom = "http://" + location.host + ":8080/graphunit/" + url;
    console.log(urlCustom);
    var xmlHttpRequest = getXMLHttpRequest();
    xmlHttpRequest.onreadystatechange = function()
    {
        if (xmlHttpRequest.readyState === 4 && xmlHttpRequest.status === 200)
        {
            // alert("task has been saved");
        }
    };
    xmlHttpRequest.open("POST", urlCustom, false);
    xmlHttpRequest.send();
}

function getNumberOfInputs(elem) {
    var value = elem.value;
    if (value.trim() !== "" && value !== "0") {
        numberOfInputs = value;

        //now create widgets for the answer type and type description
        var inputInfoDiv = document.getElementById("inputInfo");

        removeDivChildren(inputInfoDiv);

        for (var i = 1; i <= value; i++) {
            //create the inputtype

            var parag1 = document.createElement("p");
            parag1.innerHTML = "Provide a shortname for the type of input #" + i + "(e.g. singleSelection)";

            var inputtype = document.createElement("input");
            inputtype.setAttribute("type", "text");
            inputtype.setAttribute("id", "inputShortName" + i);
            inputtype.setAttribute("class", "right");
            inputtype.setAttribute("size", "24");

            parag1.appendChild(inputtype);

            var parag2 = document.createElement("p");
            parag2.innerHTML = "Provide a short description of input-type #" + i;

            var inputtypedesc = document.createElement("textarea");
            inputtypedesc.setAttribute("rows", "2");
            inputtypedesc.setAttribute("cols", "26");
            inputtypedesc.setAttribute("id", "inputShortDescription" + i);
            inputtypedesc.setAttribute("class", "right");



            parag2.appendChild(inputtypedesc);


            inputInfoDiv.appendChild(parag1);
            inputInfoDiv.appendChild(parag2);

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