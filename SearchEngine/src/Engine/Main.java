package Engine;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.*;


public class Main {
    
    protected static Queue<WebPage> IndexerQueue = new LinkedList<>();
    protected static Queue<WebPage> ParserQueue = new LinkedList<>();
    static boolean crawlerEnd = false;
    static boolean webPageParserEnd = false;
    static boolean indexerEnd = false;
    static public DB databaseManager;
    public static void main(String[] args) throws IOException, URISyntaxException, InterruptedException {
        System.out.println("Hello from Main");

        Thread webPageParseManager = new Thread(new WebPageParserManager());
        webPageParseManager.setName("WebPageParseManager");

        Thread indexerManager = new Thread(new IndexerManager());
        indexerManager.setName("IndexerManager");

        //////////////////////////////////////////////
        //Only for testing the indexer
        /*Scanner scanner = new Scanner(new File("links.txt"));
        List<String> links = new ArrayList<String>();
        String token1 = "";
        while (scanner.hasNext()) {
            // find next line
            token1 = scanner.next();
            links.add(token1);
        }
        scanner.close();
        String urlString = "";
        for (int i = 0; i < links.size(); i++) {
            urlString = links.get(i);
            String url = URI.create((new URL(urlString)).toString()).normalize().toString();
            Document doc = Jsoup.parse((Jsoup.connect(urlString).get()).toString());
            WebPage webPage = new WebPage(url, doc);
            ParserQueue.add(webPage);
        }
        crawlerEnd = true;*/
        //////////////////////////////////////////////
        Crawler c = new Crawler();
        c.PopulatePagesToVisit("seeds.txt");
        c.Search(Crawler.PagesToVisit.get(0));

        databaseManager = new DB();
        webPageParseManager.start();
        indexerManager.start();

        QueryProcessor Q = new QueryProcessor();
        Q.Process("Troy Egypt");

        webPageParseManager.join();
        indexerManager.join();

        //databaseManager.countKeyword();

        databaseManager.closeConnection();
    }
}
