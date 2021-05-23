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
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


// Declaring a WebServlet called StarsServlet, which maps to url "/api/stars"
@WebServlet(name = "movieServlet", urlPatterns = "/api/movies")
public class movieServlet extends HttpServlet {

    private static final long serialVersionUID = 3L;
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
    
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doGet(req,resp);
    }
    

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json"); // Response mime type

        // Output stream to STDOUT
        PrintWriter out = response.getWriter();

        // if value of search = 1, means , now we are doing search. otherwise we are doing browse
        String search = request.getParameter("search");
        String type = request.getParameter("type");
        String content = request.getParameter("content");

        int currentPage = Integer.parseInt(request.getParameter("page"));
        int displayNumber = Integer.parseInt(request.getParameter("display"));
        //sort = 1 : sort by title asc order, rating asc
        //sort = 2 : sort by title asc order, rating desc
        //sort = 3 : sort by title desc order, rating asc
        //sort = 4 : sort by title desc order, rating desc
        //sort = 5 : sort by rating asc order, title asc
        //sort = 6 : sort by rating asc order, title desc
        //sort = 7 : sort by rating desc order, title asc
        //sort = 8 : sort by rating desc order, title desc
        String sort = request.getParameter("sort");
        HashMap<Integer,String> sortMap = new HashMap<>();
        sortMap.put(1,"order by m.title asc, r.rating asc");
        sortMap.put(2,"order by m.title asc, r.rating desc");
        sortMap.put(3,"order by m.title desc, r.rating asc");
        sortMap.put(4,"order by m.title desc, r.rating desc");
        sortMap.put(5,"order by r.rating asc, m.title asc");
        sortMap.put(6,"order by r.rating asc, m.title desc");
        sortMap.put(7,"order by r.rating desc, m.title asc");
        sortMap.put(8,"order by r.rating desc, m.title desc");
        String contentTemp = content;
        System.out.println("contentTemp: "+contentTemp);
        System.out.println(type+":"+content);
//        System.out.println("search: "+ search+" type: "+type+" content: "+ content);
//        System.out.println("page: "+currentPage);
//        System.out.println("display: "+ displayNumber);
        String temp = "";
        String normalTemp = "";
//        System.out.println(type == "multiSearch");
//        System.out.println(type.equals("multiSearch"));







        if (type.equals("multiSearch") ){
//            System.out.println("we got here=====");
            String[] help = contentTemp.split("-",0);
            HashMap<String,String> map = new HashMap<>();
            for(String s : help){
                String[] s2 = s.split(":",0);
                ArrayList<String> arr = new ArrayList<>();
                for (String r : s2){
                    if (!r.equals("")){
                        arr.add(r);
                    }
                }
                if (arr.size()==2){
                    map.put(arr.get(0),arr.get(1));
                }else if(arr.size()==1){
                    map.put(arr.get(0),null);
                }
            }
            //We got here
//            System.out.println("we got here");
//            System.out.println(map);
            if (map.get("title") != null){
//                System.out.println("1");
                temp += " and m.title like '%" + map.get("title")+ "%' ";


            }
            if (map.get("star") != null){
//                System.out.println("2");
                temp += " and s.name like '%" + map.get("star") + "%' ";


            }
            System.out.println(map.get("director") != null);
            if (map.get("director") != null){
//                System.out.println("3");
                temp += " and m.director like'%" + map.get("director")+ "%' ";


            }
            if (map.get("year") != null){
//                System.out.println("4");
                temp += " and m.year ='" + map.get("year") + "' ";

            }
            System.out.println(temp);
            contentTemp = temp;
        }
        System.out.println("144");
        try {
            // Get a connection from dataSource
            Connection dbcon = dataSource.getConnection();

            String query = "";
            String countQuery = "";

            if (type.equals("multiSearch")){
                query = "select distinct m.title, m.director, m.year, r.rating, m.id from stars as s, stars_in_movies as sim ,genres as g , genres_in_movies as gim , movies as m left join ratings as r on m.id = r.movieId where s.id = sim.starId and sim.movieId = m.id  and gim.genreId = g.id and gim.movieId = m.id";
                countQuery = "select count(distinct m.title, m.director, m.year, r.rating, m.id) from stars as s, stars_in_movies as sim ,genres as g , genres_in_movies as gim, movies as m left join ratings as r on m.id = r.movieId where s.id = sim.starId and sim.movieId = m.id  and gim.genreId = g.id and gim.movieId = m.id";
                query += contentTemp + sortMap.get(Integer.parseInt(sort)) +" limit ?,?;";
                countQuery += contentTemp + ";";
            }
            if(type.equals("star")){
                query = "select s.name, m.title, m.director, m.year, r.rating, m.id from stars as s, stars_in_movies as sim , movies as m left join ratings as r on m.id = r.movieId where s.name = ? and s.id = sim.starId and sim.movieId = m.id  %s limit ?,?;";
                countQuery = "select count(*) from stars as s, stars_in_movies as sim , movies as m left join ratings as r on m.id = r.movieId where s.name = ? and s.id = sim.starId and sim.movieId = m.id ;";
            }
            if(type.equals("year")){
                query = "select m.title, m.director, m.year, r.rating, m.id from  movies as m left join ratings as r on m.id = r.movieId where m.year = ?  %s limit ?,?;";
                countQuery = "select count(*) from  movies as m left join ratings as r on m.id = r.movieId where m.year = ? ;";
            }

            if (type.equals("director")){
                query = "select m.title, m.director, m.year, r.rating, m.id from  movies as m left join ratings as r on m.id = r.movieId where m.director like ?  %s limit ?,?;";
                countQuery = "selct count(*) from  movies as m left join ratings as r on m.id = r.movieId where m.director like ? ;";
                contentTemp = "%" + content + "%";
            }
            //browsing
            if (type.equals("genres")){
                query = "select m.title, m.director, m.year, r.rating, m.id, g.name from genres as g , genres_in_movies as gim,movies as m left join ratings as r on  m.id = r.movieId where g.name = ? and gim.genreId = g.id and gim.movieId = m.id %s limit ?,?;";
                countQuery = "select count(*) from genres as g , genres_in_movies as gim , movies as m left join ratings as r on m.id = r.movieId where g.name = ? and gim.genreId = g.id and gim.movieId = m.id ;";
            }
            System.out.println("177type: " + type);
            if(type.equals("title")){
                //System.out.println("Here!");
                if (search.equals("1")) {
                    //searching
                    System.out.println("182search 1");
                    normalTemp  = "'"+contentTemp + "'";
                    query = "select m.title, m.director, m.year, r.rating, m.id from ft, movies as m left join ratings as r on m.id = r.movieId WHERE (MATCH (ft.entry) AGAINST ( ? IN BOOLEAN MODE) or ft.entry = %s or ed(ft.entry,%s) <=3 ) and m.id = ft.entryID %s limit ?,?;";
                    countQuery = "select count(*) from ft, movies as m left join ratings as r on m.id = r.movieId WHERE (MATCH (ft.entry) AGAINST ( ? IN BOOLEAN MODE) or ft.entry = %s or ed(ft.entry,%s) <= 3) and m.id = ft.entryID ;";
                    countQuery = String.format(countQuery,normalTemp,normalTemp);
                    //System.out.println("query1" + query);
//                    content = "'" +content + "%'";
                    System.out.println("190query:" + query);
                    System.out.println("191CountQuery:" + countQuery);
                    ArrayList<String> words = new ArrayList<>();
                    String[] convert = contentTemp.split(" ");
                    String tempString = "";
                    for (String word: convert){
//                        if (word.length() > 2) {
//
//                        }
                        words.add(word);
                    }
                    for (int i = 0; i < words.size();i++){
                        tempString += "+" + words.get(i) +"* ";
                    }

                    contentTemp = tempString;
                }
                if (search.equals("0")){
                    //browsing
                    //System.out.println("browsing 0");
//                    query = String.format("select m.title, m.director, m.year, r.rating, m.id from  movies as m, ratings as r where m.title like 's%' and m.id = r.movieId;",content);
                    query = "select m.title, m.director, m.year, r.rating, m.id from  movies as m left join ratings as r on m.id = r.movieId where m.title like ?  %s limit ?,?;";
                    countQuery = "select count(*) from  movies as m left join ratings as r on m.id = r.movieId where m.title like ? ;";
//                    System.out.println("query0" + query);
                    contentTemp =  content + "%";
                }
            }
            //Get the total count of rows for pagination!=============================
            if (type.equals("title")&&content.equals("*")){
                countQuery = "select count(*) from  movies as m left join ratings as r on m.id = r.movieId where m.title REGEXP '^[^a-zA-Z0-9]' ;";
            }
            PreparedStatement statement = dbcon.prepareStatement(countQuery);
            if (!type.equals("multiSearch") && !content.equals("*")) {
                statement.setString(1, contentTemp);
            }
            System.out.println(statement);
            ResultSet countRs = statement.executeQuery();
//            String rowCount = countRs.getString("count(*)");
            int totalCount = 0;
            while (countRs.next()) { //count(distinct m.title, m.director, m.year, r.rating, m.id)
                if (type.equals("multiSearch")){
                    totalCount = Integer.parseInt(countRs.getString("count(distinct m.title, m.director, m.year, r.rating, m.id)"));
                }else {
                    totalCount = Integer.parseInt(countRs.getString("count(*)"));
                }
            }
            int totalPage = (int)Math.ceil((double)totalCount / displayNumber);
//            System.out.println("total Count: "+ totalCount);
//            System.out.println("total Page: "+ totalPage);
            //=================================================================
//            System.out.println("before sort query: "+ query);
            //update the query with sorting
//            System.out.println(sortMap.get(Integer.parseInt(sort)));
            if(!type.equals("multiSearch")) {
                query = String.format(query,normalTemp ,normalTemp,sortMap.get(Integer.parseInt(sort)));
            }
            if (type.equals("title")&&content.equals("*")){
                query = "select m.title, m.director, m.year, r.rating, m.id from  movies as m left join ratings as r on m.id = r.movieId where m.title REGEXP '^[^a-zA-Z0-9]' limit ?,?;";
            }
//            System.out.println("======================");
            System.out.println("sortquery: "+ query);
            //start the statement
            statement = dbcon.prepareStatement(query);
            if(!type.equals("multiSearch") && !content.equals("*")){
                //set the content
                statement.setString(1, contentTemp);
                //set the offset
                statement.setInt(2,displayNumber*(currentPage-1));
                //set the display number
                statement.setInt(3,displayNumber);
            }else{
                //set the offset
                statement.setInt(1,displayNumber*(currentPage-1));
                //set the display number
                statement.setInt(2,displayNumber);
            }
            System.out.println("statment: "+ statement);
            JsonObject pageObject = new JsonObject();
            pageObject.addProperty("totalPage",totalPage);
            pageObject.addProperty("currentPage",currentPage);
            pageObject.addProperty("displayNumber",displayNumber);
            pageObject.addProperty("sort",sort);
            pageObject.addProperty("search",search);
            pageObject.addProperty("type",type);
            pageObject.addProperty("content",content);
            // Perform the query
            ResultSet rs = statement.executeQuery();

            JsonArray jsonArray = new JsonArray();

            // Iterate through each row of rs
            while (rs.next()) {

                String movieTitle = rs.getString("title");
                String movieYear = rs.getString("year");
                String movieDirector = rs.getString("director");
                String movieId = rs.getString("id");
                String movieRating = rs.getString("rating");

                // get 3 stars
                String starsQuery = "SELECT m.title, s.name, s.id from movies as m, stars as s,stars_in_movies as sim where m.id = ? and sim.starId = s.id and sim.movieId = m.id limit 3;";
                statement = dbcon.prepareStatement(starsQuery);
                statement.setString(1, movieId);
                ResultSet starsRs = statement.executeQuery();
                //JsonArray starsArray = new JsonArray();
                JsonObject starsObject = new JsonObject();
                int counter = 0;
                // get all satrs from the movie
                while (starsRs.next()){
                    String starsNameTemp = starsRs.getString("name");
                    String starsIdTemp = starsRs.getString("id");
//                    starObject.addProperty(Integer.toString(counter),starsNameTemp);
//                    starObject.addProperty(Integer.toString(counter + 100),starsIdTemp);
                    starsObject.addProperty(starsNameTemp,starsIdTemp);
                    counter++;
                }
                //starsArray.add(starObject);

                // get 3 genres
                String genresQuery = "SELECT m.title, g.name from movies as m, genres as g, genres_in_movies as gim  where m.id = ? and gim.movieId = m.id and gim.genreId = g.id limit 3;";
                statement = dbcon.prepareStatement(genresQuery);
                statement.setString(1, movieId);
                ResultSet genresRs = statement.executeQuery();

                String genresResult = "";
                // get genres
                while (genresRs.next()){
                    String genres = genresRs.getString("name");
                    genresResult += genres;
                    genresResult += ", ";
                }

                // Create a JsonObject based on the data we retrieve from rs
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("movieTitle", movieTitle);
                jsonObject.addProperty("movieYear", movieYear);
                jsonObject.addProperty("movieDirector", movieDirector);
                jsonObject.addProperty("movieId", movieId);
                jsonObject.addProperty("movieRating", movieRating);
                //jsonObject.addProperty("movieRating", movieRating);
                jsonObject.addProperty("genresResult", genresResult);
                jsonObject.add("starsObject", starsObject);
                jsonObject.add("pageObject",pageObject);


                jsonArray.add(jsonObject);
            }
//            System.out.println(jsonArray);
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
}
