import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class domTest3 {
    List<Stars> stars = new ArrayList<>();
    Document dom;

    private void parseXmlFile() {
        // get the factory
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();

        try {

            // using factory get an instance of document builder
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();

            // parse using builder to get DOM representation of the XML file
            dom = documentBuilder.parse("employees.xml");

        } catch (ParserConfigurationException | SAXException | IOException error) {
            error.printStackTrace();
        }
    }



    private void parseDocument() {
        // get the document root Element
        Element documentElement = dom.getDocumentElement();

        // get a nodelist of employee Elements, parse each into Employee object
        NodeList nodeList = documentElement.getElementsByTagName("actor");
        if (nodeList != null) {
            for (int i = 0; i < nodeList.getLength(); i++) {

                // get the directorfilms element
                Element element = (Element) nodeList.item(i);

                // get the director object
                parseActor(element);

            }
        }
    }

    private void parseActor(Element element) {
        String name = getTextValue(element, "stagename");
        String id = "";
        String dob = getTextValue(element, "dob");
//        System.out.println("name: " + name);
//        System.out.println("day of birth: " + dob);
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

    public static void main(String[] args) {
        domTest3 dt = new domTest3();
        System.out.println("Star parsing actors xml file....");
        long starTime = System.currentTimeMillis();
        dt.parseXmlFile();
        dt.parseDocument();
        long endTime = System.currentTimeMillis();
        System.out.println("finished parsing actors xml file.....");
        System.out.println("it took " + (endTime-starTime) + " milliseconds");

    }

}
