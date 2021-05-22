import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;


public class domTest {
    List<Movies> movies = new ArrayList<>();
    List<movieGenres> movieGenres = new ArrayList<>();
    List<movieGenres> inconsistency_data = new ArrayList<>();
    Document dom;
    List<String> genres = new ArrayList<>();

    private void parseXmlFile() {
        // get the factory
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();

        try {

            // using factory get an instance of document builder
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();

            // parse using builder to get DOM representation of the XML file
            dom = documentBuilder.parse("mains243.xml");

        } catch (ParserConfigurationException | SAXException | IOException error) {
            error.printStackTrace();
        }
    }



    private void parseDocument() {
        // get the document root Element
        Element documentElement = dom.getDocumentElement();

        // get a nodelist of employee Elements, parse each into Employee object
        NodeList nodeList = documentElement.getElementsByTagName("directorfilms");
        if (nodeList != null) {
            for (int i = 0; i < nodeList.getLength(); i++) {

                // get the directorfilms element
                Element element = (Element) nodeList.item(i);

                // get the director object
                parseDirecotr(element);

            }
        }
    }

    private void parseDirecotr(Element element) {
        NodeList nodeList = element.getElementsByTagName("director");
        String dirid = "";
        String dirstart = "";
        String dirname = "";
        String converage = "";
        if (nodeList != null) {
            for (int i = 0; i < nodeList.getLength(); i++) {

                Element element2 = (Element) nodeList.item(i);

                dirid = getTextValue(element2, "dirid");
                dirstart = getTextValue(element2, "dirstart");
                dirname = getTextValue(element2, "dirname");
                converage = getTextValue(element2, "coverage");

            }
        }

        String fid = "";
        String t = "";
        int year = 0;
        String genres = "";
        nodeList = element.getElementsByTagName("film");
        if (nodeList != null) {
            for (int i = 0; i < nodeList.getLength(); i++) {

                Element element2 = (Element) nodeList.item(i);

                //An internally generated id for the film.  This is the key of the film entries and is unique.
                fid = getTextValue(element2, "fid");
                //The film's title
                t = getTextValue(element2, "t");
                //Year the movie was completed
                year = getIntValue(element2, "year");

                NodeList cats = element2.getElementsByTagName("cats");
                List<String> temp = new ArrayList<>();
                if(cats!=null){
                    for(int k=0; k<cats.getLength(); k++){
                        Element element3 = (Element) nodeList.item(k);
                        temp.add(getTextValue(element3,"cat").trim());
                    }
                }

                Movies movie = new Movies(fid, t, year, dirname);
                movies.add(movie);
                movieGenres mg = new movieGenres(fid, temp);
                movieGenres.add(mg);

            }
        }

    }

    /**
     * Calls getTextValue and returns a int value
     */
    private int getIntValue(Element element, String tagName) {
        // in production application you would catch the exception
        String str = getTextValue(element, tagName);
        if(str.matches("^[0-9]+$"))  return Integer.parseInt(getTextValue(element, tagName));
        return -1;
    }

    private String getTextValue(Element element, String tagName) {
        String textVal = null;

        NodeList nodeList = element.getElementsByTagName(tagName);
        if(nodeList.item(0)==null){
            return "null";
        }

        if(nodeList.item(0).getFirstChild()!=null && nodeList.item(0).getFirstChild().getNodeValue()!=null){
            textVal = nodeList.item(0).getFirstChild().getNodeValue();
        }
        else{
            textVal = "null";
        }

        return textVal;
    }

