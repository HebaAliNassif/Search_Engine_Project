package Engine;

import java.io.IOException;
import java.util.*;

public class Crawler {

    Crawler() {

    }

    private static final int MAX_PAGES_TO_SEARCH = 1000;
    public Set<String> PagesVisited = new HashSet<String>();
    public List<String> PagesToVisit = new LinkedList<String>(); //depth first approach


    public String NextUrl() {
        String NextUrl = this.PagesToVisit.remove(0);

        while (this.PagesVisited.contains(NextUrl)) {
            NextUrl = this.PagesToVisit.remove(0);
        }
        this.PagesVisited.add(NextUrl);
        return NextUrl;
    }

    public void Search(String URL, String KeyWord) throws IOException {

        while (this.PagesVisited.size() < MAX_PAGES_TO_SEARCH) {
            String CurrentURL;
            SpiderLeg Leg = new SpiderLeg();

            if (this.PagesToVisit.isEmpty()) {  // if the seeds are empty , we will only search given URL
                CurrentURL = URL;
                this.PagesVisited.add(CurrentURL);

            } else {
                CurrentURL = this.NextUrl();
            }

            Leg.Crawl(CurrentURL);

            boolean WordFound = Leg.searchForWord(KeyWord);
            if(WordFound)


            this.PagesVisited.addAll(Leg.GetLinks());
        }
    }


   /* public static void main(String[] args) {
        Crawler c = new Crawler();
        c.PagesToVisit.add("www.google.com");

        String URL = c.NextUrl();
    }*/

}