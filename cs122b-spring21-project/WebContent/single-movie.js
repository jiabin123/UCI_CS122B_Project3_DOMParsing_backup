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

    console.log("handleResult: populating single movie into from resultData");

    // populate the star info h3
    // find the empty h3 body by id "star_info"
    let starInfoElement = jQuery("#movie_info");

    // append two html <p> created to the h3 body, which will refresh the page
    starInfoElement.append("<p>Movie Name: " + resultData[0]["movieTitle"] + "</p>" +
        "<p>Year: " + resultData[0]["movieYear"] + "</p>");

    // Populate the star table
    // Find the empty table body by id "movie_table_body"
    let movieTableBodyElement = jQuery("#movie_table_body");

    // Concatenate the html tags with resultData jsonObject to create table rows
    for (let i = 0; i < Math.min(10, resultData.length); i++) {
        let rowHTML = "";
        rowHTML += "<tr>";
        rowHTML += "<th>" + resultData[i]["movieTitle"] + "</th>";
        rowHTML += "<th>" + resultData[i]["movieYear"] + "</th>";
        rowHTML += "<th>" + resultData[i]["movieDirector"] + "</th>";
        rowHTML += "<th>" + resultData[i]["movieRating"] + "</th>";
        rowHTML += "<th>" + resultData[i]["genresResult"] + "</th>";
        rowHTML += "<th>";
        for ( var key in resultData[i]["starsObject"]){
            rowHTML += '<a href="single-star.html?search='+resultData[0]["pageObject"]["search"]
                +'&sort=' + resultData[0]["pageObject"]["sort"].toString()
                +'&page='+resultData[0]["pageObject"]["currentPage"].toString()
                +'&display='+resultData[0]["pageObject"]["displayNumber"].toString()
                +'&type='+resultData[0]["pageObject"]["type"]
                +'&content=' + resultData[0]["pageObject"]["content"]
                + '&starId=' + resultData[i]['starsObject'][key] + '">'+ key.toString() + '</a>' + ', ';

            // rowHTML +='<a href="single-star.html?id=' + resultData[i]['starsArray'][0][(j+100).toString()] + '">'
            //     + resultData[i]["starsArray"][0][j.toString()] +
            //     '</a>'
            //rowHTML += resultData[i]["starsArray"][0][j.toString()]
        }
        rowHTML += "</th>" + "</tr>";

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
    movieHTML += ' | ';
    movieHTML += '<span><button id="' + resultData[0]["movieTitle"] + '"onclick="addToCart(id)">Add to Cart</button>' + '</span>'+'<br/>';
    movielistBody.append(movieHTML);
    console.log(resultData[0]["movieTitle"]);
}

/**
 * Once this .js is loaded, following scripts will be executed by the browser\
 */

// Get id from URL
let movieId = getParameterByName('movieId');

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
    url: "api/single-movie?"+"search="+search+"&sort="+sort+"&page="+page+"&display="+display+"&type=" + type + "&content=" + content +"&movieId=" + movieId, // Setting request url, which is mapped by StarsServlet in Stars.java
    success: (resultData) => handleResult(resultData) // Setting callback function to handle data returned successfully by the SingleStarServlet
});


function addToCart(movieTitle){
    window.alert(movieTitle + " successfully added")
    console.log("add item to shopping cart")
    jQuery.ajax({
        dataType: "json", // Setting return data type
        method: "GET", // Setting request method
        url: "api/shopping-cart?movieTitle=" + movieTitle + "&sign=plus", // Setting request url, which is mapped by StarsServlet in Stars.java
        // success: (resultData) => handleAddResult(resultData) // Setting callback function to handle data returned successfully by the StarsServlet
    });
}