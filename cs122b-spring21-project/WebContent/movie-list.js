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
function getMultiContent(target){
    let url = window.location.href;
    let index = url.indexOf("&content=");
    var indices = [];
    let second = 0;
    for(var i=0; i<url.length;i++) {
        if (url[i] === "&") indices.push(i);
    }
    for(var j = 0; j < indices.length;j++){
        if (indices[j] == index){
            // first = j;
            second = indices[j+1];
        }
    }
    var content = url.slice(index+9,second);
    return content;
}
/**
 * Handles the data returned by the API, read the jsonObject and populate data into html elements
 * @param resultData jsonObject
 */
let movieTitle = "";
function handleStarResult(resultData) {
    // console.log("handleMovieResult: populating move table sorted by top 20 rating ");
    //
    // console.log(type);
    // console.log(content);
    // console.log(resultData.length);
    let starTableBodyElement = jQuery("#movie_table_body");


    for (let i = 0; i < resultData.length; i++) {
        //console.log(resultData)
        // Concatenate the html tags with resultData jsonObject
        movieTitle = resultData[i]["movieTitle"];
        let rowHTML = "";
        rowHTML += "<tr>" + "<td>";
        //single movie:==================================================
        rowHTML +='<a href="single-movie.html?search='+resultData[0]["pageObject"]["search"]
            +'&sort=' + resultData[0]["pageObject"]["sort"].toString()
            +'&page='+resultData[0]["pageObject"]["currentPage"].toString()
            +'&display='+resultData[0]["pageObject"]["displayNumber"].toString()
            +'&type='+resultData[0]["pageObject"]["type"]
            +'&content=' + resultData[0]["pageObject"]["content"]
            + '&movieId=' + resultData[i]['movieId']+ '">'
            + '<img src="picture/movieIcon.jpeg" width="120" height="90" alt= "Movie Info" >' +'</a>' + '<br/>';
        //movie title + movie year=============================================
        rowHTML +=resultData[i]["movieTitle"] + '      ' + resultData[i]["movieYear"] + '<br/>';
        // '<a href="single-movie.html?id=' + resultData[i]['movieId'] + '">' + '<img src="picture/movieIcon.jpeg" width="120" height="90" alt= "Movie Info" >' + '</a>' + '<br/>'
        //==================================================================
        rowHTML += resultData[i]["genresResult"] + '<br/>';
        //movie director + movie rating=====================================
        rowHTML += resultData[i]["movieDirector"] + "    rating:"+ resultData[i]["movieRating"] + '<br/>';
        //====================================================================
        rowHTML += '<button id="' + movieTitle + '"onclick="addToCart(id)">Add to Cart</button>' + '<br/>';
        // rowHTML += '<button class=" ">' + '<a href="shopping-cart.html?movieTitle=' + resultData[i]["movieTitle"] + '">' + 'Add to Cart</button>' + '<br/>';
        for(var key in resultData[i]["starsObject"]){
            // rowHTML += '<a href="single-star.html?id=' + resultData[i]['starsObject'][key] + '">' + key.toString() + ", ";

            rowHTML +='<a href="single-star.html?search='+resultData[0]["pageObject"]["search"]
                +'&sort=' + resultData[0]["pageObject"]["sort"].toString()
                +'&page='+resultData[0]["pageObject"]["currentPage"].toString()
                +'&display='+resultData[0]["pageObject"]["displayNumber"].toString()
                +'&type='+resultData[0]["pageObject"]["type"]
                +'&content=' + resultData[0]["pageObject"]["content"]
                + '&starId=' + resultData[i]['starsObject'][key] + '">'+ key.toString() + '</a>' + ", ";

        }
        rowHTML += '<br>' + "</td>" + "</tr>";

        // Append the row created to the table body, which will refresh the page
        starTableBodyElement.append(rowHTML);
    }

    //We are at page:
    jQuery("#currentPage").append(resultData[0]["pageObject"]["currentPage"])

    let pageBody = jQuery("#pages");
    let pageHTML = "";
    //add the previous page first=================================================
    pageHTML+= '<button class=" " id="prevButton" onclick='
        + 'location.href="movie-list.html?search='+resultData[0]["pageObject"]["search"]
        +'&sort=' + resultData[0]["pageObject"]["sort"].toString()
        +'&page='+ (parseInt(resultData[0]["pageObject"]["currentPage"]) - 1).toString()
        +'&&display='+resultData[0]["pageObject"]["displayNumber"].toString()
        +'&type='+resultData[0]["pageObject"]["type"]
        +'&content=' + resultData[0]["pageObject"]["content"] + '">Prev'
        + '</button>';


    //display pages=======================================================================
    for(let j = 0; j <parseInt(resultData[0]["pageObject"]["totalPage"]);j++ ){
        pageHTML += '<a href="movie-list.html?search='+resultData[0]["pageObject"]["search"]
            +'&sort=' + resultData[0]["pageObject"]["sort"].toString()
            +'&page='+(j+1).toString()
            +'&display='+resultData[0]["pageObject"]["displayNumber"].toString()
            +'&type='+resultData[0]["pageObject"]["type"]
            +'&content=' + resultData[0]["pageObject"]["content"] + '">' + (j+1).toString() +'</a>' +" | " ;

    }

    // next button
    pageHTML+= '<button class=" " id="nextButton" onclick='
        + 'location.href="movie-list.html?search='+resultData[0]["pageObject"]["search"]
        +'&sort=' + resultData[0]["pageObject"]["sort"].toString()
        +'&page='+ (parseInt(resultData[0]["pageObject"]["currentPage"]) + 1).toString()
        +'&&display='+resultData[0]["pageObject"]["displayNumber"].toString()
        +'&type='+resultData[0]["pageObject"]["type"]
        +'&content=' + resultData[0]["pageObject"]["content"] + '">Next'
        + '</button>';
    pageBody.append(pageHTML);
    //disable or enable the prev and next button
    if(resultData[0]["pageObject"]["currentPage"] == "1") {
        document.getElementById("prevButton").setAttribute('disabled', 'disabled');
        document.getElementById("prevLink")
    }else {
        document.getElementById("prevButton").removeAttribute('disabled');
    }
    if (resultData[0]["pageObject"]["currentPage"] == resultData[0]["pageObject"]["totalPage"]){
        document.getElementById("nextButton").setAttribute('disabled', 'disabled');
    }else{
        document.getElementById("nextButton").removeAttribute('disabled');
    }



    let displayBody = jQuery("#display-content");
    let displayHTML = "";
    //display 25 movies =======================================================================
    displayHTML += '<a href="movie-list.html?search='+resultData[0]["pageObject"]["search"]
        +'&sort=' + resultData[0]["pageObject"]["sort"].toString()
        +'&page='+resultData[0]["pageObject"]["currentPage"].toString()
        +'&display=25'
        +'&type='+resultData[0]["pageObject"]["type"]
        +'&content=' + resultData[0]["pageObject"]["content"] + '">25</a>';
    //display 50 movies =======================================================================
    displayHTML += '<a href="movie-list.html?search='+resultData[0]["pageObject"]["search"]
        +'&sort=' + resultData[0]["pageObject"]["sort"].toString()
        +'&page='+resultData[0]["pageObject"]["currentPage"].toString()
        +'&display=50'
        +'&type='+resultData[0]["pageObject"]["type"]
        +'&content=' + resultData[0]["pageObject"]["content"] + '">50</a>';
    //display 100 movies =======================================================================
    displayHTML += '<a href="movie-list.html?search='+resultData[0]["pageObject"]["search"]
        +'&sort=' + resultData[0]["pageObject"]["sort"].toString()
        +'&page='+resultData[0]["pageObject"]["currentPage"].toString()
        +'&display=100'
        +'&type='+resultData[0]["pageObject"]["type"]
        +'&content=' + resultData[0]["pageObject"]["content"] + '">100</a>';
    // console.log(displayHTML);
    displayBody.append(displayHTML);

    let sortBody = jQuery("#sort-content");
    let sortHTML = "";
    var sortDict = {
        1:"Title ASC, Rating ASC",
        2:"Title ASC, Rating DESC",
        3:"Title DESC, Rating ASC",
        4:"Title DESC, Rating DESC",
        5:"Rating ASC, Title ASC",
        6:"Rating ASC, Title DESC",
        7:"Rating DESC, Title ASC",
        8:"Rating DESC, Title DESC"
    };
    for (var key in sortDict){
        sortHTML +='<a href="movie-list.html?search='+resultData[0]["pageObject"]["search"]
            +'&sort=' + key
            +'&page='+resultData[0]["pageObject"]["currentPage"].toString()
            +'&display=' + resultData[0]["pageObject"]["displayNumber"].toString()
            +'&type='+resultData[0]["pageObject"]["type"]
            +'&content=' + resultData[0]["pageObject"]["content"] + '">' + sortDict[key] + '</a>';
    }
    sortBody.append(sortHTML);

}
let content = "";
// get parameter from url
let type = getParameterByName("type");

