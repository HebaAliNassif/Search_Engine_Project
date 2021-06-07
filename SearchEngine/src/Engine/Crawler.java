package Engine;

import java.io.*;
import java.net.MalformedURLException;


public class Crawler implements Runnable {

    Crawler() {

    }


    private static final int MAX_PAGES_TO_SEARCH = 50;
   
    //public Set<String> PagesVisited = new HashSet<String>();
    //public List<String> PagesToVisit = new LinkedList<String>(); //breadth first approach

    // public void PopulatePagesToVisit(String FileName) {
    //   BufferedReader reader;


    public String NextUrl() throws MalformedURLException {
        synchronized (CrawlerManager.PagesToVisit) {
            String NextUrl = CrawlerManager.PagesToVisit.remove(0);
            
            while (CrawlerManager.PagesVisited.contains(NextUrl)) {
                NextUrl = CrawlerManager.PagesToVisit.remove(0);
                CrawlerManager.actual_idx++;
            }
            CrawlerManager.PagesVisited.add(NextUrl);
           
            System.out.println("Updating Stat...");
            try {
	            FileWriter myWriter = new FileWriter(CrawlerManager.FILE_STAT);
	            myWriter.write(Integer.toString(CrawlerManager.numVisited) + "\n");
	            myWriter.write(Integer.toString(CrawlerManager.actual_idx));
	            myWriter.close();
            }
            catch (IOException e) {
				System.out.println("couldn't Update STAT File!!!");
			}
            System.out.println("\nnum Visited: " + CrawlerManager.numVisited);

            //NextUrl = URI.create((new URL(NextUrl)).toString()).normalize().toString();

            return NextUrl;
        }

    }

    public void Search(String URL) throws IOException {


        while (true) {
            synchronized (CrawlerManager.PagesVisited) {
                if (CrawlerManager.numVisited >= MAX_PAGES_TO_SEARCH) {
                    break;
                }
            }

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
                    
                    CrawlerManager.pagesWriter.write(CurrentURL+"\n");
                    CrawlerManager.actual_idx++;
                    CrawlerManager.numVisited++;
                    synchronized (Main.ParserQueue) {
                        Main.ParserQueue.add(webPage);
                    }
                }

                synchronized (CrawlerManager.PagesToVisit) {
                	// Add only of PagesToVist.length < MAX_PAGES 
                	if(CrawlerManager.numVisited < Crawler.MAX_PAGES_TO_SEARCH) {
                		CrawlerManager.PagesToVisit.addAll(Leg.GetLinks());
	                	for(String link: Leg.GetLinks()) {
	                		CrawlerManager.pagesWriter.write(link+"\n");
	                	}
                	}
                	else
                		System.out.println("Insertion Denied, Visited has Reached Max");
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
//        Crawler c = new Crawler();
    	System.out.println("Main Start");
        try {
            CrawlerManager CW = new CrawlerManager();
            Thread Crawler = new Thread(CW);
            Crawler.start();
            Crawler.join();
            // Set the stat File to 0
        } catch (InterruptedException e) {

        }
        // Write 0 to stat when done
        
        
        //c.PopulatePagesToVisit("seeds.txt");
        //c.Search("https://www.yahoo.com");
        /*Iterator it = c.PagesVisited.iterator();

        while (it.hasNext()) {
            System.out.println(it.next());
        }*/

    }

}