package Engine;


import java.io.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class CrawlerManager implements Runnable {


    int nThreads;

    // static public boolean userTerminates;

    public static Set<String> PagesVisited = new HashSet<String>();
    public static List<String> PagesToVisit = new LinkedList<String>(); //breadth first approach
    public static List<String> docs =  new LinkedList<String>();
    public static final String FILE_STAT = "stat.txt";
    public static final String FILE_SEED = "seeds.txt";
    public static final String FILE_PAGE = "pages.txt";
    public static int actual_idx = 0;
    public static int numVisited = 0;

    public static FileWriter pagesWriter;
    public static File stat_file;
    public static File pages_file;
    public static File seeds_file;
    BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

    CrawlerManager() {
        CrawlerManager.stat_file = new File(FILE_STAT);
        CrawlerManager.pages_file = new File(FILE_PAGE);
        CrawlerManager.seeds_file = new File(FILE_SEED);

        try {
            PopulatePagesToVisit("seeds.txt");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private static void readPagesToPVT(File f) throws IOException {
        Scanner scanner = new Scanner(f);

        // Skip Visited and duplicated Pages
        int max = 0;
        while(max < CrawlerManager.actual_idx && scanner.hasNext()) {
            max++;
            String temp = scanner.next();
        }
        System.out.println(max);
        // Populate the PagesToVisit
        String token1 = "";
        System.out.println("Reading " + f.getName());
        while (scanner.hasNext()) {
            token1 = scanner.next();
            synchronized (CrawlerManager.PagesVisited) {
                CrawlerManager.PagesToVisit.add(token1);
            }
        }
        CrawlerManager.pagesWriter = new FileWriter(FILE_PAGE);
        scanner.close();
    }

    private static void writePVTtoPages() throws IOException {
        System.out.println("Writing init to Pages.txt");
        for(String page: CrawlerManager.PagesToVisit)
            CrawlerManager.pagesWriter.write(page + "\n");
    }

    public void PopulatePagesToVisit(String FileName) throws IOException {
        System.out.println("Populating the PagesToVisit");
        System.out.println("Checking Previous State");

        // read stat_file to check for prev_stat
        if(!stat_file.createNewFile()) {	//file already exists
            System.out.println("Previous state was detected... retreiving last info....");
            Scanner stateScanner = new Scanner(stat_file);
            CrawlerManager.numVisited = stateScanner.nextInt();
            CrawlerManager.actual_idx = stateScanner.nextInt();
            System.out.println("Previous Parameters: " + CrawlerManager.actual_idx + " " + CrawlerManager.numVisited);
            stateScanner.close();
            CrawlerManager.readPagesToPVT(CrawlerManager.pages_file);
            CrawlerManager.writePVTtoPages();
        }

        else {	// Start Fresh if state == 0 or file doesn't exist
            CrawlerManager.pages_file.createNewFile();
            System.out.println("No Previous state was detected... Reading " + FILE_SEED);
            CrawlerManager.readPagesToPVT(CrawlerManager.seeds_file);
            CrawlerManager.writePVTtoPages();
        }
    }

    @Override
    public void run() {
        Thread.currentThread().setName("CrawlerManager");
        //System.out.println("Enter the number of crawler threads: ");
        try {
            System.out.print("Please, Enter Number of Threads: ");
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

        for (int i = 0; i < nThreads; i++) {
            try {
                CrawlerThreads[i].join();
            } catch (InterruptedException ex) {
                Logger.getLogger(CrawlerManager.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        System.out.println("Crawler Done");
        try {
            CrawlerManager.pagesWriter.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        CrawlerManager.stat_file.delete();
        CrawlerManager.pages_file.delete();
    }

    public static void main(String[] args) {

    }
}