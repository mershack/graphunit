/**
 * This function makes a request to the setup servlet to display the demo page.
 * @returns {undefined}
 */


/*var datasets = [{name: "miserables", type: "json"}, {name: "imdb_small", type: "tsv"},
 {name: "imdb_large", type: "tsv"}, {name: "book_recommendation", type: "tsv"}]; */

var uploadedHtmls = [];
var datasets = [];
var datasetsExt = [];
var systDatasetsSize = 0;
var userDatasetsSize = 0;
var viewerDirectoriesSize = 0;
var viewerDirectories = [];
var directoryFiles = [];
var theURL = "graphunit";
var existingStudyNames = [];
var originalStudyName = "";
function prepareNewSetupForm() {
    var actualStudyDiv = document.getElementById("qnDiv1");
    var preStudyDiv = document.getElementById("preStudyQnDiv1");
    var postStudyDiv = document.getElementById("postStudyQnDiv1");
    //load the task divs
    loadTasks(preStudyDiv, "label", "1", "preStudyTaskType");
    loadTasks(postStudyDiv, "label", "1", "postStudyTaskType");
    loadTasks(actualStudyDiv, "label", "1", "taskType");
    loadDatasets("1");
    getExistingDirectoryNamesAndHTMLFilenames();
    getExistingStudyNames();
    var userid = document.getElementById("userid").value;
    var command = "getStudyName";
    var url = "StudySetup?command=" + command + "&userid=" + userid;
    var xmlHttpRequest = getXMLHttpRequest();
    xmlHttpRequest.onreadystatechange = function()
    {
        if (xmlHttpRequest.readyState === 4 && xmlHttpRequest.status === 200)
        {                        //set the studyname variable
            // alert(xmlHttpRequest.responseText);
            document.getElementById("studyname").value = xmlHttpRequest.responseText;
        }
    };
    xmlHttpRequest.open("GET", url, true);
    xmlHttpRequest.send();
}


function getExistingStudyNames() {
    var userid = document.getElementById("userid").value;
    var command = "getAllStudyNames";
    var url = "StudySetup?command=" + command + "&userid=" + userid;
    var xmlHttpRequest = getXMLHttpRequest();
    xmlHttpRequest.onreadystatechange = function()
    {
        if (xmlHttpRequest.readyState === 4 && xmlHttpRequest.status === 200)
        {
            existingStudyNames = xmlHttpRequest.responseText.split("::");
        }
    };
    xmlHttpRequest.open("GET", url, true);
    xmlHttpRequest.send();
}


function prepareEditingCopySetupForm(mgmtCommand) {
    var actualStudyDiv = document.getElementById("qnDiv1");
    var preStudyDiv = document.getElementById("preStudyQnDiv1");
    var postStudyDiv = document.getElementById("postStudyQnDiv1");
    //load the task divs
    loadTasks(preStudyDiv, "label", "1", "preStudyTaskType");
    loadTasks(postStudyDiv, "label", "1", "postStudyTaskType");
    loadTasks(actualStudyDiv, "label", "1", "taskType");
    loadDatasets();
    getExistingDirectoryNamesAndHTMLFilenames();
    getExistingStudyNames();
    var command = "loadExistingStudyDetails";
    var userid = document.getElementById("userid").value;
    var url = "StudySetup?command=" + command + "&userid=" + userid;
    var xmlHttpRequest = getXMLHttpRequest();
    xmlHttpRequest.onreadystatechange = function()
    {
        if (xmlHttpRequest.readyState === 4 && xmlHttpRequest.status === 200)
        {                        //set the studyname variable

            var studydetailsFilename = xmlHttpRequest.responseText.trim();
            //  alert(studydetailsFilename);

            //alert(studydetailsFilename);

            d3.json(studydetailsFilename, function(error, study) {
                document.getElementById("studyname").value = study.name;
                document.getElementById("expType_vis").value = study.experimentType_vis;
//                document.getElementById("expType_ds").value = study.experimentType_ds;
                document.getElementById("trainingSize").value = study.trainingSize;
                //dataset
                var datasetElement = document.getElementById("dataset");
                document.getElementById("dataset").value = study.dataset;
                var datasetEvent = new Event('change');
                // Dispatch it.
                datasetElement.dispatchEvent(datasetEvent);
                //datasetFormat
                document.getElementById("datasetFormat").value = study.datasetFormat;
                //viewer-conditions
                var conditions = study.conditions;
                var cnt = 0;
                //if conditions are more than 2, create more rows of conditions

                if (conditions.length > 2) {
                    var additionalConds = conditions.length - 2;
                    // alert(additionalConds);
                    for (var i = 0; i < additionalConds; i++) {
                        addAnotherCondition()
                    }
                }




                conditions.forEach(function(condition) {
                    cnt++;
                    var url = condition.url;
                    var shortname = condition.shortname;
                    document.getElementById("conditionShortName" + cnt).value = shortname;
                    //note the url is in the form  dir/html-filename
                    var element = document.getElementById("existingViewerDirName" + cnt);
                    document.getElementById("existingViewerDirName" + cnt).value = url.split("/")[0].trim();
                    // Create a new 'change' event for the select options, 
                    // since its currently not being triggered automatically

                    var event = new Event('change');
                    // Dispatch it.
                    element.dispatchEvent(event);
                    document.getElementById("condition" + cnt).value = url.split("/")[1].trim();
                });
                //tasks
                var tasks = study.tasks;
                if (tasks.length > 1) {
                    var additionalTasks = tasks.length - 1;
                    for (var i = 0; i < additionalTasks; i++) {
                        newQuantitativeTaskDetails();
                    }
                }
                cnt = 0;
                setTimeout(function() {
                    tasks.forEach(function(task) {
                        cnt++;
                        // alert("taskType"+cnt);
                        document.getElementById("taskType" + cnt).value = task.question;
                        document.getElementById("taskSize" + cnt).value = task.size;
                        document.getElementById("taskTime" + cnt).value = task.time;
                    })
                }, 200);
                if (mgmtCommand.trim() === "copy") {
                    //copy to the name of the study name.                    
                    document.getElementById("studyname").value = study.name + "_copy";
                }

                originalStudyName = study.name;
            });
        }
    };
    xmlHttpRequest.open("GET", url, true);
    xmlHttpRequest.send();
}
function prepareCopyingSetupForm() {

}




function getDemo() {

//first check the form to make sure the form is complete

    if (checkStudyForm() === true) {
//set the command variable
        document.getElementById("command").value = "Demo";
        submitFormDetails();
    }
    return false;
}

function uploadFiles() {
    var studyname = document.getElementById("studyname").value;
    var thefiles = document.getElementById("thefiles").files;
    var userid = document.getElementById("userid").value;
    //alert(userid);

    var url = "FileUpload?studyname=" + studyname + "&userid=" + userid;
    var formData = new FormData();
    for (var i = 0; i < thefiles.length; i++)
        formData.append("File", thefiles[i]);
    var xmlHttpRequest = getXMLHttpRequest();
    xmlHttpRequest.onreadystatechange = function()
    {

        if (xmlHttpRequest.readyState === 4 && xmlHttpRequest.status === 200)
        {

        }
    };
    xmlHttpRequest.open("POST", url, false);
    xmlHttpRequest.send(formData);
}

function setSubmitCommand() {
    if (checkStudyForm() === true) {
        document.getElementById("command").value = "Submit";
        submitFormDetails();
    }
}

function submitFormDetails() {
    var command = document.getElementById("command").value;
    //get the viewer-conditions
    var conditions = "";
    // var viewerDirs = "";
    for (var i = 1; i <= viewerCondsCounter; i++) {
//alert(i);
        var cond = document.getElementById("condition" + i).value;
        var viewerDir = document.getElementById("existingViewerDirName" + i).value;
        var sn = document.getElementById("conditionShortName" + i).value;
        if (i === 1) {
            conditions = "conditions=" + viewerDir + "/" + cond + "&condition-shortnames=" + sn;
        }
        else {
            conditions += "&conditions=" + viewerDir + "/" + cond + "&condition-shortnames=" + sn;
        }
    }

//get the viewer dimensions
    var viewerDimensions = "";
    var viewerH = document.getElementById("viewerHeight").value;
    var viewerW = document.getElementById("viewerWidth").value;
    viewerDimensions = "viewerWidth=" + viewerW + "&viewerHeight=" + viewerH;
    //get the datasets
    var datasets = "";
    var datasetTypes = "";
    for (var i = 1; i <= datasetsCounter; i++) {

        var select = document.getElementById("dataset" + i);
        var dataset = select.value;
        if (i === 1) {
            datasets = "datasets=" + dataset;
        }
        else {
            datasets += "&datasets=" + dataset;
        }

//get type of dataset i.e. system/ user datasets
        var indx = select.selectedIndex;
        var selectedDataset = select.options[indx];
        var datasetType = selectedDataset.parentNode.label;
        if (i === 1) {
            datasetTypes = "datasetTypes=" + datasetType;
        }
        else {
            datasetTypes += "&datasetTypes=" + datasetType;
        }
    }


//datasetFormat
    var datasetFormats = "";
    for (var i = 1; i <= datasetsCounter; i++) {
        var datasetFormat = document.getElementById("datasetFormat" + i).value;
        if (i === 1) {
            datasetFormats = "datasetFormats=" + datasetFormat;
        }
        else {
            datasetFormats += "&datasetFormats=" + datasetFormat;
        }
    }

//experiment type vis
    var experimenttype_vis = "expType_vis=" + document.getElementById("expType_vis").value;
    //experiment type ds
    var experimenttype_ds = "expType_ds=Between"; // + document.getElementById("expType_ds").value;
    //quantitativeTasks=&quantitativeTaskSize=&quantitativeTaskTime=&trainingSize=
    //&awsAccessKey=&awsSecretKey=&hitTitle=&maxAssignments=&hitReward=&command=&studyname=study26&userid=user2 




    //get the tasks
    var tasks = "";
    for (var i = 1; i <= quantTaskCounter; i++) {
//get the task, the size and time.
        var task = document.getElementById("taskType" + i).value;
        var size = document.getElementById("taskSize" + i).value;
        var time = document.getElementById("taskTime" + i).value;
        if (i === 1) {
            tasks = "quantitativeTasks=" + task + "&quantitativeTaskSize=" + size + "&quantitativeTaskTime=" + time;
        }
        else {
            tasks += "&quantitativeTasks=" + task + "&quantitativeTaskSize=" + size + "&quantitativeTaskTime=" + time;
        }
    }

//now get the training size too.
    var trainingSize = "trainingSize=" + document.getElementById("trainingSize").value;
    //get the introduction
    var intros = "";
    var introCounter = document.getElementById("introFileCounter").value;
    for (var i = 1; i <= introCounter; i++) {
        //var url = document.getElementById("introductionFileURL" + i);
        var intro_url = document.getElementById("introductionFileURL" + i).value;
        var intro_cond = document.getElementById("introductionFileCondition" + i).value;
        if (i === 1) {
            intros = "introURLs=" + intro_url + "&introConds=" + intro_cond;
        }
        else {
            intros += "&introURLs=" + intro_url + "&introConds=" + intro_cond;
        }
    }

//get the standard tests
    var standardTests = "";
    var stCounter = document.getElementById("standardTestCounter").value;
    for (var i = 1; i <= stCounter; i++) {
        var st_url = document.getElementById("standardTestURL" + i).value;
        var respInterface = document.getElementById("standardTestUserResponseInterface" + i).value;
        var perfInterface = document.getElementById("standardTestUserPerformanceInterface" + i).value;

        if (i === 1) {
            standardTests = "standardTestURLs=" + st_url
                    + "&standardTestUserRespInterface=" + respInterface
                    + "&standardTestUserPerfInterface=" + perfInterface;
        }
        else if (i > 1) {
            standardTests += "&standardTestURLs=" + st_url
                    + "&standardTestUserRespInterface=" + respInterface
                    + "&standardTestUserPerfInterface=" + perfInterface;
        }
    }


    //get the prestudytasks
    var preStudyTasks = "";
    var preStudyTasksCounter = document.getElementById("numberOfPreStudyTasks").value;
    var pstCount = 0;
            
    for (var i = 1; i <= preStudyTasksCounter; i++) {
        //how do I get this from  
       
        var pst = document.getElementById("preStudyTaskType" + i).value;
        
        if (pst) {
            
            if (pstCount === 1) {
                 preStudyTasks = "preStudyTasks=" + pst;
            }
            else {
                preStudyTasks += "&preStudyTasks=" + pst;
            }
            
            pstCount++;
            
        }

        
    }

    //get the poststudytasks
    var postStudyTasks = "";
    var postStudyTasksCounter = document.getElementById("numberOfPostStudyTasks").value;
    var postStudyTaskCount = 0;
    for (var i = 1; i <= postStudyTasksCounter; i++) {
        //how do I get this from         
        var pst = document.getElementById("postStudyTaskType" + i).value;

        if (pst) {
            if (postStudyTaskCount === 0) {
                postStudyTasks = "postStudyTasks=" + pst;
            }
            else {
                postStudyTasks += "&postStudyTasks=" + pst;
            }
            
            postStudyTaskCount++;
        }
        
    }

//get the mechanical turk details
    var mechTechDetails = "";
    var awsAccessKey = document.getElementById("awsAccessKey").value;
    var awsSecretKey = document.getElementById("awsSecretKey").value;
    var maxAssignments = document.getElementById("maxAssignments").value;
    var hitReward = document.getElementById("hitReward").value;
    var hitTitle = document.getElementById("hitTitle").value;
    mechTechDetails = "&awsAccessKey=" + awsAccessKey
            + "&awsSecretKey=" + awsSecretKey
            + "&hitTitle=" + hitTitle
            + "&maxAssignments=" + maxAssignments
            + "&hitReward=" + hitReward;
    //now compose the whole url.
    var command = document.getElementById("command").value;
    var studyname = document.getElementById("studyname").value;
    var userid = document.getElementById("userid").value;
    //alert("datasets  " + datasets);

    //form the url now.
    var url = "StudySetup?command=" + command
            + "&studyname=" + studyname
            + "&userid=" + userid
            + "&" + conditions
            + "&" + viewerDimensions
            + "&" + datasets
            + "&" + datasetFormats
            + "&" + datasetTypes
            + "&" + experimenttype_vis
            + "&" + experimenttype_ds
            + "&" + tasks
            + "&" + intros
            + "&" + standardTests
            + "&" + preStudyTasks
            + "&" + postStudyTasks
            + "&" + trainingSize
            + "&" + mechTechDetails;
    var xmlHttpRequest = getXMLHttpRequest();
    xmlHttpRequest.onreadystatechange = function()
    {

        if (xmlHttpRequest.readyState === 4 && xmlHttpRequest.status === 200)
        {

            if (command === "Submit") {
                //finished
                window.location = "setup-completed.html";
            }
            else {
                //open a new window to show the demo
                var demoPage = "StudyManager?studyname=" + studyname
                        + "&userid=" + userid;
                window.open(demoPage, "_blank");
            }


        }
    };
    xmlHttpRequest.open("POST", url, true);
    xmlHttpRequest.send();
    // http://localhost:8080/graphunit/userstudy_setup.html?conditions=&condition-shortnames=cond1&conditions=&condition-shortnames=cond2&viewerWidth=860&viewerHeight=800&dataset=&expType=&quantitativeTasks=&quantitativeTaskSize=&quantitativeTaskTime=&trainingSize=
    //&awsAccessKey=&awsSecretKey=&hitTitle=&maxAssignments=&hitReward=&command=&studyname=study26&userid=user2


}

