let login_form = $("#login_form");

/**
 * Handle the data returned by LoginServlet
 * @param resultDataString jsonObject
 */
function handleLoginResult(resultDataString) {
    let resultDataJson = JSON.parse(resultDataString);

    console.log("handle login response");
    console.log(resultDataJson);
    console.log(resultDataJson["status"]);

    //reCAPTCHA
    if(resultDataJson["reCAPTCHA"] == "true"){
        window.alert("check reCAPTCHA");
        return;
    }

    // If login succeeds, it will redirect the user to index.html
    // console.log(resultDataString["userType"] == "employee");
    // console.log(resultDataString["userType"]);
    if (resultDataJson["status"] == "success" && resultDataJson["userType"] == "user") {
        window.location.replace("index.html");
        console.log("deng lu cheng gong ");
    } else if(resultDataJson["status"] == "success" && resultDataJson["userType"] == "employee"){
        window.location.replace("_dashboard.html");
        console.log("deng lu cheng gong ");
    }
    else {
        // If login fails, the web page will display
        // error messages on <div> with id "login_error_message"
        console.log("show error message");
        console.log(resultDataJson["message"]);
        window.alert(resultDataJson["message"])
        $("#login_error_message").text(resultDataJson["message"]);
    }

}

/**
 * Submit the form content with POST method
 * @param formSubmitEvent
 */
function submitLoginForm(formSubmitEvent) {
    console.log("submit login form");
    /**
     * When users click the submit button, the browser will not direct
     * users to the url defined in HTML form. Instead, it will call this
     * event handler when the event is triggered.
     */
    formSubmitEvent.preventDefault();

    $.ajax(
        "api/login", {
            method: "POST",
            // Serialize the login form to the data sent by POST request
            data: login_form.serialize(),
            success: handleLoginResult
        }
    );
}

// Bind the submit action of the form to a handler function
login_form.submit(submitLoginForm);

