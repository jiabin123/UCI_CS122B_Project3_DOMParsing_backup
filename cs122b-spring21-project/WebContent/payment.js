let payment_form = $("#payment_form");

/**
 * Retrieve parameter from request URL, matching by parameter name
 * @param target String
 * @returns {*}
 */
function getParameterByName(target) {
    // Get request URL
    let url = window.location.href;
    // Encode target parameter name to url encoding
    target = target.replace(/[\[\]]/g, "\\$&");

    // Ues regular expression to find matched parameter value
    let regex = new RegExp("[?&]" + target + "(=([^&#]*)|&|#|$)"),
        results = regex.exec(url);
    //console.log("result: ", results);
    if (!results) return null;
    if (!results[2]) return '';

    // Return the decoded parameter value
    return decodeURIComponent(results[2].replace(/\+/g, " "));
}

let totalPrice = getParameterByName("totalPrice");

function display(){
    let bodyElement = $("#price");
    let html = "<h1>Total price of shopping cart: $" + totalPrice + "</h1>";
    bodyElement.append(html);
}
display();
/**
 * Handle the data returned by paymentServlet
 * @param resultDataString jsonObject
 */
function handlePaymentResult(resultDataString) {
    let resultDataJson = JSON.parse(resultDataString);

    console.log("handle paymentServlet response");

    // If payment succeeds, it will alert customer
    if (resultDataJson["status"] === "success") {
        alert("credit card validation succeed");
        console.log("credit card validation succeed");
        window.location.replace("confirmation-page.html");
    } else {
        alert("credit card validation failed");
        console.log("credit card validation succeed");
    }

}

/**
 * Submit the form content with POST method
 * @param formSubmitEvent
 */
function submitPaymentForm(formSubmitEvent) {
    console.log("submit payment form");
    /**
     * When users click the submit button, the browser will not direct
     * users to the url defined in HTML form. Instead, it will call this
     * event handler when the event is triggered.
     */
    formSubmitEvent.preventDefault();

    $.ajax(
        "api/payment", {
            method: "POST",
            // Serialize the login form to the data sent by POST request
            data: payment_form.serialize(),
            success: handlePaymentResult
        }
    );
}


payment_form.submit(submitPaymentForm)

