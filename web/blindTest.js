var allBlindTestAnswers = "";
var numberOfStimuli = 8;
function checkAllColorBlindTestAnswers() {
    var answer = "";
    for (var i = 1; i <= numberOfStimuli; i++) {
        answer = document.getElementById("btAnswer" + i).value;

       // alert(answer);
        if (answer === "") {
            document.getElementById("btFeedback").style.display = "block";
            return false;
        }

        if (i === 1) {
            allBlindTestAnswers = answer;
        }
        else {
            allBlindTestAnswers += "," + answer;
        }
    }

    hideColorBlindnessTest(); //this method is in the userstudy.html class, so we are calling it now
    //alert(allBlindTestAnswers);
}

function getColorBlindTestAnswers() {
    return allBlindTestAnswers;
}
