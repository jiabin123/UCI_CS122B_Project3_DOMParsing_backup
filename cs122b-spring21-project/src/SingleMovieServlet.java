import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

//import javax.annotation.Resource;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

@WebServlet(name = "SingleMovieServlet", urlPatterns = "/api/single-movie")
public class SingleMovieServlet extends HttpServlet {
    private static final long serialVersionUID = 2L;

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

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
     *      response)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json"); // Response mime type
        String search = request.getParameter("search");
        String type = request.getParameter("type");
        String content = request.getParameter("content");
        int currentPage = Integer.parseInt(request.getParameter("page"));
        int displayNumber = Integer.parseInt(request.getParameter("display"));
        String sort = request.getParameter("sort");
        JsonObject pageObject = new JsonObject();
        pageObject.addProperty("currentPage",currentPage);
        pageObject.addProperty("displayNumber",displayNumber);
        pageObject.addProperty("sort",sort);
        pageObject.addProperty("search",search);
        pageObject.addProperty("type",type);
        pageObject.addProperty("content",content);

        // Retrieve parameter id from url request.
        String id = request.getParameter("movieId");

        // Output stream to STDOUT
        PrintWriter out = response.getWriter();

        try {
            // Get a connection from dataSource
            Connection dbcon = dataSource.getConnection();

            String query = "SELECT * from movies left join ratings on ratings.movieId = movies.id where movies.id = ? ;";

            // Declare our statement
            PreparedStatement statement = dbcon.prepareStatement(query);

            // Set the parameter represented by "?" in the query to the id we get from url,
            // num 1 indicates the first "?" in the query
            statement.setString(1, id);

            // Perform the query
            ResultSet rs = statement.executeQuery();

            JsonArray jsonArray = new JsonArray();


            //movie row
            while (rs.next()) {

                String genresQuery = "SELECT m.title, g.name from movies as m, genres as g, genres_in_movies as gim where m.id = ? and gim.movieId = m.id and gim.genreId = g.id;";
                statement = dbcon.prepareStatement(genresQuery);
                statement.setString(1, id);
                ResultSet genresRs = statement.executeQuery();

                String genresResult = "";
                // get genres
                while (genresRs.next()){
                    String genres = genresRs.getString("name");
                    genresResult += genres;
                    genresResult += ", ";
                }


                String starsQuery = "SELECT m.title, s.name, s.id from movies as m, stars as s,stars_in_movies as sim where m.id = ? and sim.starId = s.id and sim.movieId = m.id;";
                statement = dbcon.prepareStatement(starsQuery);
                statement.setString(1, id);
                ResultSet starsRs = statement.executeQuery();
                JsonObject starsObject = new JsonObject();
                int counter = 0;
                // get all satrs from the movie
                while (starsRs.next()){
                    String starsNameTemp = starsRs.getString("name");
                    String starsIdTemp = starsRs.getString("id");
                    starsObject.addProperty(starsNameTemp,starsIdTemp);
                    counter++;
                }


                String movieTitle = rs.getString("title");
                String movieYear = rs.getString("year");
                String movieDirector = rs.getString("director");
                String movieId = rs.getString("id");
                String movieRating = rs.getString("rating");

                // Create a JsonObject based on the data we retrieve from rs
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("movieTitle", movieTitle);
                jsonObject.addProperty("movieYear", movieYear);
                jsonObject.addProperty("movieDirector", movieDirector);
                jsonObject.addProperty("movieId", movieId);
                jsonObject.addProperty("movieRating", movieRating);
                jsonObject.addProperty("genresResult", genresResult);
                jsonObject.add("starsObject", starsObject);
                jsonObject.add("pageObject",pageObject);

                jsonArray.add(jsonObject);
            }
            // write JSON string to output
            out.write(jsonArray.toString());
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
    
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doGet(req, resp);
    }

}
