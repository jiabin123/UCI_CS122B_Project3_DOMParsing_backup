let search_from = $("#search_form");
let multisearch_form = $("#multisearch_form");


function handleSearchResult(resultDataString){
    console.log(resultDataString[0]["type"]);
    console.log(resultDataString[0]["content"]);
    window.location.href = "movie-list.html?search=1&sort=1&page=1&display=50&type=" + resultDataString[0]["type"] + "&content=" + resultDataString[0]["content"];
}

function handleMultiSearchResult(resultDataString){
    console.log(resultDataString[0]["type"]);
    console.log(resultDataString[0]["content"]);
    window.location.href = "movie-list.html?search=1&sort=1&page=1&display=50&type=" + resultDataString[0]["type"] + "&content=" + resultDataString[0]["content"];
}
// function handleMultiSearchResult(resultDataString){
//     console.log(resultDataString[0]["type"]);
//     console.log(resultDataString[0]["content"]);
//     window.location.href = "movie-list.html?search=1&sort=1&page=1&display=50&type=" + resultDataString[0]["type"] + "&content=" + resultDataString[0]["content"];
// }
function handleGenresResult(resultData){
    // brose movie by genres, setting href for each type
    let genresBodyElement = jQuery("#genresMenu");
    let rowHTML = "";
    for(let i=0; i<resultData.length; i++){
        rowHTML+= '<a href="movie-list.html?search=0&sort=1&page=1&display=50&type=genres&content=' + resultData[i]["genres"] + '">'
            + resultData[i]["genres"] +
            '</a>';
        rowHTML += '|';
    }
    genresBodyElement.append(rowHTML);

    // brose movie by title, setting href for each type
    let titleBodyElement = jQuery("#titleMenu");
    rowHTML = "";
    var titles = ["0","1","2","3","4","5","6","7","8","9"
        ,"A","B","C","D","E","F","G","H","I","J","K","L"
        ,"M","N","O","P","Q","R","S","T","U","V","W","X","Y","Z", "*"];

    for(let i=0; i<titles.length; i++){
        rowHTML+= '<a href="movie-list.html?search=0&sort=1&page=1&display=50&type=title&content=' + titles[i] + '">'
            + titles[i] +
            '</a>';
        rowHTML += '|';
    }
    titleBodyElement.append(rowHTML);

    // var lis = document.getElementsByTagName("a");
    // for (var i = 0; i < lis.length; i++) {
    //     var li = lis[i];
    //     li.addEventListener("mouseover", function() {
    //         this.getElementsByTagName("a")[0].style.color = "#8080ff";
    //     });
    //     li.addEventListener("mouseout", function() {
    //         this.getElementsByTagName("a")[0].style.color = "#000000";
    //     });
    // }
    // var sheet = window.document.styleSheets[0];
    // sheet.insertRule('#a:hover { color: #8080ff; }', sheet.cssRules.length);
}
function submitSearchForm(formSubmitEvent){
    console.log("submit search form");
    /**
     * When users click the submit button, the browser will not direct
     * users to the url defined in HTML form. Instead, it will call this
     * event handler when the event is triggered.
     */
    formSubmitEvent.preventDefault();

    // send the request to servlet for jdbc search
    $.ajax(
        "api/search", {
            method: "GET",
            // Serialize the login form to the data sent by POST request
            data: search_from.serialize(),
            success: handleSearchResult
        }
    );
}
function submitMultiSearchForm(formSubmitEvent){
    formSubmitEvent.preventDefault();
    $.ajax(
        "api/search", {
            method: "GET",
            // Serialize the login form to the data sent by POST request
            data: multisearch_form.serialize(),
            success: handleMultiSearchResult
        }
    );

}

jQuery.ajax({
    dataType: "json",  // Setting return data type
    method: "GET",// Setting request method
    url: "api/browse", // Setting request url, which is mapped by StarsServlet in Stars.java
    success: (resultData) => handleGenresResult(resultData) // Setting callback function to handle data returned successfully by the SingleStarServlet
});

multisearch_form.submit(submitMultiSearchForm)
search_from.submit(submitSearchForm);