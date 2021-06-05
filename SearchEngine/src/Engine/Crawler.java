package Engine;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.*;

public class Crawler implements Runnable {

    Crawler() {

    }


    private static final int MAX_PAGES_TO_SEARCH = 5000;
    //public Set<String> PagesVisited = new HashSet<String>();
    //public List<String> PagesToVisit = new LinkedList<String>(); //breadth first approach

    // public void PopulatePagesToVisit(String FileName) {
    //   BufferedReader reader;


    public String NextUrl() throws MalformedURLException {
        synchronized (CrawlerManager.PagesToVisit) {
            String NextUrl = CrawlerManager.PagesToVisit.remove(0);

            while (CrawlerManager.PagesVisited.contains(NextUrl)) {
                NextUrl = CrawlerManager.PagesToVisit.remove(0);
            }
            CrawlerManager.PagesVisited.add(NextUrl);

            //NextUrl = URI.create((new URL(NextUrl)).toString()).normalize().toString();

            return NextUrl;
        }

    }

    public void Search(String URL) throws IOException {


        while (true) {
            synchronized (CrawlerManager.PagesVisited) {
                if (CrawlerManager.PagesVisited.size() > MAX_PAGES_TO_SEARCH) {
                    break;
                }
            }
             //System.out.println("size of pages visited" + CrawlerManager.PagesVisited.size());
             System.out.println("size of pages to visit" + CrawlerManager.PagesToVisit.size());


            String CurrentURL = new String();
            boolean NotIsEmpty = false;
            SpiderLeg Leg = new SpiderLeg();
            synchronized (CrawlerManager.PagesToVisit) {
                if (!CrawlerManager.PagesToVisit.isEmpty()) {
                    NotIsEmpty = true;
                    CurrentURL = this.NextUrl();


                }
            }
            if (NotIsEmpty) {
                RobotManager RB = new RobotManager();
                boolean isRobotSafe = RB.RobotSafe(CurrentURL);


                if (isRobotSafe && !(CurrentURL == null)) {
                    Leg.Crawl(CurrentURL);
                    WebPage webPage = new WebPage(CurrentURL, Leg.document);
                    synchronized (Main.ParserQueue) {
                        Main.ParserQueue.add(webPage);
                    }
                }

                synchronized (CrawlerManager.PagesToVisit) {
                CrawlerManager.PagesToVisit.addAll(Leg.GetLinks());
                }
            }
        }


    }

    public void run() {
        System.out.println(Thread.currentThread().getName());
        try {
            Search("https://www.yahoo.com");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    public static void main(String[] args) throws IOException, MalformedURLException {
        Crawler c = new Crawler();
        try {
            CrawlerManager CW = new CrawlerManager();
            Thread Crawler = new Thread(CW);
            Crawler.start();
            Crawler.join();
        } catch (InterruptedException e) {

        }
        //c.PopulatePagesToVisit("seeds.txt");
        //c.Search("https://www.yahoo.com");
        /*Iterator it = c.PagesVisited.iterator();

        while (it.hasNext()) {
            System.out.println(it.next());
        }*/

    }

}