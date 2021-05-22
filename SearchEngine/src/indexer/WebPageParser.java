package indexer;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

public class WebPageParser {
    public static final String DEFAULT_PAGE_TITLE = "Default Title";
    private WebPage webPage;                //Current web page
    private StringBuilder rawWebPage;      //Web page as string
    public WebPageParser(URL url, org.jsoup.nodes.Document pageHTML) throws URISyntaxException {
        rawWebPage = new StringBuilder();
        webPage = new WebPage();
        webPage.url = URI.create(url.toString()).normalize().toString();
        Document doc = Jsoup.parse(pageHTML.toString());
        String title = doc.title();
        if (title.isEmpty())
            webPage.title = DEFAULT_PAGE_TITLE;
        else
            webPage.title = title;
        recursiveDFS(doc.body(), doc.tagName());
        System.out.println("Page Title: " + webPage.title);
        System.out.println("Page URL: " + webPage.url);
        System.out.println(rawWebPage);
    }

    /*
     *Get the parser web page
     */
    public WebPage getWebPage() {
        return webPage;
    }

    /*
     *Use DFS to iterate over the content of the web page (DOM)
     */
    private void recursiveDFS(Node node, String tag) {
        if (node instanceof TextNode) {
            String currentNodeText = ((TextNode) node).text().trim();
            if (!currentNodeText.isEmpty())
            {
                rawWebPage.append(currentNodeText + " ");
            }
        }
        if (node instanceof Element) {
            for (Node child : node.childNodes()) {
                recursiveDFS(child, ((Element) node).tagName());
            }
        }
    }
}
