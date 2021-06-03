package Engine;

import java.io.IOException;
import java.io.BufferedReader;
import java.io.FileReader;
import java.net.MalformedURLException;
import java.util.*;

public class Crawler {

    Crawler() {
    }

    private static final int MAX_PAGES_TO_SEARCH = 5000;
    public Set<String> PagesVisited = new HashSet<String>();
    public List<String> PagesToVisit = new LinkedList<String>(); //depth first approach

    public void PopulatePagesToVisit(String FileName) {
        BufferedReader reader;
        try {
            reader = new BufferedReader(new FileReader(FileName));
            String line = reader.readLine();
            while (line != null) {
                line = reader.readLine();
                this.PagesToVisit.add(line);
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public String NextUrl() {
        String NextUrl = this.PagesToVisit.remove(0);

        while (this.PagesVisited.contains(NextUrl)) {
            NextUrl = this.PagesToVisit.remove(0);
        }
        this.PagesVisited.add(NextUrl);
        return NextUrl;
    }

    public void Search(String URL) throws IOException {

        while (this.PagesVisited.size() < MAX_PAGES_TO_SEARCH) {
            System.out.println("size of pages visited" + this.PagesVisited.size());
            System.out.println("size of pages to visit" + this.PagesToVisit.size());
            String CurrentURL;
            SpiderLeg Leg = new SpiderLeg();

            if (this.PagesToVisit.isEmpty()) {  // if the seeds are empty , we will only search given URL
                CurrentURL = URL;
                this.PagesVisited.add(CurrentURL);

            } else {
                CurrentURL = this.NextUrl();
                System.out.println(CurrentURL);
            }



            RobotManager RB = new RobotManager();
            boolean isRobotSafe = RB.RobotSafe(CurrentURL);

            if (isRobotSafe && !(CurrentURL ==null))
                Leg.Crawl(CurrentURL);


            this.PagesToVisit.addAll(Leg.GetLinks());
        }
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