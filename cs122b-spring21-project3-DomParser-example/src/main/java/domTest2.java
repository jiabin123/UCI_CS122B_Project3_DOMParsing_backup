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
import java.util.HashSet;
import java.util.List;

public class domTest2 {
    List<Stars> stars = new ArrayList<>();
    List<starMovies> starMovies = new ArrayList<>();
    HashSet<String> inconsistencies = new HashSet<>();
    Document dom;

    private void parseActorXml() {
        // get the factory
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();

        try {

            // using factory get an instance of document builder
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();

            // parse using builder to get DOM representation of the XML file
            dom = documentBuilder.parse("actors63.xml");

        } catch (ParserConfigurationException | SAXException | IOException error) {
            error.printStackTrace();
        }
    }

    private void parseActorDocument() {
        // get the document root Element
        Element documentElement = dom.getDocumentElement();

        // get a nodelist of employee Elements, parse each into Employee object
        NodeList nodeList = documentElement.getElementsByTagName("actor");
        int id = 0;
        if (nodeList != null) {
            for (int i = 0; i < nodeList.getLength(); i++) {

                // get the directorfilms element
                Element element = (Element) nodeList.item(i);

                // get the director object
                parseActor(element, id++);

            }
        }
    }
    private void parseActor(Element element, int id) {
        String starId = "sid";

        starId += id;

        String name = getTextValue(element, "stagename");
        String dob = getTextValue(element, "dob");

        stars.add(new Stars(starId, name, dob));

    }


    private void parseCastXml() {
        // get the factory
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();

        try {

            // using factory get an instance of document builder
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();

            // parse using builder to get DOM representation of the XML file
            dom = documentBuilder.parse("casts124.xml");

        } catch (ParserConfigurationException | SAXException | IOException error) {
            error.printStackTrace();
        }
    }


    private void parseDocument() {
        // get the document root Element
        Element documentElement = dom.getDocumentElement();

        // get a nodelist of employee Elements, parse each into Employee object
        NodeList nodeList = documentElement.getElementsByTagName("dirfilms");
        if (nodeList != null) {
            for (int i = 0; i < nodeList.getLength(); i++) {

                // get the directorfilms element
                Element element = (Element) nodeList.item(i);

                // get the director object
                parseFilms(element);

            }
        }
    }

    private void parseFilms(Element element) {
        NodeList nodeList = element.getElementsByTagName("filmc");

        if (nodeList != null) {
            for (int i = 0; i < nodeList.getLength(); i++) {

                Element element2 = (Element) nodeList.item(i);

                parseActors(element);
            }
        }
    }

    private void parseActors(Element element){
        NodeList nodeList = element.getElementsByTagName("m");

        if (nodeList != null) {
            for (int i = 0; i < nodeList.getLength(); i++) {

                Element element2 = (Element) nodeList.item(i);

                String movieId = getTextValue(element2,"f");
                String actor = getTextValue(element2, "a");
                String movieTitle = getTextValue(element2, "t");


                String starId = getStarId(stars, actor);

                if(starId.equals("null")){
                    // actors in casts124.xml, but missing in actors63.xml
                    inconsistencies.add(actor);
                }else{
                    starMovies sm = new starMovies(starId,movieId);
                    starMovies.add(sm);
                }
            }
        }
    }

    public String getStarId(List<Stars> stars, String actor){
        for(Stars s : stars){
            if(s.getName().equals(actor)){
                return s.getId();
            }
        }
        return "null";
    }

    /**
     * Calls getTextValue and returns a int value
     */
    private int getIntValue(Element ele, String tagName) {
        // in production application you would catch the exception
        return Integer.parseInt(getTextValue(ele, tagName));
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

    private void insertStarsTable(domTest2 dt, Connection conn, PreparedStatement psInsertRecord, String sqlInsertRecord){
        int[] iNoRows=null;

        //create table if not exists stars(id varchar(10) primary key not null, name varchar(100) not null, birthYear int );
        System.out.println("start inserting records into stars table....");
        System.out.println("the stars data size to be inserted: " + dt.stars.size());
        long starTime = System.currentTimeMillis();
        sqlInsertRecord="insert into stars (id, name, birthYear) values(?,?,?)";
        int index = 0;
        int null_number = 0;
        HashSet<String> duplicate = new HashSet<>();
        while(index < dt.stars.size()){
            try {
                conn.setAutoCommit(false);

                psInsertRecord=conn.prepareStatement(sqlInsertRecord);

                for(int i=0; i<50; i++)
                {
                    if(index<dt.stars.size() && !duplicate.contains(dt.stars.get(index).getId())){

                        psInsertRecord.setString(1, dt.stars.get(index).getId());
                        psInsertRecord.setString(2,dt.stars.get(index).getName());
                        if(dt.stars.get(index).getBirthYear().equals("null") || !dt.stars.get(index).getBirthYear().matches("[0-9]+")){
                            psInsertRecord.setNull(3, java.sql.Types.NULL);
                        }else {
                            psInsertRecord.setInt(3,Integer.parseInt(dt.stars.get(index).getBirthYear()));
                        }
                        psInsertRecord.addBatch();
                        duplicate.add(dt.stars.get(index).getId());
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

    private void insertStars_in_MoviesTable(domTest2 dt, Connection conn, PreparedStatement psInsertRecord, String sqlInsertRecord){
        int[] iNoRows=null;

        //create table if not exists stars_in_movies(starId varchar(10) not null, movieId varchar(10) not null, foreign key (starId) references stars(id), foreign key (movieId) references movies(id) );
        System.out.println("start inserting records into stars_in_movies table....");
        System.out.println("the data size to be inserted: " + dt.starMovies.size());
        long starTime = System.currentTimeMillis();
        sqlInsertRecord="insert into stars_in_movies (starId, movieId) values(?,?)";
        int index = 0;
        int null_number = 0;
        while(index < dt.starMovies.size()) {
            try {
                conn.setAutoCommit(false);

                psInsertRecord = conn.prepareStatement(sqlInsertRecord);

                for (int i = 0; i < 50; i++) {
                    if (index < dt.starMovies.size()) {

                        psInsertRecord.setString(1, dt.starMovies.get(index).getstarId());
                        psInsertRecord.setString(2, dt.starMovies.get(index).getmovieId());

                        psInsertRecord.addBatch();

                    } else {
                        null_number++;
                    }
                    index++;
                }
                iNoRows = psInsertRecord.executeBatch();
                conn.commit();

            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        long endTime = System.currentTimeMillis();
        System.out.println("finish inserting records....");
        System.out.println("it took " + (endTime-starTime) + " milliseconds");
    }

    public static void main(String[] args) throws InstantiationException, IllegalAccessException, ClassNotFoundException{
        domTest2 dt = new domTest2();

        // connect to jdbc
        Connection conn = null;

        Class.forName("com.mysql.jdbc.Driver").newInstance();
        String jdbcURL="jdbc:mysql://localhost:3306/moviedbexample";

        try {
            conn = DriverManager.getConnection(jdbcURL,"root", "a5638198");
        } catch (SQLException e) {
            e.printStackTrace();
        }


        // actor.xml
        System.out.println("Star parsing actors.xml....");
        long starTime = System.currentTimeMillis();
        dt.parseActorXml();
        dt.parseActorDocument();
        long endTime = System.currentTimeMillis();
        System.out.println("finished parsing actors.xml ....");
        System.out.println("it took " + (endTime-starTime) + " milliseconds");

        System.out.println("------------------------------------------------------------------------------------");

        // cats124.xml
        System.out.println("Star parsing casts124.xml....");
        starTime = System.currentTimeMillis();
        dt.parseCastXml();
        dt.parseDocument();
        endTime = System.currentTimeMillis();
        System.out.println("finished parsing casts124.xml.....");
        System.out.println("it took " + (endTime-starTime) + " milliseconds");


        PreparedStatement psInsertRecord=null;
        String sqlInsertRecord=null;

        // call the insertion function
        dt.insertStarsTable(dt,conn, psInsertRecord,sqlInsertRecord);

        dt.insertStars_in_MoviesTable(dt,conn, psInsertRecord,sqlInsertRecord);

        try {
            FileWriter myWriter = new FileWriter("fileData.txt");
            for(String s: dt.inconsistencies){
                myWriter.write("inconsistencies actor: " + s + "\n");

            }
            myWriter.close();
            System.out.println("Successfully wrote to the file.");
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }

    }

}
