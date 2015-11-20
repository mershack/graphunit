/* global viewerId */


var uploadedHtmls = [];
var filesForCopy;
function uploadViewer() {
    var viewer = "Viewer" + viewerId;
    var thefiles = document.getElementById("thefiles").files;
    if (thefiles.length === 0) {
        thefiles = document.getElementById("editViewerFileInput").files;
    }
    var urlCustom = "http://" + location.host + ":8080/graphunit/ManageViewerFiles?viewerid=" + viewer + "&command=addViewerFiles";

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

function getViewer() {

    var viewer = "Viewer" + viewerId;
    var thefiles = document.getElementById("thefiles").files;
    var urlCustom = "http://" + location.host + ":8080/graphunit/ManageViewerFiles?viewerid=" + viewer + "&command=getViewerFiles";

    var xmlHttpRequest = getXMLHttpRequest();

    xmlHttpRequest.onreadystatechange = function ()
    {

        if (xmlHttpRequest.readyState === 4 && xmlHttpRequest.status === 200)
        {
            if (xmlHttpRequest.responseText !== "") {
                fileName = "" + xmlHttpRequest.responseText;
                files = xmlHttpRequest.responseText.replace(/(\r\n|\n|\r)/gm, "").split("::::");
                for (var i = 0; i < files.length - 1; i++) {
                    var link = "<a href='#' onclick=\"deleteViewer('" + files[i] + "')\">X</a>";
                    $('#filesEditViewer').append('<p id="fileNameViewer_' + files[i].split('.').join('').trim() + '">' + files[i] + "    " + link + '</p>');
                }
            }
        }
    };
    xmlHttpRequest.open("POST", urlCustom, false);
    xmlHttpRequest.send(null);
}
function getViewerFiles(id) {

    var viewer = "Viewer" + id;
    var thefiles = document.getElementById("thefiles").files;
    var urlCustom = "http://" + location.host + ":8080/graphunit/ManageViewerFiles?viewerid=" + viewer + "&command=getViewerFiles";

    var xmlHttpRequest = getXMLHttpRequest();

    xmlHttpRequest.onreadystatechange = function ()
    {

        if (xmlHttpRequest.readyState === 4 && xmlHttpRequest.status === 200)
        {
            if (xmlHttpRequest.responseText !== "") {
                if (xmlHttpRequest.responseText.length > 4) {
                    fileName = "" + xmlHttpRequest.responseText;
                    files = xmlHttpRequest.responseText.replace(/(\r\n|\n|\r)/gm, "").split("::::");
                    filesForCopy = files;
                }
            }
        }
    };
    xmlHttpRequest.open("POST", urlCustom, false);
    xmlHttpRequest.send(null);
}

function copyViewerFiles(original, newid) {

    var viewer = "Viewer" + newid;
    var thefiles = filesForCopy;
    if (typeof thefiles !== 'undefined') {

        var urlCustom = "http://" + location.host + ":8080/graphunit/ManageViewerFiles?viewerid=" + viewer + "&command=addViewerFiles";

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
    }
}

function deleteViewer(file) {
    var viewer = "Viewer" + viewerId;
    var urlCustom = "http://" + location.host + ":8080/graphunit/ManageViewerFiles?viewerid=" + viewer + "&fileNames=" + file + "&command=deleteViewerFiles";

    var xmlHttpRequest = getXMLHttpRequest();

    xmlHttpRequest.onreadystatechange = function ()
    {

        if (xmlHttpRequest.readyState === 4 && xmlHttpRequest.status === 200)
        {
            console.log('#fileNameViewer_' + file.split('.').join(''));
            $('#fileNameViewer_' + file.split('.').join('')).remove();
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
