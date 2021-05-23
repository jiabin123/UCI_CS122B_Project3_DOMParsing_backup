import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;
import javax.xml.crypto.Data;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.function.DoubleToIntFunction;


@WebServlet(name = "paymentServlet", urlPatterns = "/api/payment")
public class paymentServlet extends HttpServlet {
    private DataSource dataSource;
    public void init(ServletConfig config) {
        try {
            dataSource = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/moviedb");
        } catch (NamingException e) {
            e.printStackTrace();
        }
    }
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        HttpSession session = request.getSession();
        JsonObject previousItems = (JsonObject) session.getAttribute("previousItems");

        // get the credit card info
        String firstName = request.getParameter("firstname");
        String lastName = request.getParameter("lastname");
        String cardNumber = request.getParameter("card");
        String date = request.getParameter("date");


        String SQLfirstName = "";
        String SQLlastName = "";
        String SQLdate = "";
        String SQLid = "";


        try{
            // get connection to data base
            Connection dbcon = dataSource.getConnection();

            // construct a query with parametr represented by "?"
            String query = "select * from creditcards where id = ?";

            PreparedStatement statement = dbcon.prepareStatement(query);

            statement.setString(1, cardNumber);

            ResultSet rs = statement.executeQuery();

            while(rs.next()){
                SQLfirstName = rs.getString("firstName");
                SQLlastName = rs.getString("lastName");
                SQLdate = rs.getString("expiration");
                SQLid = rs.getString("id");
            }

            response.setStatus(200);
            rs.close();
            statement.close();
            dbcon.close();
        } catch(Exception e){
            e.printStackTrace();
            response.setStatus(500);
        }

        //JsonObject for sending back the message to frontend
        JsonObject jsonObject = new JsonObject();
        User user = (User)session.getAttribute("user");
        String email = user.getUsername();
        String customerId = user.getCustomerId();
        Date today = new Date();
        SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy-MM-dd");
        String todaySQL = dateFormat.format(today).toString();
        user.setLastAccessTime(todaySQL);
        if(SQLfirstName.toLowerCase().equals(firstName.toLowerCase()) && SQLlastName.toLowerCase().equals(lastName.toLowerCase())
                && SQLdate.equals(date) && SQLid.equals(cardNumber)){
            jsonObject.addProperty("status", "success");

            try{
                // get connection to data base
                Connection dbcon = dataSource.getConnection();

                // construct a query with parametr represented by "?"
                String query = "select m.id from movies as m where m.title = ?;";

                PreparedStatement statement = dbcon.prepareStatement(query);

                for(String str :previousItems.keySet()){
                    statement.setString(1, str);
                    ResultSet rs = statement.executeQuery();
                    while (rs.next()){
                        String movieId = rs.getString("id");

                        String queryInsert = "INSERT INTO sales (customerId, movieId, saleDate) VALUES(?,?,?);";
                        PreparedStatement statementInsert = dbcon.prepareStatement(queryInsert);
                        statementInsert.setString(1,customerId);
                        statementInsert.setString(2,movieId);
                        statementInsert.setString(3,todaySQL);
                        int result = statementInsert.executeUpdate();

                    }
                }

            }catch (Exception e){
                e.printStackTrace();
                response.setStatus(500);
            }


            // clear the shopping cart while payment success
//            System.out.println(previousItems.keySet());
//            List<String> list = (List)previousItems.keySet();
//            int k = 0;
//            while(k<list.size()){
//                previousItems.remove(list.get(k++));
//            }
//            System.out.println(previousItems);
        }else{
            jsonObject.addProperty("status", "fail");
        }

        response.getWriter().write(jsonObject.toString());
    }

}