function checkStudyForm() {

    var errorExists = false;
    //0. Make sure the studyname is not empty.
    var studyname = document.getElementById("studyname").value;
    if (studyname.trim() === "") {
        document.getElementById("studyNameError").style.display = "block";
        errorExists = true;
    }
//if the command is copy or new, make sure the name chosen does not exist.
//if the command is edit and the name was changed then also check if the name already exists.
    var managementCommand = document.getElementById("managementCommand").value;
    if (managementCommand.trim() !== "edit" ||
            (managementCommand.trim() === "edit" && originalStudyName.trim() !== studyname.trim())
            ) {

        var exists = studyNameExists(studyname);
        if (exists === true) {
            document.getElementById("studyNameError").style.display = "block";
            errorExists = true;
        }
    }

//1. make sure the viewers and shortnames are not empty.
    for (var i = 1; i <= viewerCondsCounter; i++) {
//get the condition and get the shortname.
        var cond = document.getElementById("condition" + i).value;
        var sn = document.getElementById("conditionShortName" + i).value;
        if (cond.trim() === "" || sn.trim() === "") {
            document.getElementById("viewersError").style.display = "block";
            errorExists = true;
        }
        else {
            document.getElementById("viewersError").style.display = "none";
        }
    }

//2. Make sure there is at least one task and the tasks are not empty.
    for (var i = 1; i <= quantTaskCounter; i++) {
//get the task, the size and time.
        var task = document.getElementById("taskType" + i).value;
        var size = document.getElementById("taskSize" + i).value;
        var time = document.getElementById("taskTime" + i).value;
        //if any of them is emty, show the error page
        if (task.trim() === "" || size.trim() === "" || time.trim() === "") {
            document.getElementById("tasksError").style.display = "block";
            errorExists = true;
        }
        else {
            document.getElementById("tasksError").style.display = "none";
        }
    }
//make sure the viewer Dimensions are not empty
    var vh = document.getElementById("viewerHeight").value;
    var vw = document.getElementById("viewerWidth").value;
    if (vh.trim() === "" || vw.trim() === "") {
        document.getElementById("viewerDimensionsError").style.display = "block";
    }
    else {
        document.getElementById("viewerDimensionsError").style.display = "none";
    }

//make sure if there are datasets they or their formats is not empty.

    for (var i = 1; i <= datasetsCounter; i++) {
        var ds = document.getElementById("dataset" + i).value;
        var dsFormat = document.getElementById("datasetFormat" + i).value;
        if (ds.trim() !== "" && dsFormat.trim() === "") {
            document.getElementById("datasetError").style.display = "block";
            errorExists = true;
        }
        else {
            document.getElementById("datasetError").style.display = "none";
        }
    }


//make sure the experiment type of the visualizations is not empty
    var expt_vis = document.getElementById("expType_vis").value;
    if (expt_vis.trim() === "") {
        document.getElementById("experimentTypeError_vis").style.display = "block";
        errorExists = true;
    }
    else {
        document.getElementById("experimentTypeError_vis").style.display = "none";
    }



//make sure the experiment type of the datasets is not empty
//    var expt_ds = document.getElementById("expType_ds").value;
//    if (expt_ds.trim() === "") {
//        document.getElementById("experimentTypeError_ds").style.display = "block";
//    }
//    else {
//        document.getElementById("experimentTypeError_ds").style.display = "none";
//    }



//make sure number of training is not empty
    var nt = document.getElementById("trainingSize").value;
    if (nt.trim() === "") {
        document.getElementById("trainingSizeError").style.display = "block";
    }
    else {
        document.getElementById("trainingSizeError").style.display = "none";
    }

//check the introduction. NB. don't check the intro yet
/*
    var introFileCounter = document.getElementById("introFileCounter").value;
    for (var i = 1; i <= introFileCounter; i++) {
        var url = document.getElementById("introductionFileURL" + i).value;
        var url_cond = document.getElementById("introductionFileCondition" + i).value;
        if (url.trim() === "" || url_cond.trim() === "") {
            document.getElementById("introductionError").style.display = "block";
            errorExists = true;
        }
        else {
            document.getElementById("introductionError").style.display = "none";
        }
    }
    
    */






    if (errorExists === true) {
        return false;
    }





    return true;
}


