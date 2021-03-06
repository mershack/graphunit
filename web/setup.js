/**
 * This function makes a request to the setup servlet to display the demo page.
 * @returns {undefined}
 */


/*var datasets = [{name: "miserables", type: "json"}, {name: "imdb_small", type: "tsv"},
 {name: "imdb_large", type: "tsv"}, {name: "book_recommendation", type: "tsv"}]; */

var uploadedHtmls = [];
function getDemo() {
    uploadFiles();

    var studyname = document.getElementById("studyname").value;
    var command = "Demo";
    var demoPage = "StudyManager?studyname=" + studyname;

    //set the command variable
    document.getElementById("command").value = command;

    var $form = $("#setupForm");
    $.ajax({
        url: $form.attr("action"),
        data: $form.serialize(),
        type: $form.attr("method"),
        success: function() {
            //open the demo page in a new window when successful.
            window.open(demoPage);
        },
        error: function(xhr, ajaxOptions, thrownError) {
            alert(xhr.status);
            alert(thrownError);
        }
    });

    document.getElementById("command").value = "";
    return false;
}

function uploadFiles() {
    var studyname = document.getElementById("studyname").value;

    var thefiles = document.getElementById("thefiles").files;
    var url = "FileUpload?studyname=" + studyname;

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
    //alert("yay");

}

function setSubmitCommand() {
    //upload the files
    uploadFiles();
    document.getElementById("command").value = "Submit";
    return true;
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
           for(var i=1; i<=viewerCondsCounter; i++){
               var cond = document.getElementById("condition"+i).value;
               //alert(cond);
               viewerConds.push(cond);
           }
            
            var url = "studies/" + studyname + "/" + viewerConds[0];

            
            
             var myframe = document.createElement("iframe");
             myframe.setAttribute("id", "viewerFrame");
             myframe.setAttribute("src", url);
             myframe.setAttribute("style","display:none");
             
             var frameHolder = document.getElementById("frameForViewerTesting");
             frameHolder.appendChild(myframe);
             
             
              //var iframeContentWindow = document.getElementById("viewerFrame").contentWindow;
                //call the method 
             //   iframeContentWindow.window[inpInterfaceName](nodesArr);
               var iframe = document.getElementById("viewerFrame");
                
                if (typeof iframe.contentWindow.window[interfaceMethods[0]]!== "function") {
                    alert("Your visualization does not implement the following required methods -- "
                            + interfaceMethods[0]);
                }
             
        }
    };
    xmlHttpRequest.open("POST", url, false);
    xmlHttpRequest.send();
    
}

