/* global datasetId */

/**
 * This function makes a request to the setup servlet to display the demo page.
 * @returns {undefined}
 */


/*var datasets = [{name: "miserables", type: "json"}, {name: "imdb_small", type: "tsv"},
 {name: "imdb_large", type: "tsv"}, {name: "book_recommendation", type: "tsv"}]; */

var uploadedHtmls = [];
var fileName;
function uploadDataset() {
    var dataset = "Dataset" + datasetId;
    var thefiles = document.getElementById("thedataset").files;
    if(thefiles.length === 0){
        thefiles = document.getElementById("editDatasetFileInput").files;
    }
    var urlCustom = "http://" + location.host + ":8080/graphunit/ManageDatasetFiles?datasetid=" + dataset + "&command=addDatasetFiles";

    var formData = new FormData();
    for (var i = 0; i < thefiles.length; i++)
        formData.append("File", thefiles[i]);

    var xmlHttpRequest = getXMLHttpRequest();

    xmlHttpRequest.onreadystatechange = function ()
    {

        if (xmlHttpRequest.readyState === 4 && xmlHttpRequest.status === 200)
        {

        }
    };
    xmlHttpRequest.open("POST", urlCustom, false);
    xmlHttpRequest.send(formData);
//    alert("yay");
}

function getDataset() {

    var dataset = "Dataset" + datasetId;
    var thefiles = document.getElementById("thedataset").files;
    var urlCustom = "http://" + location.host + ":8080/graphunit/ManageDatasetFiles?datasetid=" + dataset + "&command=getDatasetFiles";

    var xmlHttpRequest = getXMLHttpRequest();

    xmlHttpRequest.onreadystatechange = function ()
    {

        if (xmlHttpRequest.readyState === 4 && xmlHttpRequest.status === 200)
        {
            if (xmlHttpRequest.responseText !== "") {
                fileName = "" + xmlHttpRequest.responseText;
                var link = '<a href="#" onclick="deleteDataset()">X</a>';
                $('#fileNameDataset').html(xmlHttpRequest.responseText + link);
                $('#editDatasetFileInput').hide();
            } else {
                $('#fileNameDataset').html(xmlHttpRequest.responseText);
                $('#editDatasetFileInput').show();
            }
        }
    };
    xmlHttpRequest.open("POST", urlCustom, false);
    xmlHttpRequest.send(null);
}


function deleteDataset() {
    var dataset = "Dataset" + datasetId;
    var urlCustom = "http://" + location.host + ":8080/graphunit/ManageDatasetFiles?datasetid=" + dataset + "&fileNames=" + fileName + "&command=deleteDatasetFiles";

    var xmlHttpRequest = getXMLHttpRequest();

    xmlHttpRequest.onreadystatechange = function ()
    {

        if (xmlHttpRequest.readyState === 4 && xmlHttpRequest.status === 200)
        {
            $('#fileNameDataset').html(xmlHttpRequest.responseText);
            $('#editDatasetFileInput').show();
        }
    };
    xmlHttpRequest.open("POST", urlCustom, false);
    xmlHttpRequest.send(null);

}
function getXMLHttpRequest() {
    var xmlHttpReq;
    // to create XMLHttpRequest object in non-Microsoft browsers  
    if (window.XMLHttpRequest) {
        xmlHttpReq = new XMLHttpRequest();
    } else if (window.ActiveXObject) {
        try {
            //to create XMLHttpRequest object in later versions of Internet Explorer  
            xmlHttpReq = new ActiveXObject("Msxml2.XMLHTTP");
        } catch (exp1) {
            try {
                //to create XMLHttpRequest object in later versions of Internet Explorer  
                xmlHttpReq = new ActiveXObject("Microsoft.XMLHTTP");
            } catch (exp2) {
                //xmlHttpReq = false;  
                alert("Exception in getXMLHttpRequest()!");
            }
        }
    }
    return xmlHttpReq;
}