function studyNameExists(studyname) {
//check if the studyname already exists.
    var exists = false;
    for (var i = 0; i < existingStudyNames.length; i++) {
        if (studyname.trim() === existingStudyNames[i].trim()) {
            exists = true;
            break;
        }
    }

    return exists;
}

function loadDatasets(val) {
//send command to get all the datasets

//We will get all the tasks and populate the quantitative task list  
    var userid = document.getElementById("userid").value;
    var command = "loadDatasets";
    var url = "DatasetManager?command=" + command
            + "&userid=" + userid;
    var xmlHttpRequest = getXMLHttpRequest();
    xmlHttpRequest.onreadystatechange = function()
    {
        if (xmlHttpRequest.readyState === 4 && xmlHttpRequest.status === 200)
        {
            var allDatasets = xmlHttpRequest.responseText;
            var sysDatasets = allDatasets.split("::::::")[0];
            var sysDatasetsArr = sysDatasets.split("::::");
            systDatasetsSize = sysDatasetsArr.length;
            for (var i = 0; i < sysDatasetsArr.length; i++) {
                datasets.push(sysDatasetsArr[i].split("::")[0]);
                datasetsExt.push(sysDatasetsArr[i].split("::")[1]);
            }


            var userDatasets = allDatasets.split("::::::")[1];
            var userDatasetsArr;
            //if there are user datasets, do this.
            if (userDatasets) {
                userDatasetsArr = userDatasets.split("::::");
                userDatasetsSize = userDatasetsArr.length;
                for (var i = 0; i < userDatasetsArr.length; i++) {
                    datasets.push(userDatasetsArr[i].split("::")[0]);
                    datasetsExt.push(userDatasetsArr[i].split("::")[1]);
                }
            }
            //populate the dataset options and extensions
            //var select = document.getElementById("dataset"+val);
            populateDatasetOptions(val);
        }
    };
    xmlHttpRequest.open("GET", url, true);
    xmlHttpRequest.send();
}

function datasetChanged(element) {

//get the value of this particular dataset too.


    var val = element.id.split("dataset")[1];
    var ds = element.value;
    //if it is a new dataset redirect the user to a page to create the dataset.
    /*  if (ds.trim() === "newDataset") {
     //create a new dataset
     window.open("add_dataset.html", "_blank");
     }
     else { */
    var indx = -1;
    for (var i = 0; i < datasets.length; i++) {
        if (datasets[i].trim() === ds.trim()) {
            indx = i;
            break;
        }
    }

    var dsFormat = document.getElementById("datasetFormat" + val);
    
    if (indx >= 0 && dsFormat) {
        populateDatasetFormats(val, indx);
    }
}

function createUpdateViewerDir() {
//open the window for users to create new viewer directories.
    window.open("add-viewers.html", "_blank");
}

function createUpdateTask() {
    window.open("task_creation.html", "_blank");
}

function createUpdateTaskInstance() {
    window.open("taskInstances_creation.html", "_blank");
}


function uploadUpdateDataset() {
    window.open("add_dataset.html", "_blank");
}





function populateDatasetFormats(val, indx) {

    var select = document.getElementById("datasetFormat" + val);
    //first remove the div's children
    removeDivChildren(select);
    //now add the div children.

    var exts = datasetsExt[indx];
    var extsArr = exts.split(",");
    var option1 = document.createElement("option");
    option1.setAttribute("value", "");
    option1.innerHTML = "Select One";
    option1.setAttribute("selected", true);
    var option;
    if (extsArr.length === 1) {
        option = document.createElement("option");
        option.setAttribute("value", extsArr[0].trim());
        option.innerHTML = extsArr[0].trim();
        select.appendChild(option);
    }
    else {
//add the select one option first.
        select.appendChild(option1);
        for (var i = 0; i < extsArr.length; i++) {
            option = document.createElement("option");
            option.setAttribute("value", extsArr[i].trim());
            option.innerHTML = extsArr[i].trim();
            select.appendChild(option);
        }
    }
}

function populateDatasetOptions(val) {

    var select = document.getElementById("dataset" + val);
    removeDivChildren(select);
    //create system optgroup
    var system_optgroup = document.createElement("optgroup");
    system_optgroup.setAttribute("label", "System_Datasets");
    //create user optgroup
    var userid = document.getElementById("userid").value;
    var user_optgroup = document.createElement("optgroup");
    user_optgroup.setAttribute("label", userid + "_Datasets");
    //create the first option 
    var option0 = document.createElement("option");
    option0.setAttribute("value", "");
    option0.innerHTML = "Select One";
    select.appendChild(option0);
    for (var i = 0; i < datasets.length; i++) {
//create an option and add it to select widget
        var option = document.createElement("option");
        if (i < systDatasetsSize) { // this is a system dataset
            option.setAttribute("value", datasets[i]);
            option.innerHTML = datasets[i];
            system_optgroup.appendChild(option);
            //select.appendChild(option);
        }
        else {

            option.setAttribute("value", datasets[i]);
            // ds = datasets[i];
            //var ds_indx = (datasets[i].indexOf("_")) + 1;
            //option.innerHTML = ds.substr(ds_indx);
            option.innerHTML = datasets[i];
            user_optgroup.appendChild(option);
        }
    }
    /*  var option = document.createElement("option");
     option.setAttribute("value", "newDataset");
     option.innerHTML = "Add a new Dataset";
     optgroup.appendChild(option);     */

    select.appendChild(system_optgroup);
    select.appendChild(user_optgroup);
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
//populate the conditions. if there are more than 2 conditions,  then make sure all of them are populated.
    for (var i = 1; i <= viewerCondsCounter; i++) {
        var cond = document.getElementById("condition" + i);
        populateConditionOptions(cond);
    }
}
/*
 function prepareAutoComplete() {
 $("[name='conditions']").autocomplete({
 source: uploadedHtmls
 });
 } */

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

