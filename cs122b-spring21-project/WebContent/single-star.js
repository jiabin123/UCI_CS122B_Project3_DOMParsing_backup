/**
 * This example is following frontend and backend separation.
 *
 * Before this .js is loaded, the html skeleton is created.
 *
 * This .js performs three steps:
 *      1. Get parameter from request URL so it know which id to look for
 *      2. Use jQuery to talk to backend API to get the json data.
 *      3. Populate the data to correct html elements.
 */


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
    if (!results) return null;
    if (!results[2]) return '';

    // Return the decoded parameter value
    return decodeURIComponent(results[2].replace(/\+/g, " "));
}

/**
 * Handles the data returned by the API, read the jsonObject and populate data into html elements
 * @param resultData jsonObject
 */

function handleResult(resultData) {

    console.log("handleResult: populating star info from resultData");

    // populate the star info h3
    // find the empty h3 body by id "star_info"
    let starInfoElement = jQuery("#star_info");

    // append two html <p> created to the h3 body, which will refresh the page
    starInfoElement.append("<p>Star Name: " + resultData[0]["starName"] + "</p>" +
        "<p>Date Of Birth: " + resultData[0]["starDob"] + "</p>");

    console.log("handleResult: populating movie table from resultData");

    // Populate the star table
    // Find the empty table body by id "movie_table_body"
    let movieTableBodyElement = jQuery("#movie_table_body");

    // Concatenate the html tags with resultData jsonObject to create table rows
    for (let i = 0; i <resultData.length; i++) {
        let rowHTML = "";
        rowHTML += "<tr>" + "<th>";

        // rowHTML += '<a href="single-movie.html?id=' + resultData[i]['movie_id'] + '">'
        //     + resultData[i]["movie_title"] +
        //     '</a>'
        rowHTML += '<a href="single-movie.html?search='+resultData[0]["pageObject"]["search"]
            +'&sort=' + resultData[0]["pageObject"]["sort"].toString()
            +'&page='+resultData[0]["pageObject"]["currentPage"].toString()
            +'&display='+resultData[0]["pageObject"]["displayNumber"].toString()
            +'&type='+resultData[0]["pageObject"]["type"]
            +'&content=' + resultData[0]["pageObject"]["content"]
            + '&movieId=' + resultData[i]['movieId']+ '">' + resultData[i]["movieTitle"] + '</a>';

        rowHTML += "</th>";
        rowHTML += "<th>" + resultData[i]["movieYear"] + "</th>";
        rowHTML += "<th>" + resultData[i]["movieDirector"] + "</th>";
        rowHTML += "</tr>";

        // Append the row created to the table body, which will refresh the page
        movieTableBodyElement.append(rowHTML);
    }

    let movielistBody = jQuery("#movie-list");
    let movieHTML = "";
    // <a href="movie-list.html">Movies</a>
    movieHTML += '<a href="movie-list.html?search='+resultData[0]["pageObject"]["search"]
        +'&sort=' + resultData[0]["pageObject"]["sort"].toString()
        +'&page='+resultData[0]["pageObject"]["currentPage"].toString()
        +'&display='+resultData[0]["pageObject"]["displayNumber"].toString()
        +'&type='+resultData[0]["pageObject"]["type"]
        +'&content=' + resultData[0]["pageObject"]["content"] +'">Movies</a>';
    // console.log(movieHTML);
    movielistBody.append(movieHTML);
}

/**
 * Once this .js is loaded, following scripts will be executed by the browser\
 */

// Get id from URL
let starId = getParameterByName('starId');

let type = getParameterByName("type");
let content = getParameterByName("content");
let search = getParameterByName("search");
let page = getParameterByName("page");
let display = getParameterByName("display");
let sort = getParameterByName("sort")

// Makes the HTTP GET request and registers on success callback function handleResult
jQuery.ajax({
    dataType: "json",  // Setting return data type
    method: "GET",// Setting request method
    url: "api/single-star?" + "search="+search+"&sort="+sort+"&page="+page+"&display="+display+"&type=" + type + "&content=" + content +"&starId=" + starId, // Setting request url, which is mapped by StarsServlet in Stars.java
    success: (resultData) => handleResult(resultData) // Setting callback function to handle data returned successfully by the SingleStarServlet
});