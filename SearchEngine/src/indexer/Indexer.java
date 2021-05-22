package indexer;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoIterable;

import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Indexer {
    String connectionString = "mongodb+srv://heba12345:heba12345@cluster0.fgtdk.mongodb.net/SearchEngineProject?retryWrites=true&w=majority";
    String databaseName = "SearchEngineProject";
    public Indexer() {
        try
        {
            //Set level of warnings
            Logger.getLogger("org.mongodb.driver").setLevel(Level.WARNING);

            //Create Mongo Client
            MongoClient mongoClient = MongoClients.create(connectionString);

            //Get search engine database
            MongoDatabase database = mongoClient.getDatabase(databaseName);

            //Get the information from the database

        }catch (Exception e)
        {
            System.err.println("Database connection failed");
            System.exit(-1);
        }
    }
    /*
     *Index the given web page
     */
    public void indexWebPage(URL url, org.jsoup.nodes.Document pageHTML) throws URISyntaxException {
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    WebPageParser webPageParser = new WebPageParser(url, pageHTML);
                } catch (URISyntaxException e) {
                    e.printStackTrace();
                }
            }
        });

        t.setName("Indexer-Thread\t");
        t.start();
    }

}
