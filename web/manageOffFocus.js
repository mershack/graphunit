/**
 * We will track the number of times the browser loses focus.
 * @returns {undefined}
 */


var totaloffFocusTime = 0;


var timeHidden = 0;
var timeVisible = 0;



(function() {
    var hidden = "hidden";

    // Standards:
    if (hidden in document)
        document.addEventListener("visibilitychange", onchange);
    else if ((hidden = "mozHidden") in document)
        document.addEventListener("mozvisibilitychange", onchange);
    else if ((hidden = "webkitHidden") in document)
        document.addEventListener("webkitvisibilitychange", onchange);
    else if ((hidden = "msHidden") in document)
        document.addEventListener("msvisibilitychange", onchange);
    // IE 9 and lower:
    else if ("onfocusin" in document)
        document.onfocusin = document.onfocusout = onchange;
    // All others:
    else
        window.onpageshow = window.onpagehide
                = window.onfocus = window.onblur = onchange;

    function onchange(evt) {
        var v = "visible", h = "hidden",
                evtMap = {
                    focus: v, focusin: v, pageshow: v, blur: h, focusout: h, pagehide: h
                };

        evt = evt || window.event;
        //alert(evt);
        var theEvent;

        if (evt.type in evtMap) {
            //alert(evtMap[evt.type] +" in map");
            //document.body.className = evtMap[evt.type];
            theEvent = evtMap[evt.type];
        }

        else {
            theEvent = this[hidden] ? "hidden" : "visible";           
        }
        //alert(this[hidden] ? "hidden" : "visible");
        // document.body.className = this[hidden] ? "hidden" : "visible";
        //}

        if (theEvent === "hidden") {
            var d = new Date();
            timeHidden = d.getTime();
        }
        else {
            var d = new Date();
            timeVisible = d.getTime();

            if (timeHidden > 0) {
                totaloffFocusTime += (timeVisible - timeHidden);
                alert("timeVisible: " + timeVisible + " timeHidden: " + timeHidden + " realTime: " + (timeVisible - timeHidden));
            }
        }


    }

    //set the initial state (but only if browser supports the Page Visibility API)
    /*  if (document[hidden] !== undefined)
     onchange({type: document[hidden] ? "blur" : "focus"});*/
})();





function getTotalOffFocusTime() {
    //alert("hey")
    return totaloffFocusTime;
}

function resetTotalOffFocusTime() {
    totaloffFocusTime = 0;
}










