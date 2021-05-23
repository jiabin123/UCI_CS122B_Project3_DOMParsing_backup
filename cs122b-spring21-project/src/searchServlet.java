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

@WebServlet(name = "searchServlet", urlPatterns = "/api/search")
public class searchServlet extends HttpServlet {


    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        response.setContentType("application/json"); // Response mime type

        System.out.println("we get here");

        String type = (String) request.getParameter("search-option");

        String content = (String)request.getParameter("content");

        String title =  (String)request.getParameter("title");
        String star = (String) request.getParameter("star");
        String year = (String) request.getParameter("year");
        String director =  (String)request.getParameter("director");
//        System.out.println("==============");
//        System.out.println(title+","+star+","+year+","+director);
        String temp = "";
        boolean first = true;
//        System.out.println("content:" + content);
//        System.out.println(content==null); // true
//        System.out.println(director.equals(""));
//        System.out.println(content.equals(""));
        if (content==null){
            type = "multiSearch";
        }
//        System.out.println("type: "+ type);
        if (type =="multiSearch" ){
            if (!title.equals("")){
//                System.out.println("1");
//                temp += "and m.title like '%" + title + "%' ";
                temp += "-title:" + title+ "-";

            }
            if (!star.equals("")){
//                System.out.println("2");
//                temp += " and s.name like '%" + star + "%' ";
                temp += "-star:" + star+ "-";

            }
            if (!director.equals("")){
//                System.out.println("3");
//                temp += " and s.name like'%" + director + "%' ";
                temp += "-director:" + director+ "-";

            }
            if (!year.equals("")){
//                System.out.println("4");
//                temp += " and m.year ='" + year + "' ";
                temp += "-year:" + year+ "-";

            }
        }else{
            temp = content;
        }
        content = temp;
//        System.out.println("type: "+ type);
//        System.out.println("conetnt: "+ content);
//        System.out.println("content:" + content ); //"content:"
//        System.out.println(content.equals(null)); // false
//        System.out.println(content.equals("")); // true
        // Output stream to STDOUT

        // Output stream to STDOUT
        PrintWriter out = response.getWriter();


        JsonArray jsonArray = new JsonArray();
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("type", type);
        jsonObject.addProperty("content", content);


        jsonArray.add(jsonObject);

        out.write(jsonArray.toString());

        // set response status to 200 (OK)
        response.setStatus(200);

        out.close();
    }
}
