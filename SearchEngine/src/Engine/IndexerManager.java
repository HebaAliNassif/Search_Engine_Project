package Engine;

public class IndexerManager implements Runnable{
    public static int indexedPagesCount = 0;
    @Override
    public void run() {
        while(true)
        {
            WebPage webPage;
            try {
                synchronized (Main.IndexerQueue) {

                    webPage = Main.IndexerQueue.remove();

                }
            } catch (Exception ex) {
                if(Main.webPageParserEnd && Main.IndexerQueue.size() == 0)
                {
                    Main.indexerEnd = true;
                    return;
                }
                else
                {
                    continue;
                }
            }
            Thread webPageIndexer = new Thread(new Indexer(webPage));
            webPageIndexer.setName("WebPageIndexer");
            webPageIndexer.start();

            try {
                webPageIndexer.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
