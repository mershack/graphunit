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

function removeDivChildren(div) {
    var last;
    while (last = div.lastChild)
        div.removeChild(last);
}
//for loading tasks from the server and populate it.
function loadTasks(div, label, num, id) {
    //We will get all the tasks and populate the quantitative task list  
    var userid = document.getElementById("userid").value;
    var command = "getQuantitativeTasks";
    var url = "TaskInstancesCreator?command=" + command
            + "&userid=" + userid;
    var xmlHttpRequest = getXMLHttpRequest();
    xmlHttpRequest.onreadystatechange = function()
    {
        if (xmlHttpRequest.readyState === 4 && xmlHttpRequest.status === 200)
        {
            //create the task option box.                    
            createTasksSelectWidget(xmlHttpRequest.responseText, div, label, num, id);
        }
    };
    xmlHttpRequest.open("GET", url, true);
    xmlHttpRequest.send();
}

//function for creating the task option box.
function createTasksSelectWidget(tasklist,div, label, num, id) {
    var select = document.createElement("select");
    select.setAttribute("id", id+num);
    select.setAttribute("name", "quantitativeTasks");

    var opt1 = document.createElement("option");
    opt1.setAttribute("value", "");
    opt1.innerHTML = "Select a Task";
    select.appendChild(opt1);

    //now create the task options with groups
    var taskGroups = tasklist.split(":::::");
    for (var i = 0; i < taskGroups.length; i++) {
        var group = taskGroups[i].split("::::")[0];
        var tasks = taskGroups[i].split("::::")[1].split(":::");

        var optgroup = document.createElement("optgroup");
        optgroup.setAttribute("label", group);

        for (var j = 0; j < tasks.length; j++) {
            var option = document.createElement("option");
            option.setAttribute("value", tasks[j]);
            option.innerHTML = tasks[j];
            //append it to the option group
            optgroup.appendChild(option);
        }

        //append it to the group
        select.appendChild(optgroup);
    }


    //create a paragraph
    var p = document.createElement("p");
    //p.innerHTML = label;

    //append the select to the paragraph
    //p.appendChild(select);


    //var qnDiv = document.getElementById("qnDiv");
    div.appendChild(select);

}