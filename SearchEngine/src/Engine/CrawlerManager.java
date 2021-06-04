package Engine;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CrawlerManager implements Runnable {


    int nThreads;

    static public boolean userTerminates;

    BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

    CrawlerManager() {
        userTerminates = false;

    }


    @Override
    public void run() {
        Thread.currentThread().setName("CrawlerManager");
        //System.out.println("Enter the number of crawler threads: ");
        try {
            nThreads = Integer.parseInt(reader.readLine());
        } catch (IOException e) {
            Logger.getLogger(CrawlerManager.class.getName()).log(Level.SEVERE, null, e);
        }

        Thread[] CrawlerThreads = new Thread[nThreads];
        //System.out.println("If you wish to stop the crawler: press any key");
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
        //Executer.CrawlerEnd = 1;

    }

    public static void main(String[] args) {

    }
}
