import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.RequestDispatcher;
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
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@WebServlet(name = "_dashboard", urlPatterns = "/api/_dashboard")
public class _dashboard extends HttpServlet {
    private static final long serialVersionUID = 30L;
    // Create a dataSource which registered in web.xml
    //@Resource(name = "jdbc/moviedb")
    private DataSource dataSource;
    public void init(ServletConfig config) {
        try {
            dataSource = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/moviedb");
        } catch (NamingException e) {
            e.printStackTrace();
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json"); // Response mime type

        // Output stream to STDOUT
        PrintWriter out = response.getWriter();
        String starName = (String) request.getParameter("starName");
        String starYear = (String) request.getParameter("starYear");
        String movieTitle = (String) request.getParameter("movieTitle");
        String movieDirector = (String) request.getParameter("movieDirector");
        String movieYear = (String) request.getParameter("movieYear");
        String movieGenres = (String) request.getParameter("movieGenres");
        String movieStar = (String) request.getParameter("movieStar");
        String movieStarYear = (String) request.getParameter("movieStarYear");
        String answer = "";
//        System.out.println(starYear);
//        System.out.println(starYear==null);
//        System.out.println(starYear == "");
//        System.out.println(starYear == "null");
//        System.out.println(starYear.equals(null));
//        System.out.println(starYear.equals(""));
//        System.out.println(starYear.equals(""));
        if (starYear == ""){
            starYear = "NULL";
        }
        if (movieStarYear == ""){
            movieStarYear = "NULL";
        }
//        System.out.println("the following is the request getparameter");
//        System.out.println(starName+starYear+movieTitle+movieDirector+movieYear+movieGenres+movieStar);
        try {

            Connection dbcon = dataSource.getConnection();
            String query = "show tables;";
            PreparedStatement statement = dbcon.prepareStatement(query);
            ArrayList<String> tables = new ArrayList<>();
            JsonArray result = new JsonArray();
            ResultSet rs = statement.executeQuery();
            JsonObject metaData = new JsonObject();
            while (rs.next()) {
                String table = rs.getString("Tables_in_moviedb");
                tables.add(table);
            }

            for (int i = 0; i < tables.size();i++) {
                String metaDataQuery = String.format("describe %s;", tables.get(i));
                statement = dbcon.prepareStatement(metaDataQuery);
                ResultSet metaDataRs = statement.executeQuery();
                JsonArray tableRows = new JsonArray();
                while (metaDataRs.next()){
                    JsonObject jsonObject = new JsonObject();
                    String theField = metaDataRs.getString("Field");
                    String theType = metaDataRs.getString("Type");
                    String theNull = metaDataRs.getString("Null");
                    String theKey = metaDataRs.getString("Key");
                    String theDefault = metaDataRs.getString("Default");
                    String theExtra = metaDataRs.getString("Extra");
                    jsonObject.addProperty("Field",theField);
                    jsonObject.addProperty("Type",theType);
                    jsonObject.addProperty("Null",theNull);
                    jsonObject.addProperty("Key",theKey);
                    jsonObject.addProperty("Default",theDefault);
                    jsonObject.addProperty("Extra",theExtra);
                    tableRows.add(jsonObject);
                }
                metaData.add(tables.get(i),tableRows);
            }
//            System.out.println(movieTitle);
//            System.out.println(movieTitle==null);
//            System.out.println(movieTitle == "");
//            System.out.println(movieTitle == "null");
//            System.out.println(movieTitle.equals(null));
//            System.out.println(movieTitle.equals(""));
//            System.out.println(movieTitle.equals(""));
            if (movieTitle == null){ //this is add_star form

                String addStarQuery = String.format("call add_star(?,%s);",starYear);
                statement = dbcon.prepareStatement(addStarQuery);
                statement.setString(1,starName);
                ResultSet addStarRs = statement.executeQuery();
                while (addStarRs.next()){
                    answer = addStarRs.getString("answer");
                }
            }else{ //means it is add_movie form

                String addMovieQuery = String.format("call add_movie(?,?,%s,?,?,%s);",movieYear,movieStarYear);
//                System.out.println(addMovieQuery);
                statement = dbcon.prepareStatement(addMovieQuery);
                statement.setString(1,movieTitle);
                statement.setString(2,movieDirector);
                statement.setString(3,movieGenres);
                statement.setString(4,movieStar);
                System.out.println(statement);
                ResultSet addMovieRs = statement.executeQuery();
                while (addMovieRs.next()){

                    answer = addMovieRs.getString("answer");
                }
            }
            System.out.println(metaData);
//            System.out.println(jsonArray);
            // write JSON string to output
            result.add(answer);
            result.add(metaData);
            out.write(result.toString());
            // set response status to 200 (OK)
            response.setStatus(200);

            rs.close();
            statement.close();
            dbcon.close();
        } catch (Exception e) {
            // write error message JSON object to output
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("errorMessage", e.getMessage());
            out.write(jsonObject.toString());

            // set reponse status to 500 (Internal Server Error)
            response.setStatus(500);
        }
        out.close();
    }
}
