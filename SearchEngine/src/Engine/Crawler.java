package Engine;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.*;

public class Crawler implements Runnable {

    Crawler() {
        try {
            PopulatePagesToVisit("seeds.txt");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }


    private static final int MAX_PAGES_TO_SEARCH = 5000;
    public Set<String> PagesVisited = new HashSet<String>();
    public List<String> PagesToVisit = new LinkedList<String>(); //breadth first approach

   // public void PopulatePagesToVisit(String FileName) {
     //   BufferedReader reader;



    public void PopulatePagesToVisit(String FileName) throws FileNotFoundException {
        Scanner scanner = new Scanner(new File(FileName));
        String token1 = "";
        while (scanner.hasNext()) {
            // find next line
            token1 = scanner.next();
            //System.out.println(token1);
            PagesToVisit.add(token1);
        }
        /*BufferedReader reader;

        try {
            reader = new BufferedReader(new FileReader(FileName));
            String line = reader.readLine();
            while (line != null) {

                line = reader.readLine();
                synchronized (PagesVisited) {
                    this.PagesToVisit.add(line);
                }

                this.PagesToVisit.add(line);
                line = reader.readLine();

            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }*/
    }


    public String NextUrl() throws MalformedURLException {
        String NextUrl = this.PagesToVisit.remove(0);

        while (this.PagesVisited.contains(NextUrl)) {
            NextUrl = this.PagesToVisit.remove(0);
        }
        this.PagesVisited.add(NextUrl);



        NextUrl = URI.create((new URL(NextUrl)).toString()).normalize().toString();

        return NextUrl;

    }

    public void Search(String URL) throws IOException {


        while (true) {
            synchronized (PagesVisited) {
                if (this.PagesVisited.size() > MAX_PAGES_TO_SEARCH) {
                    break;
                }
            }
            System.out.println("size of pages visited" + this.PagesVisited.size());
            System.out.println("size of pages to visit" + this.PagesToVisit.size());


            String CurrentURL;
            SpiderLeg Leg = new SpiderLeg();
            synchronized (PagesToVisit) {
                if (!this.PagesToVisit.isEmpty()) {

                        CurrentURL = this.NextUrl();
                        //System.out.println(CurrentURL);

                        RobotManager RB = new RobotManager();
                        boolean isRobotSafe = RB.RobotSafe(CurrentURL);


            if (isRobotSafe && !(CurrentURL ==null))
            {
                Leg.Crawl(CurrentURL);
                WebPage webPage = new WebPage(CurrentURL, Leg.document);
                synchronized ( Main.ParserQueue) {
                    Main.ParserQueue.add(webPage);
                }
            }


                        this.PagesToVisit.addAll(Leg.GetLinks());

                }
            }
        }
    }

    public void run() {

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