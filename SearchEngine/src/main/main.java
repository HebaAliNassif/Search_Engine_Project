package main;

import indexer.Indexer;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;


public class Main {
    public static void main(String[] args) throws IOException, URISyntaxException {
        System.out.println("Hello from Main");

        //Only for testing the indexer
        String urlString = "https://en.wikipedia.org/wiki/Ancient_Egypt";
        Indexer indexer = new Indexer();
        URL url = new URL(urlString);
        Document doc = Jsoup.connect(urlString).get();
        indexer.indexWebPage(url, doc);
    }
}
