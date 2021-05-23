import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletConfig;
import javax.sql.DataSource;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

// server endpoint URL
@WebServlet("/title-suggestion")
public class titleSuggestion extends HttpServlet {
    private static final long serialVersionUID = 5L;

    /*
     * populate the Super hero hash map.
     * Key is hero ID. Value is hero name.
     */
    public static HashMap<Integer, String> superHeroMap = new HashMap<>();

    public int id = 1;

    public titleSuggestion() {
        super();
    }

    /*
     *
     * Match the query against superheroes and return a JSON response.
     *
     * For example, if the query is "super":
     * The JSON response look like this:
     * [
     * 	{ "value": "Superman", "data": { "heroID": 101 } },
     * 	{ "value": "Supergirl", "data": { "heroID": 113 } }
     * ]
     *
     * The format is like this because it can be directly used by the
     *   JSON auto complete library this example is using. So that you don't have to convert the format.
     *
     * The response contains a list of suggestions.
     * In each suggestion object, the "value" is the item string shown in the dropdown list,
     *   the "data" object can contain any additional information.
     *
     *
     */
    private DataSource dataSource;
    public void init(ServletConfig config) {
        try {
            dataSource = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/moviedb");
        } catch (NamingException e) {
            e.printStackTrace();
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            // setup the response json arrray
            JsonArray jsonArray = new JsonArray();

            // get the query string from parameter
            String query = request.getParameter("query");

            // return the empty json array if query is null or empty
            if (query == null || query.trim().isEmpty()) {
                response.getWriter().write(jsonArray.toString());
                return;
            }
            ArrayList<String> words = new ArrayList<>();
            String[] convert = query.split(" ");
            String tempString = "";
            for (String word: convert){
                words.add(word);
            }
            for (int i = 0; i < words.size();i++){
                tempString += "+" + words.get(i) +"* ";
            }
            // get connection to data base
            Connection dbcon = dataSource.getConnection();
            // construct a query with parametr represented by "?"
            String sqlQuery = "SELECT * FROM ft , movies as m  WHERE MATCH (ft.entry) AGAINST (? IN BOOLEAN MODE) and m.id = ft.entryID limit 10;";

            PreparedStatement statement = dbcon.prepareStatement(sqlQuery);

            statement.setString(1, tempString);
//            System.out.println(statement);
            ResultSet rs = statement.executeQuery();

            // only provide 10 title for suggestion
//            int count = 1;
            while (rs.next()){
//                if(count>10){
//                    break;
//                }
                String movieId = rs.getString("id");
                String movieTitle = rs.getString("title");
                jsonArray.add(generateJsonObject(movieId, movieTitle));
//                id++;
//                count++;
            }


            response.getWriter().write(jsonArray.toString());
            return;
        } catch (Exception e) {
            System.out.println(e);
            response.sendError(500, e.getMessage());
        }
    }

    /*
     * Generate the JSON Object from hero to be like this format:
     * {
     *   "value": "Iron Man",
     *   "data": { "heroID": 11 }
     * }
     *
     */
    private static JsonObject generateJsonObject(String movieID, String movieTitle) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("value", movieTitle);

        JsonObject additionalDataJsonObject = new JsonObject();
        additionalDataJsonObject.addProperty("movieId", movieID);

        jsonObject.add("data", additionalDataJsonObject);
        return jsonObject;
    }


}
