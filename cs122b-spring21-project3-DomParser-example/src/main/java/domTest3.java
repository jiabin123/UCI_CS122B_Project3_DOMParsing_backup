import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class domTest3 {


    //create table ft(entryID varchar(10) primary key, entry text, FULLTEXT(entry));
    public static void main(String[] args) throws InstantiationException, IllegalAccessException, ClassNotFoundException {
        Connection conn = null;

        Class.forName("com.mysql.jdbc.Driver").newInstance();
        String jdbcURL="jdbc:mysql://localhost:3306/moviedb";

        try {
            conn = DriverManager.getConnection(jdbcURL,"mytestuser", "My6$Password");
            String query = "select id, title from movies";
            PreparedStatement statement = conn.prepareStatement(query);

            ResultSet rs = statement.executeQuery();

            while(rs.next()){
                String movieId = rs.getString("id");
                String movieTitle = rs.getString("title");

                String sqlInsert = "insert into ft (entryID, entry) values(?,?)";
                PreparedStatement insertStatement = conn.prepareStatement(sqlInsert);
                insertStatement.setString(1,movieId);
                insertStatement.setString(2,movieTitle);
                insertStatement.execute();

            }

            rs.close();
            statement.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }




    }

}