function getExistingDirectoryNamesAndHTMLFilenames() {
//send the command to the server to get the names of the existing directories.
    var userid = document.getElementById("userid").value;
    var url = "ViewerManager?command=getExistingDirectoryNamesAndHTMLFilenames" + "&userid=" + userid;
    var xmlHttpRequest = getXMLHttpRequest();
    xmlHttpRequest.onreadystatechange = function()
    {
        if (xmlHttpRequest.readyState === 4 && xmlHttpRequest.status === 200)
        {
            for (var i = 1; i <= viewerCondsCounter; i++) {

                var select = document.getElementById("existingViewerDirName" + i);

                if (select) {
                    //viewer directory
                    removeDivChildren(select);
                    var dirNamesStr = xmlHttpRequest.responseText.split("::::::")[0];
                    var htmlFilesStr = xmlHttpRequest.responseText.split("::::::")[1];
                    populateViewerDirectoryOptions(dirNamesStr, select);
                    setDirNamesAndHTHMLFiles(dirNamesStr, htmlFilesStr);
                    //condition name
                    var condSelect = document.getElementById("condition" + i);
                    removeDivChildren(condSelect);
                    var option = document.createElement("option");
                    option.setAttribute("value", "");
                    option.innerHTML = "Select An Uploaded Web-page";
                    condSelect.appendChild(option);
                }

            }
        }
    };
    xmlHttpRequest.open("POST", url, true);
    xmlHttpRequest.send();
}

function setDirNamesAndHTHMLFiles(dirNamesStr, htmlFilesStr) {
//alert("over here");
    viewerDirectories = [];
    directoryFiles = [];
    var dirNamesSplit = dirNamesStr.split("::::");
    var htmlNamesSplit = htmlFilesStr.split("::::");
    for (var i = 0; i < dirNamesSplit.length; i++) {
        viewerDirectories.push(dirNamesSplit[i]);
    }

    for (var i = 0; i < htmlNamesSplit.length; i++) {
        directoryFiles.push(htmlNamesSplit[i]);
    }
//alert(directoryFiles);
}

function viewerDirectoryChanged(element, rowindex) {
//we will populate the html files of that row.

//first let's get the index of the viewer name.

    var vindex = -1;
    for (var i = 0; i < viewerDirectories.length; i++) {
        if (viewerDirectories[i].trim() === element.value) {
            vindex = i;
        }
    }

    var htmlFilesSplit = [];
    var select = document.getElementById("condition" + rowindex);
    removeDivChildren(select);
    if (vindex >= 0) {
        htmlFilesSplit = directoryFiles[vindex].split(",");
    }
    else {
        htmlFilesSplit.push("Select An Uploaded Web-page");
    }

    if (htmlFilesSplit.length === 1) {
        var option = document.createElement("option");
        var value = htmlFilesSplit[0];
        if (value.trim() === "no html files in director") {
            value = "";
        }

        option.setAttribute("value", value);
        option.innerHTML = htmlFilesSplit[0];
        //append option to select.
        select.appendChild(option);
    }
    else {
//first create a select one option before the other options follow
        var option0 = document.createElement("option");
        option0.setAttribute("value", "");
        option0.innerHTML = "Select An Uploaded Web-page";
        select.appendChild(option0);
        for (var i = 0; i < htmlFilesSplit.length; i++) {
//now create an option and add it to it.
            var option = document.createElement("option");
            var value = htmlFilesSplit[i];
            if (value.trim() === "no html files in director") {
                value = "";
            }

            option.setAttribute("value", value);
            option.innerHTML = htmlFilesSplit[i];
            //append option to select.
            select.appendChild(option);
        }
    }






}


/*
 function refreshViewerDirectories(){
 //get the viewer directories again, and update it.
 
 }
 */



function checkExistenceOfInterfaceMethods(elem) {
// alert(elem.value);
//first get the needed methods

    uploadFiles(); //temporal

    var url = "StudySetup?command=getTaskInterfaceMethods"
            + "&taskQuestion=" + elem.value;
    var xmlHttpRequest = getXMLHttpRequest();
    xmlHttpRequest.onreadystatechange = function()
    {
        if (xmlHttpRequest.readyState === 4 && xmlHttpRequest.status === 200)
        {
            //alert(xmlHttpRequest.responseText);
            var interfaceMethods = xmlHttpRequest.responseText.split("::");
            //if they are not empty, then check the conditions if they have the needed methods
            //now get the condition urls
            var studyname = document.getElementById("studyname").value;
            //create a viewer, load the file, and check if the functions exist
            //get the conditions
            var viewerConds = [];
            for (var i = 1; i <= viewerCondsCounter; i++) {
                var cond = document.getElementById("condition" + i).value;
                //alert(cond);
                viewerConds.push(cond);
            }

            var url = "studies/" + studyname + "/" + viewerConds[0];
            var myframe = document.createElement("iframe");
            myframe.setAttribute("id", "viewerFrame");
            myframe.setAttribute("src", url);
            myframe.setAttribute("style", "display:none");
            var frameHolder = document.getElementById("frameForViewerTesting");
            frameHolder.appendChild(myframe);
            //var iframeContentWindow = document.getElementById("viewerFrame").contentWindow;
            //call the method 
            //   iframeContentWindow.window[inpInterfaceName](nodesArr);
            var iframe = document.getElementById("viewerFrame");
            if (typeof iframe.contentWindow.window[interfaceMethods[0]] !== "function") {
                alert("Your visualization does not implement the following required methods -- "
                        + interfaceMethods[0]);
            }

        }
    };
    xmlHttpRequest.open("POST", url, false);
    xmlHttpRequest.send();
}

function showMTDetails() {
    document.getElementById("MTDetails").style.display = "block";
    //hide the show btn and show the hide button.
    document.getElementById("showMTDetailsBtn").style.display = "none";
    document.getElementById("hideMTDetailsBtn").style.display = "block";
}
function hideMTDetails() {
    document.getElementById("MTDetails").style.display = "none";
    //hide the "hide btn" and show the "show btn"
    document.getElementById("showMTDetailsBtn").style.display = "block";
    document.getElementById("hideMTDetailsBtn").style.display = "none";
}

/**
 * show the standard test div and hide the include standard test button.
 * @returns {nothing}
 */
function showStandardTestDiv() {
    document.getElementById("standardTestsDiv").style.display = "block";
    document.getElementById("inclStandardTestBtn").style.display = "none";
}

function deleteStandardTestIconClicked(val) {


    var standardTestCounter = document.getElementById("standardTestCounter").value;
    standardTestCounter = Number(standardTestCounter);
    /** If it is one, just empty the values in the text boxes and hide it 
     *  Otherwise, we will remove the row*/
    if (standardTestCounter === 1) {

//do the emptying now.
        document.getElementById("standardTestURL1").value = "";
        document.getElementById("standardTestUserPerformanceInterface1").value = "";
        document.getElementById("standardTestUserResponseInterface1").value = "";
        //display and hide
        document.getElementById("standardTestsDiv").style.display = "none";
        document.getElementById("inclStandardTestBtn").style.display = "block";
    }
    else {

//delete this condition
        var standardTestRow = document.getElementById("standardTestRow" + val);
        standardTestRow.parentNode.removeChild(standardTestRow);
        val = Number(val);
        for (var i = val + 1; i <= standardTestCounter; i++) {
            var tr = document.getElementById("standardTestRow" + i);
            tr.setAttribute("id", "standardTestRow" + (i - 1));
            var td = document.getElementById("standardTestPosition" + i);
            td.innerHTML = " " + (i - 1) + ". ";
            td.setAttribute("id", "standardTestPosition" + (i - 1));
            //change the id of the  image that will be used to delete this task.
            //and change the value that is passed the method.
            var img = document.getElementById("deleteStandardTestIcon" + (i));
            img.setAttribute("id", "deleteStandardTestIcon" + (i - 1));
            img.setAttribute("onclick", "deleteStandardTestIconClicked('" + (i - 1) + "')");
            //change the ID of the url, and the two interface names too.

            var td_url = document.getElementById("standardTestURL" + i + "TD");
            td_url.setAttribute("id", "standardTestURL" + (i - 1) + "TD");
            var input_url = document.getElementById("standardTestURL" + i);
            input_url.setAttribute("id", "standardTestURL" + (i - 1));
            //userResponse
            var td_userResp = document.getElementById("standardTestUserResponseInterface" + i + "TD");
            td_userResp.setAttribute("id", "standardTestUserResponseInterface" + (i - 1) + "TD");
            var input_userResp = document.getElementById("standardTestUserResponseInterface" + i);
            input_userResp.setAttribute("id", "standardTestUserResponseInterface" + (i - 1));
            //user performance
            var td_userPerf = document.getElementById("standardTestUserPerformanceInterface" + i + "TD");
            td_userPerf.setAttribute("id", "standardTestUserPerformanceInterface" + (i - 1) + "TD");
            var input_userPerf = document.getElementById("standardTestUserPerformanceInterface" + i);
            input_userPerf.setAttribute("id", "standardTestUserPerformanceInterface" + (i - 1));
        }

//reduce the standardTestCounter by 1.
//  datasetsCounter = datasetsCounter - 1;
        document.getElementById("standardTestCounter").value = standardTestCounter - 1;
    }
}

function AddAnotherStandardTest() {

    var standardTestCounter = document.getElementById("standardTestCounter").value;
    standardTestCounter = Number(standardTestCounter) + 1;
    var table = document.getElementById("standardTestsTable");
    var tr = document.createElement("tr");
    tr.setAttribute("id", "standardTestRow" + standardTestCounter);
    if (standardTestCounter % 2 === 0) {
        tr.setAttribute("class", "conditionNumberEven");
    }
    else {
        tr.setAttribute("class", "conditionNumberOdd");
    }


    var td0 = document.createElement("td");
    td0.setAttribute("id", "standardTestPosition" + standardTestCounter);
    td0.innerHTML = standardTestCounter + ". ";
    tr.appendChild(td0);
    //create the combobox td
    var td1 = document.createElement("td");
    td1.setAttribute("id", "standardTestURL" + standardTestCounter + "TD");
    var parag1 = document.createElement("p");
    var input1 = document.createElement("input");
    input1.setAttribute("type", "text");
    input1.setAttribute("value", "");
    input1.setAttribute("style", "min-width:100%;");
    input1.setAttribute("id", "standardTestURL" + standardTestCounter);
    parag1.appendChild(input1);
    td1.appendChild(parag1);
    tr.appendChild(td1);
    var td2 = document.createElement("td");
    td2.setAttribute("id", "standardTestUserResponseInterface" + standardTestCounter + "TD");
    var parag2 = document.createElement("p");
    var input2 = document.createElement("input");
    input2.setAttribute("type", "text");
    input2.setAttribute("value", "");
    input2.setAttribute("style", "min-width:100%");
    input2.setAttribute("id", "standardTestUserResponseInterface" + standardTestCounter);
    parag2.appendChild(input2);
    td2.appendChild(parag2);
    tr.appendChild(td2);
    var td3 = document.createElement("td");
    td3.setAttribute("id", "standardTestUserPerformanceInterface" + standardTestCounter + "TD");
    var parag3 = document.createElement("p");
    var input3 = document.createElement("input");
    input3.setAttribute("type", "text");
    input3.setAttribute("value", "");
    input3.setAttribute("style", "min-width:100%");
    input3.setAttribute("id", "standardTestUserPerformanceInterface" + standardTestCounter);
    parag3.appendChild(input3);
    td3.appendChild(parag3);
    tr.appendChild(td3);
    var td4 = document.createElement("td");
    td4.setAttribute("class", "deleteIconTD");
    var del_img = document.createElement("img");
    del_img.setAttribute("src", "images/delete-icon.jpg");
    del_img.setAttribute("class", "deleteIcon");
    del_img.setAttribute("title", "Delete Dataset");
    del_img.setAttribute("id", "deleteStandardTestIcon" + standardTestCounter);
    del_img.setAttribute("onclick", "deleteStandardTestIconClicked('" + standardTestCounter + "')");
    td4.appendChild(del_img);
    tr.appendChild(td4);
    table.appendChild(tr);
    document.getElementById("standardTestCounter").value = standardTestCounter;
}


