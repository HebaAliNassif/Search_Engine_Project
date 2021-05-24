package Engine;

import org.jsoup.Jsoup;

import java.net.URI;

public class WebPageParserManager implements Runnable{

    @Override
    public void run() {
        WebPage webPage;
        while (true)
        {
            try {
                synchronized (Main.ParserQueue) {
                    webPage = Main.ParserQueue.remove();
                }
            }catch (Exception e){
                if(Main.crawlerEnd && Main.ParserQueue.size() == 0)
                {
                    Main.webPageParserEnd = true;
                    return;
                }
                else
                {
                    continue;
                }
            }
            Thread webPageParser = new Thread(new WebPageParser(webPage));
            webPageParser.setName("WebPageParser");
            webPageParser.start();

            try {
                webPageParser.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
