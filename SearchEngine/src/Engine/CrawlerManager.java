package Engine;


import java.io.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CrawlerManager implements Runnable {


    int nThreads;

   // static public boolean userTerminates;

    public static Set<String> PagesVisited = new HashSet<String>();
    public static List<String> PagesToVisit = new LinkedList<String>(); //breadth first approach

    BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

    CrawlerManager() {
        try {
            PopulatePagesToVisit("seeds.txt");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void PopulatePagesToVisit(String FileName) throws FileNotFoundException {
        Scanner scanner = new Scanner(new File(FileName));
        String token1 = "";
        while (scanner.hasNext()) {
            // find next line
            token1 = scanner.next();
            //System.out.println(token1);
            synchronized (CrawlerManager.PagesVisited) {
            CrawlerManager.PagesToVisit.add(token1);
            }
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

    @Override
    public void run() {
        Thread.currentThread().setName("CrawlerManager");
        System.out.println("Enter the number of crawler threads: ");
        try {
            nThreads = Integer.parseInt(reader.readLine());
        } catch (IOException e) {
            Logger.getLogger(CrawlerManager.class.getName()).log(Level.SEVERE, null, e);
        }

        Thread[] CrawlerThreads = new Thread[nThreads];
        for (int i = 0; i < nThreads; i++) {
            CrawlerThreads[i] = new Thread(new Crawler());
            CrawlerThreads[i].setName("T" + i);
            CrawlerThreads[i].start();
        }

       /* for (int i = 0; i < nThreads; i++) {
            try {
                CrawlerThreads[i].join();
            } catch (InterruptedException ex) {
                Logger.getLogger(CrawlerManager.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        Executer.CrawlerEnd = 1;*/

    }

    public static void main(String[] args) {

    }
}
