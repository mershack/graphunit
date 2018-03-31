var existingViewerDirectories = "";



function showViewerUploadForm() {
    document.getElementById("login-container").style.display = "none";
    document.getElementById("add-viewer-container").style.display = "block";
    loadExistingDirectoryNames();
}

function uploadViewerFiles() {
    var thefiles = document.getElementById("thefiles").files;
    var userid = document.getElementById("userid").value;
    var errorExists = false;

    var existingName = document.getElementById("existingViewerDirName").value;
    var newName = document.getElementById("newViewerDirName").value;
    var directoryName = "";

    //check the name of the directory.
    if (existingName.trim() === "" && newName.trim() === "") {
        errorExists = true;
        document.getElementById("dirNameError").style.display = "block";
    }
    else {
        if (existingName.trim() === "") {
            directoryName = newName.trim();
        }
        else {
            directoryName = existingName;
        }
    }

    //check the uploaded files             
    if (thefiles.length === 0) {
        errorExists = true;
        //show the errorNotices                
        document.getElementById("filesError").style.display = "block";
    }


    // do the submission if no error exists
    if (errorExists === false) {

        var url = "ViewerManager?command=add" + "&userid=" + userid
                + "&directoryName=" + directoryName;
        var formData = new FormData();


        //let's upload the viewer files.
        for (var i = 0; i < thefiles.length; i++)
            formData.append("File", thefiles[i]);

        var xmlHttpRequest = getXMLHttpRequest();
        xmlHttpRequest.onreadystatechange = function()
        {
            if (xmlHttpRequest.readyState === 4 && xmlHttpRequest.status === 200)
            {
                window.location = "add-viewer-success.html";
            }
        };
        xmlHttpRequest.open("POST", url, false);
        xmlHttpRequest.send(formData);
    }
}
function loadExistingDirectoryNames() {
    //send the command to the server to get the names of the existing directories.
    var userid = document.getElementById("userid").value;

    var url = "ViewerManager?command=getExistingDirectoryNames" + "&userid=" + userid;

    var xmlHttpRequest = getXMLHttpRequest();
    xmlHttpRequest.onreadystatechange = function()
    {
        if (xmlHttpRequest.readyState === 4 && xmlHttpRequest.status === 200)
        {
            var select = document.getElementById("existingViewerDirName");
            populateViewerDirectoryOptions(xmlHttpRequest.responseText, select);
        }
    };
    xmlHttpRequest.open("POST", url, false);
    xmlHttpRequest.send();
}
function populateViewerDirectoryOptions(optionsStr, select) {
    existingViewerDirectories = optionsStr;
    var options = optionsStr.split("::::");
    //var select = document.getElementById("existingViewerDirName");
    //create an option element for each of the options.

    if (options.length >= 1) {
        //make sure they select one of the options 
        var opt1 = document.createElement("option");
        opt1.setAttribute("value", "");
        opt1.setAttribute("selected", true);
        opt1.innerHTML = "Select One";


        select.appendChild(opt1);

        for (var i = 0; i < options.length; i++) {
            var option = document.createElement("option");
            option.setAttribute("value", options[i]);
            option.innerHTML = options[i];
            select.appendChild(option);
        }

    }





}


function populateViewerDirectoryOptions2(select) {
    var options = existingViewerDirectories.split("::::");
      
    if (options.length >= 1) {
        //make sure they select one of the options 
        var opt1 = document.createElement("option");
        opt1.setAttribute("value", "");
        opt1.setAttribute("selected", true);
        opt1.innerHTML = "Select One";


        select.appendChild(opt1);

        for (var i = 0; i < options.length; i++) {
            var option = document.createElement("option");
            option.setAttribute("value", options[i]);
            option.innerHTML = options[i];
            select.appendChild(option);
        }

    }





}