import com.google.gson.JsonObject;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

@WebServlet(name = "shoppingcartServlet", urlPatterns = "/api/shopping-cart")
public class shoppingcartServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        HttpSession session = request.getSession();
        // Retrieve data named "previousItems" from session
        JsonObject previousItems = (JsonObject) session.getAttribute("previousItems");
        String movieTitle = (String) request.getParameter("movieTitle");
        String sign = (String)request.getParameter("sign");

        //String moviePrice = request.getParameter("moviePrice");

        // If "previousItems" is not found on session, means this is a new user, thus we create a new previousItems
        // ArrayList for the user
        PrintWriter out = response.getWriter();
        if (previousItems == null) {
            // Add the newly created ArrayList to session, so that it could be retrieved next time
            previousItems = new JsonObject();
            session.setAttribute("previousItems", previousItems);
        }

        synchronized (previousItems){
            if(!movieTitle.equals("null")) {
                if (movieTitle != null && !previousItems.has(movieTitle) && (sign.equals("plus")||sign.equals("null"))) {
                    previousItems.addProperty(movieTitle, 1);
                } else if (movieTitle != null && previousItems.has(movieTitle) && (sign.equals("plus")||sign.equals("null"))) {
                    int qty = previousItems.get(movieTitle).getAsInt();
                    previousItems.remove(movieTitle);
                    previousItems.addProperty(movieTitle, qty + 1);
                }
                if (movieTitle != null && previousItems.has(movieTitle) && sign.equals("minus")){
                    int qty = previousItems.get(movieTitle).getAsInt() - 1;
                    if(qty == 0){
                        previousItems.remove(movieTitle);
                    }else {
                        previousItems.remove(movieTitle);
                        previousItems.addProperty(movieTitle, qty);
                    }
                }
                if (movieTitle != null && previousItems.has(movieTitle) && sign.equals("delete")){
                    previousItems.remove(movieTitle);
                }

            }

        }
        System.out.println(previousItems);
        out.write(previousItems.toString());

        // set response status to 200 (OK)
        response.setStatus(200);

        out.close();

    }
}