    private void insertGenresTable(domTest dt, Connection conn, PreparedStatement psInsertRecord, String sqlInsertRecord){
        int[] iNoRows=null;
        //create table if not exists genres(id int primary key not null auto_increment, name varchar(32) not null );
        System.out.println("start inserting records into genres table....");
        System.out.println("the genres data size to be inserted: " + dt.movieGenres.size());
        long starTime = System.currentTimeMillis();
        sqlInsertRecord="insert into genres (name) values(?)";
        int index = 0;
        int null_number = 0;
        HashSet<String> duplicate = new HashSet<>();
        while(index < dt.movieGenres.size()){
            try {
                conn.setAutoCommit(false);

                psInsertRecord=conn.prepareStatement(sqlInsertRecord);

                for(int i=0; i<50 && index<dt.movieGenres.size(); i++)
                {

                    List<String> genres_list = dt.movieGenres.get(index).getGenres();
                    for(String temp : genres_list){
                        if(!temp.equals("null") && !genres.contains(temp) && !existsGneres(temp, genres)){
                            psInsertRecord.setString(1, temp);
                            psInsertRecord.addBatch();
                            duplicate.add(temp);
                            genres.add(temp);

                        }else {
                            null_number++;
                        }
                    }
                    index++;
                }
                iNoRows=psInsertRecord.executeBatch();
                conn.commit();

            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        long endTime = System.currentTimeMillis();
        System.out.println("finish inserting records....");
        System.out.println("the number of Duplicate entry and null entry: " + null_number);
        System.out.println("it took " + (endTime-starTime) + " milliseconds");
    }

    private boolean existsGneres(String temp, List<String> genres){

        for(String str : genres){
            if(str.indexOf(temp) == 0) return true;
        }
        return false;
    }

    private int getGenreId(String genre){
        for(int i=0; i<genres.size(); i++){
            if(genre.equals(genres.get(i))) return i+1;
        }
        return -1;
    }
    private void insertGenres_in_Movies(domTest dt, Connection conn, PreparedStatement psInsertRecord, String sqlInsertRecord){
        int[] iNoRows=null;

        //create table if not exists genres_in_movies(genreId int not null, movieId varchar(10) not null, foreign key (genreId) references genres(id), foreign key (movieId) references movies(id) );
        System.out.println("start inserting records into generes_in_movies table....");
        System.out.println("the movies data size: " + dt.movieGenres.size());
        long starTime = System.currentTimeMillis();
        sqlInsertRecord="insert into genres_in_movies (genreId, movieId) values(?,?)";
        int index = 0;
        int null_number = 0;
        while (index < dt.movieGenres.size()){
            try {
                conn.setAutoCommit(false);

                psInsertRecord=conn.prepareStatement(sqlInsertRecord);

                for(int i=0; i<50 && index<dt.movieGenres.size(); i++)
                {
                    List<String> generes_list = dt.movieGenres.get(index).getGenres();
                    if(dt.movieGenres.get(index).getMovieId().equals("null")){
                        inconsistency_data.add(dt.movieGenres.get(index));
                        index++;
                        continue;
                    }
                    for(String temp : generes_list){
                        int genreId = getGenreId(temp);
                        if(genreId == -1 ){
                            // this pair is inconsistency data
                            inconsistency_data.add(dt.movieGenres.get(index));
//                            index++;
                            continue;
                        }
                        if(!temp.equals("null")  ){
                            psInsertRecord.setInt(1, genreId);
                            psInsertRecord.setString(2, dt.movieGenres.get(index).getMovieId());
                            psInsertRecord.addBatch();
                        }else {
                            null_number++;
                        }
//                        index++;
                    }
                    index++;
                }
                iNoRows=psInsertRecord.executeBatch();
                conn.commit();

            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        long endTime = System.currentTimeMillis();
        System.out.println("finish inserting records....");
        System.out.println("it took " + (endTime-starTime) + " milliseconds");
    }


    private void insertMovieTable(domTest dt, Connection conn, PreparedStatement psInsertRecord, String sqlInsertRecord){

        int[] iNoRows=null;

        //create table if not exists movies(id varchar(10) primary key not null, title varchar(100) not null, year int not null, director varchar(100) not null );
        System.out.println("start inserting records into movies table....");
        System.out.println("the movies data size: " + dt.movies.size());
        long starTime = System.currentTimeMillis();
        sqlInsertRecord="insert into movies (id, title, year, director) values(?,?,?,?)";
        int index = 0;
        int null_number = 0;
        HashSet<String> duplicate = new HashSet<>();
        while(index < dt.movies.size()){
            try {
                conn.setAutoCommit(false);

                psInsertRecord=conn.prepareStatement(sqlInsertRecord);

                for(int i=0; i<50; i++)
                {
                    if(index<dt.movies.size() && !dt.movies.get(index).getId().equals("null") && !duplicate.contains(dt.movies.get(index).getId())){
                        psInsertRecord.setString(1, dt.movies.get(index).getId());
                        psInsertRecord.setString(2,dt.movies.get(index).getTitle());
                        psInsertRecord.setInt(3,dt.movies.get(index).getYear());
                        psInsertRecord.setString(4,dt.movies.get(index).getDirector());
                        psInsertRecord.addBatch();
                        duplicate.add(dt.movies.get(index).getId());
                    }else {
                        null_number++;
                    }
                    index++;
                }
                iNoRows=psInsertRecord.executeBatch();
                conn.commit();

            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        long endTime = System.currentTimeMillis();
        System.out.println("finish inserting records....");
        System.out.println("the number of Duplicate entry and null entry: " + null_number);
        System.out.println("it took " + (endTime-starTime) + " milliseconds");
    }

    public static void main(String[] args) throws InstantiationException, IllegalAccessException, ClassNotFoundException{
        domTest dt = new domTest();

        System.out.println("Start parsing main243.xml....");
        long starTime = System.currentTimeMillis();
        dt.parseXmlFile();
        dt.parseDocument();
        long endTime = System.currentTimeMillis();
        System.out.println("finished parsing main243.xml.....");
        System.out.println("it took " + (endTime-starTime) + " milliseconds");


        String[] arr = new String[] {"Action", "Adult", "Adventure", "Animation", "Biography", "Comedy","Crime",
                "Documentary","Drama", "Family","Fantasy","History","Horror","Music","Musical","Mystery","Reality-TV",
                "Romance","Sci-Fi","Sport","Thriller","War", "Western"};

        for(String str : arr){
            dt.genres.add(str);
        }
        // connect to jdbc
        Connection conn = null;

        Class.forName("com.mysql.jdbc.Driver").newInstance();
        String jdbcURL="jdbc:mysql://localhost:3306/moviedbexample";

        try {
            conn = DriverManager.getConnection(jdbcURL,"root", "a5638198");
        } catch (SQLException e) {
            e.printStackTrace();
        }

        PreparedStatement psInsertRecord=null;
        String sqlInsertRecord=null;

        // call the insertion function
        dt.insertMovieTable(dt,conn, psInsertRecord,sqlInsertRecord);
        dt.insertGenresTable(dt,conn, psInsertRecord,sqlInsertRecord);
        dt.insertGenres_in_Movies(dt, conn, psInsertRecord, sqlInsertRecord);

//        System.out.println(dt.inconsistency_data);

        try {
            if(psInsertRecord!=null) psInsertRecord.close();
            if(conn!=null) conn.close();
        } catch(Exception e) {
            e.printStackTrace();
        }

        try {
            FileWriter myWriter = new FileWriter("fileData.txt");
            for(movieGenres mg : dt.inconsistency_data){
                myWriter.write("inconsistency_movieId: " + mg.getMovieId() + "\n");

            }
            myWriter.close();
            System.out.println("Successfully wrote to the file.");
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }

    }

}