function addAnotherIntroductionFile() {




    var introFileCounter = document.getElementById("introFileCounter").value;
    introFileCounter = Number(introFileCounter) + 1;
    // alert(introFileCounter);


    var table = document.getElementById("introductionFilesTable");
    var tr = document.createElement("tr");
    tr.setAttribute("id", "introductionFileRow" + introFileCounter);
    if (introFileCounter % 2 === 0) {
        tr.setAttribute("class", "conditionNumberEven");
    }
    else {
        tr.setAttribute("class", "conditionNumberOdd");
    }


    var td0 = document.createElement("td");
    td0.setAttribute("id", "introductionFilePosition" + introFileCounter);
    td0.innerHTML = introFileCounter + ". ";
    tr.appendChild(td0);
    //create the combobox td
    var td1 = document.createElement("td");
    td1.setAttribute("id", "introductionFileURL" + introFileCounter + "TD");
    var parag1 = document.createElement("p");
    var input1 = document.createElement("input");
    input1.setAttribute("type", "text");
    input1.setAttribute("value", "");
    input1.setAttribute("style", "min-width:100%;");
    input1.setAttribute("id", "introductionFileURL" + introFileCounter);
    parag1.appendChild(input1);
    td1.appendChild(parag1);
    tr.appendChild(td1);
    //create the introductionFileCondition url
    var td2 = document.createElement("td");
    td2.setAttribute("id", "introductionFileCondition" + introFileCounter + "TD");
    var parag2 = document.createElement("p");
    var select = document.createElement("select");
    select.setAttribute("id", "introductionFileCondition" + introFileCounter);
    select.setAttribute("style", "min-width:100%");
    //create the options
    var option1 = document.createElement("option");
    option1.setAttribute("value", "");
    option1.innerHTML = "Select One";
    var option2 = document.createElement("option");
    option2.setAttribute("value", "all");
    option2.innerHTML = "All visualization conditions";
    select.appendChild(option1);
    select.appendChild(option2);
    for (var i = 1; i <= viewerCondsCounter; i++) {
        var sn = document.getElementById("conditionShortName" + i).value;
        var option = document.createElement("option");
        option.setAttribute("value", sn);
        option.innerHTML = sn;
        select.appendChild(option);
    }







    parag2.appendChild(select);
    td2.appendChild(parag2);
    tr.appendChild(td2);
    var td4 = document.createElement("td");
    td4.setAttribute("class", "deleteIconTD");
    var del_img = document.createElement("img");
    del_img.setAttribute("src", "images/delete-icon.jpg");
    del_img.setAttribute("class", "deleteIcon");
    del_img.setAttribute("title", "Delete Dataset");
    del_img.setAttribute("id", "deleteIntroductionFileIcon" + introFileCounter);
    del_img.setAttribute("onclick", "deleteIntroductionFileIconClicked('" + introFileCounter + "')");
    td4.appendChild(del_img);
    tr.appendChild(td4);
    table.appendChild(tr);
    document.getElementById("introFileCounter").value = introFileCounter;
}

function deleteIntroductionFileIconClicked(val) {
    var introFileCounter = document.getElementById("introFileCounter").value;
    introFileCounter = Number(introFileCounter);
    //delete this condition
    var introFileRow = document.getElementById("introductionFileRow" + val);
    introFileRow.parentNode.removeChild(introFileRow);
    val = Number(val);
    for (var i = val + 1; i <= introFileCounter; i++) {
        var tr = document.getElementById("introductionFileRow" + i);
        tr.setAttribute("id", "introductionFileRow" + (i - 1));
        var td = document.getElementById("introductionFilePosition" + i);
        td.innerHTML = " " + (i - 1) + ". ";
        td.setAttribute("id", "introductionFilePosition" + (i - 1));
        //change the id of the  image that will be used to delete this task.
        //and change the value that is passed the method.
        var img = document.getElementById("deleteIntroductionFileIcon" + (i));
        img.setAttribute("id", "deleteIntroductionFileIcon" + (i - 1));
        img.setAttribute("onclick", "deleteIntroductionFileIconClicked('" + (i - 1) + "')");
        //change the ID of the url, and the two interface names too.

        var td_url = document.getElementById("introductionFileURL" + i + "TD");
        td_url.setAttribute("id", "introductionFileURL" + (i - 1) + "TD");
        var input_url = document.getElementById("introductionFileURL" + i);
        input_url.setAttribute("id", "introductionFileURL" + (i - 1));
        var td_condition = document.getElementById("introductionFileCondition" + i + "TD");
        td_condition.setAttribute("id", "introductionFileCondition" + (i - 1) + "TD");
        var select_condition = document.getElementById("introductionFileCondition" + i);
        select_condition.setAttribute("id", "introductionFileCondition" + (i - 1));
    }
//reduce the counter by 1.
    document.getElementById("introFileCounter").value = introFileCounter - 1;
}



/**
 * A method to populate the pre-study tasks table with one more task row.
 * @returns {nothing}
 */
function addAnotherPreStudyTask() {
    var numberOfTasks = document.getElementById("numberOfPreStudyTasks").value;
    numberOfTasks = Number(numberOfTasks) + 1;
    var qnDiv = document.createElement("div");
    qnDiv.setAttribute("id", "preStudyQnDiv" + numberOfTasks);
    var table = document.getElementById("preStudyTasksTable");
    var tr = document.createElement("tr");
    if (numberOfTasks % 2 === 0) {
        tr.setAttribute("class", "questionNumberEven");
    }
    else {
        tr.setAttribute("class", "questionNumberOdd");
    }

    tr.setAttribute("id", "preStudyTask" + numberOfTasks);
    /* For the arrows */
    var td1 = document.createElement("td");
    td1.setAttribute("class", "sortingArrowsTD");
    var upImg = document.createElement("img");
    upImg.setAttribute("src", "images/up-arrow-icon.png");
    upImg.setAttribute("class", "sortingArrows");
    upImg.setAttribute("alt", "move task up");
    upImg.setAttribute("title", "Move Task Up");
    upImg.setAttribute("onclick", "moveTaskUp('" + numberOfTasks + "');");
    var downImg = document.createElement("img");
    downImg.setAttribute("src", "images/down-arrow-icon.png");
    downImg.setAttribute("class", "sortingArrows");
    downImg.setAttribute("alt", "move task down");
    downImg.setAttribute("title", "Move Task Down");
    downImg.setAttribute("onclick", "moveTaskDown('" + numberOfTasks + "');");
    td1.appendChild(upImg);
    td1.appendChild(document.createElement("br"));
    td1.appendChild(downImg);
    //td2
    var td2 = document.createElement("td");
    //td4.setAttribute("id", "taskPosition" + quantTaskCounter);
    var parag2 = document.createElement("p");
    parag2.setAttribute("id", "taskPosition" + numberOfTasks);
    parag2.innerHTML = numberOfTasks + ".";
    td2.appendChild(parag2);
    //td3
    var td3 = document.createElement("td");
    td3.setAttribute("id", "preStudyQnDiv" + numberOfTasks + "TD");
    var task_div = document.createElement("td");
    task_div.setAttribute("id", "preStudyQnDiv" + numberOfTasks);
    td3.appendChild(task_div);
    //td4
    var td4 = document.createElement("td");
    td4.setAttribute("class", "deleteIconTD");
    var del_img = document.createElement("img");
    del_img.setAttribute("src", "images/delete-icon.jpg");
    del_img.setAttribute("class", "deleteIcon");
    del_img.setAttribute("title", "Delete Pre-Study Task");
    del_img.setAttribute("id", "deletePreStudyTaskIcon" + numberOfTasks);
    del_img.setAttribute("onclick", "deletePreStudyTaskIconClicked('" + numberOfTasks + "')");
    td4.appendChild(del_img);
    tr.appendChild(td1);
    tr.appendChild(td2);
    tr.appendChild(td3);
    tr.appendChild(td4);
    table.appendChild(tr);
    //now load the tasks into the tasks placeholder
    loadTasks(document.getElementById("preStudyQnDiv" + numberOfTasks), "Task Type " + numberOfTasks, numberOfTasks, "preStudyTaskType");
    //now increment the numberOfTasks Counter
    document.getElementById("numberOfPreStudyTasks").value = Number(numberOfTasks);
}


