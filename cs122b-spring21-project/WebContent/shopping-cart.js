
// function goBack() {
//     window.history.go(-1);
// }

var totalPrice = 0;
function shoppingResult(resultData){
    let starTableBodyElement = jQuery("#shopping-items");
    totalPrice = 0;
    for( var key in resultData){
        if(resultData.hasOwnProperty(key) && key != null){
            let quantity = resultData[key]
            totalPrice += 3*quantity;
            let plus = key + "+";
            let minus = key + "-";
            let rowHTML = "";
            console.log(key);
            console.log(plus);
            console.log(minus);
            rowHTML += "<tr class=\"trclass\">";
            rowHTML += "<td class=\"item\">" + key + "</td>"
                // + "<td class=\"number\"><span class=\"jiajie\"><input id=" + minus + " type=\"button\' value=\"-\" onclick='minus(id)'><span id=" + key + " class=\"num\">"
                + "<td class='number'><span class='jiajie'><input id='" + minus + "' type='button' value='-' onclick='minus(id)'><span id='" + key + "' class='num'>"
                + quantity
                + "</span><input id='" + plus + "'  type='button' value='+' onclick='plus(id)'></span></td>"
                + "<td class=\"price\"><span>$：</span><span id='"+ key+ "price'"  +" class=\"unit\">"
                + (3*quantity).toString() + "</span></td>"
                + "<td class='tdsix'><button class='del' id='"+ key +"' onclick='Delete(id)'>delete</button></td>"
                + "</tr>";


            starTableBodyElement.append(rowHTML);
        }
    }
    let lastHTML = "";
    lastHTML += "<tr><td colspan=\"6\"; class=\"totalPrice\">\n" +
        "                 Total Price: <span id = 'totalPrice'>" + totalPrice.toString() + "</span>\n" +
        "             </td></tr>\n" +
        "             <tr><td   colspan=\"6\"; class=\"info\">\n" +
        '<a href="index.html' + '">'+
        "                 Home" + " | " + "<a href=\"#\" onclick=\"window.history.go(-1); return false;\"> Go Back </a >" +
        " | " + "<a id= 'totalHref' href='payment.html?totalPrice=" + totalPrice.toString() + "'>"+ "Proceed to payment </a>" +
        "             </td></tr>";
    starTableBodyElement.append(lastHTML);
}

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
let movieTitle = getParameterByName("movieTitle");
let sign = getParameterByName("sign");


function minus(itemName){
    console.log("item number --");
    var string = itemName;
    string = string.substring(0, string.length-1);
    var num = document.getElementById(string).innerHTML;
    window.alert(document.getElementById(string).innerHTML);
    num--;
    document.getElementById(string).innerText = num;
    document.getElementById(string+"price").innerText = num * 3;
    document.getElementById("totalPrice").innerText = parseInt(document.getElementById("totalPrice").innerText) - 3;
    totalPrice = parseInt(document.getElementById("totalPrice").innerText);
    console.log(totalPrice);
    let paymentHref = "payment.html?totalPrice=" + totalPrice;
    document.getElementById("totalHref").setAttribute("href", paymentHref);
    jQuery.ajax({
        dataType: "json", // Setting return data type
        method: "GET", // Setting request method
        url: "api/shopping-cart?movieTitle=" + itemName.slice(0, -1) + "&sign=minus", // Setting request url, which is mapped by StarsServlet in Stars.java
        // success: (resultData) => handleAddResult(resultData) // Setting callback function to handle data returned successfully by the StarsServlet
    });
}

function plus(itemName){
    console.log("item number ++");
    var string = itemName;
    string = string.substring(0, string.length-1);
    var num = document.getElementById(string).innerHTML;
    window.alert(document.getElementById(string).innerHTML);
    num++;
    document.getElementById(string).innerText = num;
    document.getElementById(string+"price").innerText = num * 3;
    document.getElementById("totalPrice").innerText = parseInt(document.getElementById("totalPrice").innerText) + 3;
    totalPrice = parseInt(document.getElementById("totalPrice").innerText);
    console.log(totalPrice);
    let paymentHref = "payment.html?totalPrice=" + totalPrice;
    document.getElementById("totalHref").setAttribute("href", paymentHref);
    jQuery.ajax({
        dataType: "json", // Setting return data type
        method: "GET", // Setting request method
        url: "api/shopping-cart?movieTitle=" + itemName.slice(0, -1) + "&sign=plus", // Setting request url, which is mapped by StarsServlet in Stars.java
        // success: (resultData) => handleAddResult(resultData) // Setting callback function to handle data returned successfully by the StarsServlet
    });
}
function handleDeleteResult(resultData){
    window.location.replace("shopping-cart.html");
}

function Delete(movieTitle){
    window.alert(movieTitle + " successfully deleted")
    console.log("add item to shopping cart")
    jQuery.ajax({
        dataType: "json", // Setting return data type
        method: "GET", // Setting request method
        url: "api/shopping-cart?movieTitle=" + movieTitle +"&sign=delete", // Setting request url, which is mapped by StarsServlet in Stars.java
        success: (resultData) => handleDeleteResult(resultData) // Setting callback function to handle data returned successfully by the StarsServlet
    });
}

jQuery.ajax({
    dataType: "json", // Setting return data type
    method: "GET", // Setting request method
    url: "api/shopping-cart?movieTitle=" + movieTitle + "&sign=" + sign,
    success: (resultData) => shoppingResult(resultData) // Setting callback function to handle data returned successfully by the StarsServlet
});