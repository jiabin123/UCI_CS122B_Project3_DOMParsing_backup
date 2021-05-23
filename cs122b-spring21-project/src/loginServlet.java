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
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import org.jasypt.util.password.StrongPasswordEncryptor;


@WebServlet(name = "loginServlet", urlPatterns = "/api/login")
public class loginServlet extends HttpServlet {
    private DataSource dataSource;
    public void init(ServletConfig config) {
        try {
            dataSource = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/moviedb");
        } catch (NamingException e) {
            e.printStackTrace();
        }
    }
    public String getServletInfo() {
        return "Servlet connects to MySQL database and displays result of a SELECT";
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        // get the user info
        String username = request.getParameter("username");
        System.out.println(username);
        String password = request.getParameter("password");
        System.out.println(password);
        String usertype = request.getParameter("userType");
        String android = request.getParameter("android");
        System.out.println(android);
        String SQLusername = " ";
        String SQLpassword = " ";
        String SQLcustomerId = "";

        PrintWriter out = response.getWriter();

        String gRecaptchaResponse = request.getParameter("g-recaptcha-response");

        System.out.println("gRecaptchaResponse=" + gRecaptchaResponse);

        // Verify reCAPTCHA
//        if(request.getParameter("android") == null){
//            try {
//                RecaptchaVerifyUtils.verify(gRecaptchaResponse);
//            } catch (Exception e) {
//
//                JsonObject responseJsonObject = new JsonObject();
//                responseJsonObject.addProperty("reCAPTCHA", "true");
//                response.getWriter().write(responseJsonObject.toString());
//
//                out.close();
//                return;
//            }
//        }


        // Get a connection from dataSource
        try {
            // Get a connection from dataSource
            Connection dbcon = dataSource.getConnection();
            String query = "";
//            System.out.println(usertype.equals( "user"));
            if (usertype.equals( "user")) {
                // Construct a query with parameter represented by "?"
                query = "select customers.email,customers.id, customers.password from customers where customers.email = ?;";
            }else{
                query = "select employees.email,employees.fullname, employees.password from employees where employees.email = ?;";
            }
            System.out.println(query);
            // Declare our statement
            PreparedStatement statement = dbcon.prepareStatement(query);

            // Set the parameter represented by "?" in the query to the id we get from url,
            // num 1 indicates the first "?" in the query
            statement.setString(1, username);
//            System.out.println(statement);
            // Perform the query
            ResultSet rs = statement.executeQuery();

            // Iterate through each row of rs
            while (rs.next()){
                SQLusername = rs.getString("email");
                SQLpassword = rs.getString("password");
                if (usertype.equals( "user")) {
                    SQLcustomerId = rs.getString("id");
                }else{
                    SQLcustomerId = rs.getString("fullname");
                }
            }
//            System.out.println(SQLpassword+SQLusername+SQLcustomerId);
            // set response status to 200 (OK)
            response.setStatus(200);
            rs.close();
            statement.close();
            dbcon.close();
        } catch (Exception e) {
            // set reponse status to 500 (Internal Server Error)
            response.setStatus(500);
        }
//        System.out.println(usertype);
//        System.out.println(SQLusername);
//        System.out.println(SQLpassword);
        //Create session
        HttpSession session = request.getSession(true);

        JsonObject responseJsonObject = new JsonObject();
        if (username.equals(SQLusername) && new StrongPasswordEncryptor().checkPassword(password, SQLpassword)) {
            // Login success:
            request.getSession().setAttribute("user", new User(username,Long.toString(session.getLastAccessedTime()),SQLcustomerId,usertype)) ;
            responseJsonObject.addProperty("status", "success");
            responseJsonObject.addProperty("userType",usertype);
            responseJsonObject.addProperty("message", "success");

        }else {
            // Login fail
            responseJsonObject.addProperty("status", "fail");

            // sample error messages. in practice, it is not a good idea to tell user which one is incorrect/not exist.
            if (!username.equals(SQLusername)) {
                responseJsonObject.addProperty("message", "user " + username + " doesn't exist");
            } else {
                responseJsonObject.addProperty("message", "incorrect password");
            }
        }

        response.getWriter().write(responseJsonObject.toString());
    }

}