if (type == "multiSearch"){
    content = getMultiContent("content");
}else {
    content = getParameterByName("content");
}
let search = getParameterByName("search");
let page = getParameterByName("page");
let display = getParameterByName("display");
let sort = getParameterByName("sort")
console.log(type,content);
/**
 * Once this .js is loaded, following scripts will be executed by the browser
 */

// Makes the HTTP GET request and registers on success callback function handleStarResult
// jQuery.ajax({
//     dataType: "json", // Setting return data type
//     method: "GET", // Setting request method
//     url: "api/movies", // Setting request url, which is mapped by StarsServlet in Stars.java
//     success: (resultData) => handleStarResult(resultData) // Setting callback function to handle data returned successfully by the StarsServlet
// });

function addToCart(movieTitle){
    window.alert(movieTitle + " successfully added")
    console.log("add item to shopping cart")
    jQuery.ajax({
        dataType: "json", // Setting return data type
        method: "GET", // Setting request method
        url: "api/shopping-cart?movieTitle=" + movieTitle +"&sign=plus", // Setting request url, which is mapped by StarsServlet in Stars.java
        // success: (resultData) => handleAddResult(resultData) // Setting callback function to handle data returned successfully by the StarsServlet
    });
}

function chooseCallmethod(){
    // if search == 1, means now we are using search, send search parameter to back-end
    if(search == '1'){
        jQuery.ajax({
            dataType: "json", // Setting return data type
            method: "GET", // Setting request method
            url: "api/movies?search=1&sort="+sort+"&page="+page+"&display="+display+"&type=" + type + "&content=" + content, // Setting request url, which is mapped by StarsServlet in Stars.java
            success: (resultData) => handleStarResult(resultData) // Setting callback function to handle data returned successfully by the StarsServlet
        });
    }else {
        // send browse parameter to back end
        jQuery.ajax({
            dataType: "json", // Setting return data type
            method: "GET", // Setting request method
            url: "api/movies?search=0&sort="+sort+"&page="+page+"&display="+display+"&type=" + type + "&content=" + content, // Setting request url, which is mapped by StarsServlet in Stars.java
            success: (resultData) => handleStarResult(resultData) // Setting callback function to handle data returned successfully by the StarsServlet
        });
    }
}

chooseCallmethod();