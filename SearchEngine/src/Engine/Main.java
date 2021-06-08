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

        Thread crawlerManager = new Thread( new CrawlerManager());
        crawlerManager.setName("CrawlerManager");

        databaseManager = new DB();
        crawlerManager.start();
        webPageParseManager.start();
        indexerManager.start();

        crawlerManager.join();
        webPageParseManager.join();
        indexerManager.join();

        System.out.println("Finished Indexing");
        databaseManager.countKeyword();

        databaseManager.closeConnection();
    }
}
