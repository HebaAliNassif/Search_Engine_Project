package com.searchengine;

import com.mongodb.*;

public class DataBase {

MongoClient mongoClient;
DB indexerDB;
DBCollection indexCollection;
  
DB crawlerDB;
DBCollection crawlerCollection;

    public DataBase() {
        this.mongoClient = new MongoClient("localhost", 27017);
        indexerDB = mongoClient.getDB("indexerDB");
        crawlerDB = mongoClient.getDB("CrawlerDB");
        crawlerCollection = crawlerDB.getCollection("crawlerCollection");
        indexCollection = indexDB.getCollection("indexCollection");
        //indexCollection.createIndex(new BasicDBObject("word", 1).append("doc_url", 1), "indexEntry", true);
        indexCollection.createIndex("doc_url");
        
    }
}