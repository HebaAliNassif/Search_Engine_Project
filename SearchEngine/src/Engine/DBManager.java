package Engine;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;

import java.util.logging.Level;
import java.util.logging.Logger;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoIterable;

public class DBManager {
    String connectionString = "mongodb+srv://heba12345:heba12345@cluster0.fgtdk.mongodb.net/SearchEngineProject?retryWrites=true&w=majority";
    String databaseName = "SearchEngineProject";
    MongoDatabase database;
    MongoClient mongoClient;

    public DBManager() {
        try
        {
            //Set level of warnings
            Logger.getLogger("org.mongodb.driver").setLevel(Level.WARNING);

            //Create Mongo Client
            mongoClient = MongoClients.create(connectionString);

            //Get search engine database
            database = mongoClient.getDatabase(databaseName);

            System.out.println("Database connected");

        }catch (Exception e)
        {
            System.err.println("Database connection failed");
            System.exit(-1);
        }
    }
    public void closeDBConnection(){
        try {
            if (mongoClient != null) {
                mongoClient.close();
                System.out.println("Connection closed");
            }
        }catch (Exception e)
        {
            System.err.println("Database connection closing failed");
            System.exit(-1);
        }

    }
}
