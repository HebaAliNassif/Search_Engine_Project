package Engine;

import org.jsoup.internal.StringUtil;



public class WebPageParser implements Runnable {

    public static final String DEFAULT_PAGE_TITLE = "default title";
    private WebPage webPage;                //Current web page


    public WebPageParser(WebPage webPage) {
        this.webPage = webPage;
    }

    /*
     *Get the parser web page
     */
    public WebPage getWebPage() {
        return webPage;
    }

    @Override
    public void run() {
        //Dealing with titles
        try {
            String title = webPage.document.title();
            if (title.isEmpty())
                webPage.htmlText.Title = DEFAULT_PAGE_TITLE;
            else
                webPage.htmlText.Title = Utilities.processString(title);
            webPage.htmlText.Title = StringUtil.normaliseWhitespace(webPage.htmlText.Title);
        } catch (Exception e) {
            e.printStackTrace();
        }

        //Dealing with header text
        try {
            webPage.htmlText.Headers = webPage.document.body().select("h1, h2, h3, h4").eachText().toString();
            webPage.htmlText.Headers = Utilities.processString(webPage.htmlText.Headers);
            webPage.htmlText.Headers = StringUtil.normaliseWhitespace(webPage.htmlText.Headers);
        } catch (Exception e) {
            e.printStackTrace();
        }

        //Dealing with bold text
        try {
            webPage.htmlText.Bold = webPage.document.body().select("b,strong").text();
            webPage.htmlText.Bold = Utilities.processString(webPage.htmlText.Bold.toLowerCase());
            webPage.htmlText.Bold = StringUtil.normaliseWhitespace(webPage.htmlText.Bold);
        } catch (Exception e) {
            e.printStackTrace();
        }

        //Dealing with plain text
        try {
            webPage.htmlText.Plain = webPage.document.select("p,li,tbody,table,b,strong,h5,h6").text();
            webPage.htmlText.Plain = Utilities.processString(webPage.htmlText.Plain);
            webPage.htmlText.Plain = StringUtil.normaliseWhitespace(webPage.htmlText.Plain);
        } catch (Exception e) {
            e.printStackTrace();
        }
        //Insert the web page in the indexer queue
        synchronized (Main.IndexerQueue) {
            Main.IndexerQueue.add(webPage);
        }
    }
}