function deletePreStudyTaskIconClicked(value) {
    var id = "preStudyTask" + value;
    var taskrow = document.getElementById(id);
    taskrow.parentNode.removeChild(taskrow);
    var numberOfTasks = document.getElementById("numberOfPreStudyTasks").value;
    value = Number(value);
    for (var i = value + 1; i <= numberOfTasks; i++) {
        var tr = document.getElementById("preStudyTask" + i);
        tr.setAttribute("id", "preStudyTask" + (i - 1));
        var td_tp = document.getElementById("preStudyTaskPosition" + (i));
        td_tp.innerHTML = (i - 1) + ".";
        td_tp.setAttribute("id", "preStudyTaskPosition" + (i - 1));
        var td_qnDiv = document.getElementById("preStudyQnDiv" + i + "TD");
        td_qnDiv.setAttribute("id", "preStudyQnDiv" + (i - 1) + "TD");
        var qnDiv = document.getElementById("preStudyQnDiv" + i);
        qnDiv.setAttribute("id", "preStudyQnDiv" + (i - 1));
        var delIcon = document.getElementById("deletePreStudyTaskIcon" + i);
        delIcon.setAttribute("id", "deletePreStudyTaskIcon" + (i - 1));
        delIcon.setAttribute("onclick", "deletePreStudyTaskIconClicked('" + (i - 1) + "')");
    }

//decrement the number of tasks by 1
    document.getElementById("numberOfPreStudyTasks").value = numberOfTasks - 1;
}

/**
 * A method to populate the post-study tasks table with one more task row.
 * @returns {nothing}
 */
function addAnotherPostStudyTask() {
    var numberOfTasks = document.getElementById("numberOfPostStudyTasks").value;
    numberOfTasks = Number(numberOfTasks) + 1;
    var qnDiv = document.createElement("div");
    qnDiv.setAttribute("id", "postStudyQnDiv" + numberOfTasks);
    var table = document.getElementById("postStudyTasksTable");
    var tr = document.createElement("tr");
    if (numberOfTasks % 2 === 0) {
        tr.setAttribute("class", "questionNumberEven");
    }
    else {
        tr.setAttribute("class", "questionNumberOdd");
    }

    tr.setAttribute("id", "postStudyTask" + numberOfTasks);
    /* For the arrows */
    var td1 = document.createElement("td");
    td1.setAttribute("class", "sortingArrowsTD");
    var upImg = document.createElement("img");
    upImg.setAttribute("src", "images/up-arrow-icon.png");
    upImg.setAttribute("class", "sortingArrows");
    upImg.setAttribute("alt", "move task up");
    upImg.setAttribute("title", "Move Task Up");
    upImg.setAttribute("onclick", "moveTaskUp('" + numberOfTasks + "');");
    var downImg = document.createElement("img");
    downImg.setAttribute("src", "images/down-arrow-icon.png");
    downImg.setAttribute("class", "sortingArrows");
    downImg.setAttribute("alt", "move task down");
    downImg.setAttribute("title", "Move Task Down");
    downImg.setAttribute("onclick", "moveTaskDown('" + numberOfTasks + "');");
    td1.appendChild(upImg);
    td1.appendChild(document.createElement("br"));
    td1.appendChild(downImg);
    //td2
    var td2 = document.createElement("td");
    //td4.setAttribute("id", "taskPosition" + quantTaskCounter);
    var parag2 = document.createElement("p");
    parag2.setAttribute("id", "taskPosition" + numberOfTasks);
    parag2.innerHTML = numberOfTasks + ".";
    td2.appendChild(parag2);
    //td3
    var td3 = document.createElement("td");
    td3.setAttribute("id", "postStudyQnDiv" + numberOfTasks + "TD");
    var task_div = document.createElement("td");
    task_div.setAttribute("id", "postStudyQnDiv" + numberOfTasks);
    td3.appendChild(task_div);
    //td4
    var td4 = document.createElement("td");
    td4.setAttribute("class", "deleteIconTD");
    var del_img = document.createElement("img");
    del_img.setAttribute("src", "images/delete-icon.jpg");
    del_img.setAttribute("class", "deleteIcon");
    del_img.setAttribute("title", "Delete Post-Study Task");
    del_img.setAttribute("id", "deletePostStudyTaskIcon" + numberOfTasks);
    del_img.setAttribute("onclick", "deletePostStudyTaskIconClicked('" + numberOfTasks + "')");
    td4.appendChild(del_img);
    tr.appendChild(td1);
    tr.appendChild(td2);
    tr.appendChild(td3);
    tr.appendChild(td4);
    table.appendChild(tr);
    //now load the tasks into the tasks placeholder
    loadTasks(document.getElementById("postStudyQnDiv" + numberOfTasks), "Task Type " + numberOfTasks, numberOfTasks, "postStudyTaskType");
    //now increment the numberOfTasks Counter
    document.getElementById("numberOfPostStudyTasks").value = Number(numberOfTasks);
}


function deletePostStudyTaskIconClicked(value) {
    var id = "postStudyTask" + value;
    var taskrow = document.getElementById(id);
    taskrow.parentNode.removeChild(taskrow);
    var numberOfTasks = document.getElementById("numberOfPostStudyTasks").value;
    value = Number(value);
    for (var i = value + 1; i <= numberOfTasks; i++) {
        var tr = document.getElementById("postStudyTask" + i);
        tr.setAttribute("id", "postStudyTask" + (i - 1));
        var td_tp = document.getElementById("postStudyTaskPosition" + (i));
        td_tp.innerHTML = (i - 1) + ".";
        td_tp.setAttribute("id", "postStudyTaskPosition" + (i - 1));
        var td_qnDiv = document.getElementById("postStudyQnDiv" + i + "TD");
        td_qnDiv.setAttribute("id", "postStudyQnDiv" + (i - 1) + "TD");
        var qnDiv = document.getElementById("postStudyQnDiv" + i);
        qnDiv.setAttribute("id", "postStudyQnDiv" + (i - 1));
        var delIcon = document.getElementById("deletePostStudyTaskIcon" + i);
        delIcon.setAttribute("id", "deletePostStudyTaskIcon" + (i - 1));
        delIcon.setAttribute("onclick", "deletePostStudyTaskIconClicked('" + (i - 1) + "')");
    }

//decrement the number of tasks by 1
    document.getElementById("numberOfPostStudyTasks").value = numberOfTasks - 1;
}

