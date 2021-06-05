package Engine;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.*;

public class Crawler {

    Crawler() {
    }

    private static final int MAX_PAGES_TO_SEARCH = 500;
    static public Set<String> PagesVisited = new HashSet<String>();
    static public List<String> PagesToVisit = new LinkedList<String>(); //depth first approach

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

        while (this.PagesVisited.size() < MAX_PAGES_TO_SEARCH) {
            //System.out.println("size of pages visited" + this.PagesVisited.size());
            //System.out.println("size of pages to visit" + this.PagesToVisit.size());
            String CurrentURL;
            SpiderLeg Leg = new SpiderLeg();

            if (this.PagesToVisit.isEmpty()) {  // if the seeds are empty , we will only search given URL
                CurrentURL = URL;
                this.PagesVisited.add(CurrentURL);

            } else {
                CurrentURL = this.NextUrl();
                //System.out.println(CurrentURL);
            }



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
        Main.crawlerEnd = true;
    }


    public static void main(String[] args) throws IOException, MalformedURLException {
        Crawler c = new Crawler();
        c.PopulatePagesToVisit("seeds.txt");
        c.Search("https://www.yahoo.com");
        /*Iterator it = c.PagesVisited.iterator();

        while (it.hasNext()) {
            System.out.println(it.next());
        }*/

    }

}