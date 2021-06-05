package Engine;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import org.jsoup.*;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.*;
import java.util.Set;

public class SpiderLeg {  // this class will take care of HTTPS requests


    private List<String> links = new LinkedList<String>(); // list of links
    public Set<String> CompactStrings = new HashSet<String>();
    private Document HTML_Document; // web page
    private static final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:88.0) Gecko/20100101 Firefox/88.0";

    SpiderLeg() {
    }

    public boolean Crawl(String URL) throws IOException {

        try {
            Connection connection = Jsoup.connect(URL).userAgent(USER_AGENT);  // user agent trick the web server that the robot is a normal browser
            Document HTML_Document = connection.get();  //https get request for the document , then parse HTML
            this.HTML_Document = HTML_Document;


            if (connection.response().statusCode() == 200) {
                System.out.println(" HTTPS request successful at URL = " + URL);
            }
            if (connection.response().contentType().contains("text/html")) {
                boolean NotDuplicate = this.InsertCompactString();

                if (NotDuplicate) {
                    Elements LinksOnPage = this.HTML_Document.select("a[href]");
                    for (Element E : LinksOnPage) {
                        this.links.add(E.absUrl("href"));
                    }
                    return true;
                } else {
                    System.out.println(" Duplicate Compact String , will not add links ! ");
                    return false;
                }

            } else {
                return false;
            }
        } catch (IllegalArgumentException e) {
            System.out.println(" must supply valid URL ");
            return false;
        } catch (HttpStatusException e) {
            System.out.println(" Error in Fetching URL ");
            return false;
        }
    }

    public boolean InsertCompactString() {

        String bodyText = this.HTML_Document.body().text();
        if (bodyText.length() == 0) {
            return false;
        } else {
            int UpperLimit = 50;
            if (bodyText.length() > UpperLimit) {
                UpperLimit = bodyText.length() - 1;
            }
            String CompactString = bodyText.substring(0, UpperLimit);
            if (!CompactStrings.contains(CompactString)) {
                CompactStrings.add(CompactString);
                return true;
            } else {
                return false;
            }
        }

    }

    public List<String> GetLinks() {
        return this.links;
    }

    public static void main(String[] args) throws IOException {

        SpiderLeg leg = new SpiderLeg();
        boolean x = leg.Crawl("https://www.google.com");
        if (x) {
            List<String> temp = leg.GetLinks();
            while (!temp.isEmpty()) {
                System.out.println(temp.remove(0));
            }

        }

    }


}
