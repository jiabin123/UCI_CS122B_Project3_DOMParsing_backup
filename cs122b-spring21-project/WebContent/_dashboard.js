
function collapse(){
    //collapse method for metadata
    var coll = document.getElementsByClassName("collapsible");
    var i;
    for (i = 0; i < coll.length; i++) {
        coll[i].addEventListener("click", function() {
            this.classList.toggle("active");
            var content = this.nextElementSibling;
            if (content.style.display === "block") {
                content.style.display = "none";
            } else {
                content.style.display = "block";
            }
        });
    }
}

collapse()

let add_star_from = $("#add_star_form");
let add_movie_from = $("#add_movie_form");

//data structure of resultDataString: ["answer",metadata], metadata => {tableName : each 6 rows}
function handleAddStarResult(resultDataString){
    window.alert(resultDataString[0]);

}

function handleAddMovieResult(resultDataString){
    window.alert(resultDataString[0]);
}
function submitAddStarForm(formSubmitEvent){
    formSubmitEvent.preventDefault();
    $.ajax(
        "api/_dashboard", {
            method: "GET",
            // Serialize the login form to the data sent by POST request
            data: add_star_from.serialize(),
            success: handleAddStarResult
        }
    );
}
function submitAddMovieForm(formSubmitEvent){
    formSubmitEvent.preventDefault();
    $.ajax(
        "api/_dashboard", {
            method: "GET",
            // Serialize the login form to the data sent by POST request
            data: add_movie_from.serialize(),
            success: handleAddMovieResult
        }
    );
}
function handleMetaDataResult(resultDataString){
    //adding metadata
    let metadataBodyElement = jQuery("#metadata");
    let rowHTML = "";

    for (var table in resultDataString[1]){
        rowHTML += '<table style="width:100%" border="1">';
        rowHTML += '<p>'+table+' table:</p>';
        rowHTML += '<tr>\n' +
            '    <th>Field</th>\n' +
            '    <th>Type</th> \n' +
            '    <th>Null</th>\n' +
            '    <th>Key</th>\n' +
            '    <th>Default</th>\n' +
            '    <th>Extra</th>\n' +
            '  </tr>';
        for (let i=0; i<resultDataString[1][table].length; i++){
            console.log(resultDataString[1][table][i]);
            rowHTML += '<tr>\n' +
                '    <td>'+resultDataString[1][table][i]["Field"]+'</td>\n' +
                '    <td>'+resultDataString[1][table][i]["Type"]+'</td>\n' +
                '    <td>'+resultDataString[1][table][i]["Null"]+'</td>\n' +
                '    <td>'+resultDataString[1][table][i]["Key"]+'</td>\n' +
                '    <td>'+resultDataString[1][table][i]["Default"]+'</td>\n' +
                '    <td>'+resultDataString[1][table][i]["Extra"]+'</td>\n' +
                '  </tr>';
        }
        rowHTML += '</table>';
    }

    metadataBodyElement.append(rowHTML);
}
jQuery.ajax({
    dataType: "json",  // Setting return data type
    method: "GET",// Setting request method
    url: "api/_dashboard", // Setting request url, which is mapped by StarsServlet in Stars.java
    success: (resultData) => handleMetaDataResult(resultData) // Setting callback function to handle data returned successfully by the SingleStarServlet
});


add_star_from.submit(submitAddStarForm)
add_movie_from.submit(submitAddMovieForm)